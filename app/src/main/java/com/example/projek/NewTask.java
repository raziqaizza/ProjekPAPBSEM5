package com.example.projek;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projek.databinding.NewtaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class NewTask extends AppCompatActivity {
    NewtaskBinding binding;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    StorageReference storageReference;
    Uri image;
    ImageView imageView;
    String userID, pUID, pTitle, pDesc, pImage, pImageUID, imageUID;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                image = result.getData().getData();
                Glide.with(getApplicationContext()).load(image).into(binding.idImage);
            } else {
                Toast.makeText(NewTask.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewtaskBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            pUID = bundle.getString("pUID");
            pTitle = bundle.getString("pTitle");
            pDesc = bundle.getString("pDesc");
            pImage = bundle.getString("pImage");
            pImageUID = bundle.getString("pImageUID");

            imageView = binding.idImage;

            Glide.with(getApplicationContext()).load(pImage).into(binding.idImage);

            binding.idEditTitle.setText(pTitle);
            binding.idEditDescription.setText(pDesc);
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null){
                    if (pImage == null){

                    }else if (image.toString() != pImage){
                        deleteOldURL(pImageUID);
                    }
                    uploadImage(image);
                }

                String inputData = binding.idEditDescription.getText().toString();
                String inputTitle = binding.idEditTitle.getText().toString();

                if (bundle != null) {
                    editData(inputTitle, inputData);
                } else {
                    saveData(inputTitle, inputData);
                }
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        binding.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("attach", "onClick: Clicked");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pImage != null) {
                    download(pImageUID);
                } else {
                    Toast.makeText(getApplicationContext(), "No image to download", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Function untuk save data
    private void saveData(String title, String desc){
        Map<String, Object> data = new HashMap<>();
        String uid = UUID.randomUUID().toString();

        if (image != null){
            data.put("imageURL", image);
            data.put("imageUID", imageUID);
        }

        data.put("uid", uid);
        data.put("title", title);
        data.put("desc", desc);

        CollectionReference collectionReference = fStore.collection("users").document(userID).collection("task");
        collectionReference.document(uid).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("task UserID", "title : "+ title);
                        Log.d("ADD DATA", "Document Snapshot written with ID: " + collectionReference.document(uid).getId());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("err add data", "Error adding document", e);
                    }
                });
    }
    private void uploadImage(Uri image) {
        imageUID = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference reference = storageRef.child("images/" + imageUID);
        UploadTask uploadTask = reference.putFile(image);
        Log.d("IMAGE PATH", "uploadImage: " + reference.getPath());
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Upload Image", "onSuccess: " + uploadTask.getSnapshot());
                Toast.makeText(NewTask.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Upload Image", "GAGAL");
                Toast.makeText(NewTask.this, "Error uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editData(String title, String desc){
        Map<String, Object> data = new HashMap<>();

        if (image != null){
            data.put("imageURL", image);
            data.put("imageUID", imageUID);
        }

        data.put("uid", pUID);
        data.put("title", title);
        data.put("desc", desc);

        DocumentReference dr = fStore.collection("users").document(userID).collection("task").document(pUID);

        dr.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                Log.d("task UserID", "title : "+ title);
                Log.d("ADD DATA", "Document Snapshot written with ID: " + dr.getId());
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("err add data", "Error adding document", e);
            }
        });
    }

    private void deleteOldURL(String url){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projekpapb-5bc29.appspot.com/images/" + url);
        Log.d("PATH DELETED", "deleteOldURL: " + storageRef.getPath());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(NewTask.this, "OLD IMAGE DELETED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void download(String url){
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("/images/" + url);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String slbw = uri.toString();
                downloadImage(getApplicationContext(), "images", ".jpg", DIRECTORY_DOWNLOADS, slbw);
            }
        });
    }

    private void downloadImage(Context context, String fileName, String fileExtension, String destinationDirectory, String Url) {
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtension);

        downloadManager.enqueue(request);
    }
}
