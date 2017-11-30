package com.example.sam.duluthbikes

import android.Manifest
import android.app.LoaderManager
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import kotlinx.android.synthetic.main.activity_login_screen.skip_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.google_sign_in_button
//import kotlinx.android.synthetic.main.activity_login_screen

class LoginScreenActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, ModelViewPresenterComponents.View, MVPLogin.loginView {

    private var mPresenter : Presenter? = null
    private var mGoogleApiClient : GoogleApiClient? = null

    private var REQ_CODE = 5566

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        var mGSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, mGSO).build()

        mPresenter = Presenter(this.baseContext, this, this)

        skip_sign_in_button.setOnClickListener {
            val intent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(intent)
        }

        google_sign_in_button.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(intent, REQ_CODE)
        }

    }

    override fun getClient(): GoogleApiClient? {
        return mGoogleApiClient
    }

    override fun locationChanged(location: Location?) {

    }

    override fun setClient(googleApiClient: GoogleApiClient?) {

    }

    override fun userResults(results: String?) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

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
            val intent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(intent)
        }
    }

}
