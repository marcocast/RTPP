package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class CreateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }


        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);


        final Intent estimateIntent = new Intent(this, EstimationActivity.class);

        Button createButton = (Button) findViewById(R.id.btnCreate);

        final EditText sessionNameText = (EditText) findViewById(R.id.sessionName);



        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                final String sessionName = sessionNameText.getText().toString();


                final Map<String, String> userName = new HashMap<String, String>();
                userName.put("username", sharedPref.getString("username", ""));

                createSession(firebaseFacade,sessionName, userName, sharedPref.edit(), estimateIntent);


            }

        });
    }

    private void createSession(final FirebaseFacade firebaseFacade, final String sessionName, final Map<String, String> userName, final SharedPreferences.Editor editor, final Intent estimateIntent) {
        firebaseFacade.getRef().child("session-owner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(sessionName).getValue() != null){
                    editor.putString("sessionOwner", firebaseFacade.getUid());
                    editor.putString("sessionName", sessionName);
                    editor.commit();
                    startActivity(estimateIntent);
                }else{
                    firebaseFacade.getRef().child("session-owner").child(sessionName).setValue(firebaseFacade.getUid(), new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(CreateActivity.this, "Session could not be created.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                firebaseFacade.getRef().child("session-participants").child(sessionName).child(firebaseFacade.getUid()).setValue(userName, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError != null) {
                                            Toast.makeText(CreateActivity.this, "Session could not be created.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            final Map<String, String> cardPost = new HashMap<String, String>();
                                            cardPost.put("card", "none");
                                            firebaseFacade.getRef().child("session-votes").child(sessionName).child(firebaseFacade.getUid()).setValue(cardPost, new Firebase.CompletionListener() {
                                                @Override
                                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                    if (firebaseError != null) {
                                                        Toast.makeText(CreateActivity.this, "Session could not be created.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        editor.putString("sessionOwner", firebaseFacade.getUid());
                                                        editor.putString("sessionName", sessionName);
                                                        editor.commit();
                                                        startActivity(estimateIntent);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });


                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
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

            final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
            if (firebaseFacade.isLogged()) {
                firebaseFacade.logout();
                startActivity(new Intent(this, JoinStartActivity.class));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
