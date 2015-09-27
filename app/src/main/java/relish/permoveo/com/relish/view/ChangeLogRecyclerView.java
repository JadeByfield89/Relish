package relish.permoveo.com.relish.view;

/**
 * Created by byfieldj on 9/23/15.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import it.gmariotti.changelibs.R.string;
import it.gmariotti.changelibs.R.styleable;
import it.gmariotti.changelibs.library.Constants;
import it.gmariotti.changelibs.library.Util;
import it.gmariotti.changelibs.library.internal.ChangeLog;
import it.gmariotti.changelibs.library.internal.ChangeLogRecyclerViewAdapter;
import it.gmariotti.changelibs.library.parser.XmlParser;
import relish.permoveo.com.relish.adapter.list.CustomChangeLogRecyclerAdapter;

public class ChangeLogRecyclerView extends RecyclerView {
    protected static String TAG = "ChangeLogRecyclerView";
    protected int mRowLayoutId;
    protected int mRowHeaderLayoutId;
    protected int mChangeLogFileResourceId;
    protected String mChangeLogFileResourceUrl;
    protected CustomChangeLogRecyclerAdapter mAdapter;

    public ChangeLogRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ChangeLogRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeLogRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRowLayoutId = Constants.mRowLayoutId;
        this.mRowHeaderLayoutId = Constants.mRowHeaderLayoutId;
        this.mChangeLogFileResourceId = Constants.mChangeLogFileResourceId;
        this.mChangeLogFileResourceUrl = null;
        this.init(attrs, defStyle);
    }

    @TargetApi(21)
    protected void init(AttributeSet attrs, int defStyle) {
        this.initAttrs(attrs, defStyle);
        this.initAdapter();
        this.initLayoutManager();
    }

    protected void initLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(1);
        this.setLayoutManager(layoutManager);
    }

    protected void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = this.getContext().getTheme().obtainStyledAttributes(attrs, styleable.ChangeLogListView, defStyle, defStyle);

        try {
            this.mRowLayoutId = a.getResourceId(styleable.ChangeLogListView_rowLayoutId, this.mRowLayoutId);
            this.mRowHeaderLayoutId = a.getResourceId(styleable.ChangeLogListView_rowHeaderLayoutId, this.mRowHeaderLayoutId);
            this.mChangeLogFileResourceId = a.getResourceId(styleable.ChangeLogListView_changeLogFileResourceId, this.mChangeLogFileResourceId);
            this.mChangeLogFileResourceUrl = a.getString(styleable.ChangeLogListView_changeLogFileResourceUrl);
        } finally {
            a.recycle();
        }

    }

    protected void initAdapter() {
        try {
            XmlParser e;
            if (this.mChangeLogFileResourceUrl != null) {
                e = new XmlParser(this.getContext(), this.mChangeLogFileResourceUrl);
            } else {
                e = new XmlParser(this.getContext(), this.mChangeLogFileResourceId);
            }

            ChangeLog chg = new ChangeLog();
            this.mAdapter = new CustomChangeLogRecyclerAdapter(this.getContext(), chg.getRows());
            this.mAdapter.setRowLayoutId(this.mRowLayoutId);
            this.mAdapter.setRowHeaderLayoutId(this.mRowHeaderLayoutId);
            if (this.mChangeLogFileResourceUrl != null && (this.mChangeLogFileResourceUrl == null || !Util.isConnected(this.getContext()))) {
                Toast.makeText(this.getContext(), string.changelog_internal_error_internet_connection, Toast.LENGTH_LONG).show();
            } else {
                (new ChangeLogRecyclerView.ParseAsyncTask(this.mAdapter, e)).execute(new Void[0]);
            }

            this.setAdapter(this.mAdapter);
        } catch (Exception var3) {
            Log.e(TAG, this.getResources().getString(string.changelog_internal_error_parsing), var3);
        }

    }

    protected class ParseAsyncTask extends AsyncTask<Void, Void, ChangeLog> {
        private CustomChangeLogRecyclerAdapter mAdapter;
        private XmlParser mParse;

        public ParseAsyncTask(CustomChangeLogRecyclerAdapter adapter, XmlParser parse) {
            this.mAdapter = adapter;
            this.mParse = parse;
        }

        protected ChangeLog doInBackground(Void... params) {
            try {
                if (this.mParse != null) {
                    ChangeLog e = this.mParse.readChangeLogFile();
                    return e;
                }
            } catch (Exception var3) {
                Log.e(ChangeLogRecyclerView.TAG, ChangeLogRecyclerView.this.getResources().getString(string.changelog_internal_error_parsing), var3);
            }

            return null;
        }

        protected void onPostExecute(ChangeLog chg) {
            if (chg != null) {
                this.mAdapter.add(chg.getRows());
            }

        }
    }
}
