package com.example.learnhub;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText registerEmail,registerPassword;
    private Button buttonReg;
    private TextView LoginRedirectText;   //txtSignIn

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        registerEmail = findViewById(R.id.edtSignUpEmail);
        registerPassword =findViewById(R.id.edtSignUpPassword);
        buttonReg= findViewById(R.id.btnSignUp);
        LoginRedirectText= findViewById(R.id.txtSignIn);
        LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(register.this, login.class));
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(registerEmail.getText());
                password = String.valueOf(registerPassword.getText());
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(register.this, "enter email", Toast.LENGTH_SHORT).show();
                    return; }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(register.this, "enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //   progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(register.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(register.this,login.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }};
