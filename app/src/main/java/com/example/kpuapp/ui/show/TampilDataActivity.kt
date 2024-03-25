package com.example.kpuapp.ui.show

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpuapp.ViewModelFactory
import com.example.kpuapp.adapter.RvAdapter
import com.example.kpuapp.databinding.ActivityTampilDataBinding
import com.google.android.material.snackbar.Snackbar

class TampilDataActivity : AppCompatActivity() {
    private var _tampilDataBinding: ActivityTampilDataBinding? = null
    private val binding get() = _tampilDataBinding

    private lateinit var adapter: RvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _tampilDataBinding = ActivityTampilDataBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Set up action bar dengan judul dan tombol kembali.
        supportActionBar?.title = "Daftar Data Pemilih"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel dari VMfactory.
        val daftarDataPemilihViewModel = obtainViewModel(this@TampilDataActivity)

        // ambil dari viewmodel
        daftarDataPemilihViewModel.getAllNotes().observe(this) { datapemilihList ->
            if (datapemilihList != null && datapemilihList.isNotEmpty()) {
                adapter.setListDataPemilih(datapemilihList)
            } else {
                adapter.setListDataPemilih(emptyList())
                showNoDataSnackbar()
            }
        }

        adapter = RvAdapter()

        // Mengatur RecyclerView dengan LinearLayoutManager dan Adapter.
        binding?.rvNotes?.layoutManager = LinearLayoutManager(this)
        binding?.rvNotes?.setHasFixedSize(true)
        binding?.rvNotes?.adapter = adapter

    }

    // Menampilkan Snackbar jika tidak ada data.
    private fun showNoDataSnackbar() {
        val snackbar = Snackbar.make(
            binding?.root!!, // Root view dari layout
            "Tidak ada data saat ini",
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }

    // Membuat ViewModel dengan factory yang disediakan.
    private fun obtainViewModel(activity: AppCompatActivity): TampilDataViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(TampilDataViewModel::class.java)
    }

    // Membersihkan binding ketika Activity dihancurkan.
    override fun onDestroy() {
        super.onDestroy()
        _tampilDataBinding = null
    }

    // Menangani tombol kembali di action bar.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}