package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddSpecialCollections extends AppCompatActivity {
    File[] fileList;
    Context context;
    File directory;
    ArrayList<SpecialCollection> specialCollections = new ArrayList<>();
    ArrayList<LinearLayout> specialCollectionRows = new ArrayList<>();
    ArrayList<String> selectionToPass = new ArrayList<>();
    ArrayList<String> selectionPassedIn = new ArrayList<>();
    ArrayList<File> selectionFiles = new ArrayList<>();
    ArrayList<LinearLayout> selectionRows = new ArrayList<>();
    LinearLayout collectionLayout;

    //onCreate calls populateCollectionView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_special_collections);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collectionLayout = findViewById(R.id.collection_view);
        populateCollectionView();
        Intent intent = getIntent();
        if(intent.hasExtra("SPECIAL_SELECTION")) {
            selectionPassedIn = intent.getStringArrayListExtra("SPECIAL_SELECTION");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_special_activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_special_options_create_special:
                callCreateSpecial();
                return true;
            case R.id.add_special_options_delete_selection:
                deleteSelection();
                return true;
            case R.id.add_special_options_delete_everything:
                deleteEverything();
                return true;
            case android.R.id.home:
                callMainWithOutdatedSelection();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void callCreateSpecial(){
        Intent intent = new Intent(this, CreateSpecial.class);
        addSpecialsToIntent(intent, selectionPassedIn);
        startActivity(intent);
    }
/*    populateCollectionView creates a list of the files saved in the app. It then calls createSpecialCollectionsList.
    If no saved special collections are found a message is displayed telling the user that no special collections are saved.
    If there are saved special collections then each addCollectionRow is called for each one.*/
    public void populateCollectionView(){
        context = getApplicationContext();
        directory = context.getFilesDir();
        fileList = directory.listFiles();
        createSpecialCollectionsList();
        if(specialCollections.isEmpty()){
            displayToast("There Doesn't Seem To Be Anything Here");
        }
        for(SpecialCollection dice : specialCollections){
            addCollectionRow(dice);
        }
    }
    /*The list of files is run through in a for loop. The specialCollection object is read from each file and thrown into the list
     * specialCollections. */
    public void createSpecialCollectionsList(){
        for(File file : fileList){
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
    /*A LinearLayout is inflated so it has the format to be displayed. Then populateCollectionRow is called to fill in the details. Once the details are filled
     * in the layout is added to collectionLayout. It's also added to specialCollectionRows for checking purposes.
      * Each LinearLayout is registered for a context menu that allows the user to edit or delete the specialCollection*/
    public void addCollectionRow(SpecialCollection dice) {
        LinearLayout specialCollectionRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.special_collection_row, null);
        populateCollectionRow(dice, specialCollectionRow);
        collectionLayout.addView(specialCollectionRow);
        specialCollectionRows.add(specialCollectionRow);
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


    public void addSpecialsToIntent(Intent intent, ArrayList<String> toPass){
        intent.putExtra("FROM", "ADD_SPECIAL_COLLECTIONS");
        intent.putStringArrayListExtra("SPECIAL_SELECTION", toPass);
    }
    /*Sends the user back to main with their selection after calling buildSelection */
    public void callMainWithUpdatedSelection(View view){
        Intent intent = new Intent(this, MainActivity.class);
        buildSelection();
        if(!selectionToPass.isEmpty()){
            addSpecialsToIntent(intent, selectionToPass);
            startActivity(intent);
        }else{
            displayToast("No Selection Has Been Made");
        }

    }
    /*Builds the users selection from the checkboxes.*/
    public void buildSelection(){
        int n = specialCollections.size();
        CheckBox selectionBox;
        Boolean selected;
        LinearLayout row;
        selectionToPass = selectionPassedIn;
        for(int j = 0; j < n; j++){
            row = specialCollectionRows.get(j);
            selectionBox = row.findViewById(R.id.special_collection_include_check_box);
            selected = selectionBox.isChecked();
            if(selected){
                selectionToPass.add(specialCollections.get(j).getFileName());
                selectionRows.add(specialCollectionRows.get(j));
            }

        }
    }
    /*Sends the user back to main activity without a selection*/
    public void callMainWithOutdatedSelection(){
        Intent intent = new Intent(this, MainActivity.class);
        addSpecialsToIntent(intent, selectionPassedIn);
        startActivity(intent);
    }



    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }
    public void deleteSelection(){
        buildSelection();
        if(!selectionToPass.isEmpty()){
            for(String fileName : selectionToPass){
                File file = new File(context.getFilesDir(), fileName);
                if(file.exists()){
                    displayToast("Deleting File");
                    file.delete();
                }
            }
            for(LinearLayout row : selectionRows){
                ((ViewManager) row.getParent()).removeView(row);
            }
            selectionPassedIn.removeAll(selectionToPass);
            selectionToPass.clear();
        }
    }
    public void deleteEverything(){
        for(File file : fileList){
            file.delete();
        }
        LinearLayout display = findViewById(R.id.collection_view);
        display.removeAllViews();
    }

}
