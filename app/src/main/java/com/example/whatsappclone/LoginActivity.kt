package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.mbms.MbmsErrors
import android.widget.HorizontalScrollView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sv.fullScroll(HorizontalScrollView.FOCUS_DOWN)
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        need_new_account_link.setOnClickListener {
            val i =Intent(this,RegisterActivity::class.java)
            startActivity(i)
        }
        login_button.setOnClickListener {
            if (login_email.text.isNullOrEmpty())
            {
                Toast.makeText(this,"Please Enter the Email", Toast.LENGTH_SHORT).show()
            }
            else if (login_pass.text.isNullOrEmpty()){
                Toast.makeText(this,"Please Enter the Password", Toast.LENGTH_SHORT).show()
            }
            else if (!login_email.text.isNullOrEmpty() && !login_pass.text.isNullOrEmpty())
            {
                if (login_pass.text.length > 6) {
                        logIn(login_email.text.toString(),login_pass.text.toString())
                }
                else
                {
                    login_pass.apply {
                        error = "Password length should be greater than 6"
                    }

                }
            }
        }
        phone_login_button.setOnClickListener {
            startActivity(Intent(this,PhoneLoginActivity::class.java))
        }

    }
    private fun logIn(name: String, password: String) {
        auth.signInWithEmailAndPassword(name, password)
            .addOnCompleteListener {
                val intent=Intent(this,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                Toast.makeText(this, "LogIn Successful", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}
