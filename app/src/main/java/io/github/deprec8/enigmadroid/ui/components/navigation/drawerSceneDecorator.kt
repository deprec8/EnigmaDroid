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

package io.github.deprec8.enigmadroid.ui.components.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope

class DrawerSceneDecoratorStrategy<T : Any>(
    private val isSmallScreenLayout: Boolean, private val drawerContent: @Composable () -> Unit
) : SceneDecoratorStrategy<T> {

    override fun SceneDecoratorStrategyScope<T>.decorateScene(
        scene: Scene<T>
    ): Scene<T> {

        if (isSmallScreenLayout || ! scene.hasDrawer()) return scene

        return DrawerDecoratingScene(
            scene = scene, drawerContent = drawerContent
        )
    }

    companion object { object DrawerSceneMetadataKey : NavMetadataKey<Unit>

        fun drawerScene() = metadata {
            put(DrawerSceneMetadataKey, Unit)
        }

        fun Scene<*>.hasDrawer(): Boolean = metadata.contains<Unit>(DrawerSceneMetadataKey)
    }
}

class DrawerDecoratingScene<T : Any>(
    private val scene: Scene<T>, private val drawerContent: @Composable () -> Unit
) : Scene<T> {

    override val key = scene::class to scene.key
    override val metadata = scene.metadata
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries

    @OptIn(ExperimentalSharedTransitionApi::class)
    override val content: @Composable () -> Unit = {
        val animatedVisibilityScope = LocalNavAnimatedContentScope.current
        val sharedTransitionScope = LocalSharedTransitionScope.current

        val drawerModifier = Modifier.consumeWindowInsets(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
        )

        val sharedModifier = if (sharedTransitionScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedBounds(
                    rememberSharedContentState(key = "nav_drawer_bounds"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                )
            }
        } else Modifier

        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(drawerModifier.then(sharedModifier)) {
                    drawerContent()
                }
            }) {
            scene.content()
        }
    }
}

@Composable
fun ModalNavigationDrawerWrapper(
    enabled: Boolean,
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(enabled) {
        if (! enabled) {
            drawerState.snapTo(DrawerValue.Closed)
        }
    }

    if (enabled) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = drawerState,
                    modifier = Modifier.consumeWindowInsets(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Vertical + WindowInsetsSides.Start
                        )
                    ),
                ) {
                    drawerContent()
                }
            },
        ) {
            content()
        }
    } else {
        content()
    }
}