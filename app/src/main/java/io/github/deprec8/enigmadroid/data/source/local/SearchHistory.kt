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

package io.github.deprec8.enigmadroid.data.source.local

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Transaction
import io.github.deprec8.enigmadroid.common.enums.ContentType
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "search_histories", indices = [Index(value = ["type", "query"], unique = true)]
)
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: ContentType,
    val query: String,
    val timestamp: Long
)

@Database(entities = [SearchHistoryItem::class], version = 1)
abstract class SearchHistoriesDatabase : RoomDatabase() {

    abstract fun searchHistoriesDao(): SearchHistoriesDao
}

@Dao
interface SearchHistoriesDao {

    @Query("SELECT * FROM search_histories WHERE type = :type ORDER BY timestamp DESC LIMIT 200")
    fun get(type: ContentType): Flow<List<SearchHistoryItem>>

    @Query("SELECT DISTINCT type FROM search_histories")
    fun getTypesWithItems(): Flow<List<ContentType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchHistoryItem)

    @Query(
        """
        DELETE FROM search_histories 
        WHERE type = :type 
        AND id NOT IN (
            SELECT id FROM search_histories 
            WHERE type = :type 
            ORDER BY timestamp DESC 
            LIMIT 200
        )
        """
    )
    suspend fun trim(type: ContentType)

    @Transaction
    suspend fun insertAndTrim(item: SearchHistoryItem) {
        insert(item)
        trim(item.type)
    }

    @Delete
    suspend fun delete(item: SearchHistoryItem)

    @Query("DELETE FROM search_histories WHERE type = :type")
    suspend fun clear(type: ContentType)

    @Query("DELETE FROM search_histories WHERE type IN (:types)")
    suspend fun clear(types: Collection<ContentType>)

    @Query("DELETE FROM search_histories")
    suspend fun clearAll()
}