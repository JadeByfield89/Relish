package relish.permoveo.com.relish.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.constants.YelpCategoryFilter;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 8/16/15.
 */
public class PlacesFilterFragment extends Fragment implements View.OnClickListener {


    @Bind(R.id.filter_sort_by)
    TextView headerSortBy;

    @Bind(R.id.filter_clear)
    TextView headerClear;


    @Bind(R.id.filter_done)
    TextView headerDone;

    @Bind(R.id.tv_header_distance)
    TextView headerDistance;

    @Bind(R.id.type_caribbean)
    TextView typeCaribbean;


    @Bind(R.id.type_chinese)
    TextView typeChinese;

    @Bind(R.id.type_delis)
    TextView typeDelis;

    @Bind(R.id.type_thai)
    TextView typeThai;


    @Bind(R.id.type_japanese)
    TextView typeJapanese;

    @Bind(R.id.type_buffets)
    TextView typeBuffets;

    @Bind(R.id.type_soulfood)
    TextView typeSoulfood;

    @Bind(R.id.type_brasserie)
    TextView typeBrasserie;

    @Bind(R.id.type_mexican)
    TextView typeMexican;

    @Bind(R.id.type_kosher)
    TextView typeKosher;

    @Bind(R.id.type_greek)
    TextView typeGreek;

    @Bind(R.id.type_halal)
    TextView typeHalal;

    @Bind(R.id.type_spanish)
    TextView typeSpanish;

    @Bind(R.id.type_gluten_free)
    TextView typeGlutenFree;

    @Bind(R.id.type_breakfast)
    TextView typeBreakfast;

    @Bind(R.id.type_fast_food)
    TextView typeFastFood;

    @Bind(R.id.type_seafood)
    TextView typeSeafood;

    @Bind(R.id.type_american)
    TextView typeAmerican;

    @Bind(R.id.type_vegan)
    TextView typeVegan;

    @Bind(R.id.type_vegetarian)
    TextView typeVegetarian;

    @Bind(R.id.type_asian)
    TextView typeAsian;

    @Bind(R.id.type_italian)
    TextView typeItalian;


    @Bind(R.id.type_latin)
    TextView typeLatin;

    @Bind(R.id.type_cafes)
    TextView typeCafes;

    @Bind(R.id.distance_container)
    LinearLayout distanceContainer;

    @Bind(R.id.categories_container)
    RelativeLayout categoriesContainer;

    @Bind(R.id.header_sort_by_rating)
    TextView headerSortByRating;

    private OnFilterSelectionCompleteListener mListener;
    private ArrayList<String> categoriesSelected = new ArrayList<String>();
    private int distanceSelected;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter_places, container, false);
        ButterKnife.bind(this, v);

        headerClear.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        headerClear.setOnClickListener(this);

        headerDone.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        headerDone.setOnClickListener(this);

        headerSortBy.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        headerDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        headerSortByRating.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        setListenersToCategories();
        setListenersToDistances();

        return v;
    }

    private void setListenersToDistances() {
        for (int i = 0; i < distanceContainer.getChildCount(); i++) {
            final TextView distance = (TextView) distanceContainer.getChildAt(i);
            distance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(true);
                    for (int i = 0; i < distanceContainer.getChildCount(); i++) {
                        final TextView deselect = (TextView) distanceContainer.getChildAt(i);
                        if (deselect.getId() != view.getId()) {
                            deselect.setSelected(false);
                        }
                    }
                }
            });
        }
    }

    private void setListenersToCategories() {
        for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
            TextView category = (TextView) categoriesContainer.getChildAt(i);
            category.setOnClickListener(this);
        }
    }

    private void clearCategorySelections() {
        for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
            TextView category = (TextView) categoriesContainer.getChildAt(i);
            if (category.isSelected()) {
                category.setSelected(false);
            }
        }
    }

    private void clearDistanceSelections() {
        for (int i = 0; i < distanceContainer.getChildCount(); i++) {
            TextView distance = (TextView) distanceContainer.getChildAt(i);
            if (distance.isSelected()) {
                distance.setSelected(false);
            }
        }
    }

    private void handleDistanceSelection(int selectedId) {

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.filter_clear) {
            clearCategorySelections();
            clearDistanceSelections();
            categoriesSelected.clear();
        } else if (view.getId() == R.id.filter_done) {
            mListener.onFilterSelectionComplete(categoriesSelected);
        } else {
            view.setSelected(true);
            if (view instanceof TextView) {
                YelpCategoryFilter yelpCategoryFilter = new YelpCategoryFilter();
                String categoryTitle = ((TextView) view).getText().toString();

                categoriesSelected.add(yelpCategoryFilter.lookupCategoryIdentifier(categoryTitle));
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterSelectionCompleteListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public interface OnFilterSelectionCompleteListener {

        public void onFilterSelectionComplete(ArrayList<String> categories);
    }
}
