package relish.permoveo.com.relish.adapter.list.inviteflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Reminder;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 02.09.2015.
 */
public class ReminderAdapter extends BaseAdapter {
    private List<Reminder> mItems = new ArrayList<>();
    private LayoutInflater inflater;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.mItems = reminders;
        this.inflater = LayoutInflater.from(context);
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(Reminder reminder) {
        mItems.add(reminder);
    }

    public void addItems(List<Reminder> reminders) {
        mItems.addAll(reminders);
    }

    public int getPosition(int reminder) {
        for (int i = 0; i < mItems.size(); i++) {
            if (reminder == mItems.get(i).seconds) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = inflater.inflate(R.layout.spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = inflater.inflate(R.layout.spinner_item, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        textView.setText(position == 0 ? getTitle(position) : getTitle(position).substring(0, getTitle(position).lastIndexOf(' ')));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
    }
}
