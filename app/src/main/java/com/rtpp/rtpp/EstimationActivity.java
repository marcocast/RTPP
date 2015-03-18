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
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.rtpp.rtpp.firebase.FirebaseFacade;


public class EstimationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimation);

        Firebase.setAndroidContext(this);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final Intent intent = getIntent();

        final Intent cardIntent = new Intent(this, CardActivity.class);

        final TextView textSessionName = (TextView)findViewById(R.id.session_name);

        textSessionName.setText(sharedPref.getString("sessionName", ""));

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(EstimationActivity.this, "" + position, Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("card", String.valueOf(position));
                editor.commit();

                startActivity(cardIntent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_estimation, menu);
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
