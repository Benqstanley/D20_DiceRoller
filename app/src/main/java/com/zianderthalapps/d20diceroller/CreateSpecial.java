package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CreateSpecial extends AppCompatActivity {
    String dirname = "SpecialCollections";
    String filename;
    EditText nameView;
    ArrayList<LinearLayout> diceRows = new ArrayList<LinearLayout>();
    ArrayList<DiceCollection> collections = new ArrayList<DiceCollection>();
    Context context;
    SpecialCollection specialCollection = new SpecialCollection();
    File directory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_special);
        addDice(null);
        context  = getApplicationContext();
    }
    private void setDiceSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dice_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    public void addDice(View view) {
        LinearLayout diceLayout = (LinearLayout) findViewById(R.id.dice_view);
        LinearLayout diceRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dice_row, null);
        Spinner spinner = (Spinner) diceRow.findViewById(R.id.dice_spinner);
        setDiceSpinner(spinner);
        diceLayout.addView(diceRow);
        diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }
    public void createSpecialCollection(String name){
        createCollectionList();
        specialCollection.setSpecialCollection(collections);
        specialCollection.setName(name);
    }
    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }

    public void save(View view) {
        nameView = findViewById(R.id.name_of_collection);
        filename = "SpecialCollection" + nameView.getText().toString() + ".txt";
        directory = getDir(dirname, Context.MODE_PRIVATE);
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
    public void callMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
