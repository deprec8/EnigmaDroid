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

package io.github.deprec8.enigmadroid.ui.epg

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import io.github.deprec8.enigmadroid.data.constants.ContentType
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpgPage(
    contentType: ContentType,
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    epgViewModel: EpgViewModel = koinViewModel()
) {

}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun BouquetMenu(
//    bouquets: List<Bouquet>?, currentBouquetReference: String, onBouquetChange: (String) -> Unit
//) {
//    var showMenu by rememberSaveable { mutableStateOf(false) }
//
//    TooltipBox(
//        tooltip = {
//            PlainTooltip {
//                Text(stringResource(R.string.bouquet_menu))
//            }
//        },
//        state = rememberTooltipState(),
//        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
//            TooltipAnchorPosition.Below, 4.dp
//        )
//    ) {
//        IconButton(
//            onClick = {
//                showMenu = true
//            }, enabled = bouquets?.isNotEmpty() == true
//        ) {
//            Icon(
//                Icons.Default.MoreVert, contentDescription = stringResource(R.string.bouquet_menu)
//            )
//            DropdownMenu(
//                expanded = showMenu, onDismissRequest = { showMenu = false }) {
//                bouquets?.forEach { bouquet ->
//                    DropdownMenuItem(text = { Text(bouquet.name) }, onClick = {
//                        onBouquetChange(bouquet.reference)
//                        showMenu = false
//                    }, leadingIcon = {
//                        if (currentBouquetReference == bouquet.reference) {
//                            Icon(
//                                Icons.Filled.Check,
//                                contentDescription = stringResource(R.string.current_bouquet)
//                            )
//                        }
//                    })
//                }
//            }
//        }
//    }
//}