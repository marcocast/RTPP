package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.rtpp.rtpp.firebase.FirebaseFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class JoinStartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_start);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }


        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        Button joinButton = (Button) findViewById(R.id.btnJoin);
        Button createButton = (Button) findViewById(R.id.btnCreate);

        final Intent joinIntent = new Intent(this, JoinActivity.class);
        final Intent createIntent = new Intent(this, CreateActivity.class);

        joinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(joinIntent);
            }

        });

        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(createIntent);

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
