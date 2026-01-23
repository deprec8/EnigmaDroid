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

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.topAppBarWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit,
    onNavigateToLibraries: () -> Unit
) {
    val context = LocalContext.current
    val info = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_ACTIVITIES
    )
    val libraries by produceLibraries()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    val appDeveloperURL = stringResource(R.string.app_developer_url)
    val appSourceURL = stringResource(R.string.app_source_url)
    val appIssueURL = stringResource(R.string.app_issue_url)
    val appTranslationURL = stringResource(R.string.app_translation_url)
    val appLicenseURL = stringResource(R.string.app_license_url)

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(
                windowInsets = topAppBarWithDrawerWindowInsets(),
                title = {
                    Text(
                        text = stringResource(id = R.string.about),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                supportingContent = { Text(text = stringResource(R.string.app_developer)) },
                leadingContent = { Icon(Icons.Outlined.Person, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openUrl(context, appDeveloperURL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.version)) },
                leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
                supportingContent = {
                    Text(
                        text = info.versionName
                            ?: stringResource(R.string.version_not_found)
                    )
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.source_code)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = stringResource(R.string.app_source)) },
                leadingContent = { Icon(Icons.Outlined.Code, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openUrl(context, appSourceURL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.issue_tracker)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = stringResource(R.string.app_issue)) },
                leadingContent = { Icon(Icons.Outlined.ReportProblem, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openUrl(
                        context,
                        appIssueURL
                    )
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.translation)) },
                trailingContent = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                supportingContent = { Text(text = stringResource(R.string.app_translation)) },
                leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openUrl(
                        context,
                        appTranslationURL
                    )
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
                    IntentUtils.openUrl(context, appLicenseURL)
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.third_party_libraries)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
                },
                supportingContent = {
                    Text(
                        text = stringResource(
                            R.string.libraries,
                            libraries?.libraries?.size ?: ""
                        )
                    )
                },
                leadingContent = { Icon(Icons.Outlined.Book, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToLibraries() })
        }
    }
}