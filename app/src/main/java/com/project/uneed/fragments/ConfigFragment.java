package com.project.uneed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uneed.R;
import com.project.uneed.util.SessionUtil;

/**
 * Created by Admin on 30/07/2015.
 */
public class ConfigFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SessionUtil.printLog("Initializing ConfigFragment");
        View view = inflater.inflate(R.layout.fragment_myproposal, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
