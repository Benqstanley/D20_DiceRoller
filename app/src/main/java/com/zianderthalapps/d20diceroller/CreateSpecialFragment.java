package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CreateSpecialFragment extends Fragment {

    public CreateSpecialFragment() {
        // Required empty public constructor
    }
    //TODO: enable grouping
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set the Action Bar title to represent the appropriate fragment
        final ContainerActivity containerActivity = (ContainerActivity) getActivity();
        containerActivity.setActionBarTitle(R.string.create_special);
        //Create the view to be returned. Inflate it with the location_list layout
        rootView = inflater.inflate(R.layout.fragment_create_special, container, false);
        containerActivity.shedParent(rootView);
        containerActivity.addDice(rootView);
        containerActivity.setIcon(android.R.drawable.ic_menu_add);
        Button addDiceButton = rootView.findViewById(R.id.add_dice);
        Button saveSpecialCollection = rootView.findViewById(R.id.save);
        Button resetScreenButton = rootView.findViewById(R.id.reset_screen);
        addDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.addDice(rootView);
            }
        });
        saveSpecialCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.save(rootView);            }
        });
        resetScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.resetCreate(rootView);
            }
        });
        EditText editName = (EditText) rootView.findViewById(R.id.name_of_collection);
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    containerActivity.hideKeyboard(v);
                }
            }
        });
        return rootView;
    }
}
