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

package io.github.deprec8.enigmadroid.common.constant

import androidx.navigation3.runtime.NavKey
import io.github.deprec8.enigmadroid.model.api.MovieBatch
import kotlinx.serialization.Serializable

object RootKeys {
    @Serializable
    data object Onboarding : NavKey

    @Serializable
    data object Main : NavKey

    @Serializable
    data object RemoteControl : NavKey
}

object MainKeys {

    @Serializable
    data object Tv : NavKey

    @Serializable
    data object TvEpg : NavKey

    @Serializable
    data class ServiceEpg(val serviceReference: String, val serviceName: String) : NavKey

    @Serializable
    data object RadioEpg : NavKey

    @Serializable
    data object DeviceInfo : NavKey

    @Serializable
    data object Movies : NavKey

    @Serializable
    data class MoviesDirectory(val path: String, val preloadBatch: MovieBatch?) : NavKey

    @Serializable
    data object Timers : NavKey

    @Serializable
    data object Radio : NavKey

    @Serializable
    data object Signal : NavKey

    @Serializable
    data object Current : NavKey

    @Serializable
    data object Settings : NavKey
}

object SettingsKeys {

    @Serializable
    data object About : NavKey

    @Serializable
    data object Libraries : NavKey

    @Serializable
    data object Devices : NavKey

    @Serializable
    data object RemoteControl : NavKey

    @Serializable
    data object Search : NavKey

}