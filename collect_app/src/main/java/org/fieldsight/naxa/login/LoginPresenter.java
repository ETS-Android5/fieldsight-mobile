package org.fieldsight.naxa.login;

public interface LoginPresenter{
    void validateCredentials(String username, String password);

    void googleOauthCredentials(String googleAccessToken, String username);
}
