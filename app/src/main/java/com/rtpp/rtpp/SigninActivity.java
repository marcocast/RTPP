package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.TextUtility;

import java.util.Map;


public class SigninActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (firebaseFacade.isLogged()) {
            startActivity(new Intent(this, JoinStartActivity.class));
        }

        final EditText email = (EditText) findViewById(R.id.etEmail);
        final EditText password = (EditText) findViewById(R.id.etPass);
        final Button btnSingIn = (Button) findViewById(R.id.btnSingIn);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);


        final Intent joinstartIntent = new Intent(this, JoinStartActivity.class);

        btnSingIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                signIn(firebaseFacade.getRef(), TextUtility.getLoginTextContent(email), TextUtility.getLoginTextContent(password), sharedPref.edit(), joinstartIntent);

            }

        });
    }

    private void signIn(final Firebase ref, String email, String password, final SharedPreferences.Editor editor, final Intent joinstartIntent) {
        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        ref.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Map<String, Object> myUser = (Map<String, Object>) snapshot.getValue();
                                    editor.putString("username", myUser.get("username").toString());
                                    editor.commit();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                        startActivity(joinstartIntent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(SigninActivity.this, "Error logging in user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


}
