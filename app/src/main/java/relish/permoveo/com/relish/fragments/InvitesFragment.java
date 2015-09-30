package relish.permoveo.com.relish.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.InviteDetailsActivity;
import relish.permoveo.com.relish.activities.InviteFlowActivity;
import relish.permoveo.com.relish.adapter.list.InvitesListAdapter;
import relish.permoveo.com.relish.animation.AnimatorPath;
import relish.permoveo.com.relish.animation.PathEvaluator;
import relish.permoveo.com.relish.animation.PathPoint;
import relish.permoveo.com.relish.interfaces.CircularRevealAnimator;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.FlurryConstantUtil;
import relish.permoveo.com.relish.util.RecyclerItemClickListener;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.util.VerticalSpaceItemDecoration;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RelishDrawerToggle;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvitesFragment extends Fragment implements RelishDrawerToggle.OnDrawerSlideListener {

    public final static float SCALE_FACTOR = 13f;
    public final static int ANIMATION_DURATION = 200;
    public final static int MINIMUN_X_DISTANCE = 200;
    private static final String TO_INVITE_DETAILS_PARAM = "to_invite_details_param";
    private static final String ACTION_PARAM = "action_param";
    private static final int INVITE_FLOW_REQUEST = 228;
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
    private CircularRevealAnimator animator;
    private String inviteId;
    private boolean action;
    private boolean fromInvite;
    private boolean mRevealFlag;
    private ObjectAnimator revealAnimator;
    private ViewPropertyAnimator fabAnimator;
    private AnimatorListenerAdapter mEndRevealListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            Log.d("onAnimationEnd", "onAnimationEnd");
            revealAnimator.removeAllListeners();
            revealAnimator.end();
            revealAnimator.cancel();
            fabAnimator.setListener(null);
            fabAnimator.cancel();


            inviteButton.setVisibility(View.INVISIBLE);
            mRevealFlag = false;
            //activity_container.setBackgroundColor(getResources()
            // .getColor(R.color.main_color));
            animator.getRevealContainer().setVisibility(View.VISIBLE);
            animator.getToolbar().setVisibility(View.INVISIBLE);

            /*for (int i = 0; i < activity_container.getChildCount(); i++) {
                View v = activity_container.getChildAt(i);
                android.view.ViewPropertyAnimator animator = v.animate()
                        .scaleX(1).scaleY(1)
                        .setDuration(ANIMATION_DURATION);

                animator.setStartDelay(i * 50);
                animator.start();
            }

            if(!alphaAnimationStarted) {
                Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(500);
                invitePager.setAnimation(in);
                alphaAnimationStarted = true;
            }*/
//            for (int i = 0; i < activity_container.getChildCount(); i++) {
//                View v = activity_container.getChildAt(i);
//                android.view.ViewPropertyAnimator animator = v.animate()
//                        .scaleX(1).scaleY(1)
//                        .setDuration(ANIMATION_DURATION);
//
//                animator.setStartDelay(i * 50);
//                animator.start();
//            }

            Intent startInviteFlow = new Intent(getActivity(), InviteFlowActivity.class);
            startInviteFlow.putExtra("isFromInviteFragment", true);

            startActivityForResult(startInviteFlow, INVITE_FLOW_REQUEST);
        }
    };

    public InvitesFragment() {
        // Required empty public constructor

    }

    public static InvitesFragment newInstance(String inviteId, boolean action) {
        InvitesFragment fragment = new InvitesFragment();
        Bundle args = new Bundle();
        args.putString(TO_INVITE_DETAILS_PARAM, inviteId);
        args.putBoolean(ACTION_PARAM, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CircularRevealAnimator)
            animator = (CircularRevealAnimator) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new InvitesListAdapter(getActivity());
        if (getArguments() != null) {
            inviteId = getArguments().getString(TO_INVITE_DETAILS_PARAM, null);
            action = getArguments().getBoolean(ACTION_PARAM, false);
        }
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
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Invite invite = (Invite) adapter.getItem(position);
                View image = view.findViewById(R.id.invite_map_snapshot);
                View title = view.findViewById(R.id.invite_title);
                ViewCompat.setTransitionName(image, InviteDetailsActivity.SHARED_IMAGE_NAME);
                ViewCompat.setTransitionName(title, InviteDetailsActivity.SHARED_TITLE_NAME);
                if (!TextUtils.isEmpty(inviteId))
                    InviteDetailsActivity.launch(getActivity(), image, title, invite, action);
                else
                    InviteDetailsActivity.launch(getActivity(), image, title, invite);
            }
        }));

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FlurryAgent.logEvent(FlurryConstantUtil.EVENT.INVITES_FRAGMENT_FAB_TOUCHED);

                animator.getInvitePager().setVisibility(View.VISIBLE);
                animator.getPageIndicator().setVisibility(View.VISIBLE);
                animator.getShareCard().setVisibility(View.GONE);

                final float startX = inviteButton.getX();

                AnimatorPath path = new AnimatorPath();
                path.moveTo(0, 0);
                path.curveTo(-200, 200, -400, 100, -600, 50);


                revealAnimator = ObjectAnimator.ofObject(InvitesFragment.this, "fabLoc",
                        new PathEvaluator(), path.getPoints().toArray());

                revealAnimator.setInterpolator(new AccelerateInterpolator());
                revealAnimator.setDuration(ANIMATION_DURATION);
                revealAnimator.start();

                revealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        inviteButton.setImageDrawable(null);
                        if (Math.abs(startX - inviteButton.getX()) > MINIMUN_X_DISTANCE) {

                            if (!mRevealFlag) {
                                animator.getActivityContainer().setY(animator.getActivityContainer().getY());

                                fabAnimator = inviteButton.animate()
                                        .scaleXBy(SCALE_FACTOR)
                                        .scaleYBy(SCALE_FACTOR)
                                        .setListener(mEndRevealListener)
                                        .setDuration(ANIMATION_DURATION);

                                mRevealFlag = true;
                            }
                        }
                    }
                });
            }
        });
        inviteButton.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fromInvite) {
            animateFromInvite();
            fromInvite = false;
        }

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

                        SharedPrefsUtil.get.setLastVisibleInvitesCount(invites.size());

                        if (!TextUtils.isEmpty(inviteId) && adapter.getChildById(inviteId) != null) {
                            startActivity(new Intent(getActivity(), InviteDetailsActivity.class)
                                    .putExtra(InviteDetailsActivity.EXTRA_INVITE, adapter.getChildById(inviteId))
                                    .putExtra(InviteDetailsActivity.EXTRA_ACTION, action));

                            inviteId = null;
                            action = false;
                        }
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

    /**
     * We need this setter to translate between the information the animator
     * produces (a new "PathPoint" describing the current animated location)
     * and the information that the button requires (an xy location). The
     * setter will be called by the ObjectAnimator given the 'fabLoc'
     * property string.
     */
    public void setFabLoc(PathPoint newLoc) {
        inviteButton.setTranslationX(newLoc.mX);

        if (mRevealFlag)
            inviteButton.setTranslationY(newLoc.mY);
        else
            inviteButton.setTranslationY(newLoc.mY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INVITE_FLOW_REQUEST) {
            fromInvite = true;

            if (data != null) {

                boolean isSent = data.getBooleanExtra(InviteFlowActivity.IS_INVITE_SENT_EXTRA, false);
                if (isSent) {
                    animator.getInvitePager().setVisibility(View.GONE);
                    animator.getPageIndicator().setVisibility(View.GONE);
                    animator.getShareCard().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void animateFromInvite() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.getRevealContainer().setVisibility(View.INVISIBLE);
                inviteButton.setScaleX(SCALE_FACTOR);
                inviteButton.setScaleY(SCALE_FACTOR);
                inviteButton.setVisibility(View.VISIBLE);

                ViewPropertyAnimator scaleAnimator = inviteButton.animate()
                        .scaleXBy(-SCALE_FACTOR + 1.0f)
                        .scaleYBy(-SCALE_FACTOR + 1.0f)
                        .setDuration(500);
                animateCurveFromInvite();
            }
        }, 500);
    }

    private void animateCurveFromInvite() {
        animator.getToolbar().setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatorPath path = new AnimatorPath();
                path.moveTo(-600, 50);
                path.curveTo(-200, 200, -400, 100, 0, 0);

                revealAnimator = ObjectAnimator.ofObject(InvitesFragment.this, "fabLoc",
                        new PathEvaluator(), path.getPoints().toArray());

                revealAnimator.setInterpolator(new DecelerateInterpolator());
                revealAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        inviteButton.setImageResource(R.drawable.ic_editor_mode_edit);
//                        Animation toolbarAnimation = new AlphaAnimation(0.0f, 1.0f);
//                        toolbarAnimation.setDuration(300);
//                        toolbarAnimation.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//
//                            }
//                        });
//                        animator.getToolbar().setAnimation(toolbarAnimation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                revealAnimator.setDuration(ANIMATION_DURATION);
                revealAnimator.start();
            }
        }, 300);
    }

}
