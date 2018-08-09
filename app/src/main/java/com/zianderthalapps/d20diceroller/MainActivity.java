package com.zianderthalapps.d20diceroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random rand = new SecureRandom(); //random number generator
    ArrayList<LinearLayout> diceRows = new ArrayList<>(); //An array that keeps track of the diceRows that are displayed on screen.
    //Lists of Special Collection info
    ArrayList<LinearLayout> specialRows = new ArrayList<>(); //An array that keeps track of the specialRowsOnHomeScreen to be displayed on screen.
    ArrayList<String> specialsToAdd = new ArrayList<>(); //An array of file names of special collections to add to screen
    ArrayList<SpecialCollection> specialCollections = new ArrayList<>(); //Array of the special collections displayed on screen.

    View contextViewTouched = null; //Tells me which row to delete if a context menu calls delete.
    LinearLayout parent; //parent view in activity_main
    Context context; //context to get files directory
    ArrayList<DiceCollection> diceCollectionsToRoll = new ArrayList<>(); //List of diceCollections that need to be rolled.
    ArrayList<Integer> diceCollectionsToSum = new ArrayList<>(); //List of roll values for diceCollections that need to be summed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.parent);
        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(getParent());
                return true;
            }
        });
        context = getApplicationContext();
        Intent intent = getIntent();
        if(intent.hasExtra("SPECIAL_SELECTION")){
            specialsToAdd = intent.getStringArrayListExtra("SPECIAL_SELECTION");
            if(!specialsToAdd.isEmpty()){
                createSpecialCollectionsList();
                addSpecialsToScreen();
            }

        }
        addDice(null);

    }
    /*Fills in the dice type spinner*/
    private void setDiceSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dice_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    /*Reads in the specialCollectionsListForHomeScreen from their files and adds them to the ArrayList specialCollectionsListForHomeScreen.*/
    public void createSpecialCollectionsList(){
        for(String fileName : specialsToAdd){
            File file = new File(context.getFilesDir(), fileName);

            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                SpecialCollection temp = (SpecialCollection) inputStream.readObject();
                specialCollections.add(temp);
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*Adds all of the desired specialCollectionsListForHomeScreen to the screen*/
    public void addSpecialsToScreen(){
        LinearLayout specialSignify = findViewById(R.id.special_signifier);
        specialSignify.setVisibility(View.VISIBLE);
        View specialSeparator = findViewById(R.id.special_separator);
        specialSeparator.setVisibility(View.VISIBLE);
        specialSeparator = findViewById(R.id.special_signify_separator);
        specialSeparator.setVisibility(View.VISIBLE);
        for(SpecialCollection dice : specialCollections) {
            LinearLayout specialLayout = findViewById(R.id.special_collection_view);
            LinearLayout specialRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.special_collection_roll, null);
            populateCollectionRow(dice, specialRow);
            specialLayout.addView(specialRow);
            specialRows.add(specialRow);
            registerForContextMenu(specialRow);
        }
    }
    /*Retrieves the special collections that have 'roll' checked*/
    public void getSpecialsToRoll(){
        int k = 0;
        for(k = 0; k < specialRows.size(); k++){
            LinearLayout row = specialRows.get(k);
            CheckBox toRoll = row.findViewById(R.id.special_collection_toggle_roll);
            if(toRoll.isChecked()){
                specialCollections.get(k).setRoll(true);
            }else{
                specialCollections.get(k).setRoll(false);
            }
        }
    }
    /*
    This method is called when the "Add Dice" button is clicked. It increments the number of dice rows stored in totaldice

    It creates a LinearLayout by inflating it from the XML file dice_row.xml
    it initializes the new spinner. Adds the new row to the dice layout view. It also adds the new row to the arraylist diceRows.

    */
    public void addDice(View view) {
        LinearLayout diceLayout = (LinearLayout) findViewById(R.id.dice_view);
        LinearLayout diceRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dice_row, null);
        Spinner spinner = (Spinner) diceRow.findViewById(R.id.dice_spinner);
        setDiceSpinner(spinner);
        diceRow.findViewById(R.id.dice_input).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        diceRow.findViewById(R.id.constant_input).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        diceLayout.addView(diceRow);
        diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }

