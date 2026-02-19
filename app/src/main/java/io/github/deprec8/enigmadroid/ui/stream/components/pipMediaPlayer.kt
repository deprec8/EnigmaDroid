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

package io.github.deprec8.enigmadroid.ui.stream.components

import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toRect
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.PresentationState
import androidx.media3.ui.compose.state.rememberPresentationState
import io.github.deprec8.enigmadroid.ui.stream.components.utils.findActivity

@OptIn(UnstableApi::class)
@Composable
fun PipMediaPlayer(player: Player?, shouldEnterPipMode: Boolean) {

    val context = LocalContext.current
    val presentationState: PresentationState = rememberPresentationState(player, false)

    val scaledPipModifier =
        Modifier
            .resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)
            .onGloballyPositioned { layoutCoordinates ->
                val builder = PictureInPictureParams.Builder()

                if (shouldEnterPipMode && player != null && player.videoSize != VideoSize.UNKNOWN) {
                    val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
                    builder.setSourceRectHint(sourceRect)
                    builder.setAspectRatio(
                        Rational(player.videoSize.width, player.videoSize.height)
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    builder.setAutoEnterEnabled(shouldEnterPipMode)
                }
                context.findActivity().setPictureInPictureParams(builder.build())
            }

    PlayerSurface(
        player, scaledPipModifier
    )

    if (presentationState.coverSurface) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}