package relish.permoveo.com.relish.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.InvitesListAdapter;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.util.VerticalSpaceItemDecoration;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RelishDrawerToggle;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvitesFragment extends Fragment implements RelishDrawerToggle.OnDrawerSlideListener {

    @Bind(R.id.invites_list_view)
    RecyclerView recyclerView;

    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progressBar;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

    @Bind(R.id.fab_invite)
    FloatingActionButton inviteButton;

    private InvitesListAdapter adapter;

    public InvitesFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new InvitesListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        emptyMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emptyMessage.setIncludeFontPadding(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        int spaces = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14.0f, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spaces));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        InvitesManager.retrieveInvitesList(new InvitesManager.InvitesManagerCallback<ArrayList<Invite>, ParseException>() {
            @Override
            public void done(ArrayList<Invite> invites, ParseException e) {
                if (e == null) {
                    if (invites.size() > 0) {
                        emptyView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.swap(invites);
                    } else {
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void OnDrawerSliding(float slideOffset) {
        if (inviteButton != null) {
            inviteButton.setScaleX(1 - (slideOffset));
            inviteButton.setScaleY(1 - (slideOffset));
        }
    }


}
