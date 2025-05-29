package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gmh_app.R;

public class CreditsActivity extends AppCompatActivity {

    private LinearLayout creditsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        creditsLayout = new LinearLayout(this);
        creditsLayout.setOrientation(LinearLayout.VERTICAL);
        creditsLayout.setPadding(32, 32, 32, 32);
        scrollView.addView(creditsLayout);
        setContentView(scrollView);

        addText("GMH APP CREDITS/ACKNOWLEDGEMENTS", 20, true);

        addText("This project was made possible through the collaborative efforts of multiple teams, each playing a critical role in its development and execution. We extend our deepest appreciation to:", 16, false);

        addText("App design & development", 18, true);
        addText("Interdisciplinary Centre for Digital Futures (ICDF), University of Free State", 16, false);

        addText("Development planning, Front & back-end development, UI/UX Design, Database management and Testing", 16, false);
        addText("Nkateko Nkuna", 16, true);

        addText("Front & back-end development, UI/UX Design and Testing", 16, false);
        addText("Nicholas Coetzee", 16, true);

        addText("Project management, Development planning, Design and Testing", 16, false);
        addText("Herkulaas M.V.E Combrink", 16, true);

        addText("Testing and Quality Assurance", 16, false);
        addText("Priscilla Keche, Lurgasho H. Minnie, Ayandeji Ayantokun", 16, true);

        addText("Conceptualisation and script", 18, true);
        addText("Frederick C.V.N. Fourie", 16, true);

        addText("Gamification design and badges", 18, true);
        addText("Frederick C.V.N. Fourie, Annelize Booysen-Wolthers", 16, true);

        addText("\u00A9 This app in its entirety is protected by the South African Copyright Act 98 of 1978 and other relevant South African and international legislation.", 14, false);
    }

    private void addText(String text, float size, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setPadding(0, 16, 0, 0);
        tv.setLineSpacing(1.2f, 1.2f);
        if (bold) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        creditsLayout.addView(tv);
    }
}
