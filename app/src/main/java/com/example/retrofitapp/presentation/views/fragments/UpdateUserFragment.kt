package com.example.retrofitapp.presentation.views.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentUpdateUserBinding
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.taskeasy.design.EditTextInputLayout
import com.taskeasy.design.util.SnackBarPosition
import com.taskeasy.design.util.SnackBarType
import com.taskeasy.design.util.snackBarWithIcon

import javax.inject.Inject

class UpdateUserFragment: Fragment() {

    private lateinit var editNames : EditTextInputLayout
    private lateinit var editMobileNo : EditTextInputLayout
    private lateinit var editEmail : EditTextInputLayout
    private lateinit var updateButton : Button
    private lateinit var navController : NavController
    lateinit var binding: FragmentUpdateUserBinding
    private var emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+.+[a-z]"
    private var mobileNoPattern = "^[0-9]{10}$"


    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RetrofitApp).appComponent.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateUserBinding.inflate((layoutInflater))
        val view = binding.root
        val name = arguments?.getString("name")
        val email = arguments?.getString("email")
        val mobileNo = arguments?.getString("mobile")

        editNames = binding.editNameText
        editMobileNo =binding.editMobileNo
        editEmail = binding.editTextEmail
        updateButton = binding.updateButton

        editMobileNo.etInput.setInputType(InputType.TYPE_CLASS_NUMBER)
        val user =mainViewModel.getUserByEmail(email.toString())
        val userid = user.id
        Log.i("Name", name.toString())
        Log.i("Mobile ", mobileNo.toString())
        editNames.etInput.setText(name)
        editMobileNo.etInput.setText(mobileNo)
        editEmail.etInput.setText(email)


        updateButton.setOnClickListener{
            val editEmails = editEmail.etInput.text.toString()
            val mobile = editMobileNo.etInput.text.toString()
            val editName = editNames.etInput.text.toString()
            if(editEmails.isEmpty() || mobile.isEmpty() || editName.isEmpty() ) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter All Details",
                    Toast.LENGTH_SHORT
                ).show()
            }else {

                val userExist = mainViewModel.checkEmailOfUser(editEmails, userid)


                if (userExist > 0) {
                    Toast.makeText(requireContext(), "Email Already Exist", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (editEmails.isEmpty() || !editEmails.matches(emailPattern.toRegex())) {
                        editEmail.showError("Enter your Email")
                    } else if (mobile.isEmpty() || !mobile.matches(mobileNoPattern.toRegex())) {
                        editMobileNo.showError("Enter correct Mobile No.")
                    } else if (editName.isEmpty()) {
                        editNames.showError("Enter your Name")
                    } else {
                        mainViewModel.updateUser(
                            editNames.etInput.text.toString(),
                            editMobileNo.etInput.text.toString(),
                            editEmail.etInput.text.toString(),
                            userid
                        )

                        snackBarWithIcon(
                            "you can't update this data",
                            SnackBarType.WARNING,
                            SnackBarPosition.TOP
                        )
                        findNavController().navigateUp()
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
}