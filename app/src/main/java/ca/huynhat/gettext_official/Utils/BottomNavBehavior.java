package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by huynhat on 2018-03-15.
 */

public class BottomNavBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {



    public BottomNavBehavior(){
        super();
    }
    public BottomNavBehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
        boolean dependsOn = dependency instanceof FrameLayout;
        return dependsOn;
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                          @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
       if(dy<0){
           showBottomNav(child);
       }else if(dy>0){
           hideBottomNav(child);
       }
    }

    private void showBottomNav(BottomNavigationView child) {
        child.animate().translationY(child.getHeight());
    }

    private void hideBottomNav(BottomNavigationView child) {
        child.animate().translationY(0);
    }
}
