package relish.permoveo.com.relish.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.AddFriendsActivity;
import relish.permoveo.com.relish.adapter.list.FriendsListAdapter;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;
    @Bind(R.id.empty_message)
    TextView emptyMessage;
    @Bind(R.id.bounce_progress)
    BounceProgressBar bounceProgressBar;
    @Bind(R.id.friends_list_recycler)
    RecyclerView recyclerView;
    @Bind(R.id.add_button)
    Button addButton;
    private FriendsListAdapter adapter;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FriendsListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, v);
        //emptyMessage.setText(String.format(getString(R.string.friends_list_empty), group, group));
        emptyMessage.setText(getString(R.string.friends_list_empty));
        addButton.setText("Add Friends");
        addButton.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFriendsActivity.class));
            }
        });

        emptyMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emptyMessage.setIncludeFontPadding(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        FriendsManager.retrieveFriendsList(new FriendsManager.FriendsManagerCallback<ArrayList<Friend>, ParseException>() {
            @Override
            public void done(ArrayList<Friend> friends, ParseException e) {
                if (e == null) {
                    if (friends.size() > 0) {
                        emptyView.setVisibility(View.GONE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.swap(friends);
                    } else {
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
