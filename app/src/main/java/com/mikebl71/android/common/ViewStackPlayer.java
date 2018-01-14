package com.mikebl71.android.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Composite that can play through a stack of child views.
 */
public class ViewStackPlayer extends FrameLayout {

    private static final int DEFAULT_INTERVAL_MS = 500;

    public interface PlayerListener {
        void onPlayingCompleted();
    }

    private final Runnable playerRunnable = new PlayerRunnable();

    private int intervalMs = DEFAULT_INTERVAL_MS;
    private PlayerListener playerListener;

    private int currentChildIdx = -1;
    private boolean isPlaying = false;


    public ViewStackPlayer(Context context) {
        super(context);
    }

    public ViewStackPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }


    public void play() {
        if (!isPlaying && getChildCount() > 0) {
            isPlaying = true;
            setDisplayedChild(getChildCount() - 1);
            postDelayed(playerRunnable, intervalMs);
        }
    }

    public void stop() {
        if (isPlaying) {
            setDisplayedChild(0);
            removeCallbacks(playerRunnable);
            isPlaying = false;
        }
    }

    public void setDisplayedChild(int childIndex) {
        final int count = getChildCount();
        if (childIndex >= 0 && childIndex < count) {
            this.currentChildIdx = childIndex;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (i == childIndex) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.GONE);
                }
            }
        }
    }

    private class PlayerRunnable implements Runnable {
        @Override
        public void run() {
            if (isPlaying) {
                if (currentChildIdx > 1) {
                    setDisplayedChild(currentChildIdx - 1);
                    postDelayed(playerRunnable, intervalMs);

                } else {
                    setDisplayedChild(0);
                    isPlaying = false;
                    if (playerListener != null) {
                        playerListener.onPlayingCompleted();
                    }
                }
            }
        }
    }

}
