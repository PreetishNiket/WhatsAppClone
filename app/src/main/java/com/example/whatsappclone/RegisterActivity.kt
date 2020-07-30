package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    var id:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        already_have_account_link.setOnClickListener {
            val i =Intent(this,LoginActivity::class.java)
            startActivity(i)
        }
        register_button.setOnClickListener {

            if (register_email.text.isNullOrEmpty())
            {
                Toast.makeText(this,"Please Enter The Email",Toast.LENGTH_SHORT).show()
            }
            else if (register_pass.text.isNullOrEmpty()){
                Toast.makeText(this,"Please Enter The Password",Toast.LENGTH_SHORT).show()
            }
            else if (!register_email.text.isNullOrEmpty() && !register_pass.text.isNullOrEmpty())
            {
                if (register_pass.text.length > 6) {

                    signIn(register_email.text.toString(), register_pass.text.toString())
                }
                else
                {
                    register_pass.apply {
                        error = "Password length should be greater than 6"
                    }

                }
            }

        }

    }

    private fun signIn(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                     id=auth.currentUser?.uid

                    FirebaseDatabase.getInstance()
                        .reference
                        .child("Users").child(id!!)
                        .setValue("")



                    val intent=Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_LONG).show()

                }
                else{

                }

            }.addOnSuccessListener {

            }.addOnFailureListener {
                if (it.localizedMessage.contains("already")) {
                    Toast.makeText(this, "Account Already Exists", Toast.LENGTH_LONG).show()
                }
               // Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

}


