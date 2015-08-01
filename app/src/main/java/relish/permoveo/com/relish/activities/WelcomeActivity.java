package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.WelcomePagerAdapter;

/**
 * Created by byfieldj on 7/31/15.
 */

public class WelcomeActivity extends RelishActivity{


    @Bind(R.id.pager_welcome)
    ViewPager viewPager;

    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;


    private WelcomePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        pagerIndicator.setViewPager(viewPager, 0);

        updateStatusBar(getResources().getColor(R.color.main_color_dark));

    }
}
