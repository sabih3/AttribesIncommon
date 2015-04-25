package com.attribes.incommon.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.attribes.incommon.util.Utils;

/**
 * Created by muhammad on 11/29/14.
 */
public class InterestLayout extends ViewGroup {

    private int line_height;
    
    public static List<Integer> selectedItemPositions = new ArrayList<Integer>();

    public InterestLayout(Context context) {
        super(context);
    }

    public InterestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterestLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) /*- getPaddingTop() - getPaddingBottom()*/;
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop() ;

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.horizontal_spacing;
//                if(i == count-1){
//                	
//                	 ypos += line_height;
//                }
                	
            }
        }
       
        this.line_height = line_height;
       // height = line_height*3;
        
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width,(int) Math.round( height*1.1));//65
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(Utils.dpToPx(getContext(), 15), Utils.dpToPx(getContext(), 10)); // default of 1px spacing
    }

    public List<String> getSelectedChildViews(){
        List<String> selectedViews = new ArrayList<String>();
        for ( int i=0; i<getChildCount(); i++){
            View child = getChildAt(i);
            if ( child instanceof TextView && child.isSelected()){
                selectedViews.add(((TextView)child).getText().toString());
            }
        }
        return selectedViews;
    }
    
    public void setSelectedChildPositions(){
    	for ( int i=0; i < getChildCount(); i++){
            View child = getChildAt(i);
            if ( child instanceof TextView && child.isSelected()){
                selectedItemPositions.add(i);
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height+5;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }

	@Override
	public View getChildAt(int index) {
		return super.getChildAt(index);
	}
    
}





