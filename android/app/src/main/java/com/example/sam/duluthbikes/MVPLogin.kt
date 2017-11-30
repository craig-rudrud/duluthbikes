package com.example.sam.duluthbikes

import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by pfan on 11/29/17.
 */
interface MVPLogin {

    interface loginView {

    }

    interface loginModel {
        fun setGoogleApiClient(client : GoogleApiClient)
    }

    interface loginPresenter {

    }

}