/*
 * Copyright (C) 2025 deprec8
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding
import io.github.deprec8.enigmadroid.utils.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit
) {
    var showLibsDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val info = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_ACTIVITIES
    )
    val libraries = rememberLibraries(R.raw.aboutlibraries)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(
                modifier = Modifier.horizontalSafeContentPadding(true),
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                supportingContent = { Text(text = stringResource(R.string.app_developer)) },
                leadingContent = { Icon(Icons.Outlined.Person, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openURL(context, context.getString(R.string.app_developer_url))
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
                supportingContent = { Text(text = stringResource(R.string.app_source)) },
                leadingContent = { Icon(Icons.Outlined.Code, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openURL(context, context.getString(R.string.app_source_url))
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.issue_tracker)) },
                supportingContent = { Text(text = stringResource(R.string.app_issue)) },
                leadingContent = { Icon(Icons.Outlined.ReportProblem, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openURL(
                        context,
                        context.getString(R.string.app_issue_url)
                    )
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.translation)) },
                supportingContent = { Text(text = stringResource(R.string.crowdin_com_project_enigmadroid)) },
                leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openURL(
                        context,
                        context.getString(R.string.app_translation_url)
                    )
                })

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.license)) },
                supportingContent = {
                    Text(text = stringResource(R.string.app_license))
                },
                leadingContent = { Icon(Icons.Outlined.Shield, contentDescription = null) },
                modifier = Modifier.clickable {
                    IntentUtils.openURL(context, context.getString(R.string.app_license_url))
                })


            ListItem(
                headlineContent = { Text(text = stringResource(R.string.third_party_libraries)) },
                supportingContent = {
                    Text(
                        text = stringResource(
                            R.string.libraries,
                            libraries.value?.libraries?.size ?: ""
                        )
                    )
                },
                leadingContent = { Icon(Icons.Outlined.Book, contentDescription = null) },
                modifier = Modifier.clickable { showLibsDialog = true })

        }
    }


    if (showLibsDialog) {
        ModalBottomSheet(
            onDismissRequest = { showLibsDialog = false },
            sheetState = sheetState,
            modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Text(
                text = stringResource(R.string.third_party_libraries),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(230.dp),
            ) {
                items(libraries.value?.libraries ?: emptyList()) { library ->
                    ListItem(
                        modifier = Modifier.clickable {
                            if (library.website != null) {
                                IntentUtils.openURL(context, library.website !!)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = BottomSheetDefaults.ContainerColor),
                        trailingContent = {
                            Text(
                                text = if ((library.artifactVersion?.length ?: 0) > 15) {
                                    (library.artifactVersion?.substring(0, 10) ?: "") + "…"
                                } else {
                                    library.artifactVersion ?: ""
                                }
                            )
                        },
                        overlineContent = { Text(text = library.licenses.first().name) },
                        headlineContent = { Text(text = library.name) },
                        supportingContent = {
                            Text(
                                text = library.developers.firstOrNull()?.name
                                    ?: ""
                            )
                        }
                    )
                }
            }
        }
    }
}