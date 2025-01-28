package com.example.retrofitapp.presentation.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentLoginBinding
import com.example.retrofitapp.domain.model.LoginRequest
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.example.retrofitapp.utils.manager.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.taskeasy.design.EditTextInputLayout
import com.taskeasy.design.util.SnackBarPosition
import com.taskeasy.design.util.SnackBarType
import com.taskeasy.design.util.snackBar
import javax.inject.Inject


class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentLoginBinding
    private lateinit var editEmailText: EditTextInputLayout
    private lateinit var editPasswordText: EditTextInputLayout
    private lateinit var editRememberMe: CheckBox
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var button: SignInButton
    private lateinit var   mGoogleSignInClient: GoogleSignInClient
    private val prefName = "MyPrefs"
    private val emailKey: String = "email"
    private val passwordKey: String = "password"
    private val rememberMeKey: String = "rememberMe"
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"


    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RetrofitApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        val view = binding.root

        editEmailText = binding.emailText
        editPasswordText = binding.passwordText
        editRememberMe = binding.rememberMe
        buttonLogin = binding.buttonLogin
        buttonRegister = binding.buttonRegisters
        button = binding.signInButton

        buttonLogin.setOnClickListener {
            val email = editEmailText.etInput.text.toString().trim()
            val password = editPasswordText.etInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                editEmailText.showError("please enter correct Email")
                editPasswordText.showError("Please Enter Password")
                snackBar(
                    "Login Failed",
                    SnackBarType.ERROR,
                    position = SnackBarPosition.TOP
                )

            } else {
                if (email.matches(emailPattern.toRegex())) {

                    val user =   mainViewModel.getUserByEmail(email)
                    mainViewModel.loginUser(LoginRequest(email, password))

                    if (user != null && user.password == password) {

                        sessionManager.loginUser(user.email)
                        findNavController().navigate(R.id.action_loginFragment_to_usersListFragment)

                        snackBar(
                            "Login Success",
                            position = SnackBarPosition.TOP
                        )

                    } else if (user == null || user.password == password) {
                        editEmailText.showError("please enter correct Email")
                        editPasswordText.showError("Please Enter Correct  Password")
                    }else{
                        editPasswordText.showError("Please Enter Correct  Password")
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Invalid email/UserName address",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        button.setOnClickListener{
            signIn()
        }

        buttonRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(emailKey, "")
        val password = arguments?.getString(passwordKey,"")

        if (!email.isNullOrEmpty()) {
            editEmailText.etInput.setText(email)
            editPasswordText.etInput.setText(password)
        }
        navController = Navigation.findNavController(view)
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences  = this.activity?.getSharedPreferences(prefName, Context.MODE_PRIVATE)

        val myEdit = sharedPreferences?.edit()

        if (editRememberMe.isChecked) {
            myEdit?.putString(emailKey, editEmailText.etInput.text.toString())
            myEdit?.putString(passwordKey, editPasswordText.etInput.text.toString())
        } else {
            myEdit?.remove(emailKey)
            myEdit?.remove(passwordKey)
        }

        myEdit?.putBoolean(rememberMeKey,editRememberMe.isChecked)
        myEdit?.apply()
    }

    override fun onResume() {
        super.onResume()

        val sp = this.activity?.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val text1 = sp?.getString(emailKey, "")
        val text2 = sp?.getString(passwordKey, "")
        val checkbox = sp?.getBoolean(rememberMeKey,false)?: false

        if(checkbox) {
            editEmailText.etInput.setText(text1)
            editPasswordText.etInput.setText(text2)
        }
        else{
            editEmailText.etInput.setText("")
            editPasswordText.etInput.setText("")
        }
        editRememberMe.isChecked = checkbox

//        val successMessage = arguments?.getString("successMessage", "")
//
//        if (!successMessage.isNullOrEmpty()) {
//            val inlineMessageLayout = InlineMessageLayout(requireContext())
//            inlineMessageLayout.setMessage(successMessage, InlineMessageType.SUCCESS)
//            val rootView = binding.layoutContainer
//            (rootView as  ViewGroup).addView(inlineMessageLayout)
//        }

    }

    companion object {
        private const val TAG = "SignInFragment"
        private const val RC_SIGN_IN = 123
    }

    private fun signIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.revokeAccess()
        val signInIntent = mGoogleSignInClient.signInIntent

        @Suppress("DEPRECATION")
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Old Way")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.i( "Request Code : Success",task.toString())
            try {
                val account = task.getResult(ApiException::class.java)
                // Signed in successfully, authenticate with Firebase
                // Here, you can use account to get user details like email, name, etc.
                val email = account?.email ?: ""
                val name = account?.displayName ?: ""
                val mobileNo = "8718898787"
                val password = "         "
                val currentPhotoPath: String = (account?.photoUrl ?:" ").toString()

                val imageUrl = currentPhotoPath.ifBlank {
                    // Calculate initials from the name
                    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }
                        .joinToString("")
                    initials
                }
                // Check if the user already exists in Room database
                val existingUser = mainViewModel.getUserByEmail(email)
                if (existingUser == null) {
                    // If the user doesn't exist, store it in Room database
                    val newUser = User(
                        id = 0,
                        name = name,
                        mobileNo = mobileNo,
                        email = email,
                        password = password,
                        favourite = false,
                        imageUrl = imageUrl,
                        profileImagePlaceholder = imageUrl
                    )
                    mainViewModel.insert(newUser)
                }
                sessionManager.loginUser(email)
                findNavController().navigate(R.id.action_loginFragment_to_usersListFragment)
                snackBar(
                    "Login Success",
                    position = SnackBarPosition.TOP
                )

            } catch (e: ApiException) {
                // Sign in failed, handle error
                Log.w(TAG, "signInResult:failed code=" + e.statusCode)
                Toast.makeText(
                    requireContext(),
                    "Failed to sign in: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Log.i( "handleSignInResult: Failed",resultCode.toString())
        }
    }
}