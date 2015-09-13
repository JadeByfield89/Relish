package relish.permoveo.com.relish.fragments.search;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.CircularProgressButton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.ContactsAdapter;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private MenuItem searchItem;
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };

    private ContactsAdapter adapter;

    @Bind(R.id.empty_contacts_container)
    LinearLayout emptyView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar contactsProgress;

    @Bind(R.id.contacts_recycler)
    RecyclerView recyclerView;

    AlertDialog dialog;
    EditText dialogMessage;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new ContactsAdapter(getActivity(), new ContactsAdapter.ViewHolder.ContactsButtonClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildPosition(view);
                Contact currentContact = (Contact) adapter.getItem(position);


                   /* String message = getString(R.string.sms_invitation);
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("sms:" + currentContact.number));
                    sendIntent.putExtra("sms_body", message);
                    startActivity(sendIntent);*/

                showContactInviteDialog(currentContact);

//                    PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0,
//                            new Intent("SMS_SENT"), 0);
//
//                    getActivity().registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            if (getResultCode() == Activity.RESULT_OK) {
//                                currentBtn.setProgress(100);
//                                currentContact.isInvited = true;
//                                adapter.update(currentContact);
//                            }
//                        }
//                    }, new IntentFilter("SMS_SENT"));
//
//                    SmsManager sms = SmsManager.getDefault();
//                    sms.sendTextMessage(currentContact.number, null, getString(R.string.sms_invitation), sentPI, null);

            }
        });
    }

    private void showContactInviteDialog(final Contact contact) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_invite_contact, (ViewGroup) getView().getRootView(), false);

        View rootView = view.findViewById(R.id.dialog_root_view);

        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        dialogMessage = (EditText) view.findViewById(R.id.contact_invite_message);
        dialogMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);


        TextView dialogCancel = (TextView) view.findViewById(R.id.invite_cancel);
        dialogCancel.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        TextView dialogSend = (TextView) view.findViewById(R.id.invite_send);
        dialogSend.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);


        dialogBuilder.setView(view);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(contact);
            }
        });


        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();
        dialog.getWindow().setLayout(1000, 1000);


    }

    private void sendSMSMessage(final Contact contact) {
        PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent("SMS_SENT"), 0);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    adapter.update(contact);
                }
            }
        }, new IntentFilter("SMS_SENT"));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(contact.number, null, dialogMessage.getText().toString(), sentPI, null);

        dialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        new LoadContactsTask().execute();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);

        searchItem = menu.findItem(R.id.action_search_contacts);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private class LoadContactsTask extends AsyncTask<Void, Void, Map<String, Contact>> {

        @Override
        protected Map<String, Contact> doInBackground(Void... params) {
            Map<String, Contact> contacts = new LinkedHashMap<>();
            if (isAdded()) {
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                if (cursor != null) {
                    try {
                        final int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        final int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        final int photoUriIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);

                        while (cursor.moveToNext()) {
                            Contact contact = new Contact();
                            contact.id = cursor.getLong(contactIdIndex);
                            contact.name = cursor.getString(displayNameIndex);
                            contact.number = cursor.getString(phoneIndex);
                            contact.image = cursor.getString(photoUriIndex);
                            if (!contacts.containsKey(contact.name) && !TextUtils.isEmpty(contact.number))
                                contacts.put(contact.name, contact);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(Map<String, Contact> contacts) {
            super.onPostExecute(contacts);
            if (contacts != null && contacts.size() > 0) {
                adapter.swap(new ArrayList<>(contacts.values()));
                contactsProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                adapter.clear();
                contactsProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == SMS_SENT) {
//            if (currentBtn != null && currentBtn.getProgress() != 100) {
//                currentBtn.setProgress(50);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        currentBtn.setProgress(100);
//                        currentContact.isInvited = true;
//                        adapter.update(currentContact);
//                        currentContact = null;
//                        currentBtn = null;
//                    }
//                }, 1000);
//            }
//        }
//    }
}
