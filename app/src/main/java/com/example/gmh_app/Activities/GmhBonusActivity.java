package com.example.gmh_app.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class GmhBonusActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "VideoCompletionPrefs";
    private static final String KEY_OUTFLOWS_COMPLETED = "profit";
    private Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gmh_bonus);

        btnComplete = findViewById(R.id.btn_complete);

        SharedPreferences certPrefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE);
        boolean alreadyUnlocked = certPrefs.getBoolean("all_videos_watched", false);

        if (alreadyUnlocked) {
            btnComplete.setVisibility(View.GONE);
        } else {
            btnComplete.setVisibility(View.VISIBLE);

            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_OUTFLOWS_COMPLETED, true);
                    editor.apply();

                    certPrefs.edit().putBoolean("all_videos_watched", true).apply();

                    String messageHtml = "We would like to contact you again later about these videos. For example, we might want to ask you how the videos helped your business grow, increase profits or create jobs – or whether you have further training needs. <br><br>" +
                            "Such feedback is important to us, since <b>we realise that it can take several months or even a year or more for regular higher profits to be earned</b> after you start to improve your money and other management habits. <b>Please be patient and persist with your new good money habits.</b><br><br>" +
                            "We promise we won't share your information with anyone outside our research team. " +
                            "And if you decide later that you don’t want to participate any more, you can withdraw.<br><br>" +
                            "Is it okay if we contact you in the future about this?";

                    Spanned formattedMessage = Html.fromHtml(messageHtml, Html.FROM_HTML_MODE_LEGACY);

                    new AlertDialog.Builder(GmhBonusActivity.this)
                            .setTitle("Keeping in touch")
                            .setMessage(formattedMessage)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnComplete.setVisibility(View.GONE);
                                    startActivity(new Intent(GmhBonusActivity.this, TopicsActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    btnComplete.setVisibility(View.GONE);
                                    startActivity(new Intent(GmhBonusActivity.this, TopicsActivity.class));
                                    finish();
                                }
                            })
                            .show();
                }
            });
        }
    }
}
