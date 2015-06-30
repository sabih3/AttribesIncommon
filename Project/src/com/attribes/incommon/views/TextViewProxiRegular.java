package com.attribes.incommon.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.attribes.incommon.util.Constants;


/**
 * Created by Sabih Ahmed on 5/14/2015.
 */
public class TextViewProxiRegular extends TextView {
    public TextViewProxiRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Typeface  fontProxi=Typeface.createFromAsset(getContext().getAssets(),"fonts/"+ Constants.FONT_PROXI_REGULAR);
        //setTypeface(fontProxi);

    }

}
