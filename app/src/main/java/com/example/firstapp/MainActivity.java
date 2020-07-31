package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class MainActivity extends ScreenshotDetectionActivity{

    AlertDialog.Builder builder;
    Uri filePath;
    ImageView iv;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv=findViewById(R.id.imageview);

        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference();

    }

    @Override
    public void onScreenCaptured(final String path,final Uri uri) {
        Toast.makeText(this, "Path :"+uri, Toast.LENGTH_SHORT).show();

        builder = new AlertDialog.Builder(this);
        filePath = Uri.fromFile(new File(path));

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to save Screenshot?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();

                       // Bitmap bitmap = null;
                        //try {
                            /*bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(
                                            getContentResolver(),uri);*/
                            iv.setImageURI(uri);
                            uploadImage(uri);

                        //} catch (IOException e) {
                          //  e.printStackTrace();
                        //}


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                       // dialog.cancel();
                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmation");
        alert.show();

       // Toast.makeText(this,""+ filePath.getPath(),Toast.LENGTH_LONG).show();
    }


    @Override
    public void onScreenCapturedWithDeniedPermission() {
        Toast.makeText(getApplicationContext(), "Please grant read external storage permission for screenshot detection", Toast.LENGTH_SHORT).show();
    }

    // UploadImage method
    private void uploadImage(Uri uri)
    {
       if (filePath != null) {


           final  ProgressDialog progressDialog= new ProgressDialog(this);
           progressDialog.setTitle("Uploading Image......");
           progressDialog.show();

           //String randomKey= UUID.randomUUID().toString();
           StorageReference ref = storageReference.child("images/"+filePath.getLastPathSegment());

           //StorageReference riversRef = ref.child("images/rivers.jpg");

           ref.putFile(uri)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           // Get a URL to the uploaded content
                           //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(),"sucess",Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {
                           // Handle unsuccessful uploads
                           // ...
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>(){
                       @Override
                       public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                           double processPercent =(100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                           progressDialog.setMessage("Percentage: "+ (int) processPercent + "%");

                       }
                   });

       }
        else
        {
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }
    }
}
