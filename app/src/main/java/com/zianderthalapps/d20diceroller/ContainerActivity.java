package com.zianderthalapps.d20diceroller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class ContainerActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    FragmentManager fragmentManager;
    boolean newSpecial = false;
    public ArrayList<String> getFileNamesForSpecialsToAddToHomeScreen(){
        return fileNamesForSpecialsToAddToHomeScreen;
    }
    //FOR HOME
    Random rand = new SecureRandom();
    ArrayList<LinearLayout> diceRows = new ArrayList<>(); //An array that keeps track of the diceRows that are displayed on screen.
    ArrayList<LinearLayout> diceRowsForSpecial = new ArrayList<>(); //An array that keeps track of the diceRows that are displayed on screen when user is creating a special Collection.
    //Lists of Special Collection info
    ArrayList<LinearLayout> specialRowsOnHomeScreen = new ArrayList<>(); //An array that keeps track of the specialRowsOnHomeScreen to be displayed on screen.
    ArrayList<String> fileNamesForSpecialsToAddToHomeScreen = new ArrayList<>(); //An array of file names of special collections to add to screen in home screen
    ArrayList<SpecialCollection> specialCollectionsListForHomeScreen = new ArrayList<>(); //Array of the special collections displayed on screen.
    ArrayList<SpecialCollection> specialCollectionsListForViewScreen = new ArrayList<>();
    //String dirname = "SpecialCollections"; //An add-on to the file name so that it can be distinguished from other files
    String filename; //Keeps track of the filename
    EditText nameView; //Keeps track of the nameView for the SpecialCollection
    ArrayList<DiceCollection> collections = new ArrayList<DiceCollection>(); //A list that keeps track of all of the user's DiceCollections to be added to this SpecialCollection
    Context context; //Keeps track of context for file writing purposes
    SpecialCollection specialCollection = new SpecialCollection(); //The specialCollection that the user populates and saves
    //File directory;

    View contextViewTouched = null;
    LinearLayout parent;
    ArrayList<DiceCollection> diceCollectionsToRoll = new ArrayList<>();
    ArrayList<Integer> diceCollectionsToSum = new ArrayList<>();


    File[] fileList;
    File directory;
    ArrayList<LinearLayout> specialCollectionRowsForViewScreen = new ArrayList<>();
    ArrayList<String> selectionToDelete = new ArrayList<>();
    ArrayList<LinearLayout> selectionRowsToDelete = new ArrayList<>();
    LinearLayout collectionLayout;
    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //Display the UP button
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Change the up button to the hamburger icon to indicate the drawer
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ((TextView) findViewById(R.id.title)).setText(getText(R.string.app_name));
        //Remove the shadow that the action bar creates. Sets it to the same elevation as the rest of the layout.
        actionBar.setElevation(0);
        //Get a fragmentManager to switch out fragments
        fragmentManager = getSupportFragmentManager();
        //Initialize the screen with the FoodFragment (List of restaurants and fast food)
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        //Find the DrawerLayout so that we can close the drawer when items are selected.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Find the NavigationView so that we can respond to user interaction
        NavigationView navigationView = findViewById(R.id.nav_view);
        //Set up the ItemSelectedListener for the NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Check the selected item so that it is highlighted in the menu
                item.setChecked(true);
                //Close the Drawer once an item is selected.
                mDrawerLayout.closeDrawers();
                //Cases so that we can respond to the item selected
                switch (item.getItemId()){
                    //Each case selects the appropriate fragment and displays it.
                    case R.id.nav_home:
                        setActionBarTitle(R.string.home);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                        break;
                    case R.id.nav_create:
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new CreateSpecialFragment()).commit();
                        break;
                    case R.id.nav_view:
                        setActionBarTitle(R.string.view_special_collections);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewSpecialFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
    public void toHome(){
        setActionBarTitle(R.string.home);
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
    }
    public boolean addDiceRowsToScreen(View view){
        LinearLayout diceLayout = (LinearLayout) view.findViewById(R.id.dice_view);
        for(int i = 0; i < diceRows.size(); i++){
            diceLayout.addView(diceRows.get(i));
        }
        if(diceRows.isEmpty()){
            return true;
        }
        return false;
    }
    public void shedParent(View rootView){
        if(rootView.findViewById(R.id.roll_dice) != null){
            for(View view : diceRows){
                ((ViewManager) view.getParent()).removeView(view);
            }
        }else{
            diceRowsForSpecial.clear();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                //Open the drawer from the start side.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //A method to adjust the action bar title.
    public void setActionBarTitle(int stringResourceId){
        ((TextView) toolbar.findViewById(R.id.title)).setText(getString(stringResourceId));
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
    public void createSpecialCollectionsListForScreen(){
        specialCollectionsListForHomeScreen.clear();
        for(String fileName : fileNamesForSpecialsToAddToHomeScreen){
            File file = new File(context.getFilesDir(), fileName);

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
            LinearLayout specialRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.special_collection_roll, null);
            populateCollectionRow(dice, specialRow);
            specialLayout.addView(specialRow);
            specialRowsOnHomeScreen.add(specialRow);
            registerForContextMenu(specialRow);
        }
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
    public void addDice(View view) {
        LinearLayout diceLayout = (LinearLayout) view.findViewById(R.id.dice_view);
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
        if(view.findViewById(R.id.save) != null){
            diceRowsForSpecial.add(diceRow);
        }else{
            diceRows.add(diceRow);
        }

        registerForContextMenu(diceRow);
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

    public boolean rollDiceAndDisplay(View view){
        hideKeyboard(view);
        getSpecialsToRoll();
        boolean needToRoll = false;
        boolean rollSpecial = false;
        boolean rollNormal = false;
        createDiceCollectionsToRoll();
        LinearLayout diceController = view.findViewById(R.id.dice_display);
        ScrollView diceDisplayScroll =  view.findViewById(R.id.dice_display_scrollview);
        //View separator = view.findViewById(R.id.separator);
        diceController.removeAllViews();
        rollSpecial = !specialCollectionsListForHomeScreen.isEmpty();
        if(rollSpecial){
            addDiceReadout(view, "Special Collections", null);
        }
        for(SpecialCollection special : specialCollectionsListForHomeScreen) {
            if (special.getRoll()) {
                rollSpecialCollection(view, special);
            }
        }
        rollNormal = !diceCollectionsToRoll.isEmpty();
        if(rollNormal){
            addDiceReadout(view, "Regular Collections", null);
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
            addDiceReadout(view, toPrint, rollDiceCollection(dice));
            if(dice.getSum()){
                diceCollectionsToSum.add(dice.getTempRollTotal());
            }
        }
        if(diceCollectionsToSum.size()>1){
            handleSummedRows(diceCollectionsToSum);
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
    public void addDiceReadout(View v, String type, String temp) {
        LinearLayout diceControl = (LinearLayout) v.findViewById(R.id.dice_display);
        RelativeLayout diceReadoutRow = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dice_readout, null);
        TextView readout = (TextView) diceReadoutRow.findViewById(R.id.dice_readout);
        TextView diceType = (TextView) diceReadoutRow.findViewById(R.id.type);
        diceType.setText(type);
        readout.setText(temp);
        diceControl.addView(diceReadoutRow);
    }
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
    public void resetCreate(View view){
        diceRows.clear();
        LinearLayout diceView = findViewById(R.id.dice_view);
        diceView.removeAllViews();
        addDice(diceView);
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
        if(v.getId() == R.id.special_collection_row){
            inflater.inflate(R.menu.view_special_context_menu, menu);
        }else{
            inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        }

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
            case R.id.menu_edit:
                editSpecial(dice_row);
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void editSpecial(LinearLayout dice_row){
        filename = "SpecialCollection" + ((TextView) dice_row.findViewById(R.id.special_collection_name)).getText() + ".txt";
        //TODO: Open this file in an edit screen. Allow overwriting when saving.
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
        }else if(row.getId() == R.id.special_collection_row){
            String fileName = ((TextView) row.findViewById(R.id.special_collection_name)).getText().toString();
            String temp = "SpecialCollection" + fileName + ".txt";
            specialCollectionRowsForViewScreen.remove(row);
            File file = new File(context.getFilesDir(), temp);
            if(file.exists()){
                displayToast("Deleting File");
                file.delete();
                needed = false;
            }
        }
        if(specialRowsOnHomeScreen.isEmpty() && needed){
            LinearLayout specialSignify = findViewById(R.id.special_signifier);
            specialSignify.setVisibility(View.GONE);
            View specialSeparator = findViewById(R.id.special_separator);
            specialSeparator.setVisibility(View.GONE);
            specialSeparator = findViewById(R.id.special_signify_separator);
            specialSeparator.setVisibility(View.GONE);
        }
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
    /*Called when the save button is pressed.
     * Ensures that the SpecialCollection name isn't already used. If the fileName is available this creates a
     * SpecialCollection with the appropriate filename by calling createSpecialCollection.
     * That SpecialCollection is then saved as an object into an appropriate file. "SpecialCollection" + input name
     * The user is then sent back to the MainActivity*/
    public void save(View view) {
        nameView = view.findViewById(R.id.name_of_collection);
        filename = "SpecialCollection" + nameView.getText().toString() + ".txt";
        //directory = getDir(dirname, Context.MODE_PRIVATE);
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            displayToast("A Collection With That Name Already Exists");
        }else {
            if (!filename.equals("")) {
                createSpecialCollection(filename);
                if(!specialCollection.getSpecialCollection().isEmpty()) {
                    try {
                        FileOutputStream foutputStream = new FileOutputStream(file);
                        ObjectOutputStream outputStream = new ObjectOutputStream(foutputStream);
                        outputStream.writeObject(specialCollection);
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    displayToast("\'" + specialCollection.getPrintName() + "\'" + " successfully saved");
                    newSpecial = true;
                    setActionBarTitle(R.string.view_special_collections);
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewSpecialFragment()).commit();
                } else {
                    displayToast("This Collection is Empty!");

                }
            } else {
                displayToast("Enter a Name For Your Collection!");
            }
        }
        diceRowsForSpecial.clear();
        collections.clear();
    }

    public void createSpecialCollection(String name){
        createCollectionList();
        specialCollection.setSpecialCollection(collections);
        specialCollection.setFileName(name);
    }
    /*The diceRows ArrayList is looped through. If the row is not empty then its information is copied into a
     * DiceCollection. It's then added to the ArrayList collections.*/
    public void createCollectionList(){
        for(LinearLayout r : diceRowsForSpecial){
            EditText edit = r.findViewById(R.id.dice_input);
            String num = edit.getText().toString();
            if(!num.equals("")) {
                DiceCollection temp = new DiceCollection();
                temp.setNumberOfDice(Integer.parseInt(num));
                Spinner spinner = r.findViewById(R.id.dice_spinner);
                temp.setDiceType(spinner.getSelectedItem().toString());
                edit = r.findViewById(R.id.constant_input);
                num = edit.getText().toString();
                if(!num.equals("")){
                    temp.setModifier(Integer.parseInt(num));
                }else{
                    temp.setModifier(0);
                }
                collections.add(temp);
            }

        }
    }

    /*    populateCollectionView creates a list of the files saved in the app. It then calls createSpecialCollectionsListForViewScreen.
    If no saved special collections are found a message is displayed telling the user that no special collections are saved.
    If there are saved special collections then each is called for each one.*/
    public void populateCollectionView(){
        context = getApplicationContext();
        directory = context.getFilesDir();
        fileList = directory.listFiles();
        if(!(specialCollectionsListForViewScreen.size() == fileList.length - 1) || newSpecial){
            createSpecialCollectionsListForViewScreen();
            newSpecial = false;
        }
        if(specialCollectionsListForViewScreen.isEmpty()){
            displayToast("There Doesn't Seem To Be Anything Here");
        }
        specialCollectionRowsForViewScreen.clear();
        for(SpecialCollection dice : specialCollectionsListForViewScreen){
            addCollectionRow(dice);
        }
    }

    /*A LinearLayout is inflated so it has the format to be displayed. Then populateCollectionRow is called to fill in the details. Once the details are filled
     * in the layout is added to collectionLayout. It's also added to specialCollectionRowsForViewScreen for checking purposes.
     * Each LinearLayout is registered for a context menu that allows the user to edit or delete the specialCollection*/
    public void addCollectionRow(SpecialCollection dice) {
        LinearLayout specialCollectionRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.special_collection_row, null);
        populateCollectionRow(dice, specialCollectionRow);
        collectionLayout.addView(specialCollectionRow);
        specialCollectionRowsForViewScreen.add(specialCollectionRow);
        registerForContextMenu(specialCollectionRow);
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
    /*The list of files is run through in a for loop. The specialCollection object is read from each file and thrown into the list
     * specialCollectionsListForHomeScreen. */
    public void createSpecialCollectionsListForViewScreen(){
        specialCollectionsListForViewScreen.clear();
        for(File file : fileList){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                SpecialCollection temp = (SpecialCollection) inputStream.readObject();
                specialCollectionsListForViewScreen.add(temp);
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCollectionLayout(LinearLayout l){
        collectionLayout = l;
    }

    /*Builds the users selection from the checkboxes.*/
    public void buildSelection(){
        selectionToDelete.clear();
        selectionRowsToDelete.clear();
        int n = specialCollectionRowsForViewScreen.size();
        Log.e("buildSelection", "specialCollectionRowsForViewScreen size: " + n);
        CheckBox selectionBox;
        Boolean selected;
        LinearLayout row;
        for(int j = 0; j < n; j++){
            row = specialCollectionRowsForViewScreen.get(j);
            selectionBox = row.findViewById(R.id.special_collection_include_check_box);
            selected = selectionBox.isChecked();
            if(selected){
                fileNamesForSpecialsToAddToHomeScreen.add(specialCollectionsListForViewScreen.get(j).getFileName());
                selectionToDelete.add(specialCollectionsListForViewScreen.get(j).getFileName());
                selectionRowsToDelete.add(specialCollectionRowsForViewScreen.get(j));
            }

        }
    }
    public void deleteSelection(){
        buildSelection();
        if(!selectionToDelete.isEmpty()){
            for(String fileName : selectionToDelete){
                File file = new File(context.getFilesDir(), fileName);
                if(file.exists()){
                    displayToast("Deleting File");
                    file.delete();
                }
            }
            for(LinearLayout row : selectionRowsToDelete){
                ((ViewManager) row.getParent()).removeView(row);
            }
            selectionToDelete.clear();
        }
    }
    public void deleteEverything(){
        for(File file : fileList){
            file.delete();
        }
        LinearLayout display = findViewById(R.id.collection_view);
        display.removeAllViews();
        setActionBarTitle(R.string.home);
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        displayToast("Everything has been destroyed");
    }

    public void setOverflowButtonIcon(Drawable icon) {
        if(icon != null) {
            icon = DrawableCompat.wrap(icon);
            toolbar.setOverflowIcon(icon);
        }
    }

    public void setIcon(int icon){
        ImageView image = (ImageView) toolbar.findViewById(R.id.action_bar_image);
        if(icon != 0) {
            image.setImageResource(icon);
            image.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.GONE);
        }
    }
}
