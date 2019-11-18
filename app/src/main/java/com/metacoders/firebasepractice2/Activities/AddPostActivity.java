package com.metacoders.firebasepractice2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metacoders.firebasepractice2.Model.Blog;
import com.metacoders.firebasepractice2.R;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mPostBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mPostDatabase;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private StorageReference mStorage;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostImage = (ImageButton) findViewById(R.id.image_upload_btn);
        mPostTitle = (EditText) findViewById(R.id.post_title_ed);
        mPostDesc = (EditText) findViewById(R.id.post_desc_ed);
        mPostBtn = (Button) findViewById(R.id.btn_post);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();    /*Trim Method deletes un-necessary spaces*/
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null) {
            /*Start Uploading*/

            StorageReference filepath = mStorage.child("Mblog_images")
                    .child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();   // getDownloadUrl
                    while (!uriTask.isSuccessful());

                    Uri downloadUri = uriTask.getResult();

                    DatabaseReference newPost = mPostDatabase.push();

                    Map<String, String> dataToSave = new HashMap<>();

                    // TODO: 9/22/2019 The name should always match with model class
                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    dataToSave.put("image", downloadUri.toString());
                    dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userID", mUser.getUid());

                    newPost.setValue(dataToSave);

//                    old way

//                    newPost.child("title").setValue(titleVal);
//                    newPost.child("desc").setValue(descVal);
//                    newPost.child("image").setValue(downloadurl.toString());
//                    newPost.child("timrStamp").setValue(String.valueOf(java.lang.System.currentTimeMillis()));

                    mProgress.dismiss();
                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();


                }
            });

        }
    }


}
