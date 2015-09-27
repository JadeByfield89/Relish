package relish.permoveo.com.relish.interfaces;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by rom4ek on 25.09.2015.
 */
public interface CircularRevealAnimator {
    ViewGroup getActivityContainer();

    ViewGroup getRevealContainer();

    Toolbar getToolbar();

    CirclePageIndicator getPageIndicator();

    CardView getShareCard();

    ViewPager getInvitePager();
}
