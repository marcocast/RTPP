package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;


public class SignupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btnSingIn = (Button) findViewById(R.id.btnSingIn);
        final EditText email = (EditText) findViewById(R.id.etEmail);
        final EditText username = (EditText) findViewById(R.id.etUserName);
        final EditText password = (EditText) findViewById(R.id.etPass);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final Intent joinstartIntenet = new Intent(this, JoinStartActivity.class);


        btnSingIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Firebase ref = new Firebase("https://rtpp.firebaseio.com");
                ref.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(final Map<String, Object> result) {

                        ref.child("users").child(result.get("uid").toString()).child("username").setValue(username.getText().toString(), new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(SignupActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Successfully created user account with uid: " + result.get("uid"), Toast.LENGTH_LONG).show();
                                    ref.authWithPassword(email.getText().toString(), password.getText().toString(),
                                            new Firebase.AuthResultHandler() {
                                                @Override
                                                public void onAuthenticated(AuthData authData) {
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putString("username", username.getText().toString());
                                                    editor.commit();
                                                    startActivity(joinstartIntenet);
                                                }

                                                @Override
                                                public void onAuthenticationError(FirebaseError firebaseError) {
                                                    Toast.makeText(SignupActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        });


                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(SignupActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final Firebase ref = new Firebase("https://rtpp.firebaseio.com");

            Button logoutButton = (Button) findViewById(R.id.action_logout);

            final Intent intentSigninIntent = new Intent(this, SigninActivity.class);

            final AuthData authData = ref.getAuth();


            if (authData != null) {
                ref.unauth();
                startActivity(intentSigninIntent);
            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
