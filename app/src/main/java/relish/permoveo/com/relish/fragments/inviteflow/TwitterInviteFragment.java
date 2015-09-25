package relish.permoveo.com.relish.fragments.inviteflow;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.MainActivity;
import relish.permoveo.com.relish.activities.TwitterWebViewActivity;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteContactsListAdapter;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteTwitterListAdapter;
import relish.permoveo.com.relish.application.RelishApplication;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by byfieldj on 9/24/15.
 */


public class TwitterInviteFragment extends Fragment implements ISelectable, Filterable {

    @Bind(R.id.twitter_invite_recycler)
    RecyclerView twitterRecycler;

    @Bind(R.id.bConnectTwitter)
    Button connectTwitter;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    private Twitter twitter;

    InviteTwitterListAdapter followersAdapter;

    @Bind(R.id.empty_followers_container)
    LinearLayout emptyView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progress;


    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 100;
    private RequestToken requestToken;

    private String twitterUsername;

    public TwitterInviteFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followersAdapter = new InviteTwitterListAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        twitterRecycler.setLayoutManager(linearLayoutManager);
        twitterRecycler.setHasFixedSize(false);
        twitterRecycler.setItemAnimator(new DefaultItemAnimator());
        twitterRecycler.setAdapter(followersAdapter);

