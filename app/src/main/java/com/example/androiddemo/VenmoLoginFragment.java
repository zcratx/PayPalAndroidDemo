package com.example.androiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenmoLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenmoLoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VenmoLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenmoLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VenmoLoginFragment newInstance(String param1, String param2) {
        VenmoLoginFragment fragment = new VenmoLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venmo_login, container, false);

        Button openUrlButton = view.findViewById(R.id.openUrlButton);
        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://venmo.com/web/login"; // Replace with your desired URL
                invokeURL(url);
            }
        });

        return view;
    }

    private void invokeURL(String url) {
        Intent tempIntent = new Intent(Intent.ACTION_VIEW);
        tempIntent.setData(Uri.parse(
                "https://venmo.com/web/login"));
        startActivity(tempIntent);
    }

}