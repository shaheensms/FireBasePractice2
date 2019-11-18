package com.metacoders.firebasepractice2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metacoders.firebasepractice2.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateACbtn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");

        mAuth = FirebaseAuth.getInstance();

        mProgressBar = new ProgressDialog(this);

        mFirstName = (EditText) findViewById(R.id.firstName_field);
        mLastName = (EditText) findViewById(R.id.lastName_field);
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        mCreateACbtn = (Button) findViewById(R.id.createAC_btn);

        mCreateACbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                createNewAccount();

            }
        });

    }

    private void createNewAccount() {

        final String firstName = mFirstName.getText().toString().trim();
        final String lastName = mLastName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressBar.setMessage("Creating Account...");
            mProgressBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = mDatabaseReference.child(userid);

                                currentUserDb.child("firstname").setValue(firstName);
                                currentUserDb.child("lastname").setValue(lastName);
                                currentUserDb.child("image").setValue("none");

                                mProgressBar.dismiss();

//                                Sent user to postlist
                                Intent intent = new Intent(RegisterActivity.this, PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);


                            }

                        }
                    });


        }


    }


}
