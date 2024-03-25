package com.example.kpuapp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.kpuapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.kpuapp.R
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var savedLocation: LatLng? = null
    private var alamat : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)

        // memilih lokasi
        binding.btnSave.setOnClickListener {
            if (savedLocation != null) {
                val intent = Intent()
                intent.putExtra("latitude", savedLocation?.latitude)
                intent.putExtra("longitude", savedLocation?.longitude)
                intent.putExtra("alamat", alamat)
                Log.d("Alamat : ",alamat.toString())
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@MapsActivity, "Tidak ada data lokasi yang tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        getMyLastLocation()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { latLng ->
            savedLocation = latLng
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Lokasi Dipilih")
                    .draggable(true)
            )
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }
            else -> {
                // No location access granted.
            }
        }
    }

    private val singlePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }




    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            singlePermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    val addressList: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (addressList!!.isNotEmpty()) {
                        val streetAddress = addressList[0].getAddressLine(0)
                        savedLocation = LatLng(location.latitude, location.longitude)
                        alamat = streetAddress
                        showStartMarker(location)
                    } else {
                        Toast.makeText(this@MapsActivity, "Lokasi Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                    savedLocation = LatLng(location.latitude, location.longitude)
                    showStartMarker(location)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Lokasi tidak ditemukan. Coba lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title("Lokasi Awal")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

}