package relish.permoveo.com.relish.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.scribe.model.Request;
import org.scribe.model.Response;
import org.scribe.model.Verb;

/**
 * Created by byfieldj on 10/8/15.
 */
public class TwitterInviteManager {

    private static final String TWITTER_POST_URL = "http://www.relishwith.us/send_twitter.php";

    public void sendTwitterInvite(final String inviteMessage){
        new SendTwitterInviteTask().execute(inviteMessage);
    }

    private class SendTwitterInviteTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            String inviteMessage = params[0];

            Request request = new Request(Verb.POST, TWITTER_POST_URL);
            request.addBodyParameter("inviteMessage", inviteMessage);

            try {
                Response response = request.send();
                Log.d("TwitterInviteManager", "Response -> " + response);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
