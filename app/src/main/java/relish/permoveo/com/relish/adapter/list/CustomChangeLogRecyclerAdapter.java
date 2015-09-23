package relish.permoveo.com.relish.adapter.list;

/**
 * Created by byfieldj on 9/23/15.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.gmariotti.changelibs.R.id;
import it.gmariotti.changelibs.library.Constants;
import it.gmariotti.changelibs.library.internal.ChangeLogRow;
import relish.permoveo.com.relish.util.TypefaceUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomChangeLogRecyclerAdapter extends Adapter<ViewHolder> {
    private static final int TYPE_ROW = 0;
    private static final int TYPE_HEADER = 1;
    private final Context mContext;
    private int mRowLayoutId;
    private int mRowHeaderLayoutId;
    private int mStringVersionHeader;
    private List<ChangeLogRow> items;

    public CustomChangeLogRecyclerAdapter(Context mContext, List<ChangeLogRow> items) {
        this.mRowLayoutId = Constants.mRowLayoutId;
        this.mRowHeaderLayoutId = Constants.mRowHeaderLayoutId;
        this.mStringVersionHeader = Constants.mStringVersionHeader;
        this.mContext = mContext;
        if(items == null) {
            items = new ArrayList();
        }

        this.items = (List)items;
    }

    public void add(LinkedList<ChangeLogRow> rows) {
        int originalPosition = this.items.size();
        this.items.addAll(rows);
        this.notifyItemRangeInserted(originalPosition, originalPosition + rows.size());
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(this.mRowHeaderLayoutId, parent, false);
            return new CustomChangeLogRecyclerAdapter.ViewHolderHeader(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(this.mRowLayoutId, parent, false);
            return new CustomChangeLogRecyclerAdapter.ViewHolderRow(view);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(this.isHeader(position)) {
            this.populateViewHolderHeader((CustomChangeLogRecyclerAdapter.ViewHolderHeader)viewHolder, position);
        } else {
            this.populateViewHolderRow((CustomChangeLogRecyclerAdapter.ViewHolderRow)viewHolder, position);
        }

    }

    private void populateViewHolderRow(CustomChangeLogRecyclerAdapter.ViewHolderRow viewHolder, int position) {
        ChangeLogRow item = this.getItem(position);
        if(item != null) {
            if(viewHolder.textRow != null) {
                viewHolder.textRow.setText(Html.fromHtml(item.getChangeText(this.mContext)));
                viewHolder.textRow.setMovementMethod(LinkMovementMethod.getInstance());
            }

            if(viewHolder.bulletRow != null) {
                if(item.isBulletedList()) {
                    viewHolder.bulletRow.setVisibility(0);
                } else {
                    viewHolder.bulletRow.setVisibility(8);
                }
            }
        }

    }

    private void populateViewHolderHeader(CustomChangeLogRecyclerAdapter.ViewHolderHeader viewHolder, int position) {
        ChangeLogRow item = this.getItem(position);
        if(item != null) {
            if(viewHolder.versionHeader != null) {
                StringBuilder sb = new StringBuilder();
                String versionHeaderString = this.mContext.getString(this.mStringVersionHeader);
                if(versionHeaderString != null) {
                    sb.append(versionHeaderString);
                }

                sb.append(item.getVersionName());
                viewHolder.versionHeader.setText(sb.toString());
            }

            if(viewHolder.dateHeader != null) {
                if(item.getChangeDate() != null) {
                    viewHolder.dateHeader.setText(item.getChangeDate());
                    viewHolder.dateHeader.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.dateHeader.setText("");
                    viewHolder.dateHeader.setVisibility(View.GONE);
                }
            }
        }

    }

    private boolean isHeader(int position) {
        return this.getItem(position).isHeader();
    }

    private ChangeLogRow getItem(int position) {
        return (ChangeLogRow)this.items.get(position);
    }

    public int getItemViewType(int position) {
        return this.isHeader(position)?1:0;
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void setRowLayoutId(int mRowLayoutId) {
        this.mRowLayoutId = mRowLayoutId;
    }

    public void setRowHeaderLayoutId(int mRowHeaderLayoutId) {
        this.mRowHeaderLayoutId = mRowHeaderLayoutId;
    }

    public static class ViewHolderRow extends ViewHolder {
        public TextView textRow;
        public TextView bulletRow;

        public ViewHolderRow(View itemView) {
            super(itemView);
            this.textRow = (TextView)itemView.findViewById(id.chg_text);
            this.textRow.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
            this.bulletRow = (TextView)itemView.findViewById(id.chg_textbullet);
            this.bulletRow.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        }
    }

    public static class ViewHolderHeader extends ViewHolder {
        public TextView versionHeader;
        public TextView dateHeader;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            this.versionHeader = (TextView)itemView.findViewById(id.chg_headerVersion);
            this.dateHeader = (TextView)itemView.findViewById(id.chg_headerDate);
        }
    }
}
