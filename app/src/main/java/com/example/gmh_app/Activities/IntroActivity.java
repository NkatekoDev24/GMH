package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.gmh_app.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private TabLayout tabIndicator;
    private Button btnNext, btnBack, btnGetStarted;
    private TextView tvSkip;
    private Animation btnAnim;
    private int position = 0;
    private List<ScreenItems> mlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trueFullscreen();
        setContentView(R.layout.activity_intro);

        initViews();
        mlist = getScreenItems();
        setupViewPager();
        setupListeners();
    }

    private void initViews() {
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnGetStarted = findViewById(R.id.btn_getstarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        tvSkip = findViewById(R.id.tv_skip);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttom_animation);
    }

    private void setupViewPager() {
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mlist);
        screenPager.setAdapter(introViewPagerAdapter);
        tabIndicator.setupWithViewPager(screenPager);
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> navigateNext());
        btnBack.setOnClickListener(v -> navigateBack());
        tvSkip.setOnClickListener(v -> skipIntro());
        btnGetStarted.setOnClickListener(v -> startMainActivity());

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private List<ScreenItems> getScreenItems() {
        List<ScreenItems> mList = new ArrayList<>();
        mList.add(new ScreenItems(R.drawable.screen1, "How to Pause a Video", "To pause, tap the screen to show the PAUSE symbol, then tap it again to stop."));
        mList.add(new ScreenItems(R.drawable.screen2, "How to Move through the Videos", "Use the slider to move forward or backward in the video."));
        mList.add(new ScreenItems(R.drawable.screen3, "Jump to Previous/Next Video", "Tap the JUMP symbols to navigate between previously watched videos."));
        return mList;
    }

    private void trueFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void navigateNext() {
        position = screenPager.getCurrentItem();
        if (position < mlist.size() - 1) {
            screenPager.setCurrentItem(++position);
        }
        updateScreenState();
    }

    private void navigateBack() {
        position = screenPager.getCurrentItem();
        if (position > 0) {
            screenPager.setCurrentItem(--position);
        }
        updateScreenState();
    }

    private void skipIntro() {
        screenPager.setCurrentItem(mlist.size());
        loadLastScreen();
    }

    private void startMainActivity() {
        startActivity(new Intent(getApplicationContext(), TopicsActivity.class));
        finish();
    }

    private void handleTabSelection(int position) {
        screenPager.setCurrentItem(position);
        updateScreenState();
    }

    private void updateScreenState() {
        position = screenPager.getCurrentItem();
        if (position == mlist.size() - 1) {
            loadLastScreen();
        } else if (position == 0) {
            loadFirstScreen();
        } else {
            loadMiddleScreen();
        }
    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.startAnimation(btnAnim);
    }

    private void loadMiddleScreen() {
        btnNext.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tvSkip.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);
    }

    private void loadFirstScreen() {
        btnNext.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tvSkip.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);
    }
}
