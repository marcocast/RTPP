package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class CardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Firebase.setAndroidContext(this);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);


        final Firebase ref = new Firebase("https://rtpp.firebaseio.com");

        final AuthData authData = ref.getAuth();

        final TextView textSessionName = (TextView)findViewById(R.id.session_name);

        final String sessionName = sharedPref.getString("sessionName", "");
        final String sessionOwner = sharedPref.getString("sessionOwner", "");

        textSessionName.setText(sessionName);

        ImageView img = (ImageView) findViewById(R.id.imageCard);

        final String cardIndex =  sharedPref.getString("card", "");

        img.setImageResource(R.drawable.card8);

        final Map<String, Object> post1 = new HashMap<String, Object>();
        post1.put("card", cardIndex);


        ref.child("user-session/" + sessionOwner + "/" + sessionName + "/participants/" + authData.getUid()).updateChildren(post1);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
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
