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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteContactsListAdapter;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsInviteFragment extends Fragment implements ISelectable, Filterable {

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };

    private InviteContactsListAdapter adapter;

    @Bind(R.id.empty_contacts_container)
    LinearLayout emptyView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar contactsProgress;

    @Bind(R.id.contacts_invite_recycler)
    RecyclerView recyclerView;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

    private InviteCreator creator;

    public ContactsInviteFragment() {
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
        adapter = new InviteContactsListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_invite, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        new LoadContactsTask().execute();
    }

    @Override
    public Filter getFilter() {
        return adapter.getFilter();
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
                for (Contact contact : contacts.values()) {
                    for (InvitePerson invited : creator.getInvite().invited) {
                        if (invited instanceof Contact && ((Contact) invited).number.equals(contact.number)) {
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

    @Override
    public ArrayList<Contact> getSelection() {
        return adapter != null ? adapter.getSelected() : new ArrayList<Contact>();
    }
}
