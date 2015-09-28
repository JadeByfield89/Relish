package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.util.BlurBuilder;
import relish.permoveo.com.relish.util.FixedSpeedScroller;
import relish.permoveo.com.relish.util.SharingUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.NonSwipeableViewPager;

public class InviteFlowActivity extends RelishActivity implements PagerCallbacks, InviteCreator, OnInviteSentListener {

    public static final String PLACE_FOR_INVITE_EXTRA = "extra_place_for_invite";
    public static final String IS_INVITE_SENT_EXTRA = "extra_is_invite_sent";
    @Bind(R.id.pager_invite)
    NonSwipeableViewPager invitePager;
    @Bind(R.id.invite_place_image)
    ImageView invitePlaceImage;
    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.invite_success_title)
    TextView inviteSuccessTitle;
    @Bind(R.id.layout_success)
    RelativeLayout successLayout;
    @Bind(R.id.invite_share_card)
    CardView successCardView;
    @Bind(R.id.share_facebook)
    ImageView shareFacebook;
    @Bind(R.id.share_twitter)
    ImageView shareTwitter;
    @Bind(R.id.share_plus)
    ImageView shareGooglePlus;
    @Bind(R.id.invite_message)
    TextView inviteMessage;
    private InvitePagerAdapter invitePagerAdapter;
    private int currentStep = 0;
    private String[] TITLES;
    private YelpPlace currentPlace;
    private Invite invite;

    protected static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_flow);
        ButterKnife.bind(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(invitePager.getContext(), new LinearInterpolator());
            mScroller.set(invitePager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }


        inviteSuccessTitle.setTypeface(TypefaceUtil.BRANNBOLL_BOLD);
        inviteMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

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
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        invite = new Invite();

        if (getIntent().hasExtra(PLACE_FOR_INVITE_EXTRA)) {
            currentPlace = (YelpPlace) getIntent().getSerializableExtra(PLACE_FOR_INVITE_EXTRA);
            invite = Invite.from(currentPlace);
            getSupportActionBar().setTitle(currentPlace.name);
            if (!TextUtils.isEmpty(currentPlace.image)) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        invitePlaceImage.setImageBitmap(BlurBuilder.blur(InviteFlowActivity.this, bitmap, 10));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        invitePlaceImage.setImageBitmap(BlurBuilder.blur(InviteFlowActivity.this,
                                BitmapFactory.decodeResource(getResources(), R.drawable.login_signup_background), 10));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                invitePlaceImage.setTag(target);
                Picasso.with(this)
                        .load(currentPlace.getOriginalImage())
                        .into((Target) invitePlaceImage.getTag());
            } else {
                invitePlaceImage.setImageBitmap(BlurBuilder.blur(InviteFlowActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.default_place_image), 10));
            }
        } else {
            getSupportActionBar().setTitle(getString(R.string.invite_first_step));

            invitePlaceImage.setImageBitmap(BlurBuilder.blur(InviteFlowActivity.this,
                    BitmapFactory.decodeResource(getResources(), R.drawable.default_place_image), 10));
        }


        invitePagerAdapter = new InvitePagerAdapter(getSupportFragmentManager(), currentPlace);

        if(getIntent().hasExtra("isFromInviteFragment")){
            invitePagerAdapter.setSource(0);
        }
        else{
            invitePagerAdapter.setSource(1);
        }
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);
        invitePager.setOffscreenPageLimit(4);

        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 && currentPlace != null) {
                    getSupportActionBar().setTitle(currentPlace.name);
                } else {
                    getSupportActionBar().setTitle(TITLES[position]);
                }
                currentStep = position;
                if (getActiveFragment(invitePager, currentStep) instanceof RenderCallbacks)
                    ((RenderCallbacks) getActiveFragment(invitePager, currentStep)).render();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SharingUtil.getFacebookIntent(InviteFlowActivity.this,
                        String.format(getString(R.string.share_social), getInvite().name)));
            }
        });

        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SharingUtil.getTwitterIntent(InviteFlowActivity.this,
                        String.format(getString(R.string.share_social), getInvite().name)));
            }
        });

        shareGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SharingUtil.getPlusIntent(InviteFlowActivity.this,
                        String.format(getString(R.string.share_social), getInvite().name)));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (invitePager != null && invitePager.getCurrentItem() == 3 && getInvite().isSent) {
            successCardView.setVisibility(View.GONE);
            setResult(RESULT_CANCELED, new Intent().putExtra(IS_INVITE_SENT_EXTRA, true));
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    protected Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }

    @Override
    public void next() {
        invitePager.setCurrentItem(invitePager.getCurrentItem() + 1, true);
    }

    @Override
    public void previous() {
        if (invitePager.getCurrentItem() == 0 || (invitePager.getCurrentItem() == 3 && getInvite().isSent)) {
            setResult(RESULT_CANCELED, new Intent().putExtra(IS_INVITE_SENT_EXTRA, invitePager.getCurrentItem() == 3));
            finish();
        } else {
            invitePager.setCurrentItem(invitePager.getCurrentItem() - 1, true);
        }
    }

    @Override
    public Invite getInvite() {
        return invite;
    }

    @Override
    public void updateInvite(Invite invite) {
        this.invite = invite;
    }

    @Override
    public void onInviteSent(boolean success) {
        if (success) {
            showInviteSuccessAnimation();
        }
    }

    private void showInviteSuccessAnimation() {
        Log.d("InviteFlowActivity", "showInviteSuccessAnimation()");
        getInvite().isSent = true;
        inviteSuccessTitle.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.DropOut).duration(1000).playOn(inviteSuccessTitle);

        successCardView.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceInUp).duration(1000).playOn(successCardView);
        inviteMessage.setVisibility(View.VISIBLE);
        shareFacebook.setVisibility(View.VISIBLE);
        shareTwitter.setVisibility(View.VISIBLE);
        shareGooglePlus.setVisibility(View.VISIBLE);
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
