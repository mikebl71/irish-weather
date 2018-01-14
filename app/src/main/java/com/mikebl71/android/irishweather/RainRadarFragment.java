package com.mikebl71.android.irishweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikebl71.android.common.MultiReadyListener;
import com.mikebl71.android.common.ReadyListener;
import com.mikebl71.android.common.ResourceContent;
import com.mikebl71.android.common.ViewStackPlayer;

import java.util.Collection;
import java.util.Date;

/**
 * Fragment for the rain radar images.
 */
public class RainRadarFragment extends Fragment {

    private MainApplication application;
    private RainRadarModel model;

    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;

    private SwipeRefreshLayout swipeRefreshContainer;
    private ViewStackPlayer player;
    private ImageButton replayButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        application = (MainApplication) getActivity().getApplication();
        model = application.getRainRadarModel();

        dateFormat = DateFormat.getMediumDateFormat(application);
        timeFormat = DateFormat.getTimeFormat(application);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rainradar, container, false);
        swipeRefreshContainer = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                application.clearModelCaches();
                deactivate();
                activate();
            }
        });

        player = view.findViewById(R.id.rainradar_player);
        player.setPlayerListener(new ViewStackPlayer.PlayerListener() {
            public void onPlayingCompleted() {
                replayButton.setEnabled(true);
            }
        });

        replayButton = view.findViewById(R.id.rainradar_replay_button);
        replayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                replay();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        swipeRefreshContainer = null;
        player = null;
        replayButton = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        activate();
    }

    @Override
    public void onPause() {
        deactivate();
        swipeRefreshContainer.setRefreshing(false);
        super.onPause();
    }


    private void activate() {
        replayButton.setVisibility(View.GONE);
        replayButton.setEnabled(true);
        model.provideLatestImage(new ReadyListener() {
            public void ready(ResourceContent result) {
                setLatestImage(result);
            }
        });
    }

    private void deactivate() {
        model.invalidateCallback();
        player.stop();
    }

    private void setLatestImage(ResourceContent result) {
        swipeRefreshContainer.setRefreshing(false);

        player.removeAllViews();
        View frameView = createFrameView(result);
        player.addView(frameView);
        player.setDisplayedChild(0);

        replayButton.setVisibility(isValidFrameView(frameView) ? View.VISIBLE : View.GONE);
    }

    private void replay() {
        replayButton.setEnabled(false);
        if (player.getChildCount() > 1) {
            player.play();
        } else {
            model.provideAllImages(new MultiReadyListener() {
                public void ready(Collection<ResourceContent> results) {
                    setAllImagesAndPlay(results);
                }
            });
        }
    }

    private void setAllImagesAndPlay(Collection<ResourceContent> results) {
        player.removeAllViews();
        for (ResourceContent result : results) {
            View frameView = createFrameView(result);
            player.addView(frameView);
        }

        View frameView = player.getChildAt(0);  // always at least a view with error message
        if (isValidFrameView(frameView)) {
            replayButton.setVisibility(View.VISIBLE);
            player.play();
        } else {
            replayButton.setVisibility(View.GONE);
        }
    }

    private View createFrameView(ResourceContent value) {
        View frameView = getActivity().getLayoutInflater().inflate(R.layout.rainradar_frame, player, false);
        TextView dateView = frameView.findViewById(R.id.rainradar_date);
        ImageView imageView = frameView.findViewById(R.id.rainradar_image);

        try {
            imageView.setImageDrawable(value.getDrawableOrThrow());
            Date imageDate = value.getReference(Date.class);
            dateView.setText(dateFormat.format(imageDate) + "  " + timeFormat.format(imageDate));
            frameView.setTag(R.id.tagValid, true);

        } catch (Exception ex) {
            dateView.setText(ex.getMessage());
            imageView.setImageDrawable(null);
            frameView.setTag(R.id.tagValid, false);
        }
        return frameView;
    }

    private boolean isValidFrameView(View frameView) {
        return (Boolean) frameView.getTag(R.id.tagValid);
    }

}
