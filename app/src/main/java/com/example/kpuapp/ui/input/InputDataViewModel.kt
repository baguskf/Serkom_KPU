package com.example.kpuapp.ui.input

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kpuapp.database.Note
import com.example.kpuapp.repository.NoteRepository

class InputDataViewModel(application: Application) : ViewModel() {
    private val mNoteRepository: NoteRepository = NoteRepository(application)
    fun insert(note: Note) {
        mNoteRepository.insert(note)
    }
    fun update(note: Note) {
        mNoteRepository.update(note)
    }
    fun delete(note: Note) {
        mNoteRepository.delete(note)
    }

    fun getDataPemilihByNIK(nik: String): LiveData<Note> {
        return mNoteRepository.getDataPemilihByNIK(nik)
    }
}