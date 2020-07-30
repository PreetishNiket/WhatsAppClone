package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Users")
    }
    private val chatref by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Chat Requests")
    }
    private val contactsref by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Contacts")
    }
    var receivedid:String=""
    var currState:String="new"
    var senderid:String=""
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
         receivedid=intent?.getStringExtra("placeid").toString()

        retrieveUserInfo()
    }

    private fun retrieveUserInfo() {

        db.child(receivedid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.hasChild("image"))
                {
                    val userImage=dataSnapshot.child("image").value.toString()
                    val userName=dataSnapshot.child("name").value.toString()
                    val userStatus=dataSnapshot.child("status").value.toString()
                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(visit_profile_image)
                    visit_user_name.text = userName
                    visit_user_status.text=userStatus
                    manageChatRequest()
                }
                else{
                    val userName=dataSnapshot.child("name").value.toString()
                    val userStatus=dataSnapshot.child("status").value.toString()
                    visit_user_name.text = userName
                    visit_user_status.text=userStatus
                    manageChatRequest()
                }
            }
            override fun onCancelled(p0:DatabaseError) {
            }
        })
    }

    private fun manageChatRequest() {
        chatref.child(senderid).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               if (dataSnapshot.hasChild(receivedid))
               {
                   var requesttype=dataSnapshot.child(receivedid).child("request_type").value.toString()
                   if (requesttype=="sent")
                   {
                       currState="request_sent"
                       send_message_request_button.text="Cancel Requests"
                   }
                   else if(requesttype=="received"){
                       currState="request_received"
                       send_message_request_button.text="Accept Chat Requests"
                       decline_message_request_button.visibility=View.VISIBLE

                       decline_message_request_button.setOnClickListener{
                           cancelChatrequests()
                       }
                   }
               }
                else{
                   contactsref.child(senderid).addListenerForSingleValueEvent(object :ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {
                       }
                       override fun onDataChange(dataSnapshot1: DataSnapshot) {
                           if (dataSnapshot1.hasChild(receivedid))
                           {
                               currState="friends"
                               send_message_request_button.text="Remove This Friend"
                           }
                       }

                   })
               }
            }
        })
        senderid=auth.currentUser!!.uid
        if (senderid != receivedid)
        {
            send_message_request_button.setOnClickListener {
                send_message_request_button.isEnabled=false
                if (currState == "new")
                {
                    sendChatRequest()
                }
                if (currState=="request_sent")
                {
                    cancelChatrequests()
                }
                if (currState=="request_received")
                {
                    acceptChatrequests()
                }
            }
        }
        else{
            send_message_request_button.visibility= View.INVISIBLE
        }

    }



    private fun sendChatRequest() {
        chatref.child(senderid)
            .child(receivedid)
            .child("request_type")
            .setValue("sent")
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    chatref.child(receivedid).child(senderid).child("request_type").setValue("received")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                send_message_request_button.isEnabled=true
                                currState="request_sent"
                                send_message_request_button.text="Cancel Request"
                            }
                        }
                }
            }
    }
    private fun cancelChatrequests() {

        chatref.child(senderid).child(receivedid)
            .removeValue().addOnCompleteListener {

                if (it.isSuccessful)
                {
                    chatref.child(receivedid).child(senderid)
                        .removeValue().addOnCompleteListener { task ->

                            if (task.isSuccessful)
                            {
                                send_message_request_button.isEnabled=true
                                currState="new"
                                send_message_request_button.text="Send Message"

                                decline_message_request_button.visibility=View.INVISIBLE
                                decline_message_request_button.isEnabled=false

                            }
                        }
                }
            }

    }
    private fun acceptChatrequests() {
        contactsref.child(senderid).child(receivedid)
            .child("Contacts").setValue("Saved")
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    contactsref.child(receivedid).child(senderid)
                        .child("Contacts").setValue("Saved")
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful)
                            {
                                    chatref.child(senderid).child(receivedid)
                                        .removeValue().addOnCompleteListener { task1 ->

                                            if (task1.isSuccessful)
                                            {
                                                chatref.child(receivedid).child(senderid)
                                                    .removeValue().addOnCompleteListener { task2 ->

                                                        if (task2.isSuccessful)
                                                        {
                                                            send_message_request_button.isEnabled=true
                                                            currState="friends"
                                                            send_message_request_button.text="Remove This Friend"
                                                            decline_message_request_button.visibility=View.INVISIBLE
                                                            decline_message_request_button.isEnabled=false
                                                        }
                                                    }
                                            }
                                        }
                            }
                        }
                }
            }

    }
}
