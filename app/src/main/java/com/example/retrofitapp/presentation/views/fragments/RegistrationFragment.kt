package com.example.retrofitapp.presentation.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentRegistrationBinding
import com.example.retrofitapp.domain.model.RegisterRequest
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.taskeasy.design.EditTextInputLayout
import com.taskeasy.design.util.SnackBarPosition
import com.taskeasy.design.util.SnackBarType
import com.taskeasy.design.util.snackBar
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RegistrationFragment: Fragment() {

    private lateinit var editName : EditTextInputLayout
    private lateinit var editMobileNo: EditTextInputLayout
    private lateinit var editEmail: EditTextInputLayout
    private lateinit var editPassword: EditTextInputLayout
    private lateinit var editConfirmPassword: EditTextInputLayout
    private lateinit var imageView: ImageView
    private lateinit var navController : NavController
    lateinit var binding: FragmentRegistrationBinding

    private var emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+.+[a-z]"
    private var mobileNoPattern = "^[0-9]{10}$"
    private var passwordPattern ="^[A-Za-z0-9._]{4,14}$"
    private val emailKey = "email"
    private val passwordKey ="password"

    private var currentPhotoPath: String? = null

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    @Inject
    lateinit var mainViewModel: MainViewModel



    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RetrofitApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        val view = binding.root


        editName = binding.editNameText
        editMobileNo = binding.editMobileNo
        editEmail = binding.editTextEmail
        editPassword =binding.editTextPassword
        editConfirmPassword = binding.editTextConfPassword
        imageView = binding.imageView


        editPassword.etInput.setText("")
        editConfirmPassword.etInput.setText("")
        editMobileNo.etInput.setInputType(InputType.TYPE_CLASS_NUMBER)

        imageView.setOnClickListener{
            checkPermissions()
        }

        binding.registerButton.setOnClickListener{
            val email = editEmail.etInput.text.toString().trim()
            val name = editName.etInput.text.toString().trim()
            val mobileNo = editMobileNo.etInput.text.toString().trim()
            val password = editPassword.etInput.text.toString().trim()
            val confirmPassword = editConfirmPassword.etInput.text.toString().trim()

            if(email.isEmpty() && mobileNo.isEmpty() && name.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()) {

                editName.showError("Enter your Name")
                editMobileNo.showError("Enter Mobile No.")
                editEmail.showError("Enter your Email")
                editPassword.showError("Enter Password")
                editConfirmPassword.showError("Confirm Your Password")

                snackBar(
                    "Registration Failed",
                    SnackBarType.ERROR,
                    position = SnackBarPosition.TOP
                )

            }else{
                 if (name.isEmpty()) {
                    editName.showError("Enter your Name")
                }
                else if (mobileNo.isEmpty() || !mobileNo.matches(mobileNoPattern.toRegex())) {
                    editMobileNo.showError("Enter Mobile No.")
                }

                else if (email.isEmpty() || !email.matches(emailPattern.toRegex())) {
                    editEmail.showError("Enter your Email")
                }
                else if (password.isEmpty() || !password.matches(passwordPattern.toRegex())) {
                    editPassword.showError("Enter Password")
                }
                else if (confirmPassword != password) {
                    editConfirmPassword.showError("Confirm Your Password")
                }
                else {
                     currentPhotoPath = if (currentPhotoPath?.isNotBlank() == true) currentPhotoPath else {
                         // Calculate initials from the name
                         val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                         initials
                     }

                     val user = mainViewModel.getUserByEmail(email)
                    if(user == null) {
                        val bundle = Bundle()
                        bundle.putString(emailKey, email)
                        bundle.putString(passwordKey, password)
                        snackBar(
                            "Registration Success",
                            position = SnackBarPosition.TOP
                        )

                        Log.i("Image Path", currentPhotoPath.toString())

                        mainViewModel.insert(
                            User(
                                id = 0,
                                name = name,
                                mobileNo = mobileNo,
                                email = email,
                                password = password,
                                favourite = false,
                                imageUrl = currentPhotoPath,
                                profileImagePlaceholder = currentPhotoPath
                            )
                        )

                        mainViewModel.registerUser(RegisterRequest(email, password))

//

                           findNavController().navigate(R.id.action_registrationFragment_to_loginFragment,bundle)
                    }else if(user != null){
                        snackBar(
                            "You are already Registered",
                            SnackBarType.ERROR,
                            position = SnackBarPosition.TOP
                        )
                    }
                }
            }
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

//         Set up navigation back button
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }

        if (grantedPermissions.contains(Manifest.permission.CAMERA) && grantedPermissions.contains(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Permissions granted, proceed with your logic
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        } else {
            // Permissions denied, handle accordingly (e.g., show a message, disable functionality)
            openImagePicker()
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            @Suppress("DEPRECATION")
            data?.extras?.get("data")?.let { imageBitmap ->
                // Handle image captured from camera
                view?.findViewById<ImageView>(R.id.imageView)?.setImageBitmap(imageBitmap as Bitmap)
                val imagePath = saveImageToInternalStorage(imageBitmap as Bitmap)
                currentPhotoPath = imagePath
            }
        }
    }

    private val selectPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                    val intent = Intent(Intent.ACTION_PICK)
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

    // Function to get image path from URI
    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val imagePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()

        return imagePath
    }
}