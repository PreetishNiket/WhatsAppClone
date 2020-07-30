package com.example.whatsappclone

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.HorizontalScrollView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_group_chat.*
import java.util.*
import java.util.HashMap

class GroupChatActivity : AppCompatActivity() {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val db by lazy {
        FirebaseDatabase.getInstance()
            .reference
    }
    var id:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        my_scroll_view.fullScroll(HorizontalScrollView.FOCUS_DOWN)
        setSupportActionBar(findViewById(R.id.group__chat_bar_layout))
        supportActionBar?.title = "GroupName"

        val currGroupName = intent.getStringExtra("groupName").toString()
        Toast.makeText(this,currGroupName,Toast.LENGTH_SHORT).show()

        id=auth.currentUser?.uid
        getUserInfo()
        send_message_button.setOnClickListener {
           // SendMessageToDb()
        }
    }
    private fun getUserInfo() {
        db.child("Users")
            .child(id!!)
            .addValueEventListener(object:ValueEventListener{
                override fun onCancelled(databaseError: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists())
                    {
                        var username=dataSnapshot.child("name").value.toString()

                    }
                }

            })
    }
    private fun SendMessageToDb() {

        val key=db.child("Groups").push().key

        val message=input_group_message.text.toString()
        if (message.isNullOrEmpty())
        {
            //Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
        }
        else{
            val calendar=Calendar.getInstance()
            val sd=SimpleDateFormat("MMM-dd-yyyy")
            var cd=sd.format(calendar.time)

            var clock=Calendar.getInstance()
            var st=SimpleDateFormat("hh:mm a")
            var ct=st.format(clock.time)

            var map=HashMap<String, Object>()
            db.child("groupName")
                .updateChildren(map.toMap())
             var keyrf =db.child("groupName").child(key!!)
        }
    }
}
