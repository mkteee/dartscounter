package com.kuehnkroeger.dartscounter.ui;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.kuehnkroeger.dartscounter.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates a {@link DialogFragment} and displays a dartboard and additionally up to 3 fields that
 * light up sequentially.
 * Create an instance with {@link #newInstance(Integer...)} with parameters being the fields
 * to flash (with D16=216, T20=320... and Bull=125, Bullseye=225).
 * Usage -
 * {@code DartboardDialogFragment dialog = DartboardDialogFragment.newInstance(params);}
 * {@code dialog.show(getSupportFragmentManager(), tag);}
 */
public class DartboardDialogFragment extends DialogFragment {

    /** defines rotations of 18° needed to reach field with score n+1*/
    private final int[] rotations = {1, 8, 10, 3, -1, 5, -8, -6, -3, 6, -5, -2, 4, -4, 7, -7, 9, 2, -9, 0};
    /** defines image resources to load*/
    private final int[] resources = {R.drawable.blink_single, R.drawable.blink_double, R.drawable.blink_triple,
            R.drawable.blink_bull, R.drawable.blink_bullseye};

    /**
     * creates a new instance of the Dartboard
     * @param params up to 3 integers that describe the fields to light up
     * @return instance to display
     */
    public static DartboardDialogFragment newInstance(Integer... params) {
        final DartboardDialogFragment dialog = new DartboardDialogFragment();
        final Bundle args = new Bundle(1);
        if(params != null)
            args.putIntegerArrayList("scores", new ArrayList<>(Arrays.asList(params)));
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dartboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if(dialog != null) {
            Window window = getDialog().getWindow();
            if(window != null)
                window.setBackgroundDrawableResource(android.R.color.transparent);
        }



        ConstraintLayout layout = view.findViewById(R.id.dartboard_fragment);
        ConstraintLayout.LayoutParams params;


        final TextView step = new TextView(getContext());
        step.setTextColor(0xFFE4FF5A);
        step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        step.setText("1");
        //adjust layout params with respect to screen orientation
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FragmentActivity activity = getActivity();
        if(activity != null && activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById(R.id.base).setLayoutParams(new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            lp.startToEnd = R.id.base;
            lp.topToTop = R.id.dartboard_fragment;
            lp.bottomToBottom = R.id.dartboard_fragment;

            params = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else {
            lp.startToStart = R.id.dartboard_fragment;
            lp.endToEnd = R.id.dartboard_fragment;
            lp.topToBottom = R.id.base;

            params = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        params.topToTop = layout.getId();
        params.startToStart = layout.getId();

        step.setLayoutParams(lp);
        layout.addView(step);

        //load blinking images and apply animations
        Bundle args = getArguments();
        ArrayList<Integer> scores;
        if(args != null && (scores = args.getIntegerArrayList("scores")) != null) {
            final ImageView[] blinks = new ImageView[scores.size()];
            final AlphaAnimation[] anims = new AlphaAnimation[scores.size()];
            for(int i = 0; i < scores.size(); i++) {
                if(scores.get(i)%100 != 25)
                    blinks[i] = getOverlay(resources[scores.get(i)/100 - 1], 18*rotations[scores.get(i)%100 - 1]);
                else
                    blinks[i] = getOverlay(resources[scores.get(i)/100 + 2], 0);
                layout.addView(blinks[i], -1, params);
                anims[i] = (AlphaAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.blink);
            }

            //cycle through animations
            for(int i = 0; i < anims.length; i++) {
                final int index = (i+1)%anims.length;
                anims[i].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        blinks[index].setVisibility(View.VISIBLE);
                        blinks[index].startAnimation(anims[index]);
                        step.setText(String.valueOf(index+1));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            blinks[0].startAnimation(anims[0]);
        }

    }

    /**
     * loads the given image resource and rotates it by given angle
     * @param resourceId id of image drawable
     * @param rotation number of degrees (angular) to rotate
     * @return requested Image with given rotation
     */
    private ImageView getOverlay(int resourceId, float rotation) {
        ImageView iv = new ImageView(getContext());
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(), resourceId, null));
        iv.setRotation(rotation);
        iv.setAlpha(1f);
        iv.setVisibility(View.INVISIBLE);
        return iv;
    }
}
