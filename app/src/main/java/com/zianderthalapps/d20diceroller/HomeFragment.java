package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;


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
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        containerActivity = (ContainerActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Button addDiceButton = rootView.findViewById(R.id.add_dice);
        Button rollDiceButton = rootView.findViewById(R.id.roll_dice);
        Button resetScreenButton = rootView.findViewById(R.id.reset_screen);
        diceRows = containerActivity.getDiceRows();
        specialCollectionsListForHomeScreen = containerActivity.getSpecialCollectionsListForHomeScreen();
        specialRowsOnHomeScreen = containerActivity.getSpecialRowsOnHomeScreen();
        fileNamesForSpecialsToAddToHomeScreen = containerActivity.getFileNamesForSpecialsToAddToHomeScreen();
        if(addDiceRowsToScreen(rootView)){
            addDice(rootView);
        }
        containerActivity.setDiceRowsNeedShed(true);

        if(!fileNamesForSpecialsToAddToHomeScreen.isEmpty()){
            createSpecialCollectionsListForScreen();
            addSpecialsToScreen(rootView);
        }
        addDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDice(rootView);
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
                reset(rootView);
            }
        });
        frameLayout = rootView.findViewById(R.id.frame);
        return rootView;
    }
    public void shedParent(){
        for (View view : diceRows) {
            ((ViewManager) view.getParent()).removeView(view);
        }
    }
    //TODO: give the popup a header
    public void createPopupForDisplay(View view){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) containerActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.dice_readout_pop_up,null);
        boolean needToDisplayPopup = rollDiceAndDisplay(layout);
        //Get the devices screen density to calculate correct pixel sizes
        if(needToDisplayPopup) {
            DisplayMetrics dm = containerActivity.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels * 75 / 100;
            int heightPixels = dm.heightPixels * 75 / 100;
            // create a focusable PopupWindow with the given layout and correct size
            final PopupWindow pw = new PopupWindow(layout, widthPixels, heightPixels);
            //Set up touch closing outside of pop-up
            pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pw.setOutsideTouchable(true);
            // display the pop-up in the center
            frameLayout.setForeground(ContextCompat.getDrawable(containerActivity, R.drawable.foreground));
            pw.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                    {
                        frameLayout.setForeground(null);
                        pw.dismiss();
                        return true;
                    }

                    return false;
                }
            });
            pw.update();
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
        void onFragmentInteraction(Uri uri);
    }
    ArrayList<LinearLayout> diceRows;
    public boolean addDiceRowsToScreen(View view){
        LinearLayout diceLayout = (LinearLayout) view.findViewById(R.id.dice_view);
        for(int i = 0; i < diceRows.size(); i++){
            LinearLayout row = diceRows.get(i);
            diceLayout.addView(row);
            registerForContextMenu(row);
        }
        return diceRows.isEmpty();
    }
    public void addDice(View view) {
        LinearLayout diceLayout = (LinearLayout) view.findViewById(R.id.dice_view);
        LinearLayout diceRow = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dice_row, null);
        Spinner spinner = (Spinner) diceRow.findViewById(R.id.dice_spinner);
        containerActivity.setDiceSpinner(spinner);
        diceRow.findViewById(R.id.dice_input).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    containerActivity.hideKeyboard(v);
                }
            }
        });
        diceRow.findViewById(R.id.constant_input).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    containerActivity.hideKeyboard(v);
                }
            }
        });
        diceLayout.addView(diceRow);
        diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }

    ArrayList<SpecialCollection> specialCollectionsListForHomeScreen = new ArrayList<>(); //Array of the special collections displayed on screen.
    ArrayList<LinearLayout> specialRowsOnHomeScreen = new ArrayList<>();
    ArrayList<String> fileNamesForSpecialsToAddToHomeScreen = new ArrayList<>();
    public void reset(View view){
        diceRows.clear();
        specialCollectionsListForHomeScreen.clear();
        specialRowsOnHomeScreen.clear();
        LinearLayout diceView = view.findViewById(R.id.dice_view);
        diceView.removeAllViews();
        addDice(diceView);
        ScrollView diceDisplay = view.findViewById(R.id.dice_display_scrollview);
        diceDisplay.setVisibility(View.GONE);
        LinearLayout specialSignify = view.findViewById(R.id.special_signifier);
        specialSignify.setVisibility(View.GONE);
        View specialSeparator = view.findViewById(R.id.special_separator);
        specialSeparator.setVisibility(View.GONE);
        specialSeparator = view.findViewById(R.id.special_signify_separator);
        specialSeparator.setVisibility(View.GONE);
        fileNamesForSpecialsToAddToHomeScreen.clear();
        LinearLayout specialLayout = view.findViewById(R.id.special_collection_view);
        specialLayout.removeAllViews();

    }
    public void createSpecialCollectionsListForScreen(){
        specialCollectionsListForHomeScreen.clear();
        for(String fileName : fileNamesForSpecialsToAddToHomeScreen){
            File file = new File(getActivity().getFilesDir(), fileName);

            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                SpecialCollection temp = (SpecialCollection) inputStream.readObject();
                specialCollectionsListForHomeScreen.add(temp);
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*Adds all of the desired specialCollectionsListForHomeScreen to the screen*/
    public void addSpecialsToScreen(View view){
        specialRowsOnHomeScreen.clear();
        LinearLayout specialSignify = view.findViewById(R.id.special_signifier);
        specialSignify.setVisibility(View.VISIBLE);
        View specialSeparator = view.findViewById(R.id.special_separator);
        specialSeparator.setVisibility(View.VISIBLE);
        specialSeparator = view.findViewById(R.id.special_signify_separator);
        specialSeparator.setVisibility(View.VISIBLE);
        for(SpecialCollection dice : specialCollectionsListForHomeScreen) {
            LinearLayout specialLayout = view.findViewById(R.id.special_collection_view);
            LinearLayout specialRow = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.special_collection_roll, null);
            populateCollectionRow(dice, specialRow);
            specialLayout.addView(specialRow);
            specialRowsOnHomeScreen.add(specialRow);
            registerForContextMenu(specialRow);
        }
    }
    /*  populateCollectionRow takes in a SpecialCollection and the LinearLayout that its info should be displayed in.
    The SpecialCollections info is added to the relevant TextViews.
* */
    public void populateCollectionRow(SpecialCollection dice, LinearLayout row){
        TextView tv = row.findViewById(R.id.special_collection_details);
        tv.setText(dice.specialCollectionDetails());
        tv = row.findViewById(R.id.special_collection_name);
        tv.setText(dice.getPrintName());
    }
    /*Retrieves the special collections that have 'roll' checked*/
    public void getSpecialsToRoll(){
        int k;
        for(k = 0; k < specialRowsOnHomeScreen.size(); k++){
            LinearLayout row = specialRowsOnHomeScreen.get(k);
            CheckBox toRoll = row.findViewById(R.id.special_collection_toggle_roll);
            if(toRoll.isChecked()){
                specialCollectionsListForHomeScreen.get(k).setRoll(true);
            }else{
                specialCollectionsListForHomeScreen.get(k).setRoll(false);
            }
        }
    }
    /*
    This method is called when the "Add Dice" button is clicked. It increments the number of dice rows stored in totaldice

    It creates a LinearLayout by inflating it from the XML file dice_row.xml
    it initializes the new spinner. Adds the new row to the dice layout view. It also adds the new row to the arraylist diceRows.

    */
    ArrayList<DiceCollection> diceCollectionsToRoll = new ArrayList<>();
    ArrayList<Integer> diceCollectionsToSum = new ArrayList<>();

    public void createDiceCollectionsToRoll(){
        diceCollectionsToRoll.clear();
        for(LinearLayout row : diceRows) {
            EditText numOfDiceView = row.findViewById(R.id.dice_input);
            String numOfDiceString = numOfDiceView.getText().toString();
            CheckBox toggleRoll = row.findViewById(R.id.toggle_roll);
            Boolean roll = toggleRoll.isChecked();
            if (!numOfDiceString.equals("") && roll) {
                DiceCollection dice = new DiceCollection();
                dice.setNumberOfDice(Integer.parseInt(numOfDiceString));
                EditText modifierView = row.findViewById(R.id.constant_input);
                String modifierString = modifierView.getText().toString();
                if (!modifierString.equals("")) {
                    dice.setModifier(Integer.parseInt(modifierString));
                }
                Spinner spinner = row.findViewById(R.id.dice_spinner);
                String diceType = spinner.getSelectedItem().toString();
                dice.setDiceType(diceType);
                CheckBox toggleSum = row.findViewById(R.id.sum_row); stat
                dice.setSum(toggleSum.isChecked());
                diceCollectionsToRoll.add(dice);

            }
        }
    }
    //handles rolling the dice.
    //Initializes a stringbuilder to eventually convert to string and be displayed.
    //Builds it up recursively
    //Sets the dice collection's temp roll total
    //Checks the dice type
    public String rollDiceCollection(DiceCollection dice){
        StringBuilder value = new StringBuilder();
        int j;
        int temp;
        int mod = dice.getModifier();
        int rowTotal = mod;
        int k = dice.getNumberOfDice();
        switch (dice.getDiceType()){
            case "d2":
                for (j = 0; j < k; j++) {
                    temp = rolld2();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d4":
                for (j = 0; j < k; j++) {
                    temp = rolld4();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d6":
                for (j = 0; j < k; j++) {
                    temp = rolld6();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d8":
                for (j = 0; j < k; j++) {
                    temp = rolld8();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d10":
                for (j = 0; j < k; j++) {
                    temp = rolld10();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d12":
                for (j = 0; j < k; j++) {
                    temp = rolld12();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d20":
                for (j = 0; j < k; j++) {
                    temp = rolld20();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "d100":
                for (j = 0; j < k; j++) {
                    temp = rolld100();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
            case "Perc":
                for (j = 0; j < k; j++) {
                    temp = rolld10();
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                    temp = 10*(rolld10() - 1);
                    //value.append(" + ").append(temp);
                    rowTotal += temp;
                }
                break;
        }
        /*if(!(mod == 0)) {
            value.append(" + ").append(mod);
        }*/
        value.append(" = ").append(rowTotal);
        dice.setTempRollTotal(rowTotal);
        return value.toString();
    }
    public void rollSpecialCollection(View view, SpecialCollection specialDice){
        StringBuilder temp = new StringBuilder();
        ArrayList<DiceCollection> specialCollection = specialDice.getSpecialCollection();
        String[] details = specialDice.specialCollectionDetails().split(",");
        for(int j = 0; j < specialCollection.size(); j++){
            temp.append(details[j]).append(" = ").append(rollDiceCollection(specialCollection.get(j)).substring(3)).append('\n');
        }
        String value = temp.toString();
        addDiceReadout(view,specialDice.getPrintName() + " : ", value.substring(0, value.length()-1));
    }
    public void addTypeSignifier(View v, String type){
        LinearLayout diceControl = (LinearLayout) v.findViewById(R.id.dice_display);
        RelativeLayout diceSigRow = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.type_signifier, null);
        TextView typeView = (TextView) diceSigRow.findViewById(R.id.type_signifier);
        typeView.setText(type);
        diceControl.addView(diceSigRow);

    }
    public boolean rollDiceAndDisplay(View view){
        containerActivity.hideKeyboard(view);
        getSpecialsToRoll();
        boolean needToRoll = false;
        boolean rollSpecial;
        boolean rollNormal;
        createDiceCollectionsToRoll();
        LinearLayout diceController = view.findViewById(R.id.dice_display);
        ScrollView diceDisplayScroll =  view.findViewById(R.id.dice_display_scrollview);
        //View separator = view.findViewById(R.id.separator);
        diceController.removeAllViews();
        rollSpecial = !specialCollectionsListForHomeScreen.isEmpty();
        if(rollSpecial){
            addTypeSignifier(view, getActivity().getString(R.string.special_collections));
        }
        for(SpecialCollection special : specialCollectionsListForHomeScreen) {
            if (special.getRoll()) {
                rollSpecialCollection(view, special);
            }
        }
        rollNormal = !diceCollectionsToRoll.isEmpty();
        if(rollNormal){
            addTypeSignifier(view, getActivity().getString(R.string.regular_collections));
        }
        if(rollNormal || rollSpecial){
            diceDisplayScroll.setVisibility(View.VISIBLE);
            diceController.setVisibility(View.VISIBLE);
            needToRoll = true;
            //separator.setVisibility(View.VISIBLE);
        }else{
            diceDisplayScroll.setVisibility(View.GONE);
            diceController.setVisibility(View.GONE);
            //separator.setVisibility(View.GONE);
        }
        int number;
        for(DiceCollection dice : diceCollectionsToRoll){
            number = dice.getNumberOfDice();
            String type = dice.getDiceType();
            StringBuilder toPrint = new StringBuilder(number + "*" + type);
            if(dice.getModifier() != 0){
                toPrint.append(" + " + dice.getModifier());
            }
            addDiceReadout(view, toPrint.toString(), rollDiceCollection(dice));
            if(dice.getSum()){
                diceCollectionsToSum.add(dice.getTempRollTotal());
            }
        }
        if(diceCollectionsToSum.size()>1){
            handleSummedRows(diceCollectionsToSum);
            Log.e("Handling", "SUMS");
        }
        diceCollectionsToSum.clear();
        return needToRoll;
    }
    public void handleSummedRows(ArrayList<Integer> sum){
        int sumTotal = 0;
        StringBuilder temp = new StringBuilder();
        for(int j : sum){
            sumTotal += j;
            temp.append(" + ").append(j);
        }
        temp.append(" = ").append(sumTotal);
        addDiceReadout("Summed Rows :", temp.toString().substring(3));
    }
    Random rand = new SecureRandom();
    public int rolld2() {
        return rand.nextInt(2) + 1;
    }
    public int rolld4() {
        return rand.nextInt(4) + 1;
    }
    public int rolld6() {
        return rand.nextInt(6) + 1;
    }
    public int rolld8() {
        return rand.nextInt(8) + 1;
    }
    public int rolld10() {
        return rand.nextInt(10) + 1;
    }
    public int rolld12() {
        return rand.nextInt(12) + 1;
    }
    public int rolld20() {
        return rand.nextInt(20) + 1;
    }
    public int rolld100(){
        return rand.nextInt(100) + 1;
    }
    /*Takes in the type of dice and the string to be printed. It then creates the dice readout row */
    public void addDiceReadout(String type, String temp) {
        LinearLayout diceControl = (LinearLayout) containerActivity.findViewById(R.id.dice_display);
        RelativeLayout diceReadoutRow = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dice_readout, null);
        TextView readout = (TextView) diceReadoutRow.findViewById(R.id.dice_readout);
        TextView diceType = (TextView) diceReadoutRow.findViewById(R.id.type);
        diceType.setText(type);
        readout.setText(temp);
        diceControl.addView(diceReadoutRow);
    }
    public void addDiceReadout(View v, String type, String temp) {
        LinearLayout diceControl = (LinearLayout) v.findViewById(R.id.dice_display);
        RelativeLayout diceReadoutRow = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dice_readout, null);
        TextView readout = (TextView) diceReadoutRow.findViewById(R.id.dice_readout);
        TextView diceType = (TextView) diceReadoutRow.findViewById(R.id.type);
        diceType.setText(type);
        readout.setText(temp);
        diceControl.addView(diceReadoutRow);
    }
    private View viewTouched;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = containerActivity.getMenuInflater();
        inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        viewTouched = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LinearLayout dice_row = (LinearLayout) viewTouched;
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteRowMenu(dice_row);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void deleteRowMenu(LinearLayout row){
        boolean needed = false;
        if(row.getId() == R.id.collection_row) {
            diceRows.remove(row);
            needed = true;
        }else if(row.getId() == R.id.special_collection_roll){
            TextView nameView = row.findViewById(R.id.special_collection_name);
            String temp = "SpecialCollection" + nameView.getText() + ".txt";
            int n = fileNamesForSpecialsToAddToHomeScreen.indexOf(temp);
            fileNamesForSpecialsToAddToHomeScreen.remove(n);
            specialCollectionsListForHomeScreen.remove(n);
            specialRowsOnHomeScreen.remove(row);
            needed = true;
        }
        if(specialRowsOnHomeScreen.isEmpty() && needed){
            LinearLayout specialSignify = rootView.findViewById(R.id.special_signifier);
            specialSignify.setVisibility(View.GONE);
            View specialSeparator = rootView.findViewById(R.id.special_separator);
            specialSeparator.setVisibility(View.GONE);
            specialSeparator = rootView.findViewById(R.id.special_signify_separator);
            specialSeparator.setVisibility(View.GONE);
        }
        ((ViewManager) row.getParent()).removeView(row);
    }
}
