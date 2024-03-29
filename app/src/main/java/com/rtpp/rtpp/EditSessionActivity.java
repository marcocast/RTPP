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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.TextUtility;

import java.util.HashMap;
import java.util.Map;


public class EditSessionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);
        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }


        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final String sessionName = sharedPref.getString("sessionName", "");


        final Intent estimateIntent = new Intent(this, EstimationActivity.class);

        Button createButton = (Button) findViewById(R.id.btnCreate);

        final EditText sessionNameText = (EditText) findViewById(R.id.sessionName);

        sessionNameText.setText(sessionName);

        final RadioGroup radioCardsTypeGroup = (RadioGroup) findViewById(R.id.radioCardsType);

        firebaseFacade.getRef().child("session-type").child(sessionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                String type = snapshot.child("cardType").getValue().toString();
                if(type.equals("Standard")){
                    RadioButton radio = (RadioButton)findViewById(R.id.radioStandard);
                    radio.setChecked(true);
                }else if(type.equals("Fibonacci")){
                    RadioButton radio = (RadioButton)findViewById(R.id.radioFibonacci);
                    radio.setChecked(true);
                }else if(type.equals("T-Shirt")){
                    RadioButton radio = (RadioButton)findViewById(R.id.radioTshirt);
                    radio.setChecked(true);
                }
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedId = radioCardsTypeGroup.getCheckedRadioButtonId();
                RadioButton radioCardsTypeButton = (RadioButton) findViewById(selectedId);
                createSession(firebaseFacade, TextUtility.getSessionTextContent(sessionNameText), sharedPref, estimateIntent, radioCardsTypeButton.getText().toString());
            }

        });
    }

    private void createSession(final FirebaseFacade firebaseFacade, final String sessionName, final SharedPreferences sharedPref, final Intent estimateIntent, final String cardsType) {

        final Map<String, String> cardType = new HashMap<String, String>();
        cardType.put("cardType", cardsType);
        firebaseFacade.getRef().child("session-type").child(sessionName).setValue(cardType, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(EditSessionActivity.this, "Session could not be created.  " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cardType", cardsType);
                    editor.commit();

                    startActivity(estimateIntent);


                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_session, menu);
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
