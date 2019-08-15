package org.fieldsight.naxa.login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.HttpException;

public class APIErrorUtils {
    public static String getNonFieldError(HttpException responseBody) {
        String errorMessage = "An unknown error occurred";

        try {
            String errorBody = responseBody.response().errorBody().string();
            JSONObject jsonObject = new JSONObject(errorBody);
            errorMessage = String.valueOf(jsonObject.getJSONArray("non_field_errors").get(0));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return errorMessage;
    }
}
