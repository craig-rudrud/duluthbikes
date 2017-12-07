package com.example.sam.duluthbikes

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import kotlinx.android.synthetic.main.activity_login_screen.skip_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.google_sign_in_button

import kotlinx.android.synthetic.main.activity_login_screen.sign_out_button
import kotlinx.android.synthetic.main.activity_login_screen.facebook_login_button


class LoginScreenActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    lateinit private var mGoogleApiClient : GoogleApiClient

    lateinit private var mCallbackManager : CallbackManager

    private var REQ_CODE = 5566
    private var signInID = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login_screen)

        val mGSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGSO)
                .build()

        skip_sign_in_button.setOnClickListener(this)

        google_sign_in_button.setOnClickListener(this)

        sign_out_button.setOnClickListener(this)

        facebook_login_button.setOnClickListener(this)

        facebookSignIn()

    }

    private fun facebookSignIn() {

        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                var intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)

            }

            override fun onCancel() {

                val alertDialog : AlertDialog = AlertDialog.Builder(this@LoginScreenActivity).create()
                alertDialog.setTitle("Facebook Login Cancelled")
                alertDialog.setMessage("Facebook Login Failed - please try again!")
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                alertDialog.show()
            }

            override fun onError(error: FacebookException) {
                throw error("Error occurred with login!")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, REQ_CODE)
        signInID = 1
    }

    private fun signOut() {
        if (mGoogleApiClient.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                updateUI(false)
                mGoogleApiClient.disconnect()
                mGoogleApiClient.connect()
            }
        }
    }

    private fun updateUI(signedIn : Boolean) {
        if (signedIn) {
            google_sign_in_button.visibility = View.GONE
            sign_out_button.visibility = View.VISIBLE
        } else {
            google_sign_in_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.skip_sign_in_button -> {
                val intent = Intent(applicationContext, MenuActivity::class.java)
                startActivity(intent)
            }

            R.id.google_sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
        }
    }

    override fun onPause() {

        super.onPause()
        mGoogleApiClient.disconnect()

    }

    override fun onStart() {

        mGoogleApiClient.connect()
        super.onStart()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (signInID) {
            1 -> {
                    super.onActivityResult(requestCode, resultCode, data)

                    if (requestCode == REQ_CODE) {
                    val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                    handleResult(result)
                }
            }

            2 -> mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleResult(result : GoogleSignInResult) {
        if (result.isSuccess) {

            val acct : GoogleSignInAccount? = result.signInAccount
            val mFullName = acct?.displayName
            val mEmail = acct?.email!!
            val personPhoto = acct.photoUrl

            val intent = Intent(this, MenuActivity::class.java)

            intent.putExtra("name", mFullName)
            intent.putExtra("email", mEmail)

            intent.data = personPhoto

            startActivity(intent)
            updateUI(true)
        } else updateUI(false)
    }
}
