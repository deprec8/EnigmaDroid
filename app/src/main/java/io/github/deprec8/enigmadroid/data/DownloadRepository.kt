/*
 * Copyright (C) 2026 deprec8
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

package io.github.deprec8.enigmadroid.data

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.github.deprec8.enigmadroid.model.api.movies.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DownloadRepository(
    private val context: Context,
    private val deviceDatabase: DeviceDatabase,
    private val dataStore: DataStore<Preferences>
) {

    private val currentDeviceKey = intPreferencesKey(PreferenceKey.CURRENT_DEVICE)

    private suspend fun getCurrentDevice(): Device? {
        val listId = dataStore.data.map { preferences ->
            preferences[currentDeviceKey]
        }.firstOrNull()
        val allDevices = deviceDatabase.deviceDao().getAll().firstOrNull()
        return if (allDevices.isNullOrEmpty()) {
            null
        } else {
            allDevices[listId ?: 0]
        }
    }

    suspend fun buildMovieDownloadUrl(
        file: String,
    ): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append(if (device.isHttps) "https://" else "http://")
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}")
                append("/file?file=${file.replace(" ", "%20")}")
            }
        }
    } ?: ""

    suspend fun buildScreenshotUrl(): String = withContext(Dispatchers.Default) {
        getCurrentDevice()?.let { device ->
            buildString {
                append(if (device.isHttps) "https://" else "http://")
                if (device.isLogin) {
                    append("${device.user}:${device.password}@")
                }
                append("${device.ip}:${device.port}")
                append("/grab?format=png")
            }
        }
    } ?: ""

    suspend fun downloadMovie(movie: Movie) {
        val request = DownloadManager.Request(buildMovieDownloadUrl(movie.fileName).toUri()).apply {
            setTitle(context.getString(R.string.downloading, movie.eventName))
            setMimeType("video/mp4")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES,
                "${movie.eventName}.mp4"
            )
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    suspend fun fetchScreenshot() {
        val request = DownloadManager.Request(buildScreenshotUrl().toUri()).apply {
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