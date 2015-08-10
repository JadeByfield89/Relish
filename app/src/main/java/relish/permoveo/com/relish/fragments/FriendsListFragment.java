package relish.permoveo.com.relish.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private static final String FRIENDS_GROUP = "friends_group";

    private String group;


    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

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
        emptyMessage.setText(String.format(getString(R.string.friends_list_empty), group));
        emptyMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emptyMessage.setIncludeFontPadding(false);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        FriendsManager.retrieveFriendsList(group.toLowerCase(), new FriendsManager.FriendsManagerCallback<ArrayList<Friend>, java.text.ParseException>() {
            @Override
            public void done(ArrayList<Friend> friends, java.text.ParseException e) {
                if (friends.size() > 0) {

                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }
}
