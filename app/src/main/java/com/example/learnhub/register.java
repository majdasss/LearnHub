package com.example.learnhub;


import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class register extends AppCompatActivity {

    public static final String TAG = "TAG";
    private FirebaseAuth mAuth;
    private EditText registerEmail,registerPassword,fullName,nPhone;
    private Button buttonReg;
    private TextView LoginRedirectText;   //txtSignIn
    FirebaseFirestore fStore;
    String userID;

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
        //instancier fire
        mAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        registerEmail = findViewById(R.id.edtSignUpEmail);
        registerPassword =findViewById(R.id.edtSignUpPassword);
        fullName = findViewById(R.id.edtSignUpFullName);
        nPhone =findViewById(R.id.edtSignUpMobile);
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
                String name,phone;
                // Récupération des valeurs des champs d'entrée
                email = registerEmail.getText().toString();
                password = registerPassword.getText().toString();
                name = fullName.getText().toString();
                phone = nPhone.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(register.this, "enter email", Toast.LENGTH_SHORT).show();
                    return; }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(register.this, "enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // register ythe user in firebase

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //   progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(register.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            //don id par defaut au user
                            userID= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            DocumentReference documentReference=fStore.collection("users").document(userID);//creer une collection users au firestore
                            Map<String,Object> user=new HashMap<>();//liste de users
                            //les champs du collection
                            //name est le nom du champ au firestore
                            user.put("name", fullName.getText().toString());
                            user.put("email", registerEmail.getText().toString());
                            user.put("phone", nPhone.getText().toString());


                            fStore.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            fStore.collection("users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });

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
    }}
