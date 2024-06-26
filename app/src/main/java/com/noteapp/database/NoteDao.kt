package com.noteapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noteapp.models.Note

@Dao
interface NoteDao {
    // Insert a new note
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    // Update a note
    /*@Query("UPDATE notes_table SET title = :title, description = :description, date = :date WHERE id = :id")
    suspend fun update(id: Int, title: String, description: String, date: String)*/
    @Update
    suspend fun update(note: Note)

    // Delete a note
    @Delete
    suspend fun delete(note: Note)

    // Delete all notes
    @Query("DELETE FROM notes_table")
    suspend fun deleteAll()

    // Get all notes
    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<Note>>
}