package com.mikebl71.android.irishweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikebl71.android.common.ReadyListener;
import com.mikebl71.android.common.ResourceContent;

/**
 * Base Fragment for displaying content of a text resource.
 */
public abstract class AbstractTextResourceFragment extends Fragment {

    private MainApplication application;
    private AbstractTextResourceModel model;

    private SwipeRefreshLayout swipeRefreshContainer;
    private TextView textView;

    protected abstract AbstractTextResourceModel lookupModel(MainApplication application);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        application = (MainApplication) getActivity().getApplication();
        model = lookupModel(application);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scrollable_text, container, false);
        textView = view.findViewById(R.id.text);

        swipeRefreshContainer = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                application.clearModelCaches();
                deactivate();
                activate();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        swipeRefreshContainer = null;
        textView = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh content when we get the focus if necessary
        activate();
    }

    @Override
    public void onPause() {
        deactivate();
        swipeRefreshContainer.setRefreshing(false);
        super.onPause();
    }

    private void activate() {
        model.provideText(new ReadyListener() {
            public void ready(ResourceContent value) {
                setText(value);
            }
        });
    }

    private void deactivate() {
        model.invalidateCallback();
    }

    private void setText(ResourceContent value) {
        swipeRefreshContainer.setRefreshing(false);
        try {
            textView.setText(Html.fromHtml(value.getStringOrThrow()));
        } catch (Exception ex) {
            textView.setText(ex.getMessage());
        }
    }

}
