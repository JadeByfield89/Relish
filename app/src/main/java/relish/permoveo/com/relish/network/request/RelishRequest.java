package relish.permoveo.com.relish.network.request;

import android.os.AsyncTask;

import com.google.gson.Gson;

import relish.permoveo.com.relish.interfaces.IRequestable;

/**
 * Created by Roman on 03.08.15.
 */
public class RelishRequest<T1, T2, T3> extends AsyncTask<T1, T2, T3> {

    protected Gson gson;
    protected IRequestable callback;

    public RelishRequest() {
    }

    public RelishRequest(IRequestable callback) {
        this.gson = new Gson();
        this.callback = callback;
    }

    @Override
    protected T3 doInBackground(T1... params) {
        return null;
    }

}
