package com.example.whatsappclone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val messageReceiverID=intent.extras!!.get("visit_user_id").toString()
        val messageReceiverName=intent.extras!!.get("visit_user_name").toString()
        val messageReceiverImage=intent.extras!!.get("visit_image").toString()
      //  Toast.makeText(this,messageReceiverID,Toast.LENGTH_SHORT).show()
      //  Toast.makeText(this,messageReceiverName,Toast.LENGTH_SHORT).show()
        setSupportActionBar(findViewById(R.id.chat_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)




    }
}

