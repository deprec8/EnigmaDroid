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

package io.github.deprec8.enigmadroid.data.temp

import io.github.deprec8.enigmadroid.core.common.ServiceType

object Reference {

    // dTv, mpeg2HdTv, avcSdTv, avcHdTv, nvecTv, nvecTv20, user134, user195
    const val SERVICE_TYPES_TV =
        "1:7:1:0:0:0:0:0:0:0:(type == 1) || (type == 17) || (type == 22) || (type == 25) || (type == 31) || (type == 134) || (type == 195) || (type == 211)"

    // dRadio, dRadioAvc
    const val SERVICE_TYPES_RADIO = "1:7:2:0:0:0:0:0:0:0:(type == 2) || (type == 10)"

    // All TV bouquets
    const val ALL_BOUQUETS_TV = "$SERVICE_TYPES_TV FROM BOUQUET \"bouquets.tv\" ORDER BY bouquet"

    fun getBouquetsRefString(serviceType: ServiceType) = when (serviceType) {
        ServiceType.Tv -> ALL_BOUQUETS_TV
        ServiceType.Radio -> ALL_BOUQUETS_RADIO
    }

    // All radio bouquets
    const val ALL_BOUQUETS_RADIO =
        "$SERVICE_TYPES_RADIO FROM BOUQUET \"bouquets.radio\" ORDER BY bouquet"

    // TV satellites
    const val ALL_SATELLITES_TV = "$SERVICE_TYPES_TV FROM SATELLITES ORDER BY satellitePosition"

    // Radio satellites
    const val ALL_SATELLITES_RADIO =
        "$SERVICE_TYPES_RADIO FROM SATELLITES ORDER BY satellitePosition"

    // TV providers
    const val ALL_PROVIDERS_TV = "$SERVICE_TYPES_TV FROM PROVIDERS ORDER BY name"

    // Radio providers
    const val ALL_PROVIDERS_RADIO = "$SERVICE_TYPES_RADIO FROM PROVIDERS ORDER BY name"

    // All TV services grouped by satellite
    const val ALL_TV_BY_SATELLITE = "$SERVICE_TYPES_TV FROM SATELLITES ORDER BY satellitePosition"

    // All TV services grouped by provider
    const val ALL_TV_BY_PROVIDER = "$SERVICE_TYPES_TV FROM PROVIDERS ORDER BY name"

    // All radio services grouped by satellite
    const val ALL_RADIO_BY_SATELLITE =
        "$SERVICE_TYPES_RADIO FROM SATELLITES ORDER BY satellitePosition"

    // All radio services grouped by provider
    const val ALL_RADIO_BY_PROVIDER = "$SERVICE_TYPES_RADIO FROM PROVIDERS ORDER BY name"

    // Generic "all services" root
    const val ALL_TV_SERVICES = "1:7:1:0:0:0:0:0:0:0:ORDER BY name"

    const val ALL_RADIO_SERVICES = "1:7:2:0:0:0:0:0:0:0:ORDER BY name"
}