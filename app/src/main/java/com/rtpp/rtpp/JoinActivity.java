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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.RtppUtility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JoinActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final EditText sessionNameText = (EditText) findViewById(R.id.sessionName);
        final Intent estimateIntent = new Intent(this, EstimationActivity.class);


        final Map<String, String> timeOfJoin = new HashMap<String, String>();
        timeOfJoin.put("time_of_join", String.valueOf(new Date().getTime()));

        Button joinButton = (Button) findViewById(R.id.btnJoin);
        joinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                joinSession(firebaseFacade, RtppUtility.getTextContent(sessionNameText), timeOfJoin, sharedPref.edit(), estimateIntent);
            }

        });


    }

    private void joinSession(final FirebaseFacade firebaseFacade, final String sessionName, final Map<String, String> timeOfJoin, final SharedPreferences.Editor editor, final Intent estimateIntent) {
        firebaseFacade.getRef().child("session-type").child(sessionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    firebaseFacade.getRef().child("session-participants").child(sessionName).child(firebaseFacade.getUid()).setValue(timeOfJoin, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(JoinActivity.this, "Session could not be joined.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                final Map<String, String> cardPost = new HashMap<String, String>();
                                cardPost.put("card", "none");
                                firebaseFacade.getRef().child("session-votes").child(sessionName).child(firebaseFacade.getUid()).setValue(cardPost, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError != null) {
                                            Toast.makeText(JoinActivity.this, "Session could not be joined.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            editor.putString("sessionOwner", "");
                                            editor.putString("sessionName", sessionName);
                                            editor.putString("sessionType", snapshot.child("cardType").getValue().toString());
                                            editor.commit();
                                            startActivity(estimateIntent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(JoinActivity.this, "Session does not exist" , Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onCancelled(FirebaseError arg0) {
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join, menu);
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

        if (id == R.id.action_edit_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
