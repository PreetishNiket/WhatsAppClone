package com.example.whatsappclone.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Modal.Contacts
import com.example.whatsappclone.MyViewHolder
import com.example.whatsappclone.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.item_users.view.*

/**
 * A simple [Fragment] subclass.
 */

class ContactsFragment : Fragment() {

     val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Contacts")
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val userref by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val contactsView=inflater.inflate(R.layout.fragment_contacts, container, false)
        contacts_list.layoutManager=LinearLayoutManager(context)

        var currentuserid=auth.currentUser?.uid
        db.child(currentuserid!!)

        return contactsView
    }

    override fun onStart() {
        super.onStart()
        var options = FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(db, Contacts::class.java)
            .build()
        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Contacts, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users,parent,false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Contacts) {
               val userid=getRef(position).key.toString()
                userref.child(userid).addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild("image"))
                        {
                            var profileImage=p0.child("image").value.toString()
                            var profileName=p0.child("name").value.toString()
                            var profileStatus=p0.child("status").value.toString()
                            holder.username.text = profileName
                            holder.userstatus.text = profileStatus
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.profileimage)
                        }
                        else{
                            var profileName=p0.child("name").value.toString()
                            var profileStatus=p0.child("status").value.toString()
                            holder.username.text = profileName
                            holder.userstatus.text = profileStatus
                        }
                    }

                })

            }
        }
        contacts_list.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

    }
}
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val username = itemView.findViewById<TextView>(R.id.user_profile_name)
    val userstatus = itemView.findViewById<TextView>(R.id.user_status)
    val profileimage = itemView.findViewById<ImageView>(R.id.user_profile_image)
    // val onlinestatus=itemView.findViewById<ImageView>(R.id.user_online_status)
}
