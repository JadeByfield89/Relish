package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.InvitePagerAdapter;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.OnInviteSentListener;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.util.BlurBehind;
import relish.permoveo.com.relish.util.FixedSpeedScroller;
import relish.permoveo.com.relish.view.NonSwipeableViewPager;

public class EditInviteActivity extends RelishActivity implements PagerCallbacks, InviteCreator, OnInviteSentListener {

    public static final String INVITE_EXTRA = "extra_invite";

    @Bind(R.id.pager_invite)
    NonSwipeableViewPager invitePager;
    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.pager_container)
//    RelativeLayout pagerContainer;

    private InvitePagerAdapter invitePagerAdapter;
    private int currentStep = 0;
    private String[] TITLES;
    private Invite invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_invite);
        ButterKnife.bind(this);

        BlurBehind.getInstance().setBackground(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(invitePager.getContext(), new LinearInterpolator());
            mScroller.set(invitePager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("current_step");
            invite = (Invite) savedInstanceState.getSerializable("current_invite");
        }

        TITLES = new String[]{getString(R.string.invite_first_step),
                getString(R.string.invite_second_step),
                getString(R.string.invite_third_step),
                getString(R.string.invite_fourth_step)};
        updateToolbar(toolbar);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.invite_first_step));
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        invite = (Invite) getIntent().getSerializableExtra(INVITE_EXTRA);
        invite.isSent = true;

        invitePagerAdapter = new InvitePagerAdapter(getSupportFragmentManager(), null);
        invitePagerAdapter.setSource(0);
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);
        invitePager.setOffscreenPageLimit(4);

        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(TITLES[position]);
                currentStep = position;
                if (getActiveFragment(invitePager, currentStep) instanceof RenderCallbacks)
                    ((RenderCallbacks) getActiveFragment(invitePager, currentStep)).render();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        YoYo.with(Techniques.SlideInUp)
//                .delay(1000)
//                .withListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        invitePager.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })
//                .playOn(invitePager);
    }

    protected static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    protected Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_step", currentStep);
        outState.putSerializable("current_invite", invite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            previous();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Invite getInvite() {
        return invite;
    }

    @Override
    public void updateInviteWithGooglePlace(GooglePlace place) {
        this.invite.updateWithGooglePlace(place);
    }

    @Override
    public void onInviteSent(boolean success) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void next() {
        invitePager.setCurrentItem(invitePager.getCurrentItem() + 1, true);
    }

    @Override
    public void previous() {
        if (invitePager.getCurrentItem() == 0) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            invitePager.setCurrentItem(invitePager.getCurrentItem() - 1, true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("InviteFlowAcitivty", "InviteflowActivity onActivityResult");
    }
}
