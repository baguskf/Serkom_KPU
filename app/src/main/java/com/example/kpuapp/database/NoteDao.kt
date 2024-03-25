package com.example.kpuapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(datapemilih: Note)

    @Update
    fun update(datapemilih: Note)

    @Delete
    fun delete(datapemilih: Note)

    @Query("SELECT * from Data ORDER BY id ASC")
    fun getAllData(): LiveData<List<Note>>

    @Query("SELECT * FROM Data WHERE nik = :nik")
    fun getDataPemilihByNIK(nik: String): LiveData<Note>
}