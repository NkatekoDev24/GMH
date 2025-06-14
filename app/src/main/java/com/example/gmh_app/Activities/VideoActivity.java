package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmh_app.Adapter.VideoAdapter;
import com.example.gmh_app.Models.VideoModel;
import com.example.gmh_app.R;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList;
    private List<Boolean> isViewed;
    private int currentVideoIndex = 0;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "VideoProgressPrefs";
    private static final String COMPLETION_PREFS = "VideoCompletionPrefs";
    private String sectionKey;

    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = getIntent().getParcelableArrayListExtra("video_list");
        sectionKey = getIntent().getStringExtra("section_key");

        if (videoList == null || videoList.isEmpty()) {
            videoList = new ArrayList<>();
            Toast.makeText(this, "No videos available to display.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Video list from intent is null or empty.");
        }

        isViewed = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            isViewed.add(false);
        }

        // Filter out questions if section is already completed
        if (wasSectionCompleted(sectionKey)) {
            videoList = filterOutQuestions(videoList);
        }

        // Restore last watched index
        currentVideoIndex = sharedPreferences.getInt(sectionKey, 0);
        if (currentVideoIndex >= videoList.size()) {
            currentVideoIndex = 0;
        }

        videoAdapter = new VideoAdapter(this, videoList, position -> {
            currentVideoIndex = position;
            playNextVideo();
        });
        videoRecyclerView.setAdapter(videoAdapter);

        videoRecyclerView.scrollToPosition(currentVideoIndex);

        // If revisiting Orientation, Inflows, Outflows, or Profit sections, do NOT auto-play
        if (shouldPreventAutoPlay(sectionKey) && wasSectionCompleted(sectionKey)) {
            return;
        }

        playNextVideo();
    }

    private List<VideoModel> filterOutQuestions(List<VideoModel> videos) {
        List<VideoModel> filteredVideos = new ArrayList<>();
        for (VideoModel video : videos) {
            if (!video.isQuestion()) { // Ensure this returns a boolean
                filteredVideos.add(video);
            }
        }
        return filteredVideos; // Ensure this returns List<VideoModel>
    }


    private void playNextVideo() {
        if (videoList == null || videoList.isEmpty()) {
            Toast.makeText(this, "No videos available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentVideoIndex >= videoList.size()) {
            Intent intent = new Intent(VideoActivity.this, TopicsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        VideoModel currentVideo = videoList.get(currentVideoIndex);
        if (currentVideo == null) {
            Toast.makeText(this, "Invalid video data.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isFiltered = wasSectionCompleted(sectionKey);

        if (currentVideo.isQuestion()) {
            openQuestionActivity(currentVideo);
        } else {
            openVideoPlaybackActivity(isFiltered);
        }

        isViewed.set(currentVideoIndex, true);
        sharedPreferences.edit().putInt(sectionKey, currentVideoIndex).apply();
        videoAdapter.notifyDataSetChanged();
    }


    private void openVideoPlaybackActivity(boolean isFiltered) {
        List<VideoModel> filteredVideos = isFiltered ? filterOutQuestions(videoList) : new ArrayList<>(videoList);

        if (filteredVideos.isEmpty()) {
            Toast.makeText(this, "No valid videos to play.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, VideoPlaybackActivity.class);
        intent.putParcelableArrayListExtra(VideoPlaybackActivity.EXTRA_VIDEO_LIST, new ArrayList<>(filteredVideos));
        intent.putExtra(VideoPlaybackActivity.EXTRA_VIDEO_INDEX, currentVideoIndex);
        intent.putExtra(VideoPlaybackActivity.EXTRA_IS_FILTERED, isFiltered);
        startActivityForResult(intent, VideoPlaybackActivity.REQUEST_CODE_VIDEO_PLAYBACK);
    }



    private void openQuestionActivity(VideoModel question) {
        Intent intent = new Intent(this, getQuestionActivityClass(question.getQuestionId()));
        if (intent != null) {
            startActivityForResult(intent, VideoPlaybackActivity.REQUEST_CODE_QUESTION);
        } else {
            Toast.makeText(this, "Unexpected question ID: " + question.getQuestionId(), Toast.LENGTH_SHORT).show();
        }
    }

    private void markSectionAsCompleted(String section) {
        SharedPreferences completionPrefs = getSharedPreferences(COMPLETION_PREFS, MODE_PRIVATE);
        completionPrefs.edit().putBoolean(section, true).apply();
    }

    private boolean wasSectionCompleted(String section) {
        SharedPreferences completionPrefs = getSharedPreferences(COMPLETION_PREFS, MODE_PRIVATE);
        return completionPrefs.getBoolean(section, false);
    }

    private boolean shouldPreventAutoPlay(String section) {
        return section.equals("orientation") || section.equals("inflows") ||
                section.equals("outflows") || section.equals("profit");
    }

    private void unlockSection(String section) {
        SharedPreferences completionPrefs = getSharedPreferences(COMPLETION_PREFS, MODE_PRIVATE);
        completionPrefs.edit().putBoolean(section, true).apply();
    }

    private Class<?> getQuestionActivityClass(int questionId) {
        switch (questionId) {
            case VideoModel.QUESTION_1: return IntroductionActivity.class;
            case VideoModel.QUESTION_2: return AfterVideo1Activity.class;
            case VideoModel.QUESTION_3: return UserRegistrationActivity.class;
            case VideoModel.QUESTION_4: return AfterVideo2Activity.class;
            case VideoModel.QUESTION_5: return BusinessDetailsActivity.class;
            case VideoModel.QUESTION_6: return AfterVideo3Activity.class;
            case VideoModel.QUESTION_7: return BusinessPracticesActivity.class;
            case VideoModel.QUESTION_8: return AfterVideo4Activity.class;
            case VideoModel.QUESTION_9: return EndofPart1Activity.class;
            case VideoModel.QUESTION_10: return MoneyInflowsActivity.class;
            case VideoModel.QUESTION_11: return AfterVideo5Activity.class;
            case VideoModel.QUESTION_12: return CountingMoneyInflows.class;
            case VideoModel.QUESTION_13: return AfterVideo6Activity.class;
            case VideoModel.QUESTION_14: return CorrectMoneyInflows.class;
            case VideoModel.QUESTION_15: return AfterVideo7Activity.class;
            case VideoModel.QUESTION_16: return UsingTransactionValuesToCalculateMoneyInflows.class;
            case VideoModel.QUESTION_17: return AfterVideo8Activity.class;
            case VideoModel.QUESTION_18: return OverallPart2Activity.class;
            case VideoModel.QUESTION_19: return EndofPart2Activity.class;
            case VideoModel.QUESTION_20: return BeforeVideo9Activity.class;
            case VideoModel.QUESTION_21: return AfterVideo9Activity.class;
            case VideoModel.QUESTION_22: return BeforeVideo10Activity.class;
            case VideoModel.QUESTION_23: return AfterVideo10.class;
            case VideoModel.QUESTION_24: return BeforeVideo11Activity.class;
            case VideoModel.QUESTION_25: return AfterVideo11Activity.class;
            case VideoModel.QUESTION_26: return OverallPart3Activity.class;
            case VideoModel.QUESTION_27: return EnfofPart3Activity.class;
            case VideoModel.QUESTION_28: return BeforeVideo12Activity.class;
            case VideoModel.QUESTION_29: return AfterVideo12Activity.class;
            case VideoModel.QUESTION_30: return BeforeVideo13Activity.class;
            case VideoModel.QUESTION_31: return AfterVideo13Activity.class;
            case VideoModel.QUESTION_32: return BeforeVideo14Activity.class;
            case VideoModel.QUESTION_33: return AfterVideo14Activity.class;
            case VideoModel.QUESTION_34: return BeforeVideo15Activity.class;
            case VideoModel.QUESTION_35: return AfterVideo15Activity.class;
            case VideoModel.QUESTION_36: return OverallAssessmentActivity.class;
            case VideoModel.QUESTION_37: return EndofPart4Activity.class;
            case VideoModel.QUESTION_38: return GmhBonusActivity.class;
            default:
                Log.w(TAG, "No activity found for question ID: " + questionId);
                return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            currentVideoIndex++;
            playNextVideo();
        } else {
            boolean isFirstItem = currentVideoIndex == 0;
            boolean previousIsVideo = !isFirstItem && !videoList.get(currentVideoIndex - 1).isQuestion();

            if (previousIsVideo) {
                goBackToPreviousVideo();
            } else {
                Intent intent = new Intent(VideoActivity.this, TopicsActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VideoActivity.this, TopicsActivity.class);
        startActivity(intent);
        finish();
    }


    private void goBackToPreviousVideo() {
        // Stay within bounds
        if (currentVideoIndex > 0) {
            currentVideoIndex--; // Step back
        }

        // Replay previous video only (skip question)
        VideoModel currentVideo = videoList.get(currentVideoIndex);
        if (!currentVideo.isQuestion()) {
            openVideoPlaybackActivity(wasSectionCompleted(sectionKey));
        } else {
            // If still a question, backtrack further until a video is found
            while (currentVideoIndex > 0 && videoList.get(currentVideoIndex).isQuestion()) {
                currentVideoIndex--;
            }
            // Finally play the actual video
            openVideoPlaybackActivity(wasSectionCompleted(sectionKey));
        }
    }

}
