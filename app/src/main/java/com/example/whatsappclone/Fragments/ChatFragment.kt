package com.example.whatsappclone.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ChatActivity
import com.example.whatsappclone.Modal.Contacts
import com.example.whatsappclone.ProfileActivity
import com.example.whatsappclone.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_friends.*
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val id=auth.currentUser?.uid
    val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Contacts").child(id!!)
    }
    val udb by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }
    val retimage="default_image"



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         val privateChatView=inflater.inflate(R.layout.fragment_chat, container, false)

        val chat = privateChatView.findViewById(R.id.chat_list) as RecyclerView
        chat.layoutManager=LinearLayoutManager(context)

        return privateChatView
    }

    override fun onStart() {
        super.onStart()
        val option = FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(db, Contacts::class.java)
            .setLifecycleOwner(this)
            .build()
        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Contacts, MyViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_users,parent,false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Contacts) {
                val usersIDs=getRef(position).key
                udb.child(usersIDs!!).addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists())
                        {
                            if (p0.hasChild("image"))
                            {
                                val retimage=p0.child("image").value.toString()
                                Picasso.get().load(retimage).placeholder(R.drawable.profile_image).into(holder.profileImage)
                            }
                            val retname=p0.child("name").value.toString()
                            val retstatus=p0.child("status").value.toString()
                            holder.username.text=retname
                            holder.userstatus.text="Last Seen:" + "\n"+"Date" + "Time"
                            holder.itemView.setOnClickListener {
                                val i=Intent(context,ChatActivity::class.java)
                                i.putExtra("visit_user_id",usersIDs)
                                i.putExtra("visit_user_name",retname)
                                i.putExtra("visit_image",retimage)
                                startActivity(i)
                            }
                        }
                    }

                })

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

