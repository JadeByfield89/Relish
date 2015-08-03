package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.API;

public class PlaceDetailsActivity extends RelishActivity {

    public static final String PLACE_ID = "place_id_extra";
    public static final String PLACE_DISTANCE = "place_distance_extra";

    private String placeId;
    private String placeDistance;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(PLACE_ID))
            placeId = getIntent().getStringExtra(PLACE_ID);
        if (getIntent().hasExtra(PLACE_DISTANCE))
            placeDistance = getIntent().getStringExtra(PLACE_DISTANCE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getResources().getColor(R.color.main_color_dark);
    }


    @Override
    protected void onResume() {
        super.onResume();
        API.getYelpPlaceDetails(placeId, new IRequestable() {
            @Override
            public void completed(Object... params) {

            }

            @Override
            public void failed(Object... params) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
