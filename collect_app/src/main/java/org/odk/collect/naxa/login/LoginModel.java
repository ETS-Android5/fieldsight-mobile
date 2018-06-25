package org.odk.collect.naxa.login;

import org.odk.collect.naxa.login.model.MeResponse;

public interface LoginModel {

    interface OnLoginFinishedListener {
        void onError();

        void onPasswordError();

        void onSuccess();
    }


    interface OnFetchUserInfoListener {

        void onError();

        void onSucess(MeResponse meResponse);
    }

    void login(String username, String password, OnLoginFinishedListener listener);

    void fetchUserInformation(OnFetchUserInfoListener listener);
}
