package edu.miracostacollege.cs134.petprotector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView petImageView;
    private Uri currentImage;

    public static final int RESULT_LOAD_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wire up the view with the layout
        petImageView = findViewById(R.id.petImageView);
        currentImage = getUriToResources(this, R.drawable.none);

        // Assign the petImageView to the currentImage (1 LINE OF CODE!!!)
        petImageView.setImageURI(currentImage);
        //petImageView.setContentDescription(get.getName());
    }

    // Make a helper method to construct a Uri in the form
    // android.resource://package name/resource type/resource name
    public static Uri getUriToResources(Context context, int resId)
    {
        // Let's get a reference to all the resources in our app:
        Resources res = context.getResources();

        String uriString = ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId) +
                "/" + res.getResourceTypeName(resId) +
                "/" + res.getResourceEntryName(resId);
        // Convert the String into a Uri object
        return Uri.parse(uriString);
    }

    // Method that responds to the click of the getImageView
    public void selectPetImage(View v)
    {
        // To request permissions from the user, let's see what
        // they have enabled already
        // permissions we want to request
        List<String> permsReqList = new ArrayList<>();

        // Make up request code for permissions
        int permsRequestCode = 100; // doesn't matter the number

        // Permissions: Camera, Read Ext Storage, Write External Storage
        // Permissions are integer codes (GRANTED or not)
        int haveCameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(haveCameraPerm != PackageManager.PERMISSION_GRANTED)
        {
            permsReqList.add(Manifest.permission.CAMERA);
        }

        // Permission: Read Ext Storage
        int haveReadExtStoragePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(haveReadExtStoragePerm != PackageManager.PERMISSION_GRANTED)
        {
            permsReqList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // Permission: Write Ext Storage
        int haveWriteExtStoragePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(haveWriteExtStoragePerm != PackageManager.PERMISSION_GRANTED)
        {
            permsReqList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // If the size of the list > 0, we have some permissions to request
        if(permsReqList.size() > 0)
        {
            // Convert the List into an array for requesting
            String[] perms = new String[permsReqList.size()];
            // make it an array
            permsReqList.toArray(perms);
            // Request the permissions
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }

        // Check to ensure that all 3 permissions have been granter
        if(haveCameraPerm == PackageManager.PERMISSION_GRANTED
            && haveReadExtStoragePerm == PackageManager.PERMISSION_GRANTED
            && haveWriteExtStoragePerm == PackageManager.PERMISSION_GRANTED)
        {
            // open the image gallery on the phone!

            // Make an intent to pick an image from gallery
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
        // User has NOT enabled permissions!
        else
        {
            Toast.makeText(this, "App requires access to external storage. Please enable permissions", Toast.LENGTH_LONG).show();
        }
    }

    // Override a method called onActivityResult (after user has picked a picture!)
    // Ctrl + o  =>  override menu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // This method us called after firing an intent for a result
        // Identify that we're loading an image
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            currentImage = data.getData();
            petImageView.setImageURI(currentImage);
        }
    }
}
