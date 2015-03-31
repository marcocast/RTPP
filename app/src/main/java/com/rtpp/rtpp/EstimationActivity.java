package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rtpp.rtpp.firebase.FirebaseFacade;

import java.util.HashMap;
import java.util.Map;


public class EstimationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimation);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);


        final String sessionName = sharedPref.getString("sessionName", "");
        final String sessionOwner = sharedPref.getString("sessionOwner", "");

        final Map<String, Object> post1 = new HashMap<String, Object>();
        post1.put("card", "none");

        firebaseFacade.getRef().child("session-votes").child(sessionName).child(firebaseFacade.getUid()).updateChildren(post1);


        final Intent estimationIntent = new Intent(this, EstimationActivity.class);

        final Intent joinStartIntent = new Intent(this, JoinStartActivity.class);

        final Intent cardIntent = new Intent(this, CardActivity.class);

        final Intent editSessionIntent = new Intent(this, EditSessionActivity.class);

        final TextView textSessionName = (TextView)findViewById(R.id.session_name);

        textSessionName.setText(sessionName);

        textSessionName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(sessionOwner != ""){
                    startActivity(editSessionIntent);
                }

            }

        });


        firebaseFacade.getRef().child("session-type").child(sessionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String cardsType = snapshot.child("cardType").getValue().toString();
                GridView gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(getApplicationContext(),cardsType));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("card", position);
                        editor.commit();

                        startActivity(cardIntent);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });



        firebaseFacade.getRef().child("session-type").child(sessionName).addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                startActivity(estimationIntent);
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                startActivity(joinStartIntent);

            }

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {}

            @Override
            public void onCancelled(FirebaseError error) {}

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_estimation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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
