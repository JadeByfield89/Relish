package relish.permoveo.com.relish.util;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;


/**
 * Created by byfieldj on 9/10/15.
 * <p/>
 * This class makes an API request to Twilio, which then sends an SMS to a specified contact on our behalf
 */
public class TwilioSmsManager {


    private static final String TWILIO_SMS_URL = "https://api.twilio.com/2010-04-01/Accounts/%s/Messages";
    private static final String ACCOUNT_SID = "AC8ee5b87d49507692506223092e785d3c";
    private static final String AUTH_TOKEN = "e485ba03a5f386b89a09db9fb6a6f5b4";
    private static final String TWILIO_PHONE_NUMBER = "+17323911373";
    private String phoneNumber;
    private String message;


    public void sendInviteSmsViaTwilio(final String number, final String message) {
        //TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        // Build a filter for the MessageList
        /*List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Body", message));
        params.add(new BasicNameValuePair("To", number));
        params.add(new BasicNameValuePair("From", TWILIO_PHONE_NUMBER));*/

       new SendSMSTask(number, message).execute();



    }

    private class SendSMSTask extends AsyncTask<Void, Void, Void>{

        private String number;
        private String message;

        private SendSMSTask(final String number, final String message){
            this.number = number;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = String.format(TWILIO_SMS_URL, ACCOUNT_SID);

            OAuthRequest request = new OAuthRequest(Verb.POST, url);

            String base64EncodedCredentials = "Basic "
                    + Base64.encodeToString(
                    (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(),
                    Base64.NO_WRAP);

            request.addHeader("Authorization", base64EncodedCredentials);

            Log.d("TwilioSMSManager", "To number ->" + number);
            Log.d("TwilioSMSManager", "Message -> " + message);

            request.addBodyParameter("Body", message);
            request.addBodyParameter("To", "3475812311");
            request.addBodyParameter("From", TWILIO_PHONE_NUMBER);
            request.addBodyParameter("MediaUrl", "http://inviteid.jpg");


            try {
                Response response = request.send();
                Log.d("TwilioSmsManager", "Twilio SMS Response -> " + response.getBody());

            }catch(OAuthException e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
