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

package io.github.deprec8.enigmadroid.ui.stream.movie


import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.model.api.movies.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MovieStreamViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController: StateFlow<MediaController?> = _mediaController.asStateFlow()

    private val _shouldEnterPipMode = MutableStateFlow(false)
    val shouldEnterPipMode: StateFlow<Boolean> = _shouldEnterPipMode.asStateFlow()

    private var playerListener: Player.Listener? = null

    @OptIn(UnstableApi::class)
    suspend fun init(
        movies: List<Movie>, index: Int, controllerFuture: ListenableFuture<MediaController>
    ) {
        val mediaItems = mutableListOf<MediaItem>()

        movies.forEach {
            mediaItems.add(
                MediaItem.Builder().setUri(apiRepository.buildMovieStreamUrl(it.fileName))
                    .setMediaMetadata(
                        MediaMetadata.Builder().setTitle(it.eventName).setArtist(it.serviceName)
                            .setDescription(it.shortDescription).build()
                    ).build()
            )
        }

        controllerFuture.addListener({
            val controller = try {
                controllerFuture.get()
            } catch (_: Exception) {
                return@addListener
            }

            _mediaController.value = controller

            playerListener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _shouldEnterPipMode.value = isPlaying
                }
            }

            controller.apply {
                setMediaItems(mediaItems)
                seekTo(index, 0L)
                addListener(playerListener !!)
                prepare()
                play()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onCleared() {
        _shouldEnterPipMode.value = false
        playerListener?.let {
            _mediaController.value?.removeListener(it)
        }
        _mediaController.value = null
        super.onCleared()
    }
}