

package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, MyAdapter.MyInterface {


    MyAdapter myAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler);




        
        
        
    }



    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


            if (direction==ItemTouchHelper.LEFT){

                MyAdapter.ViewHolder viewHolder1=(MyAdapter.ViewHolder)viewHolder;

                viewHolder1.delet();


            }

        }
    };




    


    private void SetAut(){

          startActivity(new Intent(this,LoginActivity.class));
          this.finish();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (myAdapter!=null){

            myAdapter.stopListening();
        }

    }

    @Override
    public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser()==null){
            SetAut();
            return;

        }
        initial(firebaseAuth.getCurrentUser());

        firebaseAuth.getCurrentUser().getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete( Task<GetTokenResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"is succecfull",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void initial(FirebaseUser user){


        Query query= FirebaseFirestore.getInstance()
                .collection("Note")
                .whereEqualTo("userId",user.getUid());

        FirestoreRecyclerOptions<model>options=new FirestoreRecyclerOptions.Builder<model>()
                .setQuery(query,model.class)
                .build();


        myAdapter=new MyAdapter(options,this);
        recyclerView.setAdapter(myAdapter);
        myAdapter.startListening();


        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




    }

    public void Flot(View view){

        TextInputEditText textInput=new TextInputEditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Add Note")
               .setView(textInput)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        addNote(textInput.getText().toString());

                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    private void addNote(String detail){

        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        model model=new model("Title",detail, new Timestamp(new Date()),false,userId);
        FirebaseFirestore.getInstance().collection("Note")
                .add(model)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"is succecfull",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.profil:

                startActivity(new Intent(this,ProfileActivity.class
                ));
                break;

            case R.id.signout:

                AuthUI.getInstance().signOut(this);
                Toast.makeText(getApplicationContext(),"you disconnected",Toast.LENGTH_LONG).show();

                break;

            case R.id.param:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void checkChange(boolean test, DocumentSnapshot documentSna) {

        documentSna.getReference().update("isComplete",test)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"is succefull",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    @Override
    public void editNote(DocumentSnapshot snapshot) {

        model model=snapshot.toObject(com.example.myapp.model.class);
        TextInputEditText textInput=new TextInputEditText(this);
        textInput.setText(model.getDetail().toString());
        textInput.setSelection(model.getDetail().length());
        new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(textInput)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String st=textInput.getText().toString();
                        model.setDetail(st);
                        snapshot.getReference().set(model)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete( Task<Void> task) {
                                        Toast.makeText(getApplicationContext(),"you disconnected",Toast.LENGTH_LONG).show();

                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel",null)
                .show();


    }

    @Override
    public void Delet(DocumentSnapshot snapshot) {


        DocumentReference reference=snapshot.getReference();

        model model=snapshot.toObject(com.example.myapp.model.class);

        reference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {


                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"is succefully deleted",Toast.LENGTH_LONG).show();

                        }
                    }
                });
        Snackbar.make(recyclerView,"deleted item",Snackbar.LENGTH_LONG)
                .setAction("undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.set(model);

                    }
                }).show();


    }
}