package com.example.myapp;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyAdapter extends FirestoreRecyclerAdapter<model,MyAdapter.ViewHolder>  {


    MyInterface myInterface;

    public MyAdapter( FirestoreRecyclerOptions<model> options, MyInterface myInterface) {
        super(options);
        this.myInterface=myInterface;
    }

    @Override
    protected void onBindViewHolder( MyAdapter.ViewHolder holder, int position,  model model) {

        holder.title.setText(model.getTitle());
        holder.detail.setText(model.getDetail());
        CharSequence charSequence= DateFormat.format("EEEE ,MMM dd,yyyy h:mm:ss a",model.getTimestamp().toDate());
        holder.time.setText(charSequence);

        holder.checkBox.setChecked(model.isComplete());

    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false);

        ViewHolder holder=new ViewHolder(view);




        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,detail,time;

        CheckBox checkBox;

        public ViewHolder( View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);
            detail=itemView.findViewById(R.id.detail);
            time=itemView.findViewById(R.id.time);
            checkBox=itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(getAdapterPosition());


                    myInterface.editNote(documentSnapshot);
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    DocumentSnapshot snapshot= getSnapshots().getSnapshot(getAdapterPosition());


                    model model=getItem(getAdapterPosition());
                    if (model.isComplete()!=isChecked) {

                        myInterface.checkChange(isChecked, snapshot);
                    }

                }
            });

        }


        public void delet(){

            DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(getAdapterPosition());
            myInterface.Delet(documentSnapshot);


        }
    }

    public interface MyInterface {

        void checkChange(boolean test, DocumentSnapshot documentSna);
        void editNote(DocumentSnapshot snapshot);
        void Delet(DocumentSnapshot snapshot);

    }
}
