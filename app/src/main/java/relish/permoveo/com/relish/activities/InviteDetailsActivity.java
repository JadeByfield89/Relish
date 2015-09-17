package relish.permoveo.com.relish.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.RatingView;

public class InviteDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks {

    public static final String EXTRA_INVITE = "invite_extra";
    public static final String SHARED_IMAGE_NAME = "InviteDetailsActivity:image";
    public static final String SHARED_TITLE_NAME = "InviteDetailsActivity:title";

    private Invite invite;
    private int parallaxImageHeight;
    private View currentViewInClmn;

    @Bind(R.id.invite_details_container)
    RelativeLayout activity_container;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.header_invite_details_image)
    ImageView placeDetailsImage;

    @Bind(R.id.invite_details_scroll_view)
    ObservableScrollView placeDetalsScrollView;

    @Bind(R.id.invite_details_rating_view)
    RatingView ratingView;

    @Bind(R.id.invite_details_address)
    TextView placeDetailsAddress;

    @Bind(R.id.invite_details_phone)
    TextView placeDetailsPhone;

    @Bind(R.id.invite_details_name)
    TextView placeDetailsName;

    @Bind(R.id.yelp_logo)
    ImageView yelpLogo;

    @Bind(R.id.accept_btn)
    TextView acceptBtn;

    @Bind(R.id.decline_btn)
    TextView declineBtn;

    @Bind(R.id.invited_clmn)
    LinearLayout invitedClmnn;

    @Bind(R.id.accepted_clmn)
    LinearLayout acceptedClmn;

    @Bind(R.id.declined_clmn)
    LinearLayout declinedClmn;

    @Bind(R.id.invited_title)
    TextView invitedTitle;

    @Bind(R.id.accepted_title)
    TextView acceptedTitle;

    @Bind(R.id.declined_title)
    TextView declinedTitle;

    @Bind(R.id.invite_details_note)
    TextView inviteDetailsNote;

    @Bind(R.id.invite_details_note_title)
    TextView inviteDetailsNoteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

//            getWindow().setEnterTransition(new ());
//            getWindow().setExitTransition(new Slide());

//            getWindow().setSharedElementEnterTransition(new ChangeImageTransform());
//            getWindow().setSharedElementExitTransition(new ChangeImageTransform());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15.0f);

        ViewCompat.setTransitionName(getActionBarTextView(), SHARED_TITLE_NAME);
        ViewCompat.setTransitionName(placeDetailsImage, SHARED_IMAGE_NAME);
        if (savedInstanceState != null) {
            invite = (Invite) savedInstanceState.getSerializable(EXTRA_INVITE);
        } else if (getIntent().hasExtra(EXTRA_INVITE)) {
            invite = (Invite) getIntent().getSerializableExtra(EXTRA_INVITE);
        }

        SpannableString s = new SpannableString(invite.title);
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.invite_image_size);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        updateToolbar(toolbar);

        yelpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(invite.url))
                    openWebView();
            }
        });
    }

    private void openWebView() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(invite.url));
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderPlaceDetails();
    }

    private void renderPlaceDetails() {
        if (TextUtils.isEmpty(invite.image)) {
            Picasso.with(this)
                    .load(invite.mapSnapshot)
                    .into(placeDetailsImage);
//            placeDetailsImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
        } else {
            Picasso.with(this)
                    .load(invite.image)
                    .into(placeDetailsImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
//                            placeDetailsImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
                        }
                    });
        }

        placeDetailsName.setText(invite.name);
        placeDetailsName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        placeDetailsAddress.setText(invite.location.address);
        placeDetailsAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        if (!TextUtils.isEmpty(invite.phone)) {
            placeDetailsPhone.setVisibility(View.VISIBLE);
            placeDetailsPhone.setPaintFlags(placeDetailsPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(invite.phone);
            PhoneNumberUtils.formatNumber(stringBuilder, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
            placeDetailsPhone.setText(stringBuilder.toString());
            placeDetailsPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            placeDetailsPhone.setIncludeFontPadding(false);
            placeDetailsPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + invite.phone));
                    InviteDetailsActivity.this.startActivity(intent);
                }
            });
        } else {
            placeDetailsPhone.setVisibility(View.GONE);
        }
        ratingView.setLarge(true);
        ratingView.setRating(invite.rating);

        if (!TextUtils.isEmpty(invite.note)) {
            inviteDetailsNote.setVisibility(View.VISIBLE);
            inviteDetailsNoteTitle.setVisibility(View.VISIBLE);
            inviteDetailsNote.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            inviteDetailsNoteTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

            inviteDetailsNote.setText(invite.note);
        } else {
            inviteDetailsNote.setVisibility(View.GONE);
            inviteDetailsNoteTitle.setVisibility(View.GONE);
        }

        renderGrid();
        renderButtons();
