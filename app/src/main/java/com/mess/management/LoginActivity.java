package com.mess.management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button Login;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.user_email_id);
        password = findViewById(R.id.user_password);
        Login = findViewById(R.id.login_Button);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = email.getText().toString();
                String userpassword = password.getText().toString().toLowerCase();

                if (TextUtils.isEmpty(useremail) || TextUtils.isEmpty(userpassword)) {
                    Toasty.error(LoginActivity.this, "Not full fill your data", Toasty.LENGTH_SHORT, true).show();
                } else {
                    kProgressHUD = KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setDetailsLabel("Loading...")
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();
                    firebaseFirestore.collection("Users")
                            .document(useremail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            String user_password = task.getResult().getString("password");
                                            if (userpassword.equals(user_password)) {
                                                firebaseAuth.signInWithEmailAndPassword(useremail, user_password)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    kProgressHUD.dismiss();
                                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        kProgressHUD.dismiss();
                                                        Toasty.error(LoginActivity.this, "" + e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                                                    }
                                                });
                                            } else {
                                                kProgressHUD.dismiss();
                                                Toasty.info(getApplicationContext(), "user not found", Toasty.LENGTH_SHORT, true).show();
                                            }
                                        } else {
                                            kProgressHUD.dismiss();
                                            Toasty.info(getApplicationContext(), "user not found", Toasty.LENGTH_SHORT, true).show();
                                        }
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toasty.error(LoginActivity.this, "" + e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                        }
                    });
                }
            }
        });


    }


    public void registration(View view) {
        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
    }


    public void forget(View view) {
        final FlatDialog flatDialog = new FlatDialog(LoginActivity.this);
        flatDialog.setTitle("Forget password")
                .setSubtitle("You are forget your password? change it.")
                .setFirstTextFieldHint("Username")
                .setSecondTextFieldHint("Userpassword")
                .setFirstButtonText("Change")
                .setSecondButtonText("Cancle")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String username = flatDialog.getFirstTextField().toLowerCase().toString();
                        String userpassword = flatDialog.getSecondTextField().toLowerCase().toString();
                        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userpassword)) {
                            Toasty.error(getApplicationContext(), "Enter all information", Toasty.LENGTH_SHORT, true).show();
                            return;
                        } else {

                        }
                    }
                });
    }
}