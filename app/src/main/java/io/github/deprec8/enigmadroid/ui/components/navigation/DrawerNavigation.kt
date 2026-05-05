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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

@Composable
fun rememberDrawerNavigationState(
    startKey: NavKey, topLevelKeys: Set<NavKey>
): DrawerNavigationState {

    val topLevelKey = rememberSerializable(
        startKey, topLevelKeys, serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startKey)
    }

    val backStacks = topLevelKeys.associateWith { key ->
        rememberNavBackStack(key)
    }


    return remember(startKey, topLevelKeys) {
        DrawerNavigationState(
            startKey = startKey, topLevelKey = topLevelKey, backStacks = backStacks
        )
    }
}

class DrawerNavigationState(
    val startKey: NavKey,
    topLevelKey: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {

    var topLevelKey: NavKey by topLevelKey
    val stacksInUse: List<NavKey>
        get() = if (topLevelKey == startKey) {
            listOf(startKey)
        } else {
            listOf(startKey, topLevelKey)
        }
}

@Composable
fun DrawerNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {

    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack, entryDecorators = decorators, entryProvider = entryProvider
        )
    }

    return stacksInUse.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
}

class DrawerNavigator(val state: DrawerNavigationState) {

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelKey = route
        } else {
            state.backStacks[state.topLevelKey]?.add(route)
        }
    }

    fun goBack() {
        val currentStack =
            state.backStacks[state.topLevelKey] ?: error("Stack for ${state.topLevelKey} not found")
        val currentKey = currentStack.last()

        if (currentKey == state.topLevelKey) {
            state.topLevelKey = state.startKey
        } else {
            currentStack.removeLastOrNull()
        }
    }

    fun goTop() {
        val currentStack =
            state.backStacks[state.topLevelKey] ?: error("Stack for ${state.topLevelKey} not found")

        currentStack.replaceAll { state.topLevelKey }
    }
}