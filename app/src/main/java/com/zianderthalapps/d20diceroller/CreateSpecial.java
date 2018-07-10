package com.zianderthalapps.d20diceroller;

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
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CreateSpecial extends AppCompatActivity {
    //String dirname = "SpecialCollections"; //An add-on to the file name so that it can be distinguished from other files
    String filename; //Keeps track of the filename
    EditText nameView; //Keeps track of the nameView for the SpecialCollection
    ArrayList<LinearLayout> diceRows = new ArrayList<LinearLayout>(); //Keeps track of the LinearLayouts that make up the input rows
    ArrayList<DiceCollection> collections = new ArrayList<DiceCollection>(); //A list that keeps track of all of the user's DiceCollections to be added to this SpecialCollection
    ArrayList<String> specialsToKeep = new ArrayList<>(); //Keeps track of specials already added to dice rollout
    Context context; //Keeps track of context for file writing purposes
    SpecialCollection specialCollection = new SpecialCollection(); //The specialCollection that the user populates and saves
    //File directory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_create_special);
        addDice(null);
        context  = getApplicationContext();
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(intent.hasExtra("SPECIAL_SELECTION")){
            specialsToKeep = intent.getStringArrayListExtra("SPECIAL_SELECTION");
        }
    }
    /*Sets up the dice type spinner*/
    private void setDiceSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dice_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    /*Adds a row so that the user may input a dice collection*/
    public void addDice(View view) {
        LinearLayout diceLayout = findViewById(R.id.dice_view);
        LinearLayout diceRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dice_row, null);
        Spinner spinner = diceRow.findViewById(R.id.dice_spinner);
        setDiceSpinner(spinner);
        diceLayout.addView(diceRow);
        diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }


    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }
    /*Called when the save button is pressed.
    * Ensures that the SpecialCollection name isn't already used. If the fileName is available this creates a
    * SpecialCollection with the appropriate filename by calling createSpecialCollection.
    * That SpecialCollection is then saved as an object into an appropriate file. "SpecialCollection" + input name
    * The user is then sent back to the MainActivity*/
    public void save(View view) {
        nameView = findViewById(R.id.name_of_collection);
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
                    callMain();
                } else {
                    displayToast("This Collection is Empty!");

                }
            } else {
                displayToast("Enter a Name For Your Collection!");
            }
        }
    }
    /*Calls create collectionList to create a list of DiceCollection objects based on the populated rows
    * the user has created.
     * specialCollection is then filled in with this list.
     * fileName is set to the fileName discussed in the save method*/
    public void createSpecialCollection(String name){
        createCollectionList();
        specialCollection.setSpecialCollection(collections);
        specialCollection.setFileName(name);
    }
    /*The diceRows ArrayList is looped through. If the row is not empty then its information is copied into a
    * DiceCollection. It's then added to the ArrayList collections.*/
    public void createCollectionList(){
        for(LinearLayout r : diceRows){
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
    /*This adds the special collections that were sent to this activity to the intent to call main.*/
    public void addSpecialsToIntent(Intent intent){
        Bundle b = new Bundle();
        b.putString("FROM", "MAIN_ACTIVITY");
        b.putStringArrayList("SPECIAL_SELECTION", specialsToKeep);
        intent.putExtras(b);
    }
    /*Creates intent. Calls addSpecialsToIntent to ensure that we keep any specialcollections the user has already
    * added to their rollout screen. Then it sends them back to MainActivity.*/
    public void callMain(){
        Intent intent = new Intent(this, MainActivity.class);
        addSpecialsToIntent(intent);
        startActivity(intent);
    }
    public void reset(View view){
        diceRows.clear();
        LinearLayout diceView = findViewById(R.id.dice_view);
        diceView.removeAllViews();
        addDice(diceView);
    }
    public void deleteRowMenu(LinearLayout row){
        diceRows.remove(row);
        ((ViewManager) row.getParent()).removeView(row);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        contextViewTouched = v;
    }
    View contextViewTouched;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LinearLayout dice_row = (LinearLayout) contextViewTouched;
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteRowMenu(dice_row);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_special_activity_options_menu, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.create_special_options_back_to_main:
                callMain();
                return true;
            case android.R.id.home:
                callMain();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
