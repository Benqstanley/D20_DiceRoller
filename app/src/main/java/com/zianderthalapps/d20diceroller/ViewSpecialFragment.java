package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class ViewSpecialFragment extends Fragment {
    //TODO: figure out why adding to main is broken.
    //TODO: Enable editing
    public ViewSpecialFragment() {
        // Required empty public constructor
    }
    ContainerActivity containerActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        containerActivity = (ContainerActivity) getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_view_special, container, false);
        containerActivity.setCollectionLayout((LinearLayout) rootView.findViewById(R.id.collection_view));
        containerActivity.populateCollectionView();
        containerActivity.setOverflowButtonIcon(ContextCompat.getDrawable(containerActivity,R.drawable.ic_baseline_more_vert_24_whitepx));
        containerActivity.setIcon(0);
        Button addDiceSelectionButton = rootView.findViewById(R.id.add_selection);
        addDiceSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.buildSelection();
                containerActivity.toHome();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = containerActivity.getMenuInflater();
        inflater.inflate(R.menu.view_special_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_special_options_delete_selection:
                containerActivity.deleteSelection();
                return true;
            case R.id.add_special_options_delete_everything:
                containerActivity.deleteEverything();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
