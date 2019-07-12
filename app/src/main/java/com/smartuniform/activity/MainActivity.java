package com.smartuniform.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.smartuniform.SmartUniformApp;
import com.smartuniform.SmartUniformStatics;
import com.smartuniform.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageButton mTakeVideoBtn;
    private EditText mEditAddress;
    private File mVideoFile = null;
    private Context mContext;
    private SmartUniformApp mApp;
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private String mServerUrl = "";

    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        checkPermissions();

        setContentView(R.layout.activity_main);
        mServerUrl = SmartUniformApp.getServerUrl();
        if(mServerUrl.isEmpty()) {
            SmartUniformApp.setServerUrl(SmartUniformStatics.SERVER_URL);
            mServerUrl = SmartUniformStatics.SERVER_URL;
        }
        mEditAddress = findViewById(R.id.ipaddress);
        mEditAddress.setText(mServerUrl);
        mTakeVideoBtn = findViewById(R.id.button_image);
        mApp = SmartUniformApp.getInstance();

        mTakeVideoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takeVideo(v);
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; ++i) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            this.finish();
                            break;
                        }
                    }
                }
                break;
        }
    }

    public void takeVideo(View view) {
        SmartUniformApp.setServerUrl(mEditAddress.getText().toString());

        try {
            mVideoFile = getOriginalMediaFile();
        } catch (IOException ex) {
            Log.e(getClass().getName(), "Unable to create Video File", ex);
        }
        Uri uri = FileProvider.getUriForFile(this, "com.smartuniform", mVideoFile);
        mApp.setCurrTakenVideoPath(mVideoFile.getAbsolutePath());

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, SmartUniformStatics.RESULT_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SmartUniformStatics.RESULT_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startActivityForResult(new Intent(mContext, VideoOriginActivity.class), SmartUniformStatics.RESULT_CAPTURE_RESULT);
            }
        }
    }

    private File getOriginalMediaFile() throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "SmartUniform");
        File videoFile = null;
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VIDEO_" + timeStamp + "_";
        try {
            videoFile = File.createTempFile(videoFileName, ".mp4", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoFile;
    }
}
