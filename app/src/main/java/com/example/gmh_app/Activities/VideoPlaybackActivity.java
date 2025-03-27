package com.example.gmh_app.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.gmh_app.Models.VideoModel;
import com.example.gmh_app.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import java.util.ArrayList;

public class VideoPlaybackActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_LIST = "EXTRA_VIDEO_LIST";
    public static final String EXTRA_VIDEO_INDEX = "EXTRA_VIDEO_INDEX";
    public static final String EXTRA_IS_FILTERED = "EXTRA_IS_FILTERED";  // New flag to check if filtered list is active
    public static final int REQUEST_CODE_VIDEO_PLAYBACK = 1;
    public static final int REQUEST_CODE_QUESTION = 2;

    private ArrayList<VideoModel> videoList;
    private int currentVideoIndex;
    private boolean isFiltered;  // Track if we're using the filtered list

    private SimpleExoPlayer simpleExoPlayer;
    private ProgressBar progressBar;
    private ImageView exoSkipForward, exoSkipBack, exoPlay, exoPause, exoReplay;

    private int furthestWatchedIndex; // New variable to track the furthest point the user reached

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);

        PlayerView playerView = findViewById(R.id.player);
        progressBar = findViewById(R.id.progress_bar);
        exoSkipForward = findViewById(R.id.exo_skip_forward);
        exoSkipBack = findViewById(R.id.exo_skip_back);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        exoReplay = findViewById(R.id.exo_replay);

        exoReplay.setVisibility(View.GONE);

        videoList = getIntent().getParcelableArrayListExtra(EXTRA_VIDEO_LIST);
        currentVideoIndex = getIntent().getIntExtra(EXTRA_VIDEO_INDEX, 0);
        isFiltered = getIntent().getBooleanExtra(EXTRA_IS_FILTERED, false);

        // Initialize furthestWatchedIndex
        furthestWatchedIndex = currentVideoIndex;

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);

        loadVideo();

        exoSkipForward.setOnClickListener(v -> skipToNextVideo());
        exoSkipBack.setOnClickListener(v -> skipToPreviousVideo());
        exoReplay.setOnClickListener(v -> replayVideo());

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }

                if (playbackState == Player.STATE_ENDED) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void loadVideo() {
        // Check if index is valid
        if (videoList == null || videoList.isEmpty() || currentVideoIndex < 0 || currentVideoIndex >= videoList.size()) {
            // If invalid, reset to the first video and prevent further errors
            currentVideoIndex = 0;
            showAlertDialog("Error", "Invalid video position. Returning to the first video.");
            return;
        }

        // Update the furthest watched position
        furthestWatchedIndex = Math.max(furthestWatchedIndex, currentVideoIndex);

        VideoModel currentVideo = videoList.get(currentVideoIndex);
        if (currentVideo == null || currentVideo.getVideoUri() == null || currentVideo.getVideoUri().isEmpty()) {
            showAlertDialog("Error", "Invalid video URL.");
            return;
        }

        Uri videoUri = Uri.parse(currentVideo.getVideoUri());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();

        // Ensure correct button visibility
        exoPlay.setVisibility(View.VISIBLE);
        exoPause.setVisibility(View.VISIBLE);
        exoReplay.setVisibility(View.GONE);
    }

    private void skipToNextVideo() {
        if (isFiltered) {
            // Filtered mode: Skip by 1
            if (currentVideoIndex + 1 < videoList.size()) {
                currentVideoIndex += 1;
                loadVideo();
            }
        } else {
            // Original mode: Try skipping forward up to 3 steps to a viewed video
            int maxSkip = Math.min(3, videoList.size() - currentVideoIndex - 1);
            int nextIndex = currentVideoIndex;

            // Find the closest viewed video or the furthest watched
            for (int i = 1; i <= maxSkip; i++) {
                int checkIndex = currentVideoIndex + i;
                if (checkIndex <= furthestWatchedIndex || videoList.get(checkIndex).isCompleted()) {
                    nextIndex = checkIndex;
                }
            }

            if (nextIndex > currentVideoIndex) {
                currentVideoIndex = nextIndex;
                loadVideo();
            } else {
                showAlertDialog("Access Restricted", "You cannot skip to an unviewed video.");
            }
        }
    }

    private void skipToPreviousVideo() {
        // Prevent skipping back if already on the first video
        if (currentVideoIndex <= 0) {
            showAlertDialog("Error", "You are already on the first video.");
            return;
        }

        if (isFiltered) {
            // Filtered mode: Skip back by 1
            currentVideoIndex -= 1;
            loadVideo();
        } else {
            // Original mode: Skip back by 3 videos if possible
            int previousIndex = Math.max(0, currentVideoIndex - 3);
            if (previousIndex < currentVideoIndex) {
                currentVideoIndex = previousIndex;
                loadVideo();
            }
        }
    }

    // Helper method to display an AlertDialog
    private void showAlertDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // Prevents dialog from being dismissed by outside clicks
                .show();
    }


    private void replayVideo() {
        simpleExoPlayer.seekTo(0);
        simpleExoPlayer.play();

        exoPlay.setVisibility(View.VISIBLE);
        exoPause.setVisibility(View.VISIBLE);
        exoReplay.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
}
