package com.example.retrofitapp.presentation.views.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentUsersListBinding
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.adapter.UserListAdapter
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.example.retrofitapp.utils.manager.SessionManager
import com.taskeasy.design.BottomSheetWithActionButtons
import com.taskeasy.design.util.loadingDialog
import javax.inject.Inject

class UsersListFragment: Fragment() {
    private lateinit var  userList: LiveData<MutableList<User>>
    private lateinit var dropdownMenuAppBar : ImageView
    private lateinit var  binding: FragmentUsersListBinding

    private val prefName ="MyPrefs"
    private val emailKey = "email"

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    @Inject
    lateinit var mainViewModel : MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RetrofitApp).appComponent.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUsersListBinding.inflate(layoutInflater)
        val view = binding.root
        dropdownMenuAppBar = binding.dropdownMenuAppbar

        val sp = requireActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val emailText = sp.getString(emailKey, "")
        userList= mainViewModel.selectAllUser(emailText.toString())

        val recyclerView: RecyclerView = binding.recyclerView

        mainViewModel.usersLiveData.observe(viewLifecycleOwner, Observer { users ->

            loadingDialog(true, isCancelable = true)
            Handler(Looper.getMainLooper()).postDelayed({
                val adapter = UserListAdapter(users, mainViewModel,childFragmentManager)
                recyclerView.adapter = adapter
                val linearLayoutManager = LinearLayoutManager(requireContext())
                recyclerView.setLayoutManager(linearLayoutManager)
                adapter.updateData(users)
                        loadingDialog(false)
           }, 3000)

        })
        mainViewModel.getUsers()

        if (!sessionManager.isLoggedIn()) {
            Log.i("log Information", "Login hai")
            findNavController().navigate(R.id.action_usersListFragment_to_loginFragment)
        }
        dropdownMenuAppBar.setOnClickListener{v->
            val popupMenu  = PopupMenu(view.context,v)
            popupMenu.inflate(R.menu.popup_menu_appbar)

            popupMenu.setOnMenuItemClickListener{ item->
                when(item.itemId){
                    R.id.removeAllUser->{
                        Log.i("remove clicked ?", "remove clicked")
                        BottomSheetWithActionButtons().apply {
                            title = "Alert"
                            message = "Do you want to Delete All Users ?"
                            positiveText = com.taskeasy.design.R.string.delete
                            positiveAction = {
                                sessionManager.getUserEmail()
                                    ?.let { mainViewModel.deleteAllUser(it) }
                            }
                            negativeText = com.taskeasy.design.R.string.cancel
                            isDangerButton = true
                        }.showNow(childFragmentManager, null)

                        true
                    }
                    R.id.logout->{
                        sessionManager.logoutUser()
                        findNavController().navigate(R.id.action_usersListFragment_to_loginFragment)
                        Toast.makeText(
                            requireContext(),
                            "Log out Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    else->false
                }
            }
            popupMenu.show()
        }
        return view
    }
}