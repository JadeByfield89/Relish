package relish.permoveo.com.relish.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private static final String FRIENDS_GROUP = "friends_group";

    private String group;


    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;

    @Bind(R.id.friends_list_progress)
    ProgressWheel progress;

    public static FriendsListFragment newInstance(String group) {
        FriendsListFragment friendsListFragment = new FriendsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRIENDS_GROUP, group);
        friendsListFragment.setArguments(bundle);
        return friendsListFragment;
    }

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getString(FRIENDS_GROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final ParseUser user = (ParseUser) parseObject;
                ArrayList<String> friendsIds = (ArrayList<String>) user.get(group.toLowerCase() + "Group");
                if (friendsIds == null || friendsIds.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                } else {
                }
            }
        });
    }
}
