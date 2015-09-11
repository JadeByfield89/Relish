package relish.permoveo.com.relish.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.InvitesAdapter;
import relish.permoveo.com.relish.adapter.list.InvitesListAdapter;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RelishDrawerToggle;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvitesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener, RelishDrawerToggle.OnDrawerSlideListener {

    @Bind(R.id.touch_interceptor_view)
    View touchInterceptorView;

    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;

    //@Bind(R.id.cover_view)
    //View coverView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progressBar;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

    @Bind(R.id.details_layout)
    View detailsView;

    @Bind(R.id.unfoldable_view)
    UnfoldableView unfoldableView;

//    @Bind(R.id.invites_list_view)
//    DragSortListView invitesListView;

    @Bind(R.id.fab_invite)
    FloatingActionButton inviteButton;

    private InvitesListAdapter listAdapter;

    private static final float SCALE_FACTOR = 0.0f;
    private float previousOffset;

    private boolean previousOffsetStored;
    private boolean fabReadyToAnimate;

    @Bind(R.id.invites_list_view)
    ListView invitesListView;


    private InvitesAdapter invitesAdapter;

    private boolean isUnfolded;
    private boolean animated;

    private boolean drawerClosed;

    public InvitesFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_invites, container, false);
        ButterKnife.bind(this, v);

        invitesAdapter = new InvitesAdapter(getActivity());
        touchInterceptorView.setClickable(false);

        //Test Invite
        //ArrayList<Invite> invites = new ArrayList<Invite>();
        //Invite invite = new Invite();
        //invitesAdapter.add(invite);
        //invites.add(invite);

        //listAdapter = new InvitesListAdapter(invites, getActivity());
        //invitesListView.setAdapter(listAdapter);
       // invitesListView.setOnItemClickListener(this);


//        SimpleFloatViewManager simpleFloatViewManager = new SimpleFloatViewManager(invitesListView);
//        simpleFloatViewManager.setBackgroundColor(Color.TRANSPARENT);
//        invitesListView.setFloatViewManager(simpleFloatViewManager);
        //invitesListView.setDragListener(this);
        //invitesListView.setOnItemLongClickListener(this);

        detailsView.setVisibility(View.INVISIBLE);
        detailsView.setOnClickListener(this);

        Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        unfoldableView.setFoldShading(new GlanceFoldShading(getActivity(), glance));

        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
                detailsView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
                detailsView.setVisibility(View.VISIBLE);
                isUnfolded = true;
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                touchInterceptorView.setClickable(false);
                detailsView.setVisibility(View.INVISIBLE);
            }
        });

        emptyMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emptyMessage.setIncludeFontPadding(false);

        setFabReadyToAnimation(true);
        return v;
    }

    @OnClick(R.id.touch_interceptor_view)
    public void openDetails(View itemView) {
        unfoldableView.unfold(itemView, detailsView);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ImageView image = (ImageView) view.findViewById(R.id.invite_map_image);
        openDetails(image);
    }

    @Override
    public void onClick(View view) {
        if (isUnfolded) {
            unfoldableView.foldBack();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//        invitesListView.setDragEnabled(true);
        //invitesListView.startDrag(view, 0, view.getX(), view.getY());
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setFabReadyToAnimation(boolean ready) {
        fabReadyToAnimate = true;
    }

    @Override
    public void OnDrawerSliding(float slideOffset) {

        if (inviteButton != null) {
            inviteButton.setScaleX(1 - (slideOffset));
            inviteButton.setScaleY(1 - (slideOffset));


        }


    }


}
