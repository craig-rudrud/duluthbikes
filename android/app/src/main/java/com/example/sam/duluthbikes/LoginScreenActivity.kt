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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

import kotlinx.android.synthetic.main.activity_login_screen.skip_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.google_sign_in_button

class LoginScreenActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, ModelViewPresenterComponents.View {

    private var mPresenter : ModelViewPresenterComponents.PresenterContract? = null

    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE : Int = 1

    public var duplicateCode = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        mPresenter = Presenter(this.baseContext, this, this)

        var mGSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        var mGoogleSignInClient = GoogleSignIn.getClient(this, mGSO)

        skip_sign_in_button.setOnClickListener {
            val intent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(intent)
        }

        google_sign_in_button.setOnClickListener {
            TODO("not yet implemented")
        }

    }

    override fun getClient(): GoogleApiClient? {
        return null
    }

    override fun locationChanged(location: Location?) {
        TODO("no implementation planned")
    }

    override fun setClient(googleApiClient: GoogleApiClient?) {
        TODO("no implementation planned")
    }

    override fun userResults(results: String?) {
        if (results == "good") duplicateCode = 200
        else if (results == "bad") duplicateCode = 300
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        TODO("not yet implemented")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {

        TODO("idk what this does")

    }

    override fun onLoaderReset(p0: Loader<Cursor>?) {
        TODO("no implementation planned")
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this.baseContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            val text : CharSequence = "To create a user you must allow storage permissions to store user on phone."
            val toast : Toast = Toast.makeText(baseContext, text, Toast.LENGTH_SHORT)
            toast.show()

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)

        }
    }

}
