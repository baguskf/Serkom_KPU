package com.example.kpuapp.ui.input

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.kpuapp.R
import com.example.kpuapp.ViewModelFactory
import com.example.kpuapp.camera.CameraActivity
import com.example.kpuapp.camera.rotateBitmap


import com.example.kpuapp.camera.uriToFile
import com.example.kpuapp.database.Note
import com.example.kpuapp.databinding.ActivityInputDataBinding
import com.example.kpuapp.ui.maps.MapsActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar

class InputDataActivity : AppCompatActivity() {

    // Deklarasi variabel, konstanta, dan objek yang digunakan dalam kelas
    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
        const val CAMERA_X_RESULT = 200

        private var getFile: File? = null
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_MAPS = 1001
    }

    // Deklarasi variabel dan objek yang akan digunakan
    private var isEdit = false
    private var datapemilih: Note? = null
    private lateinit var inputDataViewModel: InputDataViewModel

    private var _activityInputDataBinding: ActivityInputDataBinding? = null
    private val binding get() = _activityInputDataBinding

    private lateinit var selectedDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityInputDataBinding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        inputDataViewModel = obtainViewModel(this@InputDataActivity)

        // Pengecekan dan pengaturan mode Edit atau Tambah
        datapemilih = intent.getParcelableExtra(EXTRA_NOTE)
        if (datapemilih != null) {
            isEdit = true
        } else {
            datapemilih = Note()
        }

        // Set up ActionBar
        val actionBarTitle: String
        val btnTitle: String

        selectedDate = binding?.TextTanggal?.text.toString()

        // Mengatur UI berdasarkan mode Edit atau Tambah
        if (isEdit) {
            actionBarTitle = "Ubah"
            btnTitle = "Update"
            if (datapemilih != null) {
                datapemilih?.let { datapemilih ->
                    binding?.editTextNIK?.setText(datapemilih.nik?.toString())
                    binding?.editTextNama?.setText(datapemilih.nama)
                    binding?.editTextNomorHP?.setText(datapemilih.nohp)
                    // Mengisi radio button jenis kelamin
                    when (datapemilih.jeniskelamin) {
                        "Laki-Laki" -> binding?.radioButtonLakiLaki?.isChecked = true
                        "Perempuan" -> binding?.radioButtonPerempuan?.isChecked = true
                        else -> {
                            // Tidak melakukan apapun atau mungkin memberikan nilai default
                        }
                    }
                    binding?.TextTanggal?.setText(datapemilih.tgl)
                    binding?.editTextAlamat?.setText(datapemilih.alamat)
                    binding?.editTextLatitude?.setText(datapemilih.latitude.toString())
                    binding?.editTextLongitude?.setText(datapemilih.longitude.toString())
                    // Mengisi nilai gambar menggunakan Glide
                    if (datapemilih?.gambar != null) {
                        val bitmap = BitmapFactory.decodeByteArray(datapemilih?.gambar, 0, datapemilih?.gambar?.size ?: 0)
                        binding?.previewImageView?.setImageBitmap(bitmap)
                    } else {
                        Glide.with(applicationContext)
                            .load(R.drawable.take_photo)
                            .into(binding?.previewImageView!!)
                    }
                }
            }
        } else {
            // Konfigurasi UI
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        // Set up ActionBar
        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Permintaan permission untuk akses kamera
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        //button
        binding?.buttonTanggal?.setOnClickListener {
            showDatePicker()
        }
        binding?.btnCamera?.setOnClickListener { startCameraX() }
        binding?.btnGallery?.setOnClickListener { startGallery() }

        binding?.btnLokasi?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAPS) // Menggunakan requestCode yang telah didefinisikan
        }
        binding?.btnSubmit?.text = btnTitle
        binding?.btnSubmit?.setOnClickListener {
            submitData()
        }

    }

    // Fungsi untuk menampilkan DatePicker
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDate = "$year/${monthOfYear + 1}/$dayOfMonth"
                binding?.TextTanggal?.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Fungsi untuk menangani hasil dari Activity lain
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAPS && resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)

            if (latitude != 0.0 && longitude != 0.0) {
                binding?.editTextLatitude?.setText(latitude.toString())
                binding?.editTextLongitude?.setText(longitude.toString())
            }
        }
    }

    // Fungsi untuk menampilkan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }
    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Data Pemilih"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton("Ya") { _, _ ->
                if (!isDialogClose) {
                    inputDataViewModel.delete(datapemilih as Note)
                    showToast("Satu item berhasil dihapus")
                }
                finish()
            }
            setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            val bytes = ByteArrayOutputStream()
            result.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(this@InputDataActivity.contentResolver, result, "Title", null)
            val uri = Uri.parse(path.toString())
            getFile = uriToFile(uri, this@InputDataActivity)

            binding?.previewImageView?.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@InputDataActivity)

            getFile = myFile

            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityInputDataBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): InputDataViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(InputDataViewModel::class.java)
    }

    private fun submitData(){
        val nik = binding?.editTextNIK?.text.toString().trim()
        val nama = binding?.editTextNama?.text.toString().trim()
        val nomorhp = binding?.editTextNomorHP?.text.toString().trim()
        val jeniskelamin = when {
            binding?.radioButtonLakiLaki?.isChecked == true -> "Laki-Laki"
            binding?.radioButtonPerempuan?.isChecked == true -> "Perempuan"
            else -> {
                Log.e("FormEntry", "Jenis Kelamin tidak valid")
                ""
            }
        }
        val tanggal = selectedDate
        val alamat = binding?.editTextAlamat?.text.toString().trim()
        val latitude = binding?.editTextLatitude?.text.toString().trim()
        val longitude = binding?.editTextLongitude?.text.toString().trim()

        // Memanggil fungsi untuk mendapatkan data pemilih berdasarkan NIK
        inputDataViewModel.getDataPemilihByNIK(nik).observe(this) { existingDataPemilih ->
            if (existingDataPemilih != null && (!isEdit || existingDataPemilih.nik != datapemilih?.nik)) {
                binding?.editTextNIK?.error = "NIK already exists"
            } else {
                // Clear any previous errors
                binding?.editTextNIK?.error = null
                if (nik.length != 16) {
                    binding?.editTextNIK?.error = "NIK harus 16 digit"
                } else {
                    binding?.editTextNIK?.error = null
                    if (nama.isEmpty()) {
                        binding?.editTextNama?.error = "Tidak boleh kosong"
                    } else {
                        binding?.editTextNama?.error = null
                        if (nomorhp.isEmpty()) {
                            binding?.editTextNomorHP?.error = "Tidak boleh kosong"
                        } else {
                            binding?.editTextNomorHP?.error = null
                            if (alamat.isEmpty()) {
                                binding?.editTextAlamat?.error = "Tidak boleh kosong"
                            } else {
                                binding?.editTextAlamat?.error = null
                                //proses penyimpanan data
                                datapemilih.let { datapemilih ->
                                    datapemilih?.nik = nik
                                    datapemilih?.nama = nama
                                    datapemilih?.nohp = nomorhp
                                    datapemilih?.jeniskelamin = jeniskelamin
                                    datapemilih?.tgl = tanggal
                                    datapemilih?.alamat = alamat
                                    datapemilih?.latitude = latitude.toDoubleOrNull()
                                    datapemilih?.longitude = longitude.toDoubleOrNull()
                                    if (getFile != null) {
                                        val imageByteArray = getFile?.readBytes()
                                        datapemilih?.gambar = imageByteArray
                                    }
                                }

                                if (isEdit) {
                                    inputDataViewModel.update(datapemilih as Note)
                                    showToast("Satu item berhasil diubah")
                                } else {
                                    inputDataViewModel.insert(datapemilih as Note)
                                    showToast("Satu item berhasil ditambahkan")
                                }
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

}