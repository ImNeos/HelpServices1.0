package com.isma.soli.ad.Image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.StaticValues;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private String ImagePath;
    ContextWrapper cw;
    File directory;
    private Toolbar toolbar;

    boolean isImageFitToScreen;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);



        imageView = findViewById(R.id.imageview);

        toolbar = (Toolbar) findViewById(R.id.main_app_bar);

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); //string is custom name you want
        }catch (Exception e){}


        ImagePath = getIntent().getStringExtra(StaticValues.IntentIDFullImage);


        if (!TextUtils.isEmpty(ImagePath))
        {
            cw = new ContextWrapper(this);
            directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);
            File myImageFile = new File(directory, ImagePath);
            if (myImageFile.exists())
            {
                try
                {

                    imageView.setImageURI(Uri.fromFile(myImageFile));
                } catch (Exception e) {
                    Log.i("CheckExceptions", e.getMessage().toString());
                }
            }
            else
            {
                finish();
            }
        }
        else
        {
            finish();
        }
    }
}
