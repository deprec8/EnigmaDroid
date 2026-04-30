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

package io.github.deprec8.enigmadroid.common.enums

enum class ContentType {
    Radio, Tv
}

enum class ContentFlag(val flag: Int) {
    Channel(0), Marker(64), NumberedMarker(320), Directory(7), Group(128), Invisible(512), InvisibleNumberedMarker(
        832
    ),
    InvisibleDirectory(519)
}

enum class RemoteControlKey(val id: Int) {
    PreviousChannel(403), NextChannel(402), VolumeUp(115), VolumeDown(114), Mute(113), NextBouquet(
        407
    ),
    PreviousBouquet(412), Exit(174), Info(358), Menu(139), Help(138), Pvr(393), Audio(392), Up(103), Down(
        108
    ),
    Left(105), Right(106), Ok(352), Red(398), Green(399), Yellow(400), Blue(401), Rewind(165), Play(
        207
    ),
    Pause(119), Stop(128), Forward(163), Tv(377), Radio(385), Text(388), Record(167), Epg(365), One(
        2
    ),
    Two(3), Three(4), Four(5), Five(6), Six(7), Seven(8), Eight(9), Nine(10), Zero(11)
}

enum class RemoteControlPowerKey(val id: Int) {
    Restart(2), ToggleStandby(0), RestartGui(3), Shutdown(1)
}