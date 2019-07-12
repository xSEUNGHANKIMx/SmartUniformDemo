package com.smartuniform.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.smartuniform.SmartUniformApp;
import com.smartuniform.SmartUniformStatics;
import com.smartuniform.R;
import com.smartuniform.utils.ExifUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;


public class VideoOriginActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private String mUploadUrl = "";
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_origin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoUpload();
                mDialog.setMessage("Uploading is in Process Please wait...");
                mDialog.show();
            }
        });

        mVideoView = findViewById(R.id.videoView);
        mDialog = new ProgressDialog(this);
        mUploadUrl = SmartUniformApp.getServerUrl() + "fileupload.php";
        setCapturedVideo();
    }

    private void setCapturedVideo() {
        String path = SmartUniformApp.getCurrTakenVideoPath();
        Uri uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);
        mVideoView.seekTo( 1 );

        MediaController mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);
        mediaController.setAnchorView(mVideoView);
        mVideoView.requestFocus();
    }

    private void videoUpload() {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                mDialog.hide();

                Document doc = Jsoup.parse(response);
                String src = "";

                for (org.jsoup.nodes.Element video : doc.select("video")) {
                    if(video.attr("src").contains("output")) {
                        src = video.attr("src");
                    }
                }

                if(!src.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), VideoResultActivity.class);
                    intent.putExtra(SmartUniformStatics.EXTRA_RESULT_URI, src);
                    startActivity(intent);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                mDialog.hide();
            }
        };

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, mUploadUrl, response, errorListener);

        smr.addFile("video", SmartUniformApp.getCurrTakenVideoPath());
        SmartUniformApp.getInstance().addToRequestQueue(smr);
    }
}
