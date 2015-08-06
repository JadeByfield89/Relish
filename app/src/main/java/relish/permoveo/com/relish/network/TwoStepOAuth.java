package relish.permoveo.com.relish.network;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class TwoStepOAuth extends DefaultApi10a {

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }

//    @Override
//    public TimestampService getTimestampService() {
//        return new TimestampService() {
//            @Override
//            public String getTimestampInSeconds() {
//                return String.valueOf(new DateTime().getMillis() / 1000);
//            }
//
//            @Override
//            public String getNonce() {
//                return UUID.randomUUID().toString();
//            }
//        };
//    }
}