package com.noteapp.repository

import androidx.lifecycle.LiveData
import com.noteapp.database.NoteDao
import com.noteapp.models.Note

class NoteRepository(private val noteDao: NoteDao) {

    // Insert a new note
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    // Update a note
    suspend fun update(note: Note) {
        noteDao.update(note.id!!, note.title!!, note.description!!, note.date!!)
    }

    // Delete a note
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    // Delete all notes
    suspend fun deleteAll() {
        noteDao.deleteAll()
    }

    // Get all notes
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
}