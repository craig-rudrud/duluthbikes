package com.example.sam.duluthbikes

import android.app.Activity
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by pfan on 11/29/17.
 */

class LoginPresenter() : MVPLogin.loginPresenter {

    lateinit private var mModel : MVPLogin.loginModel
    lateinit private var mView : MVPLogin.loginView
    lateinit private var mGoogleSignInClient : GoogleSignInClient

    constructor(view : MVPLogin.loginView) : this() {
        mView = view

    }

    constructor(view : MVPLogin.loginView, googleSignInClient : GoogleSignInClient) : this() {
        mView = view
        mGoogleSignInClient = googleSignInClient
        mModel = LoginModel(googleSignInClient, this)
    }

    override fun getGoogleSignInClient(): GoogleSignInClient {
        return mModel.getGoogleSignInClient()
    }

    override fun setGoogleSignInClient(client: GoogleSignInClient) {

    }

    override fun getView(): MVPLogin.loginView {
        return mView
    }

}