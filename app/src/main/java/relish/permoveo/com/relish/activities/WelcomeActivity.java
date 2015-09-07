package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.WelcomePagerAdapter;
import relish.permoveo.com.relish.util.FixedSpeedScroller;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 7/31/15.
 */

public class WelcomeActivity extends RelishActivity implements ViewPager.OnPageChangeListener {


    @Bind(R.id.pager_welcome)
    ViewPager viewPager;

    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;

    @Bind(R.id.textview_skip)
    TextView skipText;

    @Bind(R.id.button_back)
    ImageButton backButton;

    @Bind(R.id.button_next)
    ImageButton nextButton;

    @Bind(R.id.textview_ok)
    TextView okText;

    private final int MAX_PAGE_INDEXES = 3;


    private WelcomePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new LinearInterpolator());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }

        pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        pagerIndicator.setOnPageChangeListener(this);
        pagerIndicator.setViewPager(viewPager, 0);

        skipText.setTypeface(TypefaceUtil.PROXIMA_NOVA);


        updateStatusBar(getResources().getColor(R.color.main_color_dark));



    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 3) {
            skipText.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);


            okText.setVisibility(View.VISIBLE);

            backButton.setVisibility(View.VISIBLE);
        } else {
            okText.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);

            nextButton.setVisibility(View.VISIBLE);
            skipText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @OnClick(R.id.button_next)
    public void goToNextPage() {
        int currentPage = viewPager.getCurrentItem();
        if (currentPage < MAX_PAGE_INDEXES) {
            viewPager.setCurrentItem(currentPage + 1, true);
        }
    }

    @OnClick(R.id.button_back)
    public void goToPreviousPage() {
        int currentPage = viewPager.getCurrentItem();

        if (currentPage > 0) {
            viewPager.setCurrentItem(currentPage - 1, true);
        }
    }

    @OnClick(R.id.textview_skip)
    public void skipPages() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.textview_ok)
    public void finishWalkthrough() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }


}
