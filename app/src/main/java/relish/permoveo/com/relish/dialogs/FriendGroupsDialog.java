package relish.permoveo.com.relish.dialogs;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendGroupsDialog extends DialogFragment {
    public static final String FRIEND_BUNDLE = "friend_bundle";
    public static final String CHOSEN_GROUP = "choosen_group";
    public static final String CHOSEN_FRIEND = "choosen_friend";
    @Bind(R.id.dialog_container)
    View container;
    @Bind(R.id.dialog_title)
    TextView dialogTitle;
    @Bind(R.id.add_friend_image)
    CircleImageView friendImage;
    @Bind(R.id.add_friend_message)
    TextView friendMessage;
    @Bind(R.id.add_friend_colleagues)
    TextView colleagues;
    @Bind(R.id.add_friend_friends)
    TextView friends;
    @Bind(R.id.add_friend_work)
    TextView work;
    @Bind(R.id.add_friend_button)
    TextView addFriendBtn;
    private Friend friend;
    private String group;

    public FriendGroupsDialog() {
        // Required empty public constructor
    }

    public static FriendGroupsDialog newInstance(Friend friend) {
        FriendGroupsDialog dialog = new FriendGroupsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRIEND_BUNDLE, friend);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friend = (Friend) getArguments().getSerializable(FRIEND_BUNDLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_friends_group, container, false);
        ButterKnife.bind(this, v);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (TextUtils.isEmpty(friend.image)) {
            friendImage.setImageResource(R.drawable.relish_avatar_placeholder);
        } else {
            Picasso.with(getActivity())
                    .load(friend.image)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(friendImage);
        }

        friendMessage.setText("Which group would you like to add " + friend.name + " to?");
        friendMessage.setIncludeFontPadding(false);
        friendMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        addFriendBtn.setEnabled(true);
        addFriendBtn.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        work.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        work.setIncludeFontPadding(false);
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work.setSelected(!work.isSelected());

                if (work.isSelected()) {
                    friends.setSelected(false);
                    colleagues.setSelected(false);

                    group = work.getText().toString();
                }
            }
        });

        friends.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        friends.setIncludeFontPadding(false);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friends.setSelected(!friends.isSelected());

                if (friends.isSelected()) {
                    colleagues.setSelected(false);
                    work.setSelected(false);

                    group = friends.getText().toString();
                }
            }
        });

        colleagues.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        colleagues.setIncludeFontPadding(false);
        colleagues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colleagues.setSelected(!colleagues.isSelected());

                if (colleagues.isSelected()) {
                    friends.setSelected(false);
                    work.setSelected(false);

                    group = colleagues.getText().toString();
                }
            }
        });

        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friends.isSelected() && !colleagues.isSelected() && !work.isSelected()) {
                    Toast.makeText(getActivity(), "Please select a group to add " + friend.name + " to", Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent()
                            .putExtra(CHOSEN_GROUP, group)
                            .putExtra(CHOSEN_FRIEND, friend.id));
                }
            }
        });


        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

    }
}