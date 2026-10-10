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

package io.github.deprec8.enigmadroid.di

import io.github.deprec8.enigmadroid.data.di.dataModule
import io.github.deprec8.enigmadroid.ui.current.CurrentViewModel
import io.github.deprec8.enigmadroid.ui.deviceinfo.DeviceInfoViewModel
import io.github.deprec8.enigmadroid.ui.epg.EpgViewModel
import io.github.deprec8.enigmadroid.ui.live.LiveViewModel
import io.github.deprec8.enigmadroid.ui.main.MainViewModel
import io.github.deprec8.enigmadroid.ui.movies.MoviesViewModel
import io.github.deprec8.enigmadroid.ui.onboarding.OnboardingViewModel
import io.github.deprec8.enigmadroid.ui.remotecontrol.RemoteControlViewModel
import io.github.deprec8.enigmadroid.ui.serviceepg.ServiceEpgViewModel
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesViewModel
import io.github.deprec8.enigmadroid.ui.settings.remotecontrol.RemoteControlSettingsViewModel
import io.github.deprec8.enigmadroid.ui.settings.search.SearchSettingsViewModel
import io.github.deprec8.enigmadroid.ui.signal.SignalViewModel
import io.github.deprec8.enigmadroid.ui.timers.TimersViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    includes(dataModule)

    viewModel<MainViewModel>()
    viewModel<RemoteControlViewModel>()
    viewModel<LiveViewModel>()
    viewModel<OnboardingViewModel>()
    viewModel<ServiceEpgViewModel>()
    viewModel<CurrentViewModel>()
    viewModel<MoviesViewModel>()
    viewModel<TimersViewModel>()
    viewModel<EpgViewModel>()
    viewModel<DeviceInfoViewModel>()
    viewModel<SignalViewModel>()
    viewModel<DevicesViewModel>()
    viewModel<RemoteControlSettingsViewModel>()
    viewModel<SearchSettingsViewModel>()
}