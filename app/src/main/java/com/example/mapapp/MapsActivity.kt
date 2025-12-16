package com.example.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mapapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private val db = FirebaseFirestore.getInstance()


    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        binding.fab.setOnClickListener {
            if (::map.isInitialized) {
                val center = map.cameraPosition.target
                addLocationToFirestore(center)
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
    }

    private fun listenForLocations() {
        db.collection("locations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                map.clear()
                snapshot?.documents?.forEach { doc ->
                    val lat = doc.getDouble("lat")
                    val lng = doc.getDouble("lng")
                    val name = doc.getString("name") ?: "Location"
                    if (lat != null && lng != null) {
                        val pos = com.google.android.gms.maps.model.LatLng(lat, lng)
                        map.addMarker(MarkerOptions().position(pos).title(name))
                    }
                }
            }
    }

    private fun addLocationToFirestore(pos: com.google.android.gms.maps.model.LatLng) {
        val data = hashMapOf(
            "name" to "My Place",
            "lat" to pos.latitude,
            "lng" to pos.longitude,
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("locations")
            .add(data)
            .addOnSuccessListener { Toast.makeText(this, "Saved location", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val stockholm = com.google.android.gms.maps.model.LatLng(59.3293, 18.0686)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(stockholm,12f))
        listenForLocations()
    }

}