package com.example.geolocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.geolocation.databinding.FragmentMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kotlinx.coroutines.launch

class MainFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mBinding: FragmentMainBinding
    private val myLocationServices = MyLocationServices()
    private var myLocation: Location? = null
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()
        lifecycleScope.launch {
            myLocation = myLocationServices.getUserLocation(requireContext())

            if (myLocation != null) {
                Toast.makeText(
                    requireContext(),
                    myLocation!!.latitude.toString() + "" + myLocation!!.longitude.toString(),
                    Toast.LENGTH_LONG
                ).show()

                createMyMarker()
            }
        }
        setupMap()
    }

    private fun createMyMarker() {
        val myCoordinates = LatLng(myLocation!!.latitude, myLocation!!.longitude)
        val marker = MarkerOptions().position(myCoordinates).title("Mi Posición")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(myCoordinates, 9f)
        )
    }

    private fun setupMap() {
        mBinding
            .frgmntMap
            .getFragment<SupportMapFragment>()
            .getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()

    }

    private fun createMarker() {
        val kiotoCoordinates = LatLng(35.0116, 135.7681)
        val marker = MarkerOptions().position(kiotoCoordinates).title("Prueba")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(kiotoCoordinates, 9f)
        )

    }


    private fun checkPermissions() {
        val fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermissionGranted || !coarseLocationPermissionGranted) {
            // Al menos uno de los permisos no está concedido
            requestPermissions()
        } else {
            // Ambos permisos están concedidos, puedes acceder a lo que sea
        }
    }

    private fun requestPermissions() {
        val fineLocationPermissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        )

        val storagePermissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermissionGranted || storagePermissionGranted) {
            // El usuario ya ha rechazado los permisos
            Toast.makeText(
                requireContext(),
                "Permisos rechazados",
                Toast.LENGTH_LONG
            ).show()
        } else {

            // Pedir permisos
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                777
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 777) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Ambos permisos concedidos, puedes acceder a lo que sea

            } else {
                Toast.makeText(
                    requireContext(),
                    "Permisos rechazados por primera vez",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}