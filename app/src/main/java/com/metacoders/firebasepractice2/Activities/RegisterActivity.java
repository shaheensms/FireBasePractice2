package com.metacoders.firebasepractice2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metacoders.firebasepractice2.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateACbtn;
    private CircleImageView mProfileImage;
    private Uri resultUri = null;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private StorageReference mFirebaseStorage;
    private ProgressDialog mProgressBar;

    private final static int GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");

        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_Profile_Pics");

        mAuth = FirebaseAuth.getInstance();

        mProgressBar = new ProgressDialog(this);

        mFirstName = (EditText) findViewById(R.id.firstName_field);
        mLastName = (EditText) findViewById(R.id.lastName_field);
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        mCreateACbtn = (Button) findViewById(R.id.createAC_btn);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_pic);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

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
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressBar.setMessage("Creating Account...");
            mProgressBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                StorageReference imagePath = mFirebaseStorage.child("MBlog_Profile_Pics")
                                        .child(resultUri.getLastPathSegment());

                                imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();   // getDownloadUrl
                                        while (!uriTask.isSuccessful());

                                        Uri downloadUri = uriTask.getResult();

                                        String userid = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDb = mDatabaseReference.child(userid);

                                        currentUserDb.child("firstname").setValue(firstName);
                                        currentUserDb.child("lastname").setValue(lastName);
                                        currentUserDb.child("image").setValue(downloadUri.toString());

                                        mProgressBar.dismiss();

//                                Sent user to postlist
                                        Intent intent = new Intent(RegisterActivity.this, PostListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                });




                            }

                        }
                    });


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                mProfileImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
