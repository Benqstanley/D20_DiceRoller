package com.zianderthalapps.d20diceroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


    /*public void createDiceTotals() {
        for (int h = 0; h < 7; h++) {
            diceTotals.add(0);
        }
    }*/

    Random rand = new SecureRandom();
    ArrayList<LinearLayout> diceRows = new ArrayList<LinearLayout>();
    //ArrayList<String> diceValues = new ArrayList<String>();
    //ArrayList<Integer> diceTotals = new ArrayList<Integer>();
    ArrayList<Integer> diceTotalsByRow = new ArrayList<Integer>(); //Will keep track of diceTotalsByRow
    ArrayList<String> diceValuesByRow = new ArrayList<String>(); //Keeps track of output string by row
    ArrayList<String> diceTypeByRow = new ArrayList<String>();
    String value = "none";
    View contextViewTouched = null;
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
        diceLayout.addView(diceRow);
        diceRows.add(diceRow);
        registerForContextMenu(diceRow);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dice_row_menu, menu);
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

    /*Takes in a view, id (R.layout.d#_row), and a string. It then creates the dice readout row */
    public void addDiceReadout(String type, String temp) {
        LinearLayout diceControl = (LinearLayout) findViewById(R.id.dice_display);
        LinearLayout diceReadoutRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dice_readout, null);
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

    }
    /*Rolls and records dice by rows. Calls another function to create a readout for each row*/
    public void rollDiceByRow(View view) {
        int j;
        int temp;
        int rowTotal;
        //createDiceTotals();
        for (LinearLayout l : diceRows) {
            rowTotal = 0;
            EditText constantInputView = (EditText) l.findViewById(R.id.constant_input);
            String constantInput = constantInputView.getText().toString();
            value = "";
            if(!constantInput.equals("")){
                int constant = Integer.parseInt(constantInput);
                rowTotal += constant;
                value += " + " + constant;

            }
            EditText diceRow = (EditText) l.findViewById(R.id.dice_input);
            String numberOfDice = diceRow.getText().toString();
            Spinner spinRow = (Spinner) l.findViewById(R.id.dice_spinner);
            String diceDenomination = spinRow.getSelectedItem().toString();
            CheckBox toggleRoll = l.findViewById(R.id.toggle_roll);
            if (!numberOfDice.equals("")&&toggleRoll.isChecked()) {
                int k = Integer.parseInt(numberOfDice);
                switch (diceDenomination) {
                    case "d2":
                        for (j = 0; j < k; j++) {
                            temp = rolld2();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d4":
                        for (j = 0; j < k; j++) {
                            temp = rolld4();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d6":
                        for (j = 0; j < k; j++) {
                            temp = rolld6();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d8":
                        for (j = 0; j < k; j++) {
                            temp = rolld8();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d10":
                        for (j = 0; j < k; j++) {
                            temp = rolld10();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d12":
                        for (j = 0; j < k; j++) {
                            temp = rolld12();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d20":
                        for (j = 0; j < k; j++) {
                            temp = rolld20();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "d100":
                        for (j = 0; j < k; j++) {
                            temp = rolld100();
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                    case "Perc":
                        for (j = 0; j < k; j++) {
                            temp = rolld10();
                            value += " + " + temp;
                            rowTotal += temp;
                            temp = 10*(rolld10() - 1);
                            value += " + " + temp;
                            rowTotal += temp;
                        }
                        value += " = " + rowTotal;
                        break;
                }
            }

            if(!value.equals("")){
                diceTypeByRow.add(diceDenomination);
                diceValuesByRow.add(value);
                diceTotalsByRow.add(rowTotal);
            }
        }
        displayResultByRow();
        diceTotalsByRow.clear();
        diceValuesByRow.clear();
        diceTypeByRow.clear();
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

    public int rollPerc() {
        return rand.nextInt(10) + 10*(rand.nextInt(10)) + 1;
    }



    /*public int sumList(ArrayList<Integer> list) {
        int sum = 0;
        if (!list.isEmpty()) {
            for (int h = 0; h < list.size(); h++) {
                sum += list.get(h);
            }
        }
        return sum;
    }*/

    public void deleteRow(View view) {
        LinearLayout row = (LinearLayout) view.getParent();
        diceRows.remove(row);
        ((ViewManager) row.getParent()).removeView(row);
        row = null;
    }
    public void deleteRowMenu(LinearLayout row){
        diceRows.remove(row);
        ((ViewManager) row.getParent()).removeView(row);
        row = null;
    }

    public void displayResultByRow() {
        LinearLayout diceController = (LinearLayout) findViewById(R.id.dice_display);
        ScrollView diceDisplayScroll = (ScrollView) findViewById(R.id.dice_display_scrollview);
        diceController.removeAllViews();
        String type = "None";
        String temp = "";
        int summedRows = 0;
        boolean diceControl = false; //keeps track of whether we should display the diceControl layout or not
        int k = diceValuesByRow.size();
        ArrayList<Integer> rowsToSum = new ArrayList<Integer>();
        for (int j = 0; j < k; j++) {
            CheckBox sum = diceRows.get(j).findViewById(R.id.sum_row);
            if(sum.isChecked()){
                rowsToSum.add(j);
            }
            diceControl = true;
            type = diceTypeByRow.get(j);
            switch (type) {
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
            }
            addDiceReadout(type, diceValuesByRow.get(j).substring(3));
        }
        //boolean sumChecked = false;
        if(rowsToSum.size()> 1) {
            for (Integer j : rowsToSum) {
                //sumChecked = true;
                summedRows += diceTotalsByRow.get(j);
                temp += " + " + diceTotalsByRow.get(j);
            }
            //if (sumChecked) {
            type = "Summed Rows";
            temp += " = " + summedRows;
            addDiceReadout(type, temp.substring(3));
            //}
        }
        View separator = findViewById(R.id.separator);


        if (diceControl) {
            diceDisplayScroll.setVisibility(View.VISIBLE);
            diceController.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
        } else {
            diceDisplayScroll.setVisibility(View.GONE);
            diceController.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }

    }
}
