package com.mediprice.assistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mediprice.assistant.Helpers.CoreHelper;
import com.mediprice.assistant.Helpers.CustomModel;
import com.mediprice.assistant.Helpers.ImagesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestBed extends AppCompatActivity{
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
    ImageView no_images;

    RecyclerView recyclerView;
    List<CustomModel> imagesList;
    List<String> savedImagesUri;
    Spinner spinnerHospital,spinnerTime,spinnerSymptoms;
    String timeVal,symptomVal;
    ImagesAdapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    CollectionReference reference;
    int counter;
    EditText patientName,patientAge,phoneNumber,patientGender;
    Button requestBtn,btnPickImages;
    FirebaseFirestore db;
    Intent patientdashb;
    private List<String> names=new ArrayList<>();
    private List<DocumentSnapshot> hospitalSnapList=new ArrayList<>();
    private String hospitalId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_request_bed);

        patientName = findViewById(R.id.patientName);
        patientAge = findViewById(R.id.patientAge);
        phoneNumber = findViewById(R.id.phoneNumber);
        patientGender = findViewById(R.id.patientGender);
        requestBtn = findViewById(R.id.requestBtn);
        btnPickImages = findViewById(R.id.fabChooseImage);
        patientdashb = new Intent(this,PatientDashboard.class);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = db.collection("patient");
        CollectionReference namesRef = db.collection("hospital");
        spinnerHospital = (Spinner) findViewById(R.id.spinnerHospital);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,names);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(adapter1);

        spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> timeadapter = ArrayAdapter.createFromResource(this,R.array.time_array, android.R.layout.simple_spinner_item);
        timeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeadapter);


        spinnerSymptoms = (Spinner) findViewById(R.id.spinnerSymptoms);
        ArrayAdapter<CharSequence> symptomsadapter = ArrayAdapter.createFromResource(this,R.array.symptoms_array,android.R.layout.simple_spinner_item);
        symptomsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymptoms.setAdapter(symptomsadapter);

        namesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        hospitalSnapList.add(document);
                        String hospname = document.getString("name");
                        names.add(hospname);

                    }
                    adapter1.notifyDataSetChanged();

                }
            }
        });

        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hospitalId=hospitalSnapList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeVal = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSymptoms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                symptomVal = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        savedImagesUri = new ArrayList<>();
        no_images = findViewById(R.id.no_image);
        imagesList = new ArrayList<>();
        coreHelper = new CoreHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ImagesAdapter(this, imagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() != 0) {
                    no_images.setVisibility(View.GONE);
                } else {
                    no_images.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissionAndPickImage();
            }
        });


        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImages(view);
            }
        });



    }

    private void uploadImages(View view) {
        if (imagesList.size() != 0) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploaded 0/"+imagesList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < imagesList.size(); i++) {
                final int finalI = i;
                storageReference.child("userData/").child(imagesList.get(i).getImageName()).putFile(imagesList.get(i).getImageURI()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageReference.child("userData/").child(imagesList.get(finalI).getImageName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    counter++;
                                    progressDialog.setMessage("Uploaded "+counter+"/"+imagesList.size());
                                    if (task.isSuccessful()){
                                        savedImagesUri.add(task.getResult().toString());
                                    }else{
                                        storageReference.child("userData/").child(imagesList.get(finalI).getImageName()).delete();
                                        Toast.makeText(RequestBed.this, "Couldn't save "+imagesList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                                    }
                                    if (counter == imagesList.size()){
                                        progressDialog.dismiss();
                                        saveImageDataToFirestore();
                                    }
                                }
                            });
                        }else{
                            progressDialog.setMessage("Uploaded "+counter+"/"+imagesList.size());
                            counter++;
                            Toast.makeText(RequestBed.this, "Couldn't upload "+imagesList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            coreHelper.createSnackBar(view, "Please add some images first.", "", null, Snackbar.LENGTH_SHORT);
        }
    }

private static final String TAG = "RequestBed";
    private void saveImageDataToFirestore() {
        ProgressDialog progressDialog=new ProgressDialog(RequestBed.this);
        progressDialog.setMessage("SendingData  ");
        progressDialog.show();
        reference = db.collection("patient");
        CollectionReference hospitalRef;
        if(hospitalId!=null){
            hospitalRef=FirebaseFirestore.getInstance().collection("hospital").document(hospitalId).collection("booking");

        }else {

            Toast.makeText(this,"Select a hospital",Toast.LENGTH_LONG).show();
            return;
        }
        String newId=hospitalRef.document().getId();

        String name = patientName.getText().toString().trim();
        String age = patientAge.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String relation = "N/A";//patientRelation.getText().toString().trim();
        String gender = patientGender.getText().toString().trim();
        //String time = reachingTime.getText().toString().trim();
        //String condition = patientCondition.getText().toString().trim();

        if(name.isEmpty()){
            patientName.setError("Enter name");
            patientName.requestFocus();
            return;
        }
        if(age.isEmpty()){
            patientAge.setError("Enter age");
            patientAge.requestFocus();
            return;
        }
        if(age.length()>3){
            patientAge.setError("Enter age");
            patientAge.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            phoneNumber.setError("Enter phone");
            phoneNumber.requestFocus();
            return;
        }
        if (gender.isEmpty()){
            patientGender.setError("Enter gender");
            patientGender.requestFocus();
            return;
        }

        Map<String, Object> patient = new HashMap<>();
        patient.put("name",name);
        patient.put("age",age);
        patient.put("phone",phone);
        patient.put("relation",relation);
        patient.put("gender",gender);
        patient.put("time",timeVal);
        patient.put("hospitalId",hospitalId);
        patient.put("images",savedImagesUri );
        patient.put("timestamp",FieldValue.serverTimestamp());
        patient.put("symptoms",symptomVal);


        WriteBatch batch=FirebaseFirestore.getInstance().batch();
        batch.set(hospitalRef.document(newId),patient,SetOptions.merge());
        batch.set(reference.document(newId),patient,SetOptions.merge());
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                coreHelper.createAlert("Success", "Request sent successfully,\n if not selected in 3h consider requesting again to other hospitals.", "OK", "", null, null, null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
                Log.e("MainActivity:SaveData", e.getMessage());
            }
        });
        clearData();

    }

    private void clearData() {
        patientName.getText().clear();
        patientAge.getText().clear();
        phoneNumber.getText().clear();
        patientGender.getText().clear();
        spinnerTime.setAdapter(ArrayAdapter.createFromResource(this,R.array.time_array, android.R.layout.simple_spinner_dropdown_item));
        spinnerSymptoms.setAdapter(ArrayAdapter.createFromResource(this,R.array.symptoms_array,android.R.layout.simple_spinner_dropdown_item));
        spinnerHospital.setAdapter(new ArrayAdapter<String>(RequestBed.this,android.R.layout.simple_spinner_dropdown_item,names));
        imagesList.clear();
        adapter.notifyDataSetChanged();
    }

    private void verifyPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //If permission is granted
                pickImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            //no need to check permissions in android versions lower then marshmallow
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            imagesList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Uri uri = data.getData();
                        imagesList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                    }
                }
        }
    }
}