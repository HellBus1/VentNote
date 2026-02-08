package com.digiventure.ventnote

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.drawer.NavDrawer
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.digiventure.ventnote.navigation.NavGraph
import com.digiventure.ventnote.navigation.PageNavigation
import com.digiventure.ventnote.ui.theme.components.VentNoteTheme
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    private lateinit var appUpdateManager: AppUpdateManager
    private var isDialogShowed = false
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var currentIntent by mutableStateOf<Intent?>(null)

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.ENABLE_CRASHLYTICS) {
            lifecycleScope.launch(Dispatchers.IO) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            }
        }

        // Pre-warm the database in the background to avoid main-thread blockage on first access
        lifecycleScope.launch(Dispatchers.IO) {
            databaseProxy.getObject()
        }

        currentIntent = intent

        // Initialize AppUpdate components early (must register launcher before STARTED)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}
        addUpdateStatusListener()

        enableEdgeToEdge()

        setContent {
            VentNoteTheme {
                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    PageNavigation(navController)
                }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                // Handle intent from Widget
                androidx.compose.runtime.LaunchedEffect(currentIntent) {
                    currentIntent?.let { intent ->
                        val noteId = intent.getStringExtra("noteId")
                        val actionCreate = intent.getBooleanExtra("action_create", false)

                        if (noteId != null) {
                            navigationActions.navigateToDetailPage(noteId.toInt())
                            clearIntent()
                            currentIntent = null
                        } else if (actionCreate) {
                            navigationActions.navigateToCreatePage()
                            clearIntent()
                            currentIntent = null
                        }
                    }
                }

                // Check for updates automatically on startup without blocking initial frame
                LaunchedEffect(Unit) {
                    checkUpdate(isManual = false)
                }

                Surface(
                    modifier = Modifier.safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    NavDrawer(
                        drawerState = drawerState,
                        onError = {},
                        onBackupPressed = {
                            navigationActions.navigateToBackupPage()
                        },
                        onUpdateCheckPressed = {
                            checkUpdate(isManual = true)
                        },
                        content = {
                            NavGraph(navHostController = navController, openDrawer = {
                                coroutineScope.launch { drawerState.open() }
                            })
                        },
                    )

                    TextDialog(
                        isOpened = isDialogShowed,
                        onDismissCallback = {
                            isDialogShowed = false
                        },
                        onConfirmCallback = {
                            isDialogShowed = false
                            appUpdateManager.completeUpdate()
                        },
                        title = stringResource(id = R.string.success),
                        description = stringResource(id = R.string.update_success_text)
                    )
                }
            }
        }
    }

    /**
     * Adds a listener to handle app update installation status changes.
     * 1. After the update is downloaded, show a dialog and request user confirmation to restart the app.
     */
    private fun addUpdateStatusListener() {
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                showDialogForCompleteUpdate()
            } else if (installState.installStatus() == InstallStatus.INSTALLED) {
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
        }
    }

    /**
     * Checks for available app updates and initiates the update flow if an update is available and allowed.
     * 1. Before starting an update, register a listener for updates.
     */
    private fun checkUpdate(isManual: Boolean = false) {
        appUpdateManager.registerListener(installStateUpdatedListener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                when {
                    appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE) -> {
                        startUpdateFlow(appUpdateInfo, IMMEDIATE)
                        return@addOnSuccessListener
                    }
                    appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE) -> {
                        startUpdateFlow(appUpdateInfo, FLEXIBLE)
                        return@addOnSuccessListener
                    }
                }
            } else if (isManual) {
                android.widget.Toast.makeText(this, "No update available", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        if (isManual) {
            appUpdateInfoTask.addOnFailureListener {
                android.widget.Toast.makeText(this, "Failed to check for update", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * On resuming the activity, check for 2 scenarios:
     * 1. If a flexible update has been downloaded and is awaiting installation,
     *    show a dialog prompting the user to complete the update.
     * 2. If a developer-triggered immediate update is already in progress,
     *    resume the update flow.
     */
    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo != null) {
                when {
                    appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE) &&
                            appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                        showDialogForCompleteUpdate()
                    }
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        startUpdateFlow(appUpdateInfo, IMMEDIATE)
                    }
                }
            }
        }
    }

    private fun startUpdateFlow(updateInfo: AppUpdateInfo, updateType:Int) {
        appUpdateManager.startUpdateFlowForResult(
            updateInfo,
            updateLauncher,
            AppUpdateOptions.newBuilder(updateType).build()
        )
    }

    private fun showDialogForCompleteUpdate() {
        isDialogShowed = true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent = intent
    }

    private fun clearIntent() {
        intent?.removeExtra("noteId")
        intent?.removeExtra("action_create")
    }
}