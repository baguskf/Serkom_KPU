package com.example.kpuapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kpuapp.NoteDiffCallback
import com.example.kpuapp.database.Note
import com.example.kpuapp.databinding.ItemNoteBinding
import com.example.kpuapp.ui.input.InputDataActivity
import java.util.ArrayList

class RvAdapter : RecyclerView.Adapter<RvAdapter.DataPemilihViewHolder>() {
    private val listDataPemilih = ArrayList<Note>()

    // Metode untuk mengatur atau mengupdate daftar DataPemilih.
    fun setListDataPemilih(listDataPemilih: List<Note>) {
        val diffCallback = NoteDiffCallback(this.listDataPemilih, listDataPemilih)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listDataPemilih.clear()
        this.listDataPemilih.addAll(listDataPemilih)
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataPemilihViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataPemilihViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataPemilihViewHolder, position: Int) {
        holder.bind(listDataPemilih[position])
    }

    // Mengembalikan ukuran daftar DataPemilih.
    override fun getItemCount(): Int {
        return listDataPemilih.size
    }


    inner class DataPemilihViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(datapemilih: Note) {
            with(binding) {
                tvItemNik.text = "NIK : " + datapemilih.nik?.toString() ?: ""
                tvItemDate.text = "Tanggal : " + datapemilih.tgl
                tvItemNama.text = "Nama : " + datapemilih.nama

                cvItemNote.setOnClickListener {
                    val intent = Intent(it.context, InputDataActivity::class.java)
                    intent.putExtra(InputDataActivity.EXTRA_NOTE, datapemilih)
                    it.context.startActivity(intent)
                }
            }
        }
    }
}