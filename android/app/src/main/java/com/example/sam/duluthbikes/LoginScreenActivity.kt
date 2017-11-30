package com.example.sam.duluthbikes

import android.Manifest
import android.app.AlertDialog
import android.app.LoaderManager
import android.content.DialogInterface
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_login_screen.skip_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.google_sign_in_button

import kotlinx.android.synthetic.main.activity_login_screen.sign_out_button

class LoginScreenActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, MVPLogin.loginView, View.OnClickListener {

    lateinit private var mPresenter : LoginPresenter
    lateinit private var mGoogleApiClient : GoogleApiClient

    private var REQ_CODE = 5566

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        var mGSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, mGSO).build()
        //var mGoogleSignInClient = GoogleSignIn.getClient(this, mGSO);

        skip_sign_in_button.setOnClickListener(this)

        google_sign_in_button.setOnClickListener(this)

        sign_out_button.setOnClickListener(this)

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
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            updateUI(false)
        }
    }

    private fun updateUI(signedIn : Boolean) {
        if (signedIn) {
            google_sign_in_button.visibility = View.GONE
            google_sign_in_button.visibility = View.VISIBLE
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

    override fun onStart() {
        super.onStart()

        val opr: OptionalPendingResult<GoogleSignInResult> = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)

        if (opr.isDone) {
            val result = opr.get()
            handleResult(result)
        } else opr.setResultCallback { googleSignInResult -> handleResult(googleSignInResult) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE) {
            var result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleResult(result)
        }
    }

    private fun handleResult(result : GoogleSignInResult) {
        if (result.isSuccess) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            updateUI(true)
        } else updateUI(false)
    }
}
