package com.example.sam.duluthbikes

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by pfan on 11/29/17.
 */

class LoginModel() : MVPLogin.loginModel {

    lateinit private var mPresenter : MVPLogin.loginPresenter


    lateinit var mGoogleSignInClient : GoogleSignInClient

    constructor(googleSignInClient: GoogleSignInClient, presenter: LoginPresenter) : this() {
        mPresenter = presenter
        mGoogleSignInClient = googleSignInClient

    }

    override fun setGoogleSignInClient(client: GoogleSignInClient) {
        mGoogleSignInClient = client
    }

    override fun getGoogleSignInClient(): GoogleSignInClient {
        return mGoogleSignInClient
    }

}