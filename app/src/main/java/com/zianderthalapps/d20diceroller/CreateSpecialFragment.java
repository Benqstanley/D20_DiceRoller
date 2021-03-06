package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CreateSpecialFragment extends Fragment {
    ArrayList<LinearLayout> diceRowsForSpecial = new ArrayList<>(); //An array that keeps track of the diceRows that are displayed on screen when user is creating a special Collection.
    ContainerActivity containerActivity;
    ArrayList<DiceCollection> collections = new ArrayList<DiceCollection>(); //A list that keeps track of all of the user's DiceCollections to be added to this SpecialCollection
    View viewTouched;
    public CreateSpecialFragment() {
        // Required empty public constructor
    }
    //TODO: enable grouping
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set the Action Bar title to represent the appropriate fragment
        containerActivity = (ContainerActivity) getActivity();
        //Create the view to be returned. Inflate it with the location_list layout
        rootView = inflater.inflate(R.layout.fragment_create_special, container, false);
        addDice(rootView);
        Button addDiceButton = rootView.findViewById(R.id.add_dice);
        Button saveSpecialCollection = rootView.findViewById(R.id.save);
        Button resetScreenButton = rootView.findViewById(R.id.reset_screen);
        addDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDice(rootView);
            }
        });
        saveSpecialCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(rootView);            }
        });
        resetScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(rootView);
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
    /*
    This method is called when the "Add Dice" button is clicked. It increments the number of dice rows stored in totaldice

    It creates a LinearLayout by inflating it from the XML file dice_row.xml
    it initializes the new spinner. Adds the new row to the dice layout view. It also adds the new row to the arraylist diceRows.

    */
    public void addDice(View view) {
        LinearLayout diceLayout = (LinearLayout) view.findViewById(R.id.dice_view);
        LinearLayout diceRow = (LinearLayout) LayoutInflater.from(containerActivity).inflate(R.layout.dice_row_create, null);
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
        diceRowsForSpecial.add(diceRow);
        registerForContextMenu(diceRow);
    }

    /*Called when the save button is pressed.
     * Ensures that the SpecialCollection name isn't already used. If the fileName is available this creates a
     * SpecialCollection with the appropriate filename by calling createSpecialCollection.
     * That SpecialCollection is then saved as an object into an appropriate file. "SpecialCollection" + input name
     * The user is then sent back to the MainActivity*/
    public void save(View view) {
        TextView nameView = view.findViewById(R.id.name_of_collection);
        String filename = "SpecialCollection" + nameView.getText().toString() + ".txt";
        //directory = getDir(dirname, Context.MODE_PRIVATE);
        File file = new File(getContext().getFilesDir(), filename);
        if(file.exists()){
            containerActivity.displayToast("A Collection With That Name Already Exists");
        }else {
            if (!filename.equals("")) {
                SpecialCollection specialCollection = createSpecialCollection(filename);
                if(!specialCollection.getSpecialCollection().isEmpty()) {
                    try {
                        FileOutputStream foutputStream = new FileOutputStream(file);
                        ObjectOutputStream outputStream = new ObjectOutputStream(foutputStream);
                        outputStream.writeObject(specialCollection);
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    containerActivity.displayToast("\'" + specialCollection.getPrintName() + "\'" + " successfully saved");
                    containerActivity.setNewSpecial(true);
                    containerActivity.setActionBarTitle(R.string.view_special_collections);
                    containerActivity.returnFragmentManager().beginTransaction().replace(R.id.content_frame, new ViewSpecialFragment()).commit();
                } else {
                    containerActivity.displayToast("This Collection is Empty!");

                }
            } else {
                containerActivity.displayToast("Enter a Name For Your Collection!");
            }
        }
        collections.clear();
    }

    public SpecialCollection createSpecialCollection(String name){
        createCollectionList();
        SpecialCollection specialCollection = new SpecialCollection();
        specialCollection.setSpecialCollection(collections);
        specialCollection.setFileName(name);
        return specialCollection;
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
    public void reset(View view){
        diceRowsForSpecial.clear();
        LinearLayout diceView = view.findViewById(R.id.dice_view);
        diceView.removeAllViews();
        addDice(diceView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = containerActivity.getMenuInflater();
        if(v.getId() == R.id.special_collection_row){
            inflater.inflate(R.menu.view_special_context_menu, menu);
        }else{
            inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        }

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
        diceRowsForSpecial.remove(row);
        ((ViewManager) row.getParent()).removeView(row);
    }

}
