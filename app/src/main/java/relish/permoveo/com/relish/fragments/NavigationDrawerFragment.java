package relish.permoveo.com.relish.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.NavDrawerAdapter;
import relish.permoveo.com.relish.model.NavDrawerItem;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.util.UserUtils;

public class NavigationDrawerFragment extends Fragment {

    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * A pointer to the getCurrent callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    @Bind(R.id.nav_menu)
    ListView mDrawerListView;

    @Bind(R.id.drawer_view)
    ViewGroup drawerView;

    private String[] navMenuTitles;

    @Bind(R.id.nav_header_avatar)
    CircleImageView headerAvatar;

    @Bind(R.id.nav_header_username)
    TextView headerUsername;

    @Bind(R.id.nav_header_email)
    TextView headerEmail;

    @Bind(R.id.nav_header_background)
    KenBurnsView kenBurnsView;

    NavDrawerAdapter adapter;

    private Uri outputFileUri;
    private static final int SELECT_PICTURE_REQUEST_CODE = 7;
    private AlertDialog uploadDialog;

    private String selectedImagePath = "";
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;

    private String imgPath;

    public NavigationDrawerFragment() {
    }

    public void reloadWithData(final String menuItem, final int count) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.set(menuItem, count);
            }
        });
    }

    public void reload() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public NavDrawerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, view);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        for (int i = 0; i < navMenuTitles.length; i++) {
            if (i == 3) {
                navDrawerItems.add(new NavDrawerItem());
            } else {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons
                        .getResourceId(i, -1)));
            }

//            if (i == 2) {
//                navDrawerItems.get(i).counter = 2;
//            }
        }
        navMenuIcons.recycle();

        adapter = new NavDrawerAdapter(getActivity(),
                navDrawerItems);

        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setCacheColorHint(0);
        mDrawerListView.setScrollingCacheEnabled(false);
        mDrawerListView.setScrollContainer(false);
        mDrawerListView.setFastScrollEnabled(true);
        mDrawerListView.setSmoothScrollbarEnabled(true);

        renderNavHeader();

        //Set listener for avatar icon
        headerAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUploadAvatarDialog();
            }
        });
        return view;
    }

    private void showUploadAvatarDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_upload_avatar, (ViewGroup) getView().getRootView(), false);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        CircleImageView avatar = (CircleImageView) view.findViewById(R.id.nav_header_avatar);

        String avatarUrl = UserUtils.getUserAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.with(getActivity()).load(avatarUrl).into(avatar);
        }
        TextView dialogMessage = (TextView) view.findViewById(R.id.upload_avatar_message);
        dialogMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        TextView fromCamera = (TextView) view.findViewById(R.id.from_camera);
        TextView fromGallery = (TextView) view.findViewById(R.id.from_gallery);

        fromCamera.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        fromGallery.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromCamera();
            }
        });

        fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });

        dialogBuilder.setView(view);


        uploadDialog = dialogBuilder.create();
        uploadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        uploadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        uploadDialog.show();


    }

    private void selectImageFromCamera() {
        final Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                setImageUri());
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(
                Intent.createChooser(intent, ""),
                PICK_IMAGE);
    }


    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    public String getImagePath() {
        return imgPath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {
                selectedImagePath = getAbsolutePath(data.getData());
                //File imageFile = new File(data.getData().getPath());
                headerAvatar.setImageBitmap(decodeFile(selectedImagePath));
                Log.d("NavDrawerFragment", "SelectedImagePath -> " + selectedImagePath);
            } else if (requestCode == CAPTURE_IMAGE) {
                selectedImagePath = getImagePath();
                headerAvatar.setImageBitmap(decodeFile(selectedImagePath));
            } else {
                super.onActivityResult(requestCode, resultCode,
                        data);
            }
        }
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of
            // 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void uploadAvatarToParse(byte[] bytes) {
        String avatarUrl = UserUtils.uploadUserAvatar(bytes);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.with(getActivity()).load(avatarUrl).into(headerAvatar);
        }


    }

    private byte[] getByteArrayFromFile(final File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("NavDrawerFrag", "Bytes -> " + bytes.length);
        return bytes;
    }

    private String getUniqueImageFilename() {

        return "img_" + System.currentTimeMillis() + ".jpg";
    }

    private void renderNavHeader() {

        // Set user avatar
        if (!TextUtils.isEmpty(UserUtils.getUserAvatar())) {
            Picasso.with(getActivity()).load(UserUtils.getUserAvatar()).into(headerAvatar);
        }

        // Set user name
        String username = UserUtils.getUsername().substring(0, 1).toUpperCase() + UserUtils.getUsername().substring(1);

        headerUsername.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        headerUsername.setText(username);


        //Set user email
        headerEmail.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        headerEmail.setText(UserUtils.getUserEmail());
    }

    public void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            mDrawerListView.setSelection(position);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(drawerView, position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        /*s
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(ViewGroup drawerView, int position);
    }
}