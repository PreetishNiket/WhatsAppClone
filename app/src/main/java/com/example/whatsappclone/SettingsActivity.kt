package com.example.whatsappclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    var id:String?=""
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
            .reference
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
            .reference
    }
    private val userProfileImage by lazy {
        FirebaseStorage.getInstance()
            .reference.child("Profile Images")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        update_settings.setOnClickListener {
            UpdateSettings()
        }
        id=auth.currentUser?.uid

        if (set_user_name.visibility == View.VISIBLE) {
            set_user_name.visibility = View.INVISIBLE
        }else {
            set_user_name.visibility = View.VISIBLE
        }
        RetrieveUserInfo()

        set_profile_image.setOnClickListener {
           pickImageFromGallery()
        }
    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            val imageUri=data.data

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result=CropImage.getActivityResult(data)

            if (requestCode==RESULT_OK){
                val resultUri=result.uri
                id=auth.currentUser?.uid

              val filePath=userProfileImage.child("$id.jpeg")
                filePath.putFile(resultUri).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        Toast.makeText(this,"Profile Image Uploaded Successfully",Toast.LENGTH_SHORT).show()

                        val downloadUrl=filePath.downloadUrl.toString()
                        db.child("Users").child(id!!)
                            .setValue(downloadUrl)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful)
                                {
                                    Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else{
                        val message =it.exception.toString()
                        Toast.makeText(this, "Error:$message",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }


    }

    private fun UpdateSettings() {
        set_user_name.text.toString()
        set_profile_status.text.toString()
        if (set_user_name.text.toString().isNullOrEmpty())
        {
            Toast.makeText(this, "Please Set Username", Toast.LENGTH_SHORT).show()
        }
        if (set_profile_status.text.toString().isNullOrEmpty())
        {
            Toast.makeText(this, "Please Set Status", Toast.LENGTH_SHORT).show()
        }
        else{
                var profileMap=HashMap<String,String>()
                profileMap.put("uid",id!!)
                profileMap.put("name",set_user_name.text.toString())
                profileMap.put("status",set_profile_status.text.toString())

            id=auth.currentUser?.uid
                    db.child("Users").child(id!!)
                    .setValue(profileMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                        {
                            val intent= Intent(this,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var error =it.exception.toString()
                            Toast.makeText(this, "Error :${error}",Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
    private fun RetrieveUserInfo() {
        id=auth.currentUser?.uid
        db.child("Users").child(id!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("image")))
                    {
                        val un=dataSnapshot.child("name").value.toString()
                        val st=dataSnapshot.child("status").value.toString()
                        val pi=dataSnapshot.child("image").value.toString()

                        set_user_name.setText(un)
                        set_profile_status.setText(st)
                        Picasso.get().load(pi).into(set_profile_image)

                    }
                    else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")))
                    {
                        val un=dataSnapshot.child("name").value.toString()
                        val st=dataSnapshot.child("status").value.toString()
                        set_user_name.setText(un)
                        set_profile_status.setText(st)
                    }
                    else{
                        if (set_user_name.visibility == View.VISIBLE) {
                            set_user_name.visibility = View.INVISIBLE
                        }else {
                            set_user_name.visibility = View.VISIBLE
                        }
                        Toast.makeText(baseContext, "Please Set & Update Your Profile Information",Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }
}
