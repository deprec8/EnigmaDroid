/*
 * Copyright (C) 2025 deprec8
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

package io.github.deprec8.enigmadroid.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentInfo(
    @SerialName("info") val info: CurrentDetails = CurrentDetails(),
    @SerialName("now") val now: Current = Current(),
    @SerialName("next") val next: Current = Current(),
)

@Serializable
data class CurrentDetails(
    @SerialName("result") val result: Boolean? = null,
)
@Serializable
data class Current(
    @SerialName("sname") val serviceName: String = "N/A",
    @SerialName("title") val title: String = "N/A",
    @SerialName("begin_timestamp") val beginTimestamp: Long = 0L,
    @SerialName("now_timestamp") val nowTimestamp: Long = 0L,
    @SerialName("sref") val serviceReference: String = "",
    @SerialName("duration_sec") val durationInSeconds: Int = 0,
    @SerialName("provider") val provider: String = "N/A",
    @SerialName("shortdesc") val shortDescription: String = "N/A",
)

@Serializable
data class EPGEventList(
    @SerialName("sname") val serviceName: String = "N/A",
    @SerialName("events") val events: List<EPGEvent> = emptyList(),
)

@Serializable
data class EPGEvent(
    @SerialName("begin") val begin: String = "N/A",
    @SerialName("sname") val serviceName: String = "N/A",
    @SerialName("end") val end: String = "N/A",
    @SerialName("title") val title: String = "N/A",
    @SerialName("genreid") val genreId: Int = 0,
    @SerialName("now_timestamp") val nowTimestamp: Long,
    @SerialName("shortdesc") val shortDescription: String = "N/A",
    @SerialName("begin_timestamp") val beginTimestamp: Long = 0L,
    @SerialName("duration_sec") val durationInSeconds: Int,
    @SerialName("sref") val serviceReference: String = "",
    @SerialName("longdesc") val longDescription: String = "N/A",
    @SerialName("date") val date: String = "N/A",
    @SerialName("progress") val progress: Int = 0,
    @SerialName("genre") val genre: String = "N/A",
    @SerialName("id") val id: Int = 0
)

@Serializable
data class ServiceList(
    @SerialName("services") val services: List<SubserviceList> = emptyList(),
)

@Serializable
data class SubserviceList(
    @SerialName("subservices") val subservices: List<Service> = emptyList()
)

@Serializable
data class Service(
    @SerialName("servicereference") val serviceReference: String = "",
    @SerialName("servicename") val serviceName: String = "N/A"
)

@Serializable
data class MovieList(
    @SerialName("directory") val directory: String = "",
    @Contextual @SerialName("bookmark") val bookmark: Bookmark = Bookmark(),
    @SerialName("movies") val movies: List<Movie> = emptyList(),
    @SerialName("bookmarks") val bookmarks: List<String> = emptyList(),
)

@Serializable
data class BookmarkList(
    @SerialName("directory") val directory: String = "",
    @SerialName("bookmarks") val bookmarks: List<String> = emptyList()
)

@Serializable
data class Movie(
    @SerialName("description") val shortDescription: String = "N/A",
    @SerialName("tags") val tags: String = "N/A",
    @SerialName("filename") val fileName: String = "",
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("eventname") val eventName: String = "N/A",
    @SerialName("length") val length: String = "N/A",
    @SerialName("servicename") val serviceName: String = "N/A",
    @SerialName("begintime") val begin: String = "N/A",
    @SerialName("descriptionExtended") val longDescription: String = "N/A",
    @SerialName("filesize_readable") val filesizeReadable: String = "N/A"
)

@Serializable
data class TimerList(
    @SerialName("timers") val timers: List<Timer> = emptyList(),
    @SerialName("result") val result: Boolean = false
)

@Serializable
data class Timer(
    @SerialName("begin") val beginTimestamp: Long = 0L,
    @SerialName("description") val shortDescription: String = "",
    @SerialName("tags") val tags: String = "",
    @SerialName("always_zap") val alwaysZap: Int = 0,
    @SerialName("toggledisabled") val toggleDisabled: Int = 0,
    @SerialName("disabled") val disabled: Int = 0,
    @SerialName("repeated") val repeated: Int = 0,
    @SerialName("servicename") val serviceName: String = "",
    @SerialName("duration") val durationInSeconds: Int = 0,
    @SerialName("dirname") val directoryName: String = "",
    @SerialName("realend") val end: String = "",
    @SerialName("descriptionextended") val descriptionextended: String = "",
    @SerialName("name") val title: String = "",
    @SerialName("startprepare") val startprepare: Long = 0L,
    @SerialName("realbegin") val begin: String = "",
    @SerialName("end") val endTimestamp: Long = 0L,
    @SerialName("afterevent") val afterEvent: Int = 3,
    @SerialName("justplay") val justPlay: Int = 0,
    @SerialName("serviceref") val serviceReference: String = "",
    @SerialName("filename") val fileName: String? = null,
    @SerialName("state") val state: Int = 0,
    @SerialName("nextactivation") val nextActivation: String? = null,
    @SerialName("cancelled") val cancelled: Boolean = false,
    @SerialName("eit") val eit: Int = 0,
)

@Serializable
data class EventList(
    @SerialName("bouquetName") val bouquetName: String = "N/A",
    @SerialName("events") val events: List<Event> = emptyList(),
)

@Serializable
data class Event(
    @SerialName("sname") val serviceName: String = "N/A",
    @SerialName("title") val title: String = "N/A",
    @SerialName("begin_timestamp") val beginTimestamp: Long = 0L,
    @SerialName("now_timestamp") val nowTimestamp: Long = 0L,
    @SerialName("sref") val serviceReference: String = "",
    @SerialName("id") val id: Int = 0,
    @SerialName("duration_sec") val durationInSeconds: Int = 0,
    @SerialName("shortdesc") val shortDescription: String = "N/A",
    @SerialName("genre") val genre: String = "N/A",
    @SerialName("genreid") val genreId: Int = 0,
    @SerialName("longdesc") val longDescription: String = "N/A"
)

@Serializable
data class BouquetList(
    @SerialName("bouquets") val bouquets: List<List<String>> = emptyList(),
)

@Serializable
data class Tuner(
    @SerialName("type") val type: String = "N/A",
    @SerialName("name") val name: String = "N/A"
)

@Serializable
data class Interface(
    @SerialName("ip") val ip: String = "N/A",
    @SerialName("name") val name: String = "N/A"
)

@Serializable
data class HDD(
    @SerialName("capacity") val capacity: String = "N/A",
    @SerialName("mount") val mount: String = "N/A",
    @SerialName("free") val free: String = "N/A",
    @SerialName("model") val model: String = "N/A"
)

@Serializable
data class DeviceInfo(
    @SerialName("tuners") val tuners: List<Tuner> = emptyList(),
    @SerialName("ifaces") val interfaces: List<Interface> = emptyList(),
    @SerialName("fp_version") val fpVersion: Int = 0,
    @SerialName("kernelver") val kernelVersion: String = "N/A",
    @SerialName("uptime") val uptime: String = "N/A",
    @SerialName("enigmaver") val enigmaVersion: String = "N/A",
    @SerialName("imagever") val imageVersion: String = "N/A",
    @SerialName("brand") val brand: String = "N/A",
    @SerialName("webifver") val webifVersion: String = "N/A",
    @SerialName("hdd") val hdds: List<HDD> = emptyList(),
    @SerialName("model") val model: String = "N/A",
    @SerialName("result") val result: Boolean = false
)

@Serializable
data class SignalInfo(
    @SerialName("agc") val agc: String = "",
    @SerialName("tunernumber") val tunerNumber: String = "",
    @SerialName("snr") val snr: String = "",
    @SerialName("tunertype") val tunerType: String = "N/A",
    @SerialName("inStandby") val inStandby: String = "true"
)