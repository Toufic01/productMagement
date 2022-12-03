package com.mess.management;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText name,address,email,password;
    Button submit;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    KProgressHUD kProgressHUD;
    Spinner spinnar;
    String[] gender ={"Select a gender","Male","Female","Others"};
    String value_gender;
    ImageView profileImage;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = findViewById(R.id.user_name_id);
        address = findViewById(R.id.user_address);
        email = findViewById(R.id.user_email_id);
        password = findViewById(R.id.user_password);
        submit = findViewById(R.id.submit_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        spinnar = findViewById(R.id.spinnar);
        spinnar.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.textitem,gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnar.setAdapter(arrayAdapter);


        profileImage = findViewById(R.id.profileImage);
         firebaseStorage = FirebaseStorage.getInstance();
         storageReference = FirebaseStorage.getInstance().getReference();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select an image"),1);


            }
        });










        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userfullname = name.getText().toString();
                String useraddress = address.getText().toString();
                String useremail = email.getText().toString();
                String userpassword = password.getText().toString().toLowerCase();


                if(TextUtils.isEmpty(userfullname)||TextUtils.isEmpty(useraddress)||TextUtils.isEmpty(useremail)||TextUtils.isEmpty(userpassword))
                {
                    Toasty.error(getApplicationContext(),"Full fill your information",Toasty.LENGTH_SHORT,true).show();
                    return;
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setTitle("Confirmation")
                            .setMessage("Are you want to register in this application?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            kProgressHUD = new KProgressHUD(RegistrationActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                    .setLabel("Please wait")
                                    .setDetailsLabel("Loading...")
                                    .setCancellable(true)
                                    .setAnimationSpeed(2)
                                    .setDimAmount(0.5f)
                                    .show();

                            firebaseAuth.createUserWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        UserModel userModel = new UserModel(userfullname,useraddress,useremail,userpassword,value_gender);
                                        firebaseFirestore.collection("users")
                                                .document(firebaseAuth.getCurrentUser().getEmail())
                                                .set(userModel)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful())
                                                        {
                                                            kProgressHUD.dismiss();
                                                            Toasty.success(RegistrationActivity.this,"Done",Toasty.LENGTH_SHORT,true).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    kProgressHUD.dismiss();
                                    Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
                                }
                            });

                        }
                    }).show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null && data.getData()!=null)
        {
            imageUri =data.getData();
            try{
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profileImage.setImageBitmap(imageBitmap);
                upload(imageUri);
            }catch (Exception e)
            {
                Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
            }
        }
    }

    private void upload(Uri imageUri) {

        if (imageUri != null) {
            final KProgressHUD kProgressHUD = new KProgressHUD(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Uploading...")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            StorageReference ref = storageReference.child("Image/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            final Uri downloadUri = uriTask.getResult();


                            if (uriTask.isSuccessful()) {
                                String downmainImageuri = downloadUri.toString();
                                kProgressHUD.dismiss();
                            } else {
                                kProgressHUD.dismiss();
                                Toasty.error(getApplicationContext(), "Failed", Toasty.LENGTH_SHORT, true).show();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    kProgressHUD.dismiss();
                    Toasty.error(getApplicationContext(), "Failed", Toasty.LENGTH_SHORT, true).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    kProgressHUD.setDetailsLabel("Uploaded" + (int) progress + "%");
                }
            });

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        value_gender = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void login(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
}