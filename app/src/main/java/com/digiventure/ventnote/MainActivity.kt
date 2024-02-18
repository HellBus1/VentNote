package com.digiventure.ventnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.navigation.NavGraph
import com.digiventure.ventnote.ui.theme.VentNoteTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    private lateinit var appUpdateManager: AppUpdateManager
    private var isDialogShowed = false

    companion object {
        const val REQUEST_UPDATE_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check in-app update
        appUpdateManager = AppUpdateManagerFactory.create(this)
        addUpdateStatusListener()
        checkUpdate()

        enableEdgeToEdge()

        setContent {
            VentNoteTheme {
                Surface(
                    modifier = Modifier.safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    NavGraph(navHostController = rememberNavController())

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

    private fun addUpdateStatusListener() {
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    showDialogForCompleteUpdate()
                }
                InstallStatus.INSTALLED -> {
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
                else -> {}
            }
        }
    }

    private fun checkUpdate() {
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(installStateUpdatedListener)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Check that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener {
            when (it.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, IMMEDIATE)
                    for (type in updateTypes) {
                        if (it.isUpdateTypeAllowed(type)) {
                            appUpdateManager.startUpdateFlowForResult(it, type, this, REQUEST_UPDATE_CODE)
                            break
                        }
                    }
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo != null) { // Check if appUpdateInfo is not null
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        showDialogForCompleteUpdate()
                    }
                } else {
                    if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            IMMEDIATE,
                            this,
                            REQUEST_UPDATE_CODE
                        )
                    }
                }
            }
        }
    }

    private fun showDialogForCompleteUpdate() {
        isDialogShowed = true
    }
}