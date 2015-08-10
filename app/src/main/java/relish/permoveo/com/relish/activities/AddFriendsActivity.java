package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.AddFriendsPagerAdapter;

public class AddFriendsActivity extends RelishActivity {

    private AddFriendsPagerAdapter adapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.add_friends_pager)
    ViewPager pager;

    @Bind(R.id.add_friends_tabs)
    PagerSlidingTabStrip tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        adapter = new AddFriendsPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        pager.setOffscreenPageLimit(3);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_friends_title));

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
}
