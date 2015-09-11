package relish.permoveo.com.relish.util.urlshortener;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;

import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 06.09.2015.
 */
public class UrlShortener {
    public interface UrlShortenerCallback {
        void onUrlShorten(String shortUrl);
    }

    public static void shortenUrl(String longUrl, UrlShortenerCallback callback) {
        new ShortenUrlTask(callback).execute(longUrl);
    }

    private static class ShortenUrlTask extends AsyncTask<String, Void, String> {

        private UrlShortenerCallback callback;

        public ShortenUrlTask(UrlShortenerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            GoogleShortenerPerformer shortener = new GoogleShortenerPerformer(new OkHttpClient());
            GooglShortenerResult result = shortener.shortenUrl(
                    new GooglShortenerRequestBuilder()
                            .buildRequest(params[0], ConstantUtil.GOOGLE_API_KEY)
            );
            if (GooglShortenerResult.Status.SUCCESS.equals(result.getStatus()) ) {
                return result.getShortenedUrl();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String urlShortenerResult) {
            super.onPostExecute(urlShortenerResult);
            callback.onUrlShorten(urlShortenerResult == null ? "" : urlShortenerResult);
        }
    }
}