//        placeDetalsScrollView.setOnTouchListener(null);
        placeDetalsScrollView.setScrollViewCallbacks(this);
    }

    private void renderGrid() {
        declinedTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        acceptedTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        invitedTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        if (!ParseUser.getCurrentUser().getObjectId().equals(invite.creatorId)) {
            declinedClmn.setVisibility(View.GONE);
        } else {
            declinedClmn.setVisibility(View.VISIBLE);
            renderColumn(declinedClmn, invite.declined);
        }

        renderColumn(acceptedClmn, invite.accepted);
        renderColumn(invitedClmnn, invite.invited);
    }

    private void renderColumn(ViewGroup column, ArrayList<InvitePerson> persons) {
        for (int i = column.getChildCount() - 1; i > 0; i--) {
            column.removeViewAt(i);
        }

        if (persons.size() > 0) {
            for (InvitePerson person : persons) {
                View result = addInvitePerson(column, person);
                if (person.id.equals(ParseUser.getCurrentUser().getObjectId()))
                    currentViewInClmn = result;
            }
        } else {
            addInvitePerson(column, new InvitePerson());
        }
    }

    private View addInvitePerson(ViewGroup container, InvitePerson person) {
        return addInvitePerson(container, person, false);
    }

    private View addInvitePerson(ViewGroup container, InvitePerson person, boolean isInvisible) {
        View invitePersonView = getLayoutInflater().inflate(R.layout.invite_grid_item, null);
        TextView personName = (TextView) invitePersonView.findViewById(R.id.invite_person_name);
        CircleImageView personImage = (CircleImageView) invitePersonView.findViewById(R.id.invite_person_image);

        personName.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        if (!TextUtils.isEmpty(person.image)) {
            Picasso.with(this)
                    .load(person.image).placeholder(R.drawable.relish_avatar_placeholder).fit().centerCrop()
                    .into(personImage);
        }

        if (!TextUtils.isEmpty(person.name)) {
            personName.setText(person.name);
        } else {
            personName.setText("—");
            personImage.setVisibility(View.GONE);
        }

        if (isInvisible)
            invitePersonView.setVisibility(View.GONE);

        container.addView(invitePersonView);
        return invitePersonView;
    }

    private void renderButtons() {
        acceptBtn.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        declineBtn.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvitesManager.acceptInvite(invite, new InvitesManager.InvitesManagerCallback<Boolean, ParseException>() {
                    @Override
                    public void done(Boolean success, ParseException e) {
                        if (success) {
                            switch (invite.status) {
                                case PENDING:
                                    YoYo.with(Techniques.FadeOut)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    acceptBtn.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(600)
                                            .playOn(acceptBtn);
                                    YoYo.with(Techniques.FadeOut)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    currentViewInClmn.setVisibility(View.GONE);
                                                    invitedClmnn.removeView(currentViewInClmn);
                                                    if (invitedClmnn.getChildCount() == 1) {
                                                        addInvitePerson(invitedClmnn, new InvitePerson());
                                                    }
                                                    acceptedClmn.addView(currentViewInClmn);
                                                    YoYo.with(Techniques.FadeIn)
                                                            .withListener(new Animator.AnimatorListener() {
                                                                @Override
                                                                public void onAnimationStart(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationEnd(Animator animation) {
                                                                    currentViewInClmn.setVisibility(View.VISIBLE);
                                                                }

                                                                @Override
                                                                public void onAnimationCancel(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animator animation) {

                                                                }
                                                            })
                                                            .duration(600)
                                                            .playOn(currentViewInClmn);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(600)
                                            .playOn(currentViewInClmn);
                                    break;
                                case DECLINED:
                                    YoYo.with(Techniques.SlideOutRight)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    acceptBtn.setVisibility(View.GONE);
                                                    YoYo.with(Techniques.SlideInRight)
                                                            .duration(400)
                                                            .withListener(new Animator.AnimatorListener() {
                                                                @Override
                                                                public void onAnimationStart(Animator animation) {
                                                                    declineBtn.setVisibility(View.VISIBLE);
                                                                }

                                                                @Override
                                                                public void onAnimationEnd(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationCancel(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animator animation) {

                                                                }
                                                            })
                                                            .playOn(declineBtn);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(400)
                                            .playOn(acceptBtn);
                                    currentViewInClmn = addInvitePerson(acceptedClmn, invite.getPersonById(ParseUser.getCurrentUser().getObjectId()), true);
                                    YoYo.with(Techniques.FadeIn)
                                            .duration(600)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    currentViewInClmn.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .playOn(currentViewInClmn);
                                    break;
                            }

                            invite.status = Invite.InviteStatus.ACCEPTED;
                        } else {
                            Snackbar.make(activity_container, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvitesManager.declineInvite(invite, new InvitesManager.InvitesManagerCallback<Boolean, ParseException>() {
                    @Override
                    public void done(Boolean success, ParseException e) {
                        if (success) {
                            switch (invite.status) {
                                case PENDING:
                                    YoYo.with(Techniques.FadeOut)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    declineBtn.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(600)
                                            .playOn(declineBtn);
                                    YoYo.with(Techniques.FadeOut)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    invitedClmnn.removeView(currentViewInClmn);
                                                    if (invitedClmnn.getChildCount() == 1) {
                                                        addInvitePerson(invitedClmnn, new InvitePerson());
                                                    }
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(600)
                                            .playOn(currentViewInClmn);
                                    break;
                                case ACCEPTED:
                                    YoYo.with(Techniques.SlideOutRight)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    declineBtn.setVisibility(View.GONE);
                                                    YoYo.with(Techniques.SlideInRight)
                                                            .duration(400)
                                                            .withListener(new Animator.AnimatorListener() {
                                                                @Override
                                                                public void onAnimationStart(Animator animation) {
                                                                    acceptBtn.setVisibility(View.VISIBLE);
                                                                }

                                                                @Override
                                                                public void onAnimationEnd(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationCancel(Animator animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animator animation) {

                                                                }
                                                            })
                                                            .playOn(acceptBtn);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(400)
                                            .playOn(declineBtn);
                                    YoYo.with(Techniques.FadeOut)
                                            .withListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    acceptedClmn.removeView(currentViewInClmn);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            })
                                            .duration(600)
                                            .playOn(currentViewInClmn);
                                    break;
                            }
                            invite.status = Invite.InviteStatus.DECLINED;
                        } else {
                            Snackbar.make(activity_container, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        switch (invite.status) {
            case PENDING:
                declineBtn.setVisibility(View.VISIBLE);
                acceptBtn.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                declineBtn.setVisibility(View.VISIBLE);
                acceptBtn.setVisibility(View.GONE);
                break;
            case DECLINED:
                declineBtn.setVisibility(View.GONE);
                acceptBtn.setVisibility(View.VISIBLE);
                break;
        }

        if (ParseUser.getCurrentUser().getObjectId().equals(invite.creatorId)) {
            declineBtn.setVisibility(View.GONE);
            acceptBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_INVITE, invite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
        int baseColor = getResources().getColor(R.color.main_color);
        float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(placeDetailsImage, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public static void launch(Activity activity, View transitionImage, View transitionTitle, Invite invite) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair.create(transitionImage, SHARED_IMAGE_NAME),
                        Pair.create(transitionTitle, SHARED_TITLE_NAME));
        Intent intent = new Intent(activity, InviteDetailsActivity.class);
        intent.putExtra(InviteDetailsActivity.EXTRA_INVITE, invite);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return titleTextView;
    }

}