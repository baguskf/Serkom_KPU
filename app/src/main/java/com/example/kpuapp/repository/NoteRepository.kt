package com.example.kpuapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.kpuapp.database.Note
import com.example.kpuapp.database.NoteDao
import com.example.kpuapp.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NoteRepository(application: Application) {
    private val mNotesDao: NoteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = NoteRoomDatabase.getDatabase(application)
        mNotesDao = db.noteDao()
    }
    fun getAllNotes(): LiveData<List<Note>> = mNotesDao.getAllData()

    fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }
    fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }
    fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }
    fun getDataPemilihByNIK(nik: String): LiveData<Note> {
        return mNotesDao.getDataPemilihByNIK(nik)
    }
}