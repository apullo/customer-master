package co.cvdcc.brojekcustomer.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import co.cvdcc.brojekcustomer.R;

/**
 * Created by bradhawk on 12/4/2016.
 */

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(checkCoordinateCross(ev, R.id.map)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean checkCoordinateCross(MotionEvent ev, int resId) {
        View target = findViewById(resId);
        if(target == null) {
            return false;
        }
        if(ev.getX() > target.getX() && ev.getX() < target.getX() + target.getWidth() && ev.getY() > target.getY() && ev.getY() < target.getY() + target.getHeight()) {
            return true;
        }
        return false;
    }
}
