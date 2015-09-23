package relish.permoveo.com.relish.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.ChangeLogRecyclerView;

/**
 * Created by byfieldj on 9/23/15.
 */
public class AboutDialogFragment extends DialogFragment {


    ChangeLogRecyclerView changeLogRecyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.layout_about_changelog, null);
        changeLogRecyclerView = (ChangeLogRecyclerView) v.findViewById(R.id.changelog_view);


        return new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle("About Relish")
                .setView(changeLogRecyclerView)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();


    }


}
