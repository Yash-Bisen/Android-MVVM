package com.example.retrofitapp.presentation.adapter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitapp.R
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import javax.inject.Inject

class FavouriteUserAdapter @Inject constructor(
    private val userList: MutableList<User>,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<FavouriteUserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_detail_item, parent, false)
        return UserViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    inner class UserViewHolder(itemView: View,
                         private val adapter: FavouriteUserAdapter,
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.name)
        private val mobileNoView: TextView = itemView.findViewById(R.id.mobileNo)
        private val emailView: TextView = itemView.findViewById(R.id.email)
        private val passwordView: TextView = itemView.findViewById(R.id.password)
        private val dropdown: ImageView = itemView.findViewById(R.id.dropdown_menu)
        private val favoriteB: ImageView = itemView.findViewById(R.id.heartb)
        private val profileView: ImageView = itemView.findViewById((R.id.profileView))
        fun bind(user: User) {
            nameView.text = user.name
            mobileNoView.text = user.mobileNo
            emailView.text = user.email
            passwordView.text = user.password

            val imagePath = user.imageUrl

            if (imagePath != null) {
                if (imagePath.startsWith("https://")) {
                    // If it's from Google Sign-In, load the image using Picasso directly
                    Picasso.get().load(imagePath).into(profileView)
                 } else {
                    // If it's a local file path, load the image using BitmapFactory
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    profileView.setImageBitmap(bitmap)
                }
            }
        }

        init {

            favoriteB.visibility = View.GONE
            dropdown.setOnClickListener { v ->
                val popupMenu = PopupMenu(itemView.context, v)
                popupMenu.inflate(R.menu.popup_menu_user_item)


                popupMenu.menu.findItem(R.id.unFavourite).isVisible = true
                popupMenu.menu.findItem(R.id.removeUser).isVisible = false

                val user = mainViewModel.getUserByEmail(emailView.text.toString())

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.editUser -> {
                            val bundle = Bundle()
                            bundle.putString("name", user.name)
                            bundle.putString("mobile", user.mobileNo)
                            bundle.putString("email", user.email)
                            Log.i("Edit User", user.toString())
                            itemView.findNavController()
                                .navigate(R.id.action_favouriteUserFragment_to_updateUserFragment, bundle)
                            true
                        }
                        R.id.unFavourite -> {
                            mainViewModel.updateFavourite(false, emailView.text.toString())
                            @Suppress("DEPRECATION")
                            val position = adapterPosition
                            if (position != -1) {
                                adapter.userList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }
}

