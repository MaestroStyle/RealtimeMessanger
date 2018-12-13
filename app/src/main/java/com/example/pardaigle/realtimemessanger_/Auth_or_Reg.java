package com.example.pardaigle.realtimemessanger_;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Auth_or_Reg extends AppCompatActivity {

    private static final String TAG = "Auth_or_Reg";



    String number, phoneVerificationId;
    private EditText ephoneNumber, ereg_username,  ever_code;
    private Button bauth_conf, breg_conf, bauth_reg, bverif_conf;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ConstraintLayout auth_layout, reg_layout, ver_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_or__reg);

        ephoneNumber = (EditText) findViewById(R.id.number);
        ereg_username = (EditText) findViewById(R.id.reg_username);
        ever_code = (EditText) findViewById(R.id.ver_code);

        bauth_conf = (Button) findViewById(R.id.auth_conf_butt);
        breg_conf = (Button) findViewById(R.id.reg_conf);
        bauth_reg = (Button) findViewById(R.id.auth_reg_butt);
        bverif_conf = (Button) findViewById(R.id.ver_conf);

        auth_layout = (ConstraintLayout) findViewById(R.id.Auth);
        reg_layout = (ConstraintLayout) findViewById(R.id.Reg);
        ver_layout = (ConstraintLayout) findViewById(R.id.Ver);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Auth_or_Reg.this,"Некорректный номер", Toast.LENGTH_SHORT).show();
                    onBackPressed();;
                } else
                    if (e instanceof FirebaseTooManyRequestsException) {
                }
            }

            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
            }
        };

       breg_conf.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendCode(null);
               ephoneNumber.setVisibility(View.INVISIBLE);
               reg_layout.setVisibility(View.INVISIBLE);
               ver_layout.setVisibility(View.VISIBLE);
               auth_layout.setVisibility(View.INVISIBLE);
           }
       });

       bverif_conf.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               verifyCode(null);

           }
       });

       bauth_reg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               auth_layout.setVisibility(View.INVISIBLE);
               reg_layout.setVisibility(View.VISIBLE);
           }
       });
        bauth_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(null);
                ephoneNumber.setVisibility(View.INVISIBLE);
                auth_layout.setVisibility(View.INVISIBLE);
                ver_layout.setVisibility(View.VISIBLE);

            }
        });
    }


    public void sendCode(View view){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(ephoneNumber.getText().toString(), 60, TimeUnit.SECONDS, this, verificationCallbacks);
    }

    public void verifyCode(View view) {
        if(!ever_code.getText().toString().equals("")) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, ever_code.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild(ephoneNumber.getText().toString())) {
                                mDatabase.child("users").child(ephoneNumber.getText().toString()).setValue(ereg_username.getText().toString());
                            }
                            Intent intent = new Intent(Auth_or_Reg.this, chat.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(Auth_or_Reg.this, "Неверный проверочный код", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
   }
}