//NEW CODE
    /*Fills in the SpecialCollection's info for the layout that's created for it*/
    public void populateCollectionRow(SpecialCollection dice, LinearLayout row){
        TextView tv = row.findViewById(R.id.special_collection_details);
        tv.setText(dice.specialCollectionDetails());
        tv = row.findViewById(R.id.special_collection_name);
        tv.setText(dice.getPrintName());
    }

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
                diceCollectionsToRoll.add(dice);
                CheckBox toggleSum = row.findViewById(R.id.sum_row);
                dice.setSum(toggleSum.isChecked());
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
    public void rollSpecialCollection(SpecialCollection specialDice){
        StringBuilder temp = new StringBuilder();
        ArrayList<DiceCollection> specialCollection = specialDice.getSpecialCollection();
        for(DiceCollection dice : specialCollection){
            temp.append(rollDiceCollection(dice).substring(3)).append('\n');
        }
        String value = temp.toString();
        addDiceReadout(specialDice.getPrintName(), value.substring(0, value.length()-1));
    }

    public void rollDiceAndDisplay(View view){
        hideKeyboard(view);
        getSpecialsToRoll();
        boolean rollSpecial = false;
        createDiceCollectionsToRoll();
        LinearLayout diceController = findViewById(R.id.dice_display);
        ScrollView diceDisplayScroll =  findViewById(R.id.dice_display_scrollview);
        View separator = findViewById(R.id.separator);
        diceController.removeAllViews();
        for(SpecialCollection special : specialCollections) {
            if (special.getRoll()) {
                rollSpecial = true;
                rollSpecialCollection(special);
            }
        }
        if(!diceCollectionsToRoll.isEmpty() || rollSpecial){
            diceDisplayScroll.setVisibility(View.VISIBLE);
            diceController.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
        }else{
            diceDisplayScroll.setVisibility(View.GONE);
            diceController.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }
        int number;
        for(DiceCollection dice : diceCollectionsToRoll){
            number = dice.getNumberOfDice();
            String type = dice.getDiceType();
            /*switch (type) {
                case "d2":
                    type = "  d2";
                    break;
                case "d4":
                    type = "  d4";
                    break;
                case "d6":
                    type = "  d6";
                    break;
                case "d8":
                    type = "  d8";
                    break;
                case "d10":
                    type = "d10";
                    break;
                case "d12":
                    type = "d12";
                    break;
                case "d20":
                    type = "d20";
                    break;
                case "Perc":
                    type = "    %";
                    break;
                case "d100":
                    type = "100";
                    break;
            }*/
            String toPrint = number + "*" + type + " + " + dice.getModifier();
            addDiceReadout(toPrint, rollDiceCollection(dice));
            if(dice.getSum()){
                diceCollectionsToSum.add(dice.getTempRollTotal());
            }
        }
        if(diceCollectionsToSum.size()>1){
            handleSummedRows(diceCollectionsToSum);
        }
        diceCollectionsToSum.clear();
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
        LinearLayout diceControl = (LinearLayout) findViewById(R.id.dice_display);
        RelativeLayout diceReadoutRow = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dice_readout, null);
        TextView readout = (TextView) diceReadoutRow.findViewById(R.id.dice_readout);
        TextView diceType = (TextView) diceReadoutRow.findViewById(R.id.type);
        diceType.setText(type);
        readout.setText(temp);
        diceControl.addView(diceReadoutRow);
    }
    public void reset(View view){
        diceRows.clear();
        LinearLayout diceView = findViewById(R.id.dice_view);
        diceView.removeAllViews();
        addDice(diceView);
        ScrollView diceDisplay = findViewById(R.id.dice_display_scrollview);
        diceDisplay.setVisibility(View.GONE);
        LinearLayout specialSignify = findViewById(R.id.special_signifier);
        specialSignify.setVisibility(View.GONE);
        View specialSeparator = findViewById(R.id.special_separator);
        specialSeparator.setVisibility(View.GONE);
        specialSeparator = findViewById(R.id.special_signify_separator);
        specialSeparator.setVisibility(View.GONE);
        specialsToAdd.clear();
        LinearLayout specialLayout = findViewById(R.id.special_collection_view);
        specialLayout.removeAllViews();

    }
    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        contextViewTouched = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LinearLayout dice_row = (LinearLayout) contextViewTouched;
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteRowMenu(dice_row);
                return true;
            case R.id.toggle_roll:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteRowMenu(LinearLayout row){
        if(row.getId() == R.id.collection_row) {
            diceRows.remove(row);
        }else if(row.getId() == R.id.special_collection_roll){
            TextView nameView = row.findViewById(R.id.special_collection_name);
            String temp = "SpecialCollection" + nameView.getText() + ".txt";
            int n = specialsToAdd.indexOf(temp);
            specialsToAdd.remove(n);
            specialCollections.remove(n);
            specialRows.remove(row);
        }
        if(specialRows.isEmpty()){
            LinearLayout specialSignify = findViewById(R.id.special_signifier);
            specialSignify.setVisibility(View.GONE);
            View specialSeparator = findViewById(R.id.special_separator);
            specialSeparator.setVisibility(View.GONE);
            specialSeparator = findViewById(R.id.special_signify_separator);
            specialSeparator.setVisibility(View.GONE);
        }
        ((ViewManager) row.getParent()).removeView(row);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.options_create_special:
                callCreateSpecial();
                return true;
            case R.id.options_add_special:
                callAddSpecial();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Sends the user to the CreateSpecial Activity*/
    public void callCreateSpecial(){
        Intent intent = new Intent(this, CreateSpecial.class);
        addSpecialsToIntent(intent);
        startActivity(intent);
    }
    /*Sends the user to the AddSpecial Activity*/
    public void callAddSpecial(){
        Intent intent = new Intent(this, AddSpecialCollections.class);
        addSpecialsToIntent(intent);
        startActivity(intent);
    }
    /*adds the list of specials the user has already added to their screen*/
    public void addSpecialsToIntent(Intent intent){
        Bundle b = new Bundle();
        b.putString("FROM", "MAIN_ACTIVITY");
        b.putStringArrayList("SPECIAL_SELECTION", specialsToAdd);
        intent.putExtras(b);
    }

    public void deleteRow(View view) {
        LinearLayout row = (LinearLayout) view.getParent();
        diceRows.remove(row);
        ((ViewManager) row.getParent()).removeView(row);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
