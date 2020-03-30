package com.rohit.codv19admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btSignin;
    private FirebaseAuth mAuth;
    String username, password;
    View contextView;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        contextView = findViewById(android.R.id.content);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        mAuth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btSignin = findViewById(R.id.bt_signin);

        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

    }

    private void validate(){
        username=etUsername.getText().toString();
        password=etPassword.getText().toString();

        if(username!=null && password!=null && username.length()>0 && password.length()>=6){
            goToLogin();
        }
        else{


            Snackbar.make(contextView, "Invalid inputs", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void goToLogin() {
        alertDialog.setMessage("Signing in..");
        alertDialog.show();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Snackbar.make(contextView, "Welcome Admin", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            alertDialog.dismiss();
                            Snackbar.make(contextView, "Wrong Password, please try again", Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(MainActivity.this,AdminActivity.class);
        intent.putExtra("currentUser",currentUser);
        startActivity(intent);
        finish();
    }
}
