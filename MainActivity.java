/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive=true;
  TextView loginText;
  EditText usernameEditText;
  EditText passwordEditText;

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
      signUpClicked(view);
    }
    return false;
  }

  public void showUserList(){
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);
  }

  @Override
  public void onClick(View view) {
    if (view.getId()==R.id.loginTxt){
      Button signUpButton= (Button) findViewById(R.id.signUpBtn);
      if (signUpModeActive){
        signUpModeActive=false;
        signUpButton.setText("Login");
        loginText.setText("Sign Up");
      }else{
        signUpModeActive=true;
        signUpButton.setText("Sign Up");
        loginText.setText("Login");
      }
    }else if (view.getId()==R.id.logoImageview || view.getId()==R.id.backgroundLayout){
      InputMethodManager inputM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputM.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void signUpClicked(View view){
    if (usernameEditText.getText().toString().matches("")||passwordEditText.getText().toString().matches("")){
      Toast.makeText(this,"A username and a password are required.", Toast.LENGTH_SHORT).show();
    }else{
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Sign Ip", "Success");
              showUserList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }else{
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser parseUser, ParseException e) {
            if (parseUser!=null){
              Log.i("Login", "Ok");
              showUserList();
            }else{
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    loginText = (TextView) findViewById(R.id.loginTxt);
    loginText.setOnClickListener(this);
    usernameEditText= (EditText) findViewById(R.id.usernameEditText);
    passwordEditText= (EditText) findViewById(R.id.passwordEditText);
    passwordEditText.setOnKeyListener(this);
    ImageView logoView = (ImageView) findViewById(R.id.logoImageview);
    RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
    logoView.setOnClickListener(this);
    backgroundLayout.setOnClickListener(this);

    if (ParseUser.getCurrentUser()!=null){
      showUserList();
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}
