package com.example.kpuapp.ui.show

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kpuapp.database.Note
import com.example.kpuapp.repository.NoteRepository

class TampilDataViewModel (application: Application) : ViewModel() {
    private val mNoteRepository: NoteRepository = NoteRepository(application)
    fun getAllNotes(): LiveData<List<Note>> = mNoteRepository.getAllNotes()
}