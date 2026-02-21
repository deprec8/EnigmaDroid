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

package io.github.deprec8.enigmadroid.ui.movies.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.movies.Movie

@Composable
fun RenameMovieDialog(
    movie: Movie, onDismissRequest: () -> Unit, onConfirmRequest: (input: String) -> Unit
) {
    var input by rememberSaveable {
        mutableStateOf(movie.eventName)
    }
    val isEnabled = rememberSaveable(input, movie.eventName) {
        input != movie.eventName && ! input.isBlank()
    }

    AlertDialog(onDismissRequest = {
        onDismissRequest()
    }, title = { Text(text = stringResource(R.string.rename_movie)) }, icon = {
        Icon(
            Icons.Outlined.Edit, contentDescription = null
        )
    }, text = {
        OutlinedTextField(value = input, onValueChange = { input = it }, label = {
            Text(
                text = stringResource(R.string.new_name)
            )
        })
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirmRequest(input)
            }, enabled = isEnabled
        ) { Text(stringResource(R.string.confirm)) }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) { Text(stringResource(R.string.cancel)) }
    })
}