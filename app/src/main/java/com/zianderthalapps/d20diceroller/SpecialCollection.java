package com.zianderthalapps.d20diceroller;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ben on 4/18/2018.
 */

public class SpecialCollection implements Serializable {
    String fileName;
    ArrayList<DiceCollection> specialCollection = new ArrayList<>();
    Boolean roll = false;

    public void setRoll(Boolean input){
        this.roll = input;
    }
    public Boolean getRoll(){
        return this.roll;
    }

    public void setSpecialCollection(ArrayList<DiceCollection> input){
        this.specialCollection = input;
    }
    public String getPrintName(){
        int n = this.fileName.length();
        return this.fileName.substring(17, n-4);
    }

    public ArrayList<DiceCollection> getSpecialCollection(){
        return this.specialCollection;
    }
    public void setFileName(String input){
        this.fileName = input;
    }
    public String getFileName(){
        return this.fileName;
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
