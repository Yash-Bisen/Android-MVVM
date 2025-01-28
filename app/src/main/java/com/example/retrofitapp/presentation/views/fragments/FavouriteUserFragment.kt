package com.example.retrofitapp.presentation.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitapp.R
import com.example.retrofitapp.RetrofitApp
import com.example.retrofitapp.databinding.FragmentFavouriteUsersBinding
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.adapter.FavouriteUserAdapter
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import javax.inject.Inject

class FavouriteUserFragment : Fragment() {
    private lateinit var  userList: LiveData<MutableList<User>>
    private lateinit var dropdownMenuAppBar : ImageView
    private lateinit var binding: FragmentFavouriteUsersBinding
    private lateinit var textView: TextView

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


        binding = FragmentFavouriteUsersBinding.inflate(layoutInflater)
        val view =  binding.root
        textView = binding.NoUsers
        dropdownMenuAppBar = binding.dropDownMenuAppBAr
        userList=mainViewModel.selectFavouriteUser()

        val recyclerView: RecyclerView = binding.recyclerViewFavroiteUser
        val linearLayoutManager = LinearLayoutManager(requireContext())

        recyclerView.setLayoutManager(linearLayoutManager)

        userList.observe(viewLifecycleOwner) { users ->
            val adapter = FavouriteUserAdapter(users, mainViewModel)
            recyclerView.adapter = adapter

            if(users.size == 0){
                textView.visibility = View.VISIBLE
            }else {

                textView.visibility = View.GONE
            }
        }



        dropdownMenuAppBar.setOnClickListener{v->

            val popupMenu  = PopupMenu(view.context,v)
            popupMenu.inflate(R.menu.popup_menu_appbar)

            popupMenu.menu.findItem(R.id.unFavouriteAll)?.isVisible = true
            popupMenu.menu.findItem(R.id.removeAllUser)?.isVisible = false
            popupMenu.menu.findItem(R.id.logout)?.isVisible = false

            popupMenu.setOnMenuItemClickListener{ item->

                when(item.itemId){
                    R.id.unFavouriteAll->{
                        mainViewModel.updateFavouriteAll(false)

                        true
                    }
                    else->false
                }

            }
            popupMenu.show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

//         Set up navigation back button
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}