package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Modal.Contacts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_friends.*
import kotlinx.android.synthetic.main.item_users.view.*

class FindFriendsActivity : AppCompatActivity() {
    val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)

        setSupportActionBar(findViewById(R.id.find_friends_toolbar))
        supportActionBar?.title = "FindFriends"
        ff_rv.layoutManager=LinearLayoutManager(this)
        firebaseData()
    }

    fun firebaseData() {
        val option = FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(db, Contacts::class.java)
            .setLifecycleOwner(this)
            .build()
        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Contacts, MyViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(baseContext).inflate(R.layout.item_users,parent,false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Contacts) {
                holder.username.text = model.name
                holder.userstatus.text=model.status
                Picasso.get().load(model.image).placeholder(R.drawable.profile_image).into(holder.profileImage)

                holder.itemView.setOnClickListener {
                    val placeid = getRef(position).key.toString()
                    val i=Intent(this@FindFriendsActivity,ProfileActivity::class.java)
                    i.putExtra("placeid",placeid)
                    startActivity(i)
                }

34
            }
        }
        ff_rv.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val username=itemView.findViewById<TextView>(R.id.user_profile_name)
    val userstatus=itemView.findViewById<TextView>(R.id.user_status)
    val profileImage=itemView.findViewById<ImageView>(R.id.user_profile_image)
    // val onlinestatus=itemView.findViewById<ImageView>(R.id.user_online_status)

}

