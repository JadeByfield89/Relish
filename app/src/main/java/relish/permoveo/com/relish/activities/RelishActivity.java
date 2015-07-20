package relish.permoveo.com.relish.activities;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import relish.permoveo.com.relish.R;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class RelishActivity extends AppCompatActivity {

    private View loader = null;

    protected void showLoader() {
        showLoader(null);
    }

    public void showLoader(String text) {
        if (loader == null) {
//            setViewEnabled((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content), false);
            loader = getLayoutInflater().inflate(R.layout.loader_view, null);
            TextView loading = (TextView) loader.findViewById(R.id.loader_text);
            RelativeLayout container = (RelativeLayout) loader.findViewById(R.id.loader_container);
            container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            if (!TextUtils.isEmpty(text))
                loading.setText(text);
            ViewGroup content = (ViewGroup) this.findViewById(android.R.id.content);
            content.addView(loader);
        }
    }

    public void hideLoader() {
        if (loader != null) {
            ViewGroup content = (ViewGroup) this.findViewById(android.R.id.content);
            content.removeView(loader);
            loader = null;
//            setViewEnabled((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content), true);
        }
    }

}
