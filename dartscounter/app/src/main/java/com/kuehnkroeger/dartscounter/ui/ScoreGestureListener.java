package com.kuehnkroeger.dartscounter.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom {@link View.OnTouchListener} to simply define actions for up/down swiping
 * {@link #onSwipeDown(View)}/{@link #onSwipeUp(View)} and click {@link #onClick(View)} on any {@link View}.
 * To override those functions create anonymous class
 * {@code new ScoreGestureListener(context) {...method implementation...}}
 */
public class ScoreGestureListener implements View.OnTouchListener {

    /** handles incoming gestures via custom {@link android.view.GestureDetector.SimpleOnGestureListener} */
    private GestureDetector gestureDetector;
    /** defines the threshold after which a swipe is considered */
    private int threshold;
    /** View object on which the gesture was registered on */
    private View view;

    /**
     * default Constructor
     * @param context required context object
     */
    public ScoreGestureListener(Context context, int threshold) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.threshold = threshold;
    }

    /**
     * handles touch events
     * @param view View on which the touch event was registered
     * @param motionEvent corresponding MotionEvent
     * @return {@code false} to not temper with the click animation
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.view = view;
        boolean swipe = gestureDetector.onTouchEvent(motionEvent);
        if(motionEvent.getAction() == MotionEvent.ACTION_UP && !swipe) {
            view.performClick();
            onClick(view);
        }
        return false;
    }

    /**
     * is called whenever whenever a click and no swipe is registered
     * @param view View object on which the click was performed on
     */
    public void onClick(View view) {

    }

    /**
     * is called whenever a swipe down above the {@link #threshold} is registered
     * @param view View object on which the swipe was performed on
     */
    public void onSwipeDown(View view) {

    }

    /**
     * is called whenever a swipe up above the {@link #threshold} is registered
     * @param view View object on which the swipe was performed on
     */
    public void onSwipeUp(View view) {

    }

    /**
     * Custom {@link android.view.GestureDetector.SimpleOnGestureListener} to detect swipes
     * upwards or downwards
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * calculates whether a swipe has been performed based on user input
         * @param e1 MotionEvent where the user pressed
         * @param e2 MotionEvent where the user released
         * @param velocityX velocity of the swipe on x-axis
         * @param velocityY velocity of the swipe on y-axis
         * @return {@code true} when a swipe has been registered, {@code false} else
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dy = e1.getY() - e2.getY();

            if(Math.abs(dy) > threshold) {
                if(dy > 0)
                    onSwipeUp(view);
                else
                    onSwipeDown(view);
                return true;
            }

            return false;
        }

    }
}
