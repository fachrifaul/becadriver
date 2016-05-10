package id.web.go_cak.drivergocak.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class RobotoRegularTextView extends TextView {

    private static Typeface typeface;
    public RobotoRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setRobotoTypeface();
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRobotoTypeface();
    }

    public RobotoRegularTextView(Context context) {
        super(context);
        setRobotoTypeface();
    }

    private void setRobotoTypeface()  {
        try {
            if (typeface == null) {
                typeface = Typeface.createFromAsset(this.getContext().getAssets(), "Roboto-Regular.ttf");
            }
            this.setTypeface(typeface);
        }
        catch (Exception e) {
            Log.d("Roboto", "LOOKS LIKE ROBOTO IS NOT FOUND. USE DEFAULT FONT INSTEAD");
        }
    }


}
