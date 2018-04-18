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
import java.util.ArrayList;

public class CreateSpecial extends AppCompatActivity {
    String filename = "SpecialCollections";
    EditText nameView;
    ArrayList<LinearLayout> diceRows = new ArrayList<LinearLayout>();
    ArrayList<DiceCollection> collections = new ArrayList<DiceCollection>();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_special);
        addDice(null);
        context  = getApplicationContext();
        //createDiceValues();

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

    public void save(View view) {
        nameView = findViewById(R.id.name_of_collection);
        String name = nameView.getText().toString();
        filename += name;
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            Context context = getApplicationContext();
            CharSequence text = "A Collection With That Name Already Exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else {
            StringBuilder toStore = new StringBuilder();
            if (!name.equals("")) {
                toStore.append(name);
                createCollectionList();
                if(!collections.isEmpty()) {
                    for (DiceCollection d : collections) {
                        toStore.append(d.getToSave());
                    }
                    toStore.append("End of Collection");
                    try {
                        FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(toStore.toString().getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callMain();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "This Collection is Empty!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            } else {
                Context context = getApplicationContext();
                CharSequence text = "Enter a Name For Your Collection!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                //Toast to tell user to enter a name
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
                temp.createToSave();
                collections.add(temp);
            }

        }
    }
    public void callMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
