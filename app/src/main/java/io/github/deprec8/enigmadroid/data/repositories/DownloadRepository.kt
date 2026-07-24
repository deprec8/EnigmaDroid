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
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import io.github.deprec8.enigmadroid.model.api.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DownloadRepository(
    private val context: Context,
    private val devicesLocalDataSource: DevicesLocalDataSource,
    private val networkDataSource: NetworkDataSource
) {

    suspend fun downloadMovie(movie: Movie) {
        val uri =
            devicesLocalDataSource.getCurrentStatic()?.buildMovieStreamUri(movie.fileName) ?: return
        val request = DownloadManager.Request(uri).apply {
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

    suspend fun fetchScreenshot(): Result<Uri> = withContext(Dispatchers.IO) {
        networkDataSource.getScreenshot().mapCatching { pair ->
            val bytes = pair.first
            val device = pair.second

            if (bytes.isEmpty()) throw IOException()

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            if (options.outWidth <= 0 || options.outHeight <= 0) throw IOException()

            val resolver = context.contentResolver
            var itemUri: Uri? = null

            val displayName = "screenshot_${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/EnigmaDroid/${device.name}"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            try {
                itemUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        ?: throw IOException()

                val bytesWritten = resolver.openOutputStream(itemUri)?.use { out ->
                    out.write(bytes)
                    bytes.size
                } ?: throw IOException()

                if (bytesWritten <= 0) throw IOException("No bytes written to $itemUri")

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(itemUri, contentValues, null, null)

                return@mapCatching itemUri
            } catch (e: Exception) {
                itemUri?.let { resolver.delete(it, null, null) }
                throw e
            }
        }
    }
}