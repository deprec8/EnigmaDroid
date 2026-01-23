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

package io.github.deprec8.enigmadroid.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object TimestampUtils {

    fun formatApiTimestampToTime(timestamp: Long): String {
        return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault())
            .format(timestamp * 1000)
    }

    fun formatTimestampToTime(timestamp: Long): String {
        return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault())
            .format(timestamp)
    }

    fun formatApiTimestampToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(timestamp * 1000)
    }

    fun formatTimestampToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(timestamp)
    }

    fun combineDateTime(oldTimestamp: Long, newTimestamp: Long): Long {
        val oldCalendar =
            Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = oldTimestamp }
        val newCalendar =
            Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = newTimestamp }
        return Calendar.getInstance(TimeZone.getDefault()).apply {
            set(Calendar.YEAR, newCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, newCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, oldCalendar.get(Calendar.SECOND))
            set(Calendar.MILLISECOND, oldCalendar.get(Calendar.MILLISECOND))
        }.timeInMillis
    }


    fun getMillisFromTimeString(timeString: String): Long {
        val dateFormat: SimpleDateFormat =
            if (timeString.contains("AM", true) || timeString.contains("PM", true)) {
                SimpleDateFormat("hh:mma", Locale.getDefault())
            } else {
                SimpleDateFormat("HH:mm", Locale.getDefault())
            }

        val date = dateFormat.parse(timeString)
        val calendar = Calendar.getInstance()
        calendar.time = date !!
        return calendar.timeInMillis
    }

    fun combineTimeDate(oldTimestamp: Long, newTimestamp: Long): Long {
        val oldCalendar = Calendar.getInstance().apply { timeInMillis = oldTimestamp }
        val newCalendar = Calendar.getInstance().apply { timeInMillis = newTimestamp }
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, oldCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, oldCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, oldCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, newCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, newCalendar.get(Calendar.SECOND))
            set(Calendar.MILLISECOND, newCalendar.get(Calendar.MILLISECOND))
        }.timeInMillis
    }

    fun millisToHourInt(millis: Long): Int {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = millis
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun millisToMinuteInt(millis: Long): Int {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = millis
        return calendar.get(Calendar.MINUTE)
    }
}