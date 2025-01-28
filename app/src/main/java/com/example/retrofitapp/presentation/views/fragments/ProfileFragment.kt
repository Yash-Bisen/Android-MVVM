package com.example.retrofitapp.presentation.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentProfileBinding
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.example.retrofitapp.utils.manager.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ProfileFragment: Fragment(), OnMapReadyCallback {

    private lateinit var emailText: TextView
    private lateinit var userName: TextView
    private lateinit var imageView: ImageView
    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15f
    }

    private var currentPhotoPath: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RetrofitApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        val view = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        imageView = binding.imageView
        val button = binding.buttonSaveImage

        val  email = sessionManager.getUserEmail()
        Log.i("Login id", email.toString())
        val user = email?.let { mainViewModel.getUserByEmail(it) }

        emailText = binding.email
        userName = binding.userName

         emailText.text = user?.email
        userName.text = user?.name
        val imagePath = user?.imageUrl

        if (!imagePath.isNullOrEmpty() && imagePath.length >= 3) {
            Picasso.get().load(imagePath).into(imageView)
        } else if (imagePath != null) {
            if (imagePath.length == 2){
                setInitialsImage(user.imageUrl)
            }
            else{
                // Handle the case where imagePath is null or empty
                // For example, you can set a default image
    //            imageView.setImageResource(R.drawable.users)
                user.profileImagePlaceholder?.let { imageView.setImageResource(it.toInt()) }
            }
        }

        imageView.setOnClickListener {
            checkPermissions()
            requestStoragePermission()
        }

        button.setOnClickListener {
           if (email != null) {
                mainViewModel.updateImagePath(currentPhotoPath,email)
            }
            Toast.makeText(requireContext(), "Profile saved ", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_practiceFragment)
        }

        Log.i("image path ", imagePath.toString())
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                Log.e("Image Loading", "Bitmap is null")
            }
        } else {
            Log.e("Image Loading", "Image path is null")
        }
        return view
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }

            if (grantedPermissions.contains(Manifest.permission.CAMERA) && grantedPermissions.contains(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Permissions granted, proceed with your logic
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            } else {
                // Permissions denied, handle accordingly (e.g., show a message, disable functionality)
                openImagePicker()

            }
        }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Request permissions
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Permissions are already granted, proceed with your logic
            openImagePicker()
        }
    }


    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                @Suppress("DEPRECATION")
                data?.extras?.get("data")?.let { imageBitmap ->
                    // Handle image captured from camera
                    view?.findViewById<ImageView>(R.id.imageView)
                        ?.setImageBitmap(imageBitmap as Bitmap)
                    val imagePath = saveImageToInternalStorage(imageBitmap as Bitmap)
                    currentPhotoPath = imagePath
                }
            }
        }

    private val selectPictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val selectedImageUri = data?.data
                selectedImageUri?.let { uri ->
                    // Handle image selected from gallery
                    view?.findViewById<ImageView>(R.id.imageView)?.setImageURI(uri)
                    val imagePath = getPathFromUri(uri)
                    currentPhotoPath = imagePath
                }
            }
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Enable location layer and move camera to current location
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentLocation() {
        // Get current location using FusedLocationProviderClient
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Create LatLng object from current location
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        // Move camera to current location
                        mMap.addMarker(MarkerOptions().position(currentLatLng).title("Marker in indore"))
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                DEFAULT_ZOOM
                            )
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Unable to fetch current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        return

    }

    private fun openImagePicker() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo")
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureLauncher.launch(intent)
                }
                items[item] == "Choose from Gallery" -> {
                    val intent =  Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    selectPictureLauncher.launch(intent)
                }
                items[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun saveImageToInternalStorage(image: Bitmap): String {
        // Save the image to a file in internal storage
        val contextWrapper = ContextWrapper(requireContext())
        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)
        val file = File(directory, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Return the file path
        return file.absolutePath
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, you can now access storage
                // Perform actions that require storage access
            } else {
                // Permission is denied
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val imagePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()
        return imagePath
    }


    private fun setInitialsImage(userName: String) {
        // Split the user's name into parts (first name and last name)
        // Create a bitmap with the initials
        val bitmap = createInitialsBitmap(userName[0], userName[1])

        // Set the bitmap to the ImageView
        imageView.setImageBitmap(bitmap)
    }

    private fun createInitialsBitmap(firstNameInitial: Char, lastNameInitial: Char): Bitmap {
        // Create a bitmap with a background color and dimensions
        val width = 40 //resources.getDimensionPixelSize(R.dimen.profile_image_size)
        val height = width // Square bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val backgroundPaint = Paint().apply {
            color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw the initials text on the bitmap
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40F
            textAlign = Paint.Align.CENTER
        }
        val text = "$firstNameInitial$lastNameInitial"
        val textX = width / 2f
        val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(text, textX, textY, textPaint)

        return bitmap
    }
}