package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.whatsappclone.adapter.ViewPagerAdapter
import com.example.whatsappclone.Fragments.ChatFragment
import com.example.whatsappclone.Fragments.ContactsFragment
import com.example.whatsappclone.Fragments.GroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_page_toolbar))
        supportActionBar?.title = "WhatsAppClone"
        val adapter= ViewPagerAdapter(supportFragmentManager)
        adapter.add(ChatFragment(),"Chats")
        adapter.add(GroupsFragment(),"Groups")
        adapter.add(ContactsFragment(),"Contacts")
        main_tabs_pager.adapter=adapter
        main_tabs.setupWithViewPager(main_tabs_pager)
    }
    override fun onStart() {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else{
                verifyUserExistence()
            }
        }
        super.onStart()
    }
    private fun verifyUserExistence() {
       // var id:String?=""

        var id=auth.currentUser?.uid

            db.child("Users")
            .child(id!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onCancelled(databaseError: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if ((dataSnapshot.child("name").exists()))
                    {
                        Toast.makeText(baseContext, "Welcome", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent=Intent(baseContext, SettingsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         super.onOptionsItemSelected(item)
        if (item.itemId==R.id.main_logout_options)
        {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            //Add IntentFlags,finish()
        }
        if(item.itemId==R.id.main_settings_options)
        {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
        if (item.itemId==R.id.main_find_friends_options)
        {
            startActivity(Intent(this,FindFriendsActivity::class.java))
        }
        if (item.itemId==R.id.main_create_options)
        {
            requestNewGroup()
        }
        return true
    }

    private fun requestNewGroup() {
       var  alertDialog=AlertDialog.Builder(this,R.style.AlertDialog)
        alertDialog.setTitle("Enter Group Name")

        val groupNameField=EditText(this)

        groupNameField.hint = "e.g FriendsZone"
        alertDialog.setView(groupNameField)
        alertDialog.setPositiveButton("Create") { dialog, which ->

            var groupName = groupNameField.text.toString()
            if (groupName.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter The Group Name", Toast.LENGTH_SHORT).show()
            } else {
                CreateNewGroup(groupName)
            }
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        alertDialog.show()
    }//

    private fun CreateNewGroup(groupName:String) {

        db.child("Groups").child(groupName)
            .setValue("")
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Toast.makeText(this, groupName+"Group is Created Successfully", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
