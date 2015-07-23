package relish.permoveo.com.relish.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Roman on 23.07.15.
 */
public class TypefaceUtil {

    public static Typeface BRANNBOLL;
    public static Typeface BRANNBOLL_BOLD;
    public static Typeface PROXIMA_NOVA;
    public static Typeface PROXIMA_NOVA_BOLD;

    public static void init(Context context) {
        BRANNBOLL = Typeface.createFromAsset(context.getAssets(), "fonts/BrannbollRegular.ttf");
        BRANNBOLL_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/BrannbollBold.ttf");
        PROXIMA_NOVA = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNovaRegular.ttf");
        PROXIMA_NOVA_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNovaBold.ttf");
    }

}
