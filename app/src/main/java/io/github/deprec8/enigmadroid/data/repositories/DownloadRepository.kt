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

package io.github.deprec8.enigmadroid.data.repositories

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.github.deprec8.enigmadroid.model.api.Movie

class DownloadRepository(
    private val context: Context,
    private val devicesLocalDataSource: DevicesLocalDataSource
) {

    suspend fun downloadMovie(movie: Movie) {
        val url =
            devicesLocalDataSource.getCurrentStatic()?.buildMovieStreamUrl(movie.fileName)
                ?: return
        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle(context.getString(R.string.downloading, movie.eventName))
            setMimeType("video/mp4")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES, "${movie.eventName}.mp4"
            )
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    suspend fun fetchScreenshot() {
        val url = devicesLocalDataSource.getCurrentStatic()?.buildScreenshotUrl() ?: return
        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle(context.getString(R.string.fetching_screenshot))
            setMimeType("image/png")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "enigmadroid_screenshot_${System.currentTimeMillis()}.png"
            )
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}