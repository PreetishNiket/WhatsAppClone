package com.example.whatsappclone

import android.content.Intent
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_login.*

const val TAG = "PHONEAUTH"

class PhoneLoginActivity : AppCompatActivity() {
   private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var storedVerificationId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        val callbacks= object :PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(this@PhoneLoginActivity,"Invalid PhoneNumber",Toast.LENGTH_SHORT).show()
                    if (verification_code_input.visibility ==View.VISIBLE &&ver_button.visibility==View.VISIBLE) {
                        verification_code_input.visibility = View.INVISIBLE
                        ver_button.visibility=View.INVISIBLE
                    }
                    if (phone_num_input.visibility==View.INVISIBLE&&send_ver_button.visibility==View.INVISIBLE){
                        phone_num_input.visibility=View.VISIBLE
                        send_ver_button.visibility=View.VISIBLE
                    }
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent:$verificationId")

                  storedVerificationId = verificationId
                var resendToken = token
                if (verification_code_input.visibility ==View.INVISIBLE &&ver_button.visibility==View.INVISIBLE) {
                    verification_code_input.visibility = View.VISIBLE
                    ver_button.visibility=View.VISIBLE
                }
                if (phone_num_input.visibility==View.VISIBLE&&send_ver_button.visibility==View.VISIBLE){
                    phone_num_input.visibility=View.INVISIBLE
                    send_ver_button.visibility=View.INVISIBLE
                }
            }

        }
        send_ver_button.setOnClickListener {
            var phonenumber="+91"+phone_num_input.text.toString()
            if (phonenumber.isNullOrEmpty())
            {
                Toast.makeText(this,"Please Enter The Correct Phone Number",Toast.LENGTH_SHORT).show()
            }
            else{
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks)
                //loading bar
            }
        }
        ver_button.setOnClickListener {

                phone_num_input.visibility=View.INVISIBLE
                send_ver_button.visibility=View.INVISIBLE

            val verificationCode=verification_code_input.text.toString()
            if (verificationCode.isNullOrEmpty())
            {
                Toast.makeText(this,"Please Enter The Code",Toast.LENGTH_SHORT).show()

            }
            else{
                //loading bar
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, verificationCode)
                signInWithPhoneAuthCredential(credential)
            }

        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    val user = task.result?.user
                    // ...
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this,"Verification Completed",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"signInWithCredential:failure",Toast.LENGTH_SHORT).show()

                    }
                }
            }
    }
}
