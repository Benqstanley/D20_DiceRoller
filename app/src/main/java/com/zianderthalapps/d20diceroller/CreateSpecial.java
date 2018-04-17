package com.zianderthalapps.d20diceroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class CreateSpecial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_special);
        addDice(null);
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
       // diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }
    public void save(View view){
        callMain();
    }
    public void callMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
