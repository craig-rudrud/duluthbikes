package com.example.sam.duluthbikes

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by pfan on 11/29/17.
 */
interface MVPLogin {

    interface loginView {

    }

    interface loginModel {
        fun setGoogleSignInClient(client : GoogleSignInClient)

        fun getGoogleSignInClient() : GoogleSignInClient
    }

    interface loginPresenter {
        fun setGoogleSignInClient(client : GoogleSignInClient)

        fun getGoogleSignInClient() : GoogleSignInClient

        fun getView() : MVPLogin.loginView
    }

}