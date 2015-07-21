package relish.permoveo.com.relish.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import relish.permoveo.com.relish.R;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class RelishActivity extends AppCompatActivity {

    private View loader = null;

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void updateStatusBar() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, );
            WindowManager.LayoutParams winParams = w.getAttributes();
//
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            w.setAttributes(winParams);
//
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.main_color_dark));
//            ViewGroup content = (ViewGroup) this.findViewById(android.R.id.content);
//            content.setPadding(content.getPaddingLeft(), getStatusBarHeight(), content.getPaddingRight(), content.getPaddingRight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.main_color_dark));
        }
    }

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
