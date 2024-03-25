package com.example.kpuapp

import androidx.recyclerview.widget.DiffUtil
import com.example.kpuapp.database.Note

//pengecekan apakah ada perubahan list note
class NoteDiffCallback(private val oldNoteList: List<Note>, private val newNoteList: List<Note>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.nik == newNote.nik
                && oldNote.nama == newNote.nama
                && oldNote.nohp == newNote.nohp
                && oldNote.jeniskelamin == newNote.jeniskelamin
                && oldNote.tgl == newNote.tgl
                && oldNote.alamat == newNote.alamat
                && oldNote.latitude == newNote.latitude
                && oldNote.longitude == newNote.longitude
                && oldNote.gambar.contentEquals(newNote.gambar)
    }
}