package com.rohit.codv19admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import io.reactivex.functions.Consumer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class AdminActivity extends AppCompatActivity {
    EditText etDesc, etTitle;
    ImageView chooseImg, choosenImg;
    Button btPost;
    boolean per = false;
    ImageCompression imageCompression;
    Bitmap myBitmap;
    String desc, title;
    View contextView;
    private StorageReference mStorageRef;
    String downloadUrl=null;
    String newPath=null;
    AlertDialog alertDialog;
    final String TAG="DOWNLOAD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        etDesc = findViewById(R.id.description);
        etTitle = findViewById(R.id.et_title);
        chooseImg = findViewById(R.id.imageView2);
        choosenImg = findViewById(R.id.img_selected);
        btPost = findViewById(R.id.bt_post);
        contextView=findViewById(android.R.id.content);
        imageCompression = new ImageCompression(this);
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDesc();
            }
        });

    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(
                new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            per = true;
                        } else {
                            per = false;
                        }
                    }
                }
        );
    }

    private void chooseImage() {
        if (per) {
            new ChooserDialog(AdminActivity.this)
                    .withFilter(false, true, "jpg", "jpeg", "png")
                    .withStartFile(Environment.DIRECTORY_DOWNLOADS)
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            newPath = imageCompression.compressImage(path);
                            //etDesc.setText(newPath);
                            myBitmap = BitmapFactory.decodeFile(newPath);
                            choosenImg.setImageBitmap(myBitmap);
                        }
                    })

                    .withOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {

                            dialog.cancel(); // MUST have
                        }
                    })
                    .build()
                    .show();
        } else {
            requestPermission();
        }
    }

    private void validateDesc() {
        desc=etDesc.getText().toString();
        title=etTitle.getText().toString();
        if((desc!=null && desc.length()>0)  && title.length()> 0) {
            alertDialog.setMessage("Uploading...");
            alertDialog.show();
            if(myBitmap!=null){
                uploadImage(newPath);
            }
            else{
                uploadDescription(downloadUrl);
            }
        }
        else{
            Snackbar.make(contextView, "Both fields cannot be empty, title and message is compulsory ", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void uploadDescription(String downloadUrl) {
        String address=null;
        if(downloadUrl==null){
            address="";
        }
        else{
            address=downloadUrl;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        AdminMessage adminMessage = new AdminMessage(desc,address,title);

        myRef.child("adminMessage").child(String.valueOf(System.currentTimeMillis())).setValue(adminMessage);

        alertDialog.dismiss();

        Snackbar.make(contextView,"Post Success",Snackbar.LENGTH_LONG).show();


    }

    private void uploadImage(String mPath) {

        Uri file = Uri.fromFile(new File(mPath));
        StorageReference riversRef = mStorageRef.child("images/"+System.currentTimeMillis()+".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnCompleteListener((OnCompleteListener<Uri>) uri -> {
                            //alertDialog.dismiss();
                            uploadDescription(uri.getResult().toString());
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        alertDialog.dismiss();
                        Log.i("FAILED",exception.getMessage());
                        Snackbar.make(contextView, "Failed to upload: "+exception.toString(), Snackbar.LENGTH_SHORT).show();
                    }
                });

    }
}
