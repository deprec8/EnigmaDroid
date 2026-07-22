/*
 * Copyright (C) 2025-2026 deprec8
 *
 * This file is part of EnigmaDroid.
 *
 * EnigmaDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EnigmaDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EnigmaDroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.deprec8.enigmadroid.ui.settings.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.dialogs.UrlIntentErrorDialog
import io.github.deprec8.enigmadroid.ui.components.navigation.ArrowNavigationButton
import io.github.deprec8.enigmadroid.ui.components.topAppBarWithDrawerWindowInsets

private const val DEV_URL = "github.com/deprec8"
private const val SOURCE_URL = "github.com/deprec8/EnigmaDroid/"
private const val ISSUE_URL = "github.com/deprec8/EnigmaDroid/issues"
private const val TRANSLATION_URL = "crowdin.com/project/enigmadroid"
private const val LICENSE_URL = "www.gnu.org/licenses/gpl-3.0.de.html"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit, onNavigateToLibraries: () -> Unit
) {
    val context = LocalContext.current
    val version = context.packageManager.getPackageInfo(
        context.packageName, PackageManager.GET_ACTIVITIES
    ).versionName ?: stringResource(R.string.version_not_found)
    val libraries = remember {
        try {
            Libs.Builder().withContext(context).build().libraries
        } catch (_: Exception) {
            null
        }
    }
    var showUrlIntentErrorDialog by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, ("https://$url").toUri())

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            showUrlIntentErrorDialog = true
        }
    }

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(windowInsets = topAppBarWithDrawerWindowInsets(), title = {
                Text(
                    text = stringResource(id = R.string.about),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, scrollBehavior = scrollBehavior, navigationIcon = {
                ArrowNavigationButton { onNavigateBack() }
            })
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.creator)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = DEV_URL) },
                leadingContent = { Icon(Icons.Outlined.Person, contentDescription = null) },
                modifier = Modifier.clickable {
                    openUrl(DEV_URL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.version)) },
                leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
                supportingContent = {
                    Text(text = version)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.source_code)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = SOURCE_URL) },
                leadingContent = { Icon(Icons.Outlined.Code, contentDescription = null) },
                modifier = Modifier.clickable {
                    openUrl(SOURCE_URL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.issue_tracker)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = ISSUE_URL) },
                leadingContent = { Icon(Icons.Outlined.ReportProblem, contentDescription = null) },
                modifier = Modifier.clickable {
                    openUrl(ISSUE_URL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.translation)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = TRANSLATION_URL) },
                leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null) },
                modifier = Modifier.clickable {
                    openUrl(TRANSLATION_URL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.license)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = {
                    Text(text = stringResource(R.string.app_license))
                },
                leadingContent = { Icon(Icons.Outlined.Shield, contentDescription = null) },
                modifier = Modifier.clickable {
                    openUrl(LICENSE_URL)
                })
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.third_party_libraries)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
                },
                supportingContent = {
                    Text(
                        text = if (libraries != null) {
                            stringResource(
                                R.string.libraries_amount, libraries.size
                            )
                        } else {
                            stringResource(R.string.error_loading_libraries)
                        }
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.CollectionsBookmark, contentDescription = null
                    )
                },
                modifier = Modifier.clickable { onNavigateToLibraries() })
        }
    }

    if (showUrlIntentErrorDialog) {
        UrlIntentErrorDialog {
            showUrlIntentErrorDialog = false
        }
    }
}