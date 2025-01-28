package com.example.retrofitapp.presentation.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitapp.R
import com.example.retrofitapp.domain.model.DataSource
import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import com.example.retrofitapp.utils.manager.SessionManager
import com.squareup.picasso.Picasso
import com.taskeasy.design.BottomSheetWithActionButtons
import javax.inject.Inject
import kotlin.random.Random

class UserListAdapter  @Inject constructor(
    private var userList: MutableList<DataSource.Data>,
    private var mainViewModel: MainViewModel,
    private var childFragment: FragmentManager)
    : RecyclerView.Adapter<UserListAdapter.UserViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_detail_item, parent, false)

        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    fun updateData(newList: MutableList<DataSource.Data>) {
        userList = newList
//        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position != -1) {
            userList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    inner class UserViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val nameView: TextView = itemView.findViewById(R.id.name)
        private val mobileNoView: TextView = itemView.findViewById(R.id.mobileNo)
        private val emailView: TextView = itemView.findViewById(R.id.email)
        private val passwordView: TextView = itemView.findViewById(R.id.password)
        private val profileView: ImageView = itemView.findViewById((R.id.profileView))
        private val dropdown: ImageView = itemView.findViewById(R.id.dropdown_menu)
        private val favoriteB: ImageView = itemView.findViewById(R.id.heartb)
        private val favoriteF: ImageView = itemView.findViewById(R.id.heartf)
        @Inject
        lateinit var sessionManager: SessionManager

        @SuppressLint("SetTextI18n")
        fun bind(data: DataSource.Data) {
            nameView.text = data.first_name +" "+data.last_name
            val firstDigit = (6..9).random()

            // Generate the remaining 9 digits
            val remainingDigits = (0 until 9).map { Random.nextInt(0, 10) }.joinToString("")

            mobileNoView.text = "$firstDigit$remainingDigits"
            emailView.text = data.email//data.email
            passwordView.text = data.first_name+"1234" // data.first_name

            val imagePath = data.avatar.ifBlank {
                // Calculate initials from the name
                val initials =
                    nameView.text?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }
                        ?.joinToString("")
                initials
            }

            Picasso.get().load(imagePath).into(profileView)

            if (mainViewModel.getUserByEmail(data.email) == null){
                mainViewModel.insert(
                    User(
                        id = 0,
                        name = (data.first_name + " " + data.last_name),
                        mobileNo ="$firstDigit$remainingDigits",
                        email = data.email,
                        password = data.first_name + "1234",
                        favourite = false,
                        imageUrl = data.avatar,
                        profileImagePlaceholder = imagePath
                    )
                )
            }
//            if (imagePath != null) {
//                val bitmap = BitmapFactory.decodeFile(imagePath)
//                profileView.setImageBitmap(bitmap)
//            }
            else {
                mainViewModel.updateMobileNo("$firstDigit$remainingDigits",data.email )
                val user = mainViewModel.getUserByEmail(emailView.text.toString())
                if (user.favourite) {
                    favoriteF.visibility = View.VISIBLE
                }
            }
        }

        init {
            dropdown.setOnClickListener { v ->

                val popupMenu = PopupMenu(itemView.context, v)
                popupMenu.inflate(R.menu.popup_menu_user_item)
                val user = mainViewModel.getUserByEmail(emailView.text.toString())

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.removeUser -> {
                            BottomSheetWithActionButtons().apply {
                                title = "Alert"
                                message = "Do you want to Delete All Users ?"
                                positiveText = com.taskeasy.design.R.string.delete
                                positiveAction = {
                                    mainViewModel.deleteUser(user.email)
                                    @Suppress("DEPRECATION")
                                    removeItem(adapterPosition)
                                }
                                negativeText = com.taskeasy.design.R.string.cancel
                                isDangerButton = true
                            }.showNow(childFragment, null)

                            true
                        }
                        R.id.editUser -> {
                            val bundle = Bundle()
                            bundle.putString("name", user.name)
                            bundle.putString("mobile", user.mobileNo)
                            bundle.putString("email", user.email)
                            Log.i("Edit User", user.toString())
                            itemView.findNavController().navigate(
                                R.id.action_usersListFragment_to_updateUserFragment,
                                bundle
                            )
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            favoriteB.setOnClickListener{
                favoriteF.visibility = View.VISIBLE
                favoriteB.visibility = View.GONE
                mainViewModel.updateFavourite(true, emailView.text.toString())
            }
            favoriteF.setOnClickListener{
                favoriteF.visibility = View.GONE
                favoriteB.visibility = View.VISIBLE
                mainViewModel.updateFavourite(false, emailView.text.toString())
            }
        }
    }
}

