package com.rtpp.rtpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rtpp.rtpp.firebase.FirebaseFacade;
import com.rtpp.rtpp.utility.ExifUtils;
import com.rtpp.rtpp.utility.TextUtility;


public class UserProfileActivity extends ActionBarActivity {

    private ImageView imageview;
    private Bitmap imageBitmap;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final FirebaseFacade firebaseFacade = new FirebaseFacade(this);
        if (!firebaseFacade.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        }

        uid = firebaseFacade.getUid();

        Button btnAddPitcure = (Button) findViewById(R.id.picture_button);
        Button btnSingIn = (Button) findViewById(R.id.btnSingIn);
        final EditText username = (EditText) findViewById(R.id.etUserName);

        imageview = (ImageView) findViewById(R.id.imageView1);

        firebaseFacade.getRef().child("users").child(firebaseFacade.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String usernameString = snapshot.child("username").getValue().toString();
                username.setText(usernameString);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        firebaseFacade.getRef().child("users").child(firebaseFacade.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("photo").getValue() != null) {
                    try {
                        String base64 = snapshot.child("photo").getValue().toString();
                        byte[] imageAsBytes = Base64.decode(base64.getBytes(), base64.length());
                        imageBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        imageview.setImageBitmap(imageBitmap);
                    } catch (Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Could not load your profile image, please load some other image. Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        final SharedPreferences sharedPref = this.getSharedPreferences("RTPP", Context.MODE_PRIVATE);

        final Intent joinstartIntenet = new Intent(this, JoinStartActivity.class);

        btnAddPitcure.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("image/png");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
            }
        });


        btnSingIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                editProfile(firebaseFacade.getRef(), TextUtility.getLoginTextContent(username), sharedPref.edit(), joinstartIntenet);


            }

        });
    }

    private void editProfile(final Firebase ref, final String username, final SharedPreferences.Editor editor, final Intent joinstartIntenet) {
        ref.child("users").child(uid).child("username").setValue(username, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(UserProfileActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (imageBitmap != null) {
                        ref.child("users").child(uid).child("photo").setValue(ExifUtils.getImageBytes(imageBitmap), new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(UserProfileActivity.this, "Error creating user: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    Toast.makeText(UserProfileActivity.this, "Successfully updated user account with uid: " + uid, Toast.LENGTH_LONG).show();
                    startActivity(joinstartIntenet);
                }
            }


        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            imageBitmap = ExifUtils.decodeFile(getImagePath(selectedImage));
            imageview.setImageBitmap(imageBitmap);
        }
    }

    private String getImagePath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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


        return super.onOptionsItemSelected(item);
    }

}

