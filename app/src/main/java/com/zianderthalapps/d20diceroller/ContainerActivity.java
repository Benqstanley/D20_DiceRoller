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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class ContainerActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    FragmentManager fragmentManager;
    boolean newSpecial = false;
    SpecialCollection toEdit;
    ArrayList<Integer> rowsOnScreen;

    public ArrayList<Integer> getRowsOnScreen() {
        return rowsOnScreen;
    }

    public void setToEdit(SpecialCollection s){
        toEdit = s;
    }

    public SpecialCollection getToEdit() {
        return toEdit;
    }
    //FOR HOME

    ArrayList<LinearLayout> diceRows = new ArrayList<>(); //An array that keeps track of the diceRows that are displayed on screen.
    //Lists of Special Collection info
    ArrayList<LinearLayout> specialRowsOnHomeScreen = new ArrayList<>(); //An array that keeps track of the specialRowsOnHomeScreen to be displayed on screen.
    ArrayList<String> fileNamesForSpecialsToAddToHomeScreen = new ArrayList<>(); //An array of file names of special collections to add to screen in home screen
    ArrayList<SpecialCollection> specialCollectionsListForHomeScreen = new ArrayList<>(); //Array of the special collections displayed on screen.
    Context context; //Keeps track of context for file writing purposes
    //File directory;
    AdView mAdview;
    View contextViewTouched = null;
    LinearLayout parent;

    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if(savedInstanceState != null){
            fileNamesForSpecialsToAddToHomeScreen = savedInstanceState.getStringArrayList("Specials");
            rowsOnScreen = savedInstanceState.getIntegerArrayList("Rows");
            if(rowsOnScreen.size()%3 == 0){
                createRowsFromSavedStateData();
            }
        }
        MobileAds.initialize(this, BuildConfig.AdMob_AppId);
        mAdview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //Display the UP button
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Change the up button to the hamburger icon to indicate the drawer
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        //Remove the shadow that the action bar creates. Sets it to the same elevation as the rest of the layout.
        actionBar.setElevation(0);
        //Get a fragmentManager to switch out fragments
        fragmentManager = getSupportFragmentManager();
        //Initialize the screen with the FoodFragment (List of restaurants and fast food)
        navigate(R.id.nav_home);
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
                navigate(item.getItemId());

                return true;
            }
        });
    }
    public void navigate(int id){
        shedParent();
        AdRequest adRequest = new AdRequest.Builder().build();
        switch (id){
            //Each case selects the appropriate fragment and displays it.
            case R.id.nav_home:
                setActionBarTitle(R.string.home);
                fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                mAdview.loadAd(adRequest);
                break;
            case R.id.nav_create:
                setActionBarTitle(R.string.create_special_collections);
                fragmentManager.beginTransaction().replace(R.id.content_frame, new CreateSpecialFragment()).commit();
                mAdview.loadAd(adRequest);
                break;
            case R.id.nav_view:
                setActionBarTitle(R.string.view_special_collections);
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewSpecialFragment()).commit();
                mAdview.loadAd(adRequest);
                break;
            case R.id.nav_edit:
                setActionBarTitle(R.string.edit_special_collection);
                fragmentManager.beginTransaction().replace(R.id.content_frame, new EditSpecialCollectionFragment()).commit();
                mAdview.loadAd(adRequest);
        }

    }
    boolean diceRowsNeedShed = false;
    boolean specialCollectionRowsNeedShed = false;
    public void setDiceRowsNeedShed(boolean value){
        diceRowsNeedShed = value;
    }
    public void setSpecialCollectionRowsNeedShed(boolean value){
        specialCollectionRowsNeedShed = value;
    }

    public void shedParent() {
        if(diceRowsNeedShed) {
            for (View view : diceRows) {
                ((ViewManager) view.getParent()).removeView(view);
                unregisterForContextMenu(view);
            }
            diceRowsNeedShed = false;
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
    public void setDiceSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dice_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    /*Reads in the specialCollectionsListForHomeScreen from their files and adds them to the ArrayList specialCollectionsListForHomeScreen.*/

    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //SETTERS

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

    public void setNewSpecial(boolean v){
        newSpecial = v;
    }
    public boolean getNewSpecial(){
        return newSpecial;
    }
    //GETTERS
    public FragmentManager returnFragmentManager() {
        return fragmentManager;
    }
    public ArrayList<LinearLayout> getDiceRows(){
        return diceRows;
    }

    public ArrayList<String> getFileNamesForSpecialsToAddToHomeScreen(){
        return fileNamesForSpecialsToAddToHomeScreen;
    }
    public ArrayList<SpecialCollection> getSpecialCollectionsListForHomeScreen(){
        return specialCollectionsListForHomeScreen;
    }
    public ArrayList<LinearLayout> getSpecialRowsOnHomeScreen(){
        return specialRowsOnHomeScreen;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("Specials", fileNamesForSpecialsToAddToHomeScreen);
        rowsOnScreen = new ArrayList<>();
        for(LinearLayout dice: diceRows){
            int numOfDice;
            int constant;
            int spinnerPosition;
            try{
                numOfDice = Integer.parseInt(((EditText) dice.findViewById(R.id.dice_input)).getText().toString());
            }catch(NumberFormatException e){
                numOfDice = 0;
            }
            try{
                constant = Integer.parseInt(((EditText) dice.findViewById(R.id.constant_input)).getText().toString());
            }catch (NumberFormatException e){
                constant = 0;
            }
            rowsOnScreen.add(numOfDice);
            rowsOnScreen.add(((Spinner) dice.findViewById(R.id.dice_spinner)).getSelectedItemPosition());
            rowsOnScreen.add(constant);
        }
        outState.putIntegerArrayList("Rows", rowsOnScreen);
        super.onSaveInstanceState(outState);
    }

    public void createRowsFromSavedStateData(){
        diceRows = new ArrayList<>();
        for (int i = 0; i < rowsOnScreen.size()/3; i++) {
            LinearLayout diceRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dice_row, null);
            Spinner spinner = (Spinner) diceRow.findViewById(R.id.dice_spinner);
            setDiceSpinner(spinner);
            spinner.setSelection(rowsOnScreen.get(3*i + 1));
            EditText diceInput = diceRow.findViewById(R.id.dice_input);
            diceInput.setText(Integer.toString(rowsOnScreen.get(3*i)));
            diceInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
            EditText constantInput = diceRow.findViewById(R.id.constant_input);
            constantInput.setText(Integer.toString(rowsOnScreen.get(3*i + 2)));
            constantInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
            diceRows.add(diceRow);
        }
    }
}
