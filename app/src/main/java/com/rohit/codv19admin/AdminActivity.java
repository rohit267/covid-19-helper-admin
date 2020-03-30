package com.rohit.codv19admin;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.functions.Consumer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class AdminActivity extends AppCompatActivity {
    EditText etDesc;
    ImageView chooseImg, choosenImg;
    Button btPost;
    boolean per = false;
    ImageCompression imageCompression;
    Bitmap myBitmap;
    String desc;
    View contextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etDesc = findViewById(R.id.description);
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
                            String newPath = imageCompression.compressImage(path);
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
        if((desc!=null && desc.length()>0) || ( myBitmap!=null && myBitmap.getByteCount()>0)) {
            Snackbar.make(contextView, "Valid", Snackbar.LENGTH_SHORT)
                    .show();
        }
        else{
            Snackbar.make(contextView, "Both fields cannot be empty", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
