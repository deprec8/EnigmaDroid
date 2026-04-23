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

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R

@Composable
fun RenameMovieDialog(
    oldName: String, onDismissRequest: () -> Unit, onConfirmRequest: (input: String) -> Unit
) {
    val textFieldState = rememberTextFieldState(oldName)
    val isCorrect = rememberSaveable(textFieldState.text, oldName) {
        textFieldState.text != oldName && textFieldState.text.isNotBlank()
    }

    AlertDialog(onDismissRequest = {
        onDismissRequest()
    }, title = { Text(text = stringResource(R.string.rename_movie)) }, icon = {
        Icon(
            Icons.Outlined.Edit, contentDescription = null
        )
    }, text = {
        OutlinedTextField(state = textFieldState, label = {
            Text(
                text = stringResource(R.string.new_name)
            )
        }, isError = ! isCorrect, supportingText = {
            if (! isCorrect) {
                Text(stringResource(R.string.name_must_be_changed_and_can_t_be_blank))
            }
        }, trailingIcon = {
            if (! isCorrect) {
                Icon(Icons.Default.Error, null)
            }
        })
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirmRequest(textFieldState.text.toString())
            }, enabled = isCorrect
        ) { Text(stringResource(R.string.confirm)) }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) { Text(stringResource(R.string.cancel)) }
    })
}

@Composable
fun MoveMovieDialog(
    oldDirectory: String, onDismissRequest: () -> Unit, onConfirmRequest: (input: String) -> Unit
) {
    val textFieldState = rememberTextFieldState(oldDirectory)
    val isCorrect = rememberSaveable(textFieldState.text, oldDirectory) {
        textFieldState.text != oldDirectory && textFieldState.text.firstOrNull() == '/' && textFieldState.text.lastOrNull() == '/'
    }

    AlertDialog(onDismissRequest = {
        onDismissRequest()
    }, title = { Text(text = stringResource(R.string.move_movie)) }, text = {
        OutlinedTextField(state = textFieldState, label = {
            Text(
                text = stringResource(R.string.new_directory)
            )
        }, isError = ! isCorrect, supportingText = {
            if (! isCorrect) {
                Text(stringResource(R.string.directory_must_be_changed_and_must_start_and_end_with))
            }
        }, trailingIcon = {
            if (! isCorrect) {
                Icon(Icons.Default.Error, null)
            }
        }, lineLimits = TextFieldLineLimits.SingleLine)
    }, icon = {
        Icon(
            Icons.AutoMirrored.Outlined.DriveFileMove, contentDescription = null
        )
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmRequest(textFieldState.text.toString())
        }, enabled = isCorrect) { Text(stringResource(R.string.confirm)) }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) { Text(stringResource(R.string.cancel)) }
    })
}