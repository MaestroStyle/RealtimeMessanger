package com.example.pardaigle.realtimemessanger_;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class chat extends AppCompatActivity {

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    public EditText SendMsg;
    public EditText Id;
    public TextView GetMsg;
    public String Gm,Sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SendMsg = (EditText) findViewById(R.id.editText);
        GetMsg = (TextView) findViewById(R.id.textView2);

        SendMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Sm = s.toString();
                myRef.child("send").setValue(Sm);
                GetmessagefromDB();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void GetmessagefromDB (){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gm = dataSnapshot.child("send").getValue().toString();
                GetMsg.setText(Gm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(postListener);
    }
    public void click(View view) {
    }

}