        connectTwitter.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        connectTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask().execute();
            }
        });

        if (RelishApplication.getTwitterFollowersList() != null) {
            emptyView.setVisibility(View.GONE);
            twitterRecycler.setVisibility(View.VISIBLE);
            followersAdapter = new InviteTwitterListAdapter(getContext());
            followersAdapter.swap(RelishApplication.getTwitterFollowersList());
            twitterRecycler.setAdapter(followersAdapter);
        }
    }

    @Override
    public Filter getFilter() {
        return followersAdapter.getFilter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_twitter_invite, container, false);



        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();


        return v;
    }


    /* Reading twitter essential configuration parameters from strings.xml */
    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void loginToTwitter() {
        if (!SharedPrefsUtil.get.isLoggedIntoTwitter()) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);
                Log.d("TwitterInviteFragment", "Request Token -> " + requestToken);

                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(getContext(), TwitterWebViewActivity.class);
                intent.putExtra(TwitterWebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                getParentFragment().startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        // User is already logged in, load their followers
        else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    emptyView.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);


                    new GetFollowersTask().execute();


                }
            });

        }
    }

    private ArrayList<Contact> getTwitterFollowers() {
        final ArrayList<Contact> followersList = new ArrayList<Contact>();


        try {

            long cursor = -1;
            int i = 0;
            PagableResponseList<User> followers;

            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            Twitter newTwitter = factory.getInstance();

            String accessToken = SharedPrefsUtil.get.getSavedAccessToken();
            Log.d("TwitterInviteFragment", "Getting followers, access token -> " + accessToken);
            String accessTokenSecret = SharedPrefsUtil.get.getSavedAccessTokenSecret();
            Log.d("TwitterInviteFragment", "Getting followers, access token secret -> " + accessTokenSecret);

            AccessToken oathAccessToken = new AccessToken(accessToken, accessTokenSecret);

            newTwitter.setOAuthAccessToken(oathAccessToken);
            newTwitter.verifyCredentials();

            //do {


            //IDs ids = newTwitter.getFollowersIDs(-1);

            //int[] followerPage = new int[100];

            //long[] array = Arrays.copyOfRange(ids.getIDs(), 100, followerPage.length);

            //First, let's get the current user's follower count
            User currentUser = newTwitter.showUser(SharedPrefsUtil.get.getTwitterUsername());
            int followersCount = currentUser.getFollowersCount();

            //Twitter returns a maximum of 200 user results per cursor
            // And only 15 GET requests can be made within a 15 minute window

            // Therefore 15 * 200 = 3,000 followers can be fetched at most using this approach
            // If the current user has more than 3,000 followers, we need to get batch IDs instead

            if (followersCount < 3000) {
                do {
                    Log.d("TwitterInviteFragment", "Getting Followers for -> " + SharedPrefsUtil.get.getTwitterUsername());
                    followers = newTwitter.getFollowersList(SharedPrefsUtil.get.getTwitterUsername(), cursor, 200);
                    Log.d("TwitterInviteFragment", "Followers count -> " + followers.size());
                    for (int j = 0; j < followers.size(); j++) {
                        // TODO: Collect top 10 followers here


                        User user = followers.get(j);
                        Log.d("TwitterInviteFragment", "Follower count -> " + j);
                        Contact contact = new Contact();
                        contact.name = user.getName();
                        contact.twitterUsername = "@" + user.getScreenName();
                        Log.d("TwitterInviteFragment", "Follower username -> " + contact.twitterUsername);

                        contact.image = user.getProfileImageURL();
                        followersList.add(contact);
                        //} else {
                        //  break;
                        //}

                    }

                    //cursor = followers.getNextCursor();
                } while ((cursor = followers.getNextCursor()) != 0);

                RelishApplication.setTwitterFollowersList(followersList);
            }


            // User has more than 3k followers
            else {

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return followersList;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TwitterInviteFragment", "TwitterInviteFragment onActivityResult");
        if (resultCode == Activity.RESULT_OK) {

            emptyView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            new GetFollowersTask(data).execute();

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    //--------------------------------- Log user in and get their followers in the background----//

    private class LoginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            loginToTwitter();
            Log.d("TwitterInviteFragment", "Logging in to Twitter...");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class GetFollowersTask extends AsyncTask<Void, Void, ArrayList<Contact>> {

        Intent intent;
        Twitter localTwitter;

        public GetFollowersTask(Intent intent) {
            this.intent = intent;
        }

        public GetFollowersTask() {


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            Log.d("TwitterInviteFragment", "doInBackground");

            String verifier = "";
            AccessToken accessToken = null;

            if (this.intent != null) {
                verifier = intent.getExtras().getString(oAuthVerifier);
            }

            try {


                if (this.intent != null) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                }

                // Create Access Token from saved key/secret
                else {
                    String accessTokenString = SharedPrefsUtil.get.getSavedAccessToken();
                    String accessTokenSecret = SharedPrefsUtil.get.getSavedAccessTokenSecret();
                    accessToken = new AccessToken(accessTokenString, accessTokenSecret);

                    Log.d("TwitterInviteFragment", "Access Token -> " + accessTokenString);

                }


                long userID = accessToken.getUserId();
                final User user;

                // Original Twitter from login is still alive
                if (twitter != null) {
                    user = twitter.showUser(userID);
                }

                //
                else {
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(consumerKey);
                    builder.setOAuthConsumerSecret(consumerSecret);

                    final Configuration configuration = builder.build();
                    final TwitterFactory factory = new TwitterFactory(configuration);
                    localTwitter = factory.getInstance();

                    String token = SharedPrefsUtil.get.getSavedAccessToken();
                    String secret = SharedPrefsUtil.get.getSavedAccessTokenSecret();

                    AccessToken oathAccessToken = new AccessToken(token, secret);

                    localTwitter.setOAuthAccessToken(oathAccessToken);

                    localTwitter.verifyCredentials();
                    user = this.localTwitter.showUser(userID);
                }

                twitterUsername = user.getScreenName();

                if (twitter != null) {
                    SharedPrefsUtil.get.saveTwitterInfo(accessToken, twitter);
                } else {
                    if (localTwitter != null) {
                        SharedPrefsUtil.get.saveTwitterInfo(accessToken, localTwitter);

                    }
                }


            } catch (TwitterException e) {
                Log.d("TwitterInviteFragment", e.getMessage());
            }
            return getTwitterFollowers();
        }


        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

            Toast.makeText(getContext(), twitterUsername, Toast.LENGTH_SHORT).show();
            Log.d("TwitterInviteFragment", "Followers Contacts -> " + contacts.size());
            emptyView.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);


            twitterRecycler.setVisibility(View.VISIBLE);
            followersAdapter = new InviteTwitterListAdapter(getContext());
            followersAdapter.swap(contacts);
            twitterRecycler.setAdapter(followersAdapter);
            //followersAdapter.notifyDataSetChanged();


        }
    }

    @Override
    public ArrayList<Contact> getSelection() {
        return followersAdapter != null ? followersAdapter.getSelected() : new ArrayList<Contact>();
    }


}



