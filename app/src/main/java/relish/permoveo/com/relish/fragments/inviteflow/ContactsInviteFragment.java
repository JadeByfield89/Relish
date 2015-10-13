package relish.permoveo.com.relish.fragments.inviteflow;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteContactsListAdapter;
import relish.permoveo.com.relish.interfaces.ContactsLoader;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsInviteFragment extends Fragment implements ISelectable, Filterable, ContactsLoader {

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };
    @Bind(R.id.empty_contacts_container)
    LinearLayout emptyView;
    @Bind(R.id.bounce_progress)
    BounceProgressBar contactsProgress;
    @Bind(R.id.contacts_invite_recycler)
    RecyclerView recyclerView;
    @Bind(R.id.empty_message)
    TextView emptyMessage;
    private InviteContactsListAdapter adapter;
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            new LoadContactsTask().execute();
        } else if (getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new LoadContactsTask().execute();
        }
    }

    @Override
    public Filter getFilter() {
        return adapter.getFilter();
    }

    private String formatPhoneNumber(String number) {
        String formattedNumber = number.replace("(", "");
        formattedNumber = formattedNumber.replace(")", "");
        formattedNumber = formattedNumber.replace("-", "");
        formattedNumber = formattedNumber.replace(" ", "");

        // Needs to prefixed with +1
        if (formattedNumber.length() == 10) {
            formattedNumber = "+1" + formattedNumber;
        }
        else if(formattedNumber.length() == 11 && formattedNumber.startsWith("1")){
            formattedNumber = "+" + formattedNumber;
        }
        else if(formattedNumber.length() == 11){
            formattedNumber = "+" + formattedNumber;

        }
        Log.d("ContactsInviteFragment", "formatted number" + formattedNumber);
        return formattedNumber;
    }

    @Override
    public ArrayList<Contact> getSelection() {
        return adapter != null ? adapter.getSelected() : new ArrayList<Contact>();
    }

    @Override
    public void loadContactsWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                adapter.clear();
                contactsProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyMessage.setText(getString(R.string.contacts_permission_declined));
            } else {
                new LoadContactsTask().execute();
            }
        } else {
            new LoadContactsTask().execute();
        }
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
                            contact.longId = cursor.getLong(contactIdIndex);
                            contact.name = cursor.getString(displayNameIndex);
                            contact.number = cursor.getString(phoneIndex).trim();
                            contact.number = formatPhoneNumber(contact.number);
                            Log.d("ContactsInviteFragment ", "non formatted number " + contact.number);
                            contact.image = cursor.getString(photoUriIndex);

                            if (!TextUtils.isEmpty(contact.image)) {
                                InputStream inputStream;
                                try {
                                    inputStream = getActivity().getContentResolver().openInputStream(Uri.parse(contact.image));
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    try {
                                        byte[] buf = new byte[1024];
                                        int n;
                                        while (-1 != (n = inputStream.read(buf)))
                                            baos.write(buf, 0, n);

                                        String fileName = "avatar" + UUID.randomUUID().toString() + ".jpg";
                                        contact.imageFile = new ParseFile(fileName, baos.toByteArray());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

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
                        if (invited instanceof Contact && !TextUtils.isEmpty(((Contact) invited).number)
                                && ((Contact) invited).number.equals(contact.number)) {
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
}
