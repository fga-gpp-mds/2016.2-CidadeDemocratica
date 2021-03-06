package com.mdsgpp.cidadedemocratica.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mdsgpp.cidadedemocratica.R;
import com.mdsgpp.cidadedemocratica.requester.AuthenticateRequestResponseHandler;
import com.mdsgpp.cidadedemocratica.requester.RequestResponseHandler;
import com.mdsgpp.cidadedemocratica.requester.RequestUpdateListener;
import com.mdsgpp.cidadedemocratica.requester.Requester;

public class MainActivity extends AppCompatActivity implements RequestUpdateListener {

    private ProgressDialog progressDialog;
    private AuthenticateRequestResponseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        String token = getToken();

        if (token != null) {
            Requester.setUserToken(token);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            progressDialog = FeedbackManager.createProgressDialog(this, getString(R.string.message_authenticate));
            requestUserToken();
        }

    }

    public  void showProposalList(View view){
        Intent proposalIntent = new Intent(this, ProposalsList.class);
        startActivity(proposalIntent);
    }

    public void showTagsList(View view){
        Intent tagsIntent = new Intent(this,TagsList.class);
        startActivity(tagsIntent);
    }
    public void showUsersList(View view){
        Intent usersIntent = new Intent(this,UsersList.class);
        startActivity(usersIntent);
    }

    private String getToken() {
        SharedPreferences preferences = this.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        String token = preferences.getString("userToken", "");
        if(token.equals("")){
            token = null;
        }
        return token;
    }

    private void requestUserToken() {
        handler = new AuthenticateRequestResponseHandler();
        handler.setRequestUpdateListener(this);
        Requester requester = new Requester(AuthenticateRequestResponseHandler.authenticateEndpointUrl, handler);

        requester.async(Requester.RequestMethod.GET);
    }

    @Override
    public void afterSuccess(RequestResponseHandler handler, Object response) {

        final MainActivity self = this;
        if (String.class.isInstance(response)) {
            SharedPreferences preferences = this.getSharedPreferences(getString(R.string.name_preferences_data), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(getString(R.string.key_user_token), (String) response);
            editor.apply();

            Requester.setUserToken((String) response);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FeedbackManager.createToast(self, getString(R.string.message_authenticate_success));

                    if (ActivityCompat.checkSelfPermission(self, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(self, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(self,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FeedbackManager.createToast(self,getString(R.string.message_authenticate_fail));
                }
            });

        }
        progressDialog.dismiss();
    }

    @Override
    public void afterError(RequestResponseHandler handler, String message) {

    }
}
