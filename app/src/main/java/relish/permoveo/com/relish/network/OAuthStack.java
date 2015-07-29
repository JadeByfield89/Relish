package relish.permoveo.com.relish.network;

import com.android.volley.toolbox.HurlStack;
import com.parse.signpost.OAuthConsumer;
import com.parse.signpost.exception.OAuthCommunicationException;
import com.parse.signpost.exception.OAuthExpectationFailedException;
import com.parse.signpost.exception.OAuthMessageSignerException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class OAuthStack extends HurlStack {
    private final OAuthConsumer consumer;

    public OAuthStack(OAuthConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            consumer.sign(connection);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
        }
        return connection;
    }
}