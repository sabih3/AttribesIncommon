package com.attribes.incommon.views;

/**
 * Created by Sabih Ahmed on 13-Jul-15.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.attribes.incommon.R;


/**
 * Created with IntelliJ IDEA.
 * User: Sabih Ahmed
 * Date: 13/7/15
 * Time: 3:25 PM
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(context, attrs);
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTextView_fontName:
                    try {
                        Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/"+a.getString(attr));
                        if (font != null) {
                            this.setTypeface(font);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
