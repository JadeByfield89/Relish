package relish.permoveo.com.relish.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.AddFriendsPagerAdapter;
import relish.permoveo.com.relish.util.TypefaceSpan;

public class AddFriendsActivity extends RelishActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_friends_pager)
    ViewPager pager;
    @Bind(R.id.add_friends_tabs)
    PagerSlidingTabStrip tabs;
    private AddFriendsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        adapter = new AddFriendsPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    InputMethodManager inputManager = (InputMethodManager) AddFriendsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(tabs.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setOffscreenPageLimit(3);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString(getString(R.string.add_friends_title));
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        updateToolbar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
