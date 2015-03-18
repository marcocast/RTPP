package com.rtpp.rtpp.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.ConsumerIrManager;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by marco on 18/03/15.
 */
public class FirebaseFacade {

    private final Firebase ref;
    private final Context context;

    public FirebaseFacade(Context context){
        this.context = context;
        Firebase.setAndroidContext(context);
        this.ref = new Firebase("https://rtpp.firebaseio.com");
    }



    public boolean isLogged(){
        AuthData authData = ref.getAuth();
        return (authData != null);
    }

    public String getUid(){
       return ref.getAuth() == null ? "" : ref.getAuth().getUid();
    }

    public void logout(){

        final AuthData authData = ref.getAuth();

        if (authData != null) {
            ref.unauth();
        }

    }

    public Firebase getRef(){
        return ref;
    }


}
