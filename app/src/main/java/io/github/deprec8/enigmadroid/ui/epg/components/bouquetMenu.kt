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

package io.github.deprec8.enigmadroid.ui.epg.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.Bouquet

@Composable
fun BouquetMenu(
    bouquets: List<Bouquet>,
    currentBouquetReference: String,
    loadingState: LoadingState,
    onBouquetChange: (bouquetReference: String) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = true
    }, enabled = bouquets.isNotEmpty() && loadingState == LoadingState.LOADED) {
        Icon(
            Icons.Default.MoreVert, contentDescription = stringResource(R.string.open_bouquet_menu)
        )
        DropdownMenu(
            expanded = showMenu, onDismissRequest = { showMenu = false }) {
            bouquets.forEach { bouquet ->
                DropdownMenuItem(text = { Text(bouquet.name) }, onClick = {
                    onBouquetChange(bouquet.reference)
                    showMenu = false
                }, leadingIcon = {
                    if (currentBouquetReference == bouquet.reference) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.current_bouquet)
                        )
                    }
                })
            }
        }
    }
}