package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.ExifUtils;
import com.rtpp.rtpp.utility.RtppUtility;

import java.util.Map;


public class SignupActivity extends ActionBarActivity {

    private ImageView imageview;
    private Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (firebaseFacade.isLogged()) {
            startActivity(new Intent(this, JoinStartActivity.class));
        }

        Button btnAddPitcure = (Button) findViewById(R.id.picture_button);
        Button btnSingIn = (Button) findViewById(R.id.btnSingIn);
        final EditText email = (EditText) findViewById(R.id.etEmail);
        final EditText username = (EditText) findViewById(R.id.etUserName);
        final EditText password = (EditText) findViewById(R.id.etPass);

        imageview = (ImageView) findViewById(R.id.imageView1);

        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final Intent joinstartIntenet = new Intent(this, JoinStartActivity.class);

        btnAddPitcure.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
            }
        });


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
                            if (imageBitmap != null) {
                                ref.child("users").child(result.get("uid").toString()).child("photo").setValue(ExifUtils.getImageBytes(imageBitmap), new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError != null) {
                                            Toast.makeText(SignupActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            String path = "";
            try {
                path = getPath(imageReturnedIntent.getData());
                imageBitmap = ExifUtils.decodeFile(path);
                imageview.setImageBitmap(imageBitmap);
            }catch(Exception e){
                Toast.makeText(SignupActivity.this, "Could not load your image : "+path+" Please load some other image", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


}
