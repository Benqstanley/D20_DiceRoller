package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    //TODO: figure out why edit is showing up in all of the context menus
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    ContainerActivity containerActivity;
    FrameLayout frameLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        containerActivity = (ContainerActivity) getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        containerActivity.setIcon(android.R.drawable.ic_menu_add);
        Button addDiceButton = rootView.findViewById(R.id.add_dice);
        Button rollDiceButton = rootView.findViewById(R.id.roll_dice);
        Button resetScreenButton = rootView.findViewById(R.id.reset_screen);
        containerActivity.shedParent(rootView);
        if(containerActivity.addDiceRowsToScreen(rootView)){
            containerActivity.addDice(rootView);
        }

        if(!containerActivity.getFileNamesForSpecialsToAddToHomeScreen().isEmpty()){
            containerActivity.createSpecialCollectionsListForScreen();
            containerActivity.addSpecialsToScreen(rootView);
        }
        addDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.addDice(rootView);
            }
        });
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //containerActivity.rollDiceAndDisplay(rootView);
                createPopupForDisplay(rootView);           }
        });
        resetScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.reset(rootView);
            }
        });
        frameLayout = rootView.findViewById(R.id.frame);
        return rootView;
    }
    //TODO: don't gray out screen if there is nothing to roll
    //TODO: give the popup a header
    public void createPopupForDisplay(View view){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) containerActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.dice_readout_pop_up,null);
        boolean needToDisplayPopup = containerActivity.rollDiceAndDisplay(layout);
        //Get the devices screen density to calculate correct pixel sizes
        if(needToDisplayPopup) {
            DisplayMetrics dm = containerActivity.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels * 8 / 10;
            int heightPixels = dm.heightPixels * 8 / 10;
            // create a focusable PopupWindow with the given layout and correct size
            final PopupWindow pw = new PopupWindow(layout, widthPixels, heightPixels);
            //Set up touch closing outside of pop-up
            pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pw.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pw.dismiss();
                    frameLayout.setForeground(null);
                    return false;
                }
            });
            /*frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                    frameLayout.setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
            });*/
            pw.setOutsideTouchable(true);
            // display the pop-up in the center
            frameLayout.setForeground(ContextCompat.getDrawable(containerActivity, R.drawable.foreground));
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            containerActivity.hideKeyboard(view);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
