package com.example.myapp;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {


    EditText inputEditText;
    ImageView imageView;
    ProgressBar progressBar;

    final static int Codes=100;

    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        inputEditText=findViewById(R.id.textInputLayout);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progressBar);


       FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){


            inputEditText.setText(user.getDisplayName());
            inputEditText.setSelection(user.getDisplayName().length());
        }

        if (user!=null){

            Glide.with(this).load(user.getPhotoUrl()).into(imageView);

        }


    }

    public void Upload(View view) {

        userName=inputEditText.getText().toString();

        FirebaseUser User=FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        User.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"is profile updated",Toast.LENGTH_LONG).show();

                        }
                    }
                });





    }

    public void show(View view) {


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference();
        StorageReference reference=storageRef.child("experiments__.jpg");

       // Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.crashlytics);
      //  ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        //bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

      //  reference.putBytes(outputStream.toByteArray())
               // .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   // @Override
                   // public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                    //    if (task.isSuccessful()){

                           // Toast.makeText(getApplicationContext(),"image is uploaded",Toast.LENGTH_LONG).show();
                      //  }

                   // }
               // });



        reference.getBytes(1024*1024)
               .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                   @Override
                   public void onSuccess(byte[] bytes) {


                       Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                       imageView.setImageBitmap(bitmap);

                   }
               });









    }

    public void upImage(View view) {


        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (intent.resolveActivity(getPackageManager())!=null){

            startActivityForResult(intent,Codes);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==Codes){

            switch (resultCode){

                case RESULT_OK:

               Bitmap bitmap=(Bitmap)data.getExtras().get("data");

               imageView.setImageBitmap(bitmap);
               ImageUpdate(bitmap);
            }

        }
    }



    private void ImageUpdate(Bitmap bitmap){

        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage=FirebaseStorage.getInstance();

        StorageReference storageReference=storage.getReference().child(userId+".jpg");
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

        storageReference.putBytes(outputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Dowload(storageReference);

                    }
                });

    }




    private void Dowload(StorageReference reference){

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                userUpdate(uri);
            }

        });


    }


    private void userUpdate(Uri uri){


        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();



        UserProfileChangeRequest  request=new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
                user.updateProfile(request)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {



                            }
                        });
    }



}