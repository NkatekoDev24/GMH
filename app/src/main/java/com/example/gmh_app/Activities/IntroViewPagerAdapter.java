package com.example.gmh_app.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.gmh_app.R;
import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<ScreenItems> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItems> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View layoutScreen = LayoutInflater.from(mContext).inflate(R.layout.layout_screen, container, false);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        //TextView description = layoutScreen.findViewById(R.id.intro_description);

        ScreenItems screenItem = mListScreen.get(position);
        imgSlide.setImageResource(screenItem.getScreenImg());
        title.setText(screenItem.getTitle());
        //description.setText(screenItem.getDescription());

        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
