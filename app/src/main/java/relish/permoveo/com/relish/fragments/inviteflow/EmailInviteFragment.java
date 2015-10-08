package relish.permoveo.com.relish.fragments.inviteflow;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteEmailListAdapter;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * Created by byfieldj on 9/16/15.
 */
public class EmailInviteFragment extends Fragment implements ISelectable, Filterable {

    private static final String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Email.DATA,
            ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
    @Bind(R.id.email_empty_contacts_container)
    LinearLayout emptyView;
    @Bind(R.id.bounce_progress)
    BounceProgressBar contactsProgress;
    @Bind(R.id.email_contacts_invite_recycler)
    RecyclerView recyclerView;
    @Bind(R.id.email_empty_message)
    TextView emptyMessage;
    private InviteEmailListAdapter adapter;
    private InviteCreator creator;

    public EmailInviteFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InviteCreator) {
            creator = (InviteCreator) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new InviteEmailListAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        new LoadEmailContactsTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_invite, container, false);


    }

    @Override
    public ArrayList<Contact> getSelection() {
        return adapter != null ? adapter.getSelected() : new ArrayList<Contact>();
    }

    @Override
    public Filter getFilter() {
        return adapter.getFilter();
    }

    private class LoadEmailContactsTask extends AsyncTask<Void, Void, Map<String, Contact>> {

        @Override
        protected Map<String, Contact> doInBackground(Void... params) {

            Log.d("EmailInviteFragment", "Getting Email Contacts..");
            Map<String, Contact> contacts = new LinkedHashMap<>();
            if (isAdded()) {

                ArrayList<String> emlRecs = new ArrayList<String>();
                HashSet<String> emlRecsHS = new HashSet<String>();
                Context context = getActivity();
                ContentResolver cr = context.getContentResolver();

                String order = "CASE WHEN "
                        + ContactsContract.Contacts.DISPLAY_NAME
                        + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                        + ContactsContract.Contacts.DISPLAY_NAME
                        + ", "
                        + ContactsContract.CommonDataKinds.Email.DATA
                        + " COLLATE NOCASE";
                String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
                Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);

                if (cur.moveToFirst()) {
                    final int contactIdIndex = cur.getColumnIndex(ContactsContract.Contacts._ID);
                    final int contactPhotoUriIndex = cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);

                    do {
                        // names comes in hand sometimes
                        String name = cur.getString(1);
                        String emlAddr = cur.getString(3);

                        // keep unique only
                        if (emlRecsHS.add(emlAddr.toLowerCase())) {
                            emlRecs.add(emlAddr);
                            Log.d("EmailInviteFragment", "Contact Email -> " + emlAddr);

                            //Create Contact object for each email contact
                            Contact contact = new Contact();

                            contact.longId = cur.getLong(contactIdIndex);
                            contact.name = name;
                            contact.email = emlAddr;
                            contact.image = cur.getString(contactPhotoUriIndex);

                            if (!contacts.containsKey(contact.name) && !TextUtils.isEmpty(contact.email))
                                contacts.put(contact.name, contact);


                        }
                    } while (cur.moveToNext());


                }

                cur.close();
                Log.d("EmailInviteFragment", "Email Contacts Count -> " + emlRecs.size());

            }

            return contacts;
        }

        @Override
        protected void onPostExecute(Map<String, Contact> contacts) {
            super.onPostExecute(contacts);
            if (contacts != null && contacts.size() > 0) {
                for (Contact contact : contacts.values()) {
                    for (InvitePerson invited : creator.getInvite().invited) {
                        if (invited instanceof Contact && !TextUtils.isEmpty(((Contact) invited).email)
                                && ((Contact) invited).email.equals(contact.email)) {
                            contact.isSelected = true;
                        }
                    }
                }
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

    /*public ArrayList<String> getEmailContacts() {
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = getActivity();
        ContentResolver cr = context.getContentResolver();

        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.add(emlAddr);
                    Log.d("EmailInviteFragment", "Contact Email -> " + emlAddr);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        Log.d("EmailInviteFragment", "Email Contacts Count -> " + emlRecs.size());
        return emlRecs;
    }*/

}
