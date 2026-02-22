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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.CalendarContract
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.model.api.events.Event

object IntentUtils {

    fun addReminder(context: Context, event: Event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Intent.ACTION_CREATE_REMINDER).apply {
                putExtra(Intent.EXTRA_TITLE, event.title)
                putExtra(
                    Intent.EXTRA_TEXT,
                    event.shortDescription
                )
                putExtra(
                    Intent.EXTRA_TIME,
                    event.beginTimestamp * 1000
                )
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(
                        CalendarContract.Events.TITLE,
                        event.title
                    )
                    putExtra(
                        CalendarContract.Events.DESCRIPTION,
                        event.shortDescription
                    )
                    putExtra(
                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        event.beginTimestamp * 1000
                    )
                    putExtra(
                        CalendarContract.EXTRA_EVENT_END_TIME,
                        (event.beginTimestamp + event.durationInSeconds) * 1000
                    )
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        R.string.no_calendar_found, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(
                    CalendarContract.Events.TITLE,
                    event.title
                )
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    event.shortDescription
                )
                putExtra(
                    CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    event.beginTimestamp * 1000
                )
                putExtra(
                    CalendarContract.EXTRA_EVENT_END_TIME,
                    (event.beginTimestamp + event.durationInSeconds) * 1000
                )
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    R.string.no_calendar_found, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun playMedia(context: Context, url: String, title: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndTypeAndNormalize(
                url.toUri(), "video/mp4"
            )
            putExtra("title", title)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.no_media_player_found), Toast.LENGTH_SHORT
            ).show()

        }
    }

    fun openUrl(context: Context, url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        ).addCategory(Intent.CATEGORY_BROWSABLE)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, R.string.no_browser_found, Toast.LENGTH_SHORT).show()
        }
    }

    fun pinDevice(context: Context, device: Device, deviceId: Int) {
        context.getSystemService(ShortcutManager::class.java).requestPinShortcut(
            ShortcutInfo
                .Builder(context, "device_${device.id}")
                .setIcon(
                    Icon.createWithResource(
                        context,
                        R.mipmap.ic_shortcut_device
                    )
                )
                .setShortLabel(device.name).setIntent(
                    Intent(
                        "io.github.deprec8.enigmadroid.OPEN_WITH_DEVICE",
                    ).putExtra("device_id", deviceId)
                ).build(), null
        )
    }

    fun pinOwifDevice(context: Context, device: Device, url: String) {
        context.getSystemService(ShortcutManager::class.java).requestPinShortcut(
            ShortcutInfo
                .Builder(context, "openwebif_${device.id}")
                .setIcon(
                    Icon.createWithResource(
                        context,
                        R.mipmap.ic_shortcut_website
                    )
                )
                .setShortLabel(device.name + " (Web)").setIntent(
                    Intent(
                        Intent.ACTION_DEFAULT,
                        url.toUri()
                    )
                ).build(), null
        )
    }

    fun openOwif(context: Context, url: String) {
        val activityIntent = Intent(Intent.ACTION_VIEW, "http://www.example.com".toUri())
        val resolveInfos =
            context.packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL)
        val packageNames = ArrayList<String>()
        for (info in resolveInfos) {
            packageNames.add(info.activityInfo.packageName)
        }
        val packageName = CustomTabsClient.getPackageName(context, packageNames, true)
        if (packageName != null) {
            CustomTabsIntent.Builder().setShowTitle(true).setDownloadButtonEnabled(false)
                .setBookmarksButtonEnabled(false).setShareState(
                    CustomTabsIntent.SHARE_STATE_ON
                ).setUrlBarHidingEnabled(true).build().launchUrl(context, url.toUri())
        } else {
            openUrl(context, url)
        }
    }
}