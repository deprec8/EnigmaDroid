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

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.model.api.MovieBatch

@Composable
fun MoviesActionBar(movieBatch: MovieBatch?, freeSpace: String?, connectionState: ConnectionState) {

    if ((movieBatch != null || freeSpace != null) && connectionState == ConnectionState.CONNECTED) {

        OutlinedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            FlowRow(modifier = Modifier.padding(8.dp)) {
                if (movieBatch != null) {
                    Text(
                        movieBatch.directory,
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.StartEllipsis,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.weight(1f))
                if (freeSpace != null) {
                    Text(
                        stringResource(
                            R.string.free, freeSpace
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}