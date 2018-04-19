package com.zianderthalapps.d20diceroller;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ben on 4/18/2018.
 */

public class SpecialCollection implements Serializable {
    String name;
    ArrayList<DiceCollection> specialCollection = new ArrayList<>();

    public void setSpecialCollection(ArrayList<DiceCollection> input){
        this.specialCollection = input;
    }
    public ArrayList<DiceCollection> getSpecialCollection(){
        return this.specialCollection;
    }
    public void setName(String input){
        this.name = input;
    }
    public String getName(){
        return this.name;
    }
    public String specialCollectionDetails(){
        StringBuilder temp = new StringBuilder();
        for(DiceCollection dice : this.specialCollection){
            temp.append(dice.getNumberOfDice() + "x" + dice.getDiceType());
            if(!(dice.getModifier() == 0)){
                temp.append("+" + dice.getModifier());
            }
            temp.append(", ");
        }
        temp.setLength(temp.length()-2);
        return temp.toString();
    }
}
