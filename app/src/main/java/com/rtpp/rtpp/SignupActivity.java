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
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.RtppUtility;

import java.util.Map;


public class SignupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (firebaseFacade.isLogged()) {
            startActivity(new Intent(this, JoinStartActivity.class));
        }

        Button btnSingIn = (Button) findViewById(R.id.btnSingIn);
        final EditText email = (EditText) findViewById(R.id.etEmail);
        final EditText username = (EditText) findViewById(R.id.etUserName);
        final EditText password = (EditText) findViewById(R.id.etPass);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final Intent joinstartIntenet = new Intent(this, JoinStartActivity.class);


        btnSingIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                signUp(firebaseFacade.getRef(), RtppUtility.getTextContent(email), RtppUtility.getTextContent(password), RtppUtility.getTextContent(username), sharedPref.edit(), joinstartIntenet);


            }

        });
    }

    private void signUp(final Firebase ref, final String email, final String password, final String username, final SharedPreferences.Editor editor, final Intent joinstartIntenet) {
        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(final Map<String, Object> result) {

                ref.child("users").child(result.get("uid").toString()).child("username").setValue(username, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(SignupActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Successfully created user account with uid: " + result.get("uid"), Toast.LENGTH_LONG).show();
                            ref.authWithPassword(email, password,
                                    new Firebase.AuthResultHandler() {
                                        @Override
                                        public void onAuthenticated(AuthData authData) {
                                            editor.putString("username", username);
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


}
