package com.example.sam.duluthbikes

import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by pfan on 11/29/17.
 */

class LoginModel : MVPLogin.loginModel {

    var mGoogleApiClient : GoogleApiClient? = null

    override fun setGoogleApiClient(client: GoogleApiClient) {
        mGoogleApiClient = client
    }

}