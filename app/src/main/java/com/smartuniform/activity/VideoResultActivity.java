package com.smartuniform.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.misc.AsyncTask;
import com.smartuniform.SmartUniformApp;
import com.smartuniform.SmartUniformStatics;
import com.smartuniform.R;

import java.io.File;
import static java.lang.Thread.sleep;

public class VideoResultActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private String mVideoUri = "";
    private Context mContext = null;
    private BroadcastReceiver onDownloadComplete = null;
    private long mDownloadId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mVideoView = findViewById(R.id.videoView);
        mVideoUri = SmartUniformApp.getServerUrl() + getIntent().getStringExtra(SmartUniformStatics.EXTRA_RESULT_URI).substring(2);
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mDownloadId == id) {
                    Toast.makeText(mContext, "Download Completed", Toast.LENGTH_SHORT).show();
                    setResultVideo();
                }
            }
        };
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadResulVideo();
    }

    private void downloadResulVideo() {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(mVideoUri);
        String filename = mVideoUri.substring(mVideoUri.lastIndexOf(File.separator) + 1);
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "SmartUniformOutput");
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                return;
            }
        }
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "SmartUniformOutput" + File.separator  + filename);
        mDownloadId = downloadmanager.enqueue(request);
    }

    private void setResultVideo() {
            String path = SmartUniformApp.getCurrTakenVideoPath();
            Uri uri = Uri.parse(path);
            mVideoView.setVideoURI(uri);
            mVideoView.seekTo( 1 );

            MediaController mediaController = new MediaController(this);
            mVideoView.setMediaController(mediaController);
            mediaController.setAnchorView(mVideoView);
            mVideoView.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
