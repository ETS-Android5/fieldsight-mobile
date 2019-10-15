package org.fieldsight.naxa.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.odk.collect.android.application.Collect;

import java.util.concurrent.atomic.AtomicInteger;


public final class ViewUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context ctx, float spValue) {
        final float scaledDensity = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    public static void showOrHide(TextView textView, String text) {
        if (android.text.TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }

        textView.setVisibility(View.VISIBLE);
        text = " " + text + " ";
        textView.setText(text);


    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }


    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static void setButtonTint(FloatingActionButton button, ColorStateList tint) {
        button.setBackgroundTintList(tint);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static GlideRequest<Drawable> loadLocalImage(Context context, String path) {
        return GlideApp.with(context)
                .load(path)
                .centerInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }


    public static GlideRequest<Drawable> loadRemoteImage(Context context, String path) {
        return GlideApp.with(context)
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerInside()
                .skipMemoryCache(false)
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    static Drawable getProgressBarIndeterminate() {
        final int[] attrs = {android.R.attr.indeterminateDrawable};
        final int attrs_indeterminateDrawable_index = 0;
        TypedArray a = Collect.getInstance().obtainStyledAttributes(android.R.style.Widget_ProgressBar, attrs);
        try {
            return a.getDrawable(attrs_indeterminateDrawable_index);
        } finally {
            a.recycle();
        }
    }


    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    //https://stackoverflow.com/a/40282873/3730505
    public static void animateViewVisibility(final View view, final int visibility) {
        // cancel runnning animations and remove and listeners
        view.animate().cancel();
        view.animate().setListener(null);

        // animate making view visible
        if (visibility == View.VISIBLE) {
            view.animate().alpha(1f).start();
            view.setVisibility(View.VISIBLE);
        }
        // animate making view hidden (HIDDEN or INVISIBLE)
        else {
            view.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(visibility);
                }
            }).alpha(0f).start();
        }
    }

}

