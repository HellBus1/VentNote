package com.digiventure.ventnote.feature.drawer

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.digiventure.ventnote.BuildConfig
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.feature.drawer.components.NavDrawerColorPicker
import com.digiventure.ventnote.feature.drawer.components.NavDrawerItem
import com.digiventure.ventnote.feature.drawer.components.NavDrawerItemColorSchemeSwitch
import com.digiventure.ventnote.feature.drawer.components.SectionTitle
import com.digiventure.ventnote.ui.theme.viewmodel.ThemeBaseVM
import com.digiventure.ventnote.ui.theme.viewmodel.ThemeMockVM
import com.digiventure.ventnote.ui.theme.viewmodel.ThemeVM

private const val appPath = "https://play.google.com/store/apps/details?id=com.digiventure.ventnote"
private const val devPagePath = "https://play.google.com/store/apps/developer?id=Mattrmost"

private fun openPlayStore(context: Context, appURL: String, onError: (String) -> Unit) {
    val playIntent: Intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = appURL.toUri()
    }
    try {
        context.startActivity(playIntent)
    } catch (e: ActivityNotFoundException) {
        onError("Cannot open URL: Play Store not found or no app can handle this action.")
    } catch (e: Exception) {
        onError("Cannot open URL")
    }
}


@Composable
fun NavDrawer(
    drawerState: DrawerState,
    content: @Composable () -> Unit,
    onError: (String) -> Unit,
    onBackupPressed: () -> Unit,
    themeViewModel: ThemeBaseVM = hiltViewModel<ThemeVM>()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val maxDrawerWidth = 320.dp

    var currentSchemeName by themeViewModel.currentColorSchemeName

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(
            drawerShape = DrawerDefaults.shape,
            drawerContainerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxHeight()
                .padding(0.dp)
                .widthIn(max = maxDrawerWidth)
                .width(screenWidth * 0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())
            ) {
                SectionTitle(title = stringResource(id = R.string.about_us))

                NavDrawerItem(leftIcon = Icons.Filled.ThumbUp,
                    title = stringResource(id = R.string.rate_app),
                    subtitle = stringResource(id = R.string.rate_app_description),
                    testTagName = TestTags.RATE_APP_TILE,
                    onClick = { openPlayStore(context, appPath, onError) })

                NavDrawerItem(leftIcon = Icons.Filled.Search,
                    title = stringResource(id = R.string.more_apps),
                    subtitle = stringResource(id = R.string.more_apps_description),
                    onClick = { openPlayStore(context, devPagePath, onError) })

                NavDrawerItem(leftIcon = Icons.Filled.Info,
                    title = stringResource(id = R.string.app_version),
                    subtitle = BuildConfig.VERSION_NAME,
                    onClick = { })

                SectionTitle(title = stringResource(id = R.string.preferences))

                NavDrawerColorPicker(
                    leftIcon = Icons.Filled.Settings,
                    title = stringResource(id = R.string.theme_color),
                ) {
                    themeViewModel.updateColorPallet(it.second)
                }

                NavDrawerItemColorSchemeSwitch(
                    leftIcon = Icons.Filled.Person,
                    title = stringResource(id = R.string.theme_setting),
                    currentScheme = currentSchemeName,
                ) {
                    themeViewModel.updateColorScheme(it)
                }

                SectionTitle(title = stringResource(id = R.string.settings))

                NavDrawerItem(
                    leftIcon = Icons.Filled.Share,
                    title = stringResource(id = R.string.backup),
                    subtitle = stringResource(id = R.string.backup_description),
                    onClick = { onBackupPressed() })
            }
        }
    }, content = { content() }, modifier = Modifier.semantics { testTag = TestTags.NAV_DRAWER })
}

@Preview
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun DrawerPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    NavDrawer(
        drawerState = drawerState,
        content = { },
        onError = {},
        onBackupPressed = {},
        themeViewModel = ThemeMockVM()
    )
}

