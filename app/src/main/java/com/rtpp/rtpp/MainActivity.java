package com.rtpp.rtpp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.rtpp.rtpp.firebase.FirebaseFacade;

//http://hmkcode.com/android-designing-a-login-screen-sign-in-sign-up-screens/
public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button signup = (Button) findViewById(R.id.btnSignUp);
        Button signin = (Button) findViewById(R.id.btnSingIn);

        final Intent intentSignupIntent = new Intent(this, SignupActivity.class);

        final Intent intentSigninIntent = new Intent(this, SigninActivity.class);

        FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (firebaseFacade.isLogged()) {
            startActivity(new Intent(this, JoinStartActivity.class));
        }




        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(intentSignupIntent);
            }

        });

        signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(intentSigninIntent);
            }

        });
    }

}
