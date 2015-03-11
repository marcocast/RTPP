package com.rtpp.rtpp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class JoinStartActivity extends ActionBarActivity {

    public final static String EXTRA_SESSION_NAME = "com.rtpp.rtpp.EXTRA_SESSION_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_start);
        final Firebase ref = new Firebase("https://rtpp.firebaseio.com");

        Button joinButton = (Button) findViewById(R.id.btnJoin);
        Button createButton = (Button) findViewById(R.id.btnCreate);

        final Intent joinIntent = new Intent(this, JoinActivity.class);
        final Intent estimateIntent = new Intent(this, EstimationActivity.class);
        Resources res = getResources();
        final String[] sessionsNames = res.getStringArray(R.array.sessions_names);

        final AuthData authData = ref.getAuth();

        joinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(joinIntent);
            }

        });

        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                int randomIndexName = new Random().nextInt(sessionsNames.length);
                int randomIndex = new Random().nextInt(1000);
                final String sessionName = sessionsNames[randomIndexName] + randomIndex;
                final Map<String, String> post1 = new HashMap<String, String>();
                post1.put("card", "none");

                ref.child("user-session").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.child(authData.getUid()).getValue()!=null){
                            Map<String,Object> newPost=(Map<String,Object>)snapshot.child(authData.getUid()).getValue();
                            estimateIntent.putExtra(EXTRA_SESSION_NAME, newPost.keySet().iterator().next());
                            startActivity(estimateIntent);
                        }else{
                            ref.child("session-user").child(sessionName).setValue(authData.getUid());
                            ref.child("user-session").child(authData.getUid()).child(sessionName).child("participants").child(authData.getUid()).setValue(post1, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        Toast.makeText(JoinStartActivity.this, "\"Session could not be created.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    } else {
                                        estimateIntent.putExtra(EXTRA_SESSION_NAME, sessionName);
                                        startActivity(estimateIntent);
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

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_start, menu);
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
