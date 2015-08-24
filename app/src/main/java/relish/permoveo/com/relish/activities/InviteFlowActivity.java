package relish.permoveo.com.relish.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.InvitePagerAdapter;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.util.BlurBuilder;

public class InviteFlowActivity extends RelishActivity implements PagerCallbacks{

    public static final String PLACE_FOR_INVITE_EXTRA = "extra_place_for_invite";

    private InvitePagerAdapter invitePagerAdapter;
    private int currentStep = 0;

    @Bind(R.id.pager_invite)
    ViewPager invitePager;

    @Bind(R.id.invite_place_image)
    ImageView invitePlaceImage;

    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String[] TITLES;
    private YelpPlace currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_flow);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("current_step");
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
        getSupportActionBar().setTitle(TITLES[currentStep]);

        if (getIntent().hasExtra(PLACE_FOR_INVITE_EXTRA)) {
            currentPlace = (YelpPlace) getIntent().getSerializableExtra(PLACE_FOR_INVITE_EXTRA);
            if (!TextUtils.isEmpty(currentPlace.image)) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        invitePlaceImage.setImageBitmap(BlurBuilder.fastblur(bitmap, 10));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        invitePlaceImage.setBackgroundColor(getResources().getColor(R.color.main_color));
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
                invitePlaceImage.setBackgroundColor(getResources().getColor(R.color.main_color));
            }
        } else {
            invitePlaceImage.setBackgroundColor(getResources().getColor(R.color.main_color));
        }

        invitePagerAdapter = new InvitePagerAdapter(getSupportFragmentManager(), currentPlace);
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);

        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(TITLES[position]);
                currentStep = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_step", currentStep);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void next() {
        invitePager.setCurrentItem(invitePager.getCurrentItem() + 1, true);
    }

    @Override
    public void previous() {

    }
}
