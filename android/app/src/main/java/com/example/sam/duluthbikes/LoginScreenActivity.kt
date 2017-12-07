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
import com.twitter.sdk.android.core.*

import kotlinx.android.synthetic.main.activity_login_screen.skip_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.google_sign_in_button
import kotlinx.android.synthetic.main.activity_login_screen.sign_out_button
import kotlinx.android.synthetic.main.activity_login_screen.facebook_login_button
import kotlinx.android.synthetic.main.activity_login_screen.login_button_twitter

/**
 * @Created by Pin Fan
 *
 * Implementations of Google Login through Google API Client, Facebook and Twitter Login through
 * their respective SDKs.
 *
 * Extends AppCompatActivity, GoogleApiClient.OnClickFailedListener, View.OnClickListener
 *
 * @TODO 1. Refinement of the Twitter API implementation
 * @TODO 2. Migration of mGoogleApiClient, mCallBackManager to a MVP model to facilitate cleaner and easier readable code
 *
 * For future inquiries: fanxx370@d.umn.edu
 *
 */


class LoginScreenActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    lateinit private var mGoogleApiClient : GoogleApiClient

    lateinit private var mCallbackManager : CallbackManager

    private var REQ_CODE = 5566
    private var signInID = 2

    /**
     * @Created by Pin Fan
     *
     * onCreate method which initializes Facebook and Twitter APIs, and builds the API client for Google Login.
     *
     * @param savedInstanceState of type Bundle (null-safe)
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        Twitter.initialize(this)
        setContentView(R.layout.activity_login_screen)

        val mGSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGSO)
                .build()

        initializeButtons()
        facebookSignIn()
        twitterSignIn()

    }

    /**
     * @Created by Pin Fan
     *
     * Function below sets the onClickListeners of these buttons to the specified context - the Activity.
     *
     * @param none
     */

    private fun initializeButtons() {
        skip_sign_in_button.setOnClickListener(this)
        google_sign_in_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        facebook_login_button.setOnClickListener(this)
    }

    /**
     * @Created by Pin Fan
     *
     * Function below facilitates the twitter login
     *
     * @param none
     */

    private fun twitterSignIn() {
        signInID = 3
        login_button_twitter.callback = object : Callback<TwitterSession>() {

            /**
             * Twitter API asks the developer to override success and failure functions,
             * called when either the sign-in is successful or fails
             */

            override fun success(result: Result<TwitterSession>) {
                var intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)
            }

            override fun failure(exception: TwitterException) {
                val alert : AlertDialog = AlertDialog.Builder(this@LoginScreenActivity).create()
                alert.setTitle("Twitter Login Failed")
                alert.setMessage("Twitter Login Failed - Please try again.")
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                alert.show()
            }
        }

    }

    /**
     * @Created by Pin Fan
     *
     * Function below facilitates the facebook login
     *
     * @param none
     */

    private fun facebookSignIn() {

        /**
         * Facebook Login requires three overrides, one when successful, one when cancelled,
         * and one when an error occurs.
         */

        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                var intent = Intent(baseContext, MenuActivity::class.java)
                startActivity(intent)

            }

            override fun onCancel() {

                val alertDialog : AlertDialog = AlertDialog.Builder(this@LoginScreenActivity).create()
                alertDialog.setTitle("Facebook Login Cancelled")
                alertDialog.setMessage("Facebook Login Failed - please try again.")
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

    /**
     * @Created by Pin Fan
     *
     * Function below facilitates the google login, by creating a new intent that is then passed
     * to the startActivityForResult fun.
     *
     * @param none
     */

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, REQ_CODE)
        signInID = 1
    }

    /**
     * @Created by Pin Fan
     *
     * Function below is called when the user signs out of Google
     *
     * @param none
     */

    private fun signOut() {
        if (mGoogleApiClient.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                updateUI(false)
                mGoogleApiClient.disconnect()
                mGoogleApiClient.connect()
            }
        }
    }

    /**
     * @Created by Pin Fan
     *
     * Function below hides or re-reveals a button.
     *
     * @param signedIn of type Boolean
     */

    private fun updateUI(signedIn : Boolean) {
        if (signedIn) {
            google_sign_in_button.visibility = View.GONE
            sign_out_button.visibility = View.VISIBLE
        } else {
            google_sign_in_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.GONE
        }
    }

    /**
     * @Created by Pin Fan
     *
     * The overridden onClick function, which features a when (switch in java) conditional that
     * determines actions on button press.
     *
     * @param v of type View, which features the current view
     */

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

    /**
     * @Created by Pin Fan
     *
     * Overridden function below facililates actions when login is successful, dependent on which API
     * utilized.
     *
     * @param requestCode of type Int, resultCode of type Int, and data of type Intent (null-safe)
     */

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

            3 -> login_button_twitter.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * @Created by Pin Fan
     *
     * Function below handles the actions taken by Google API when login is successful;
     * also passes user info to MenuActivity for the navigation menu.
     *
     * @param result of type GoogleSignInResult
     */

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

//You're welcome for the documentation. I knew how it felt looking at this project the first time.