package com.example.myapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            startActivity(new Intent(this,MainActivity.class));

            this.finish();
        }
    }





   private final ActivityResultLauncher<Intent>launcher=registerForActivityResult(

            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {


                    onSignRsult(result);
                }
            }


    );


    public void connect(View view) {





        List<AuthUI.IdpConfig>provi= Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        Intent intent=AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provi)
                .setTosAndPrivacyPolicyUrls("https://example.com/terms.html","https://example.com/privacy.html")
                .build();

        launcher.launch(intent);



    }


    private void onSignRsult(FirebaseAuthUIAuthenticationResult result){

        IdpResponse response=result.getIdpResponse();

        if (result.getResultCode()==RESULT_OK){


            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            if (user.getMetadata().getCreationTimestamp()==user.getMetadata().getLastSignInTimestamp()){

                Toast.makeText(getApplicationContext(),"welcom new user",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),"welcom old user",Toast.LENGTH_LONG).show();

            }

            startActivity(new Intent(this,MainActivity.class));
            this.finish();

        }else {
            if (response==null){

                Toast.makeText(getApplicationContext()," user Cancels",Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(getApplicationContext(),"Error :Fail",Toast.LENGTH_LONG).show();

            }


        }


    }




}