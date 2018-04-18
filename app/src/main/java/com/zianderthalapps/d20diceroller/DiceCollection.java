package com.zianderthalapps.d20diceroller;

/**
 * Created by Ben on 4/18/2018.
 */

public class DiceCollection {
    private String diceType;
    private int numberOfDice;
    private int modifier;
    private String toSave;

    public void setDiceType(String input){
        this.diceType = input;
    }
    public void setNumberOfDice(int input){
        this.numberOfDice = input;
    }
    public void setModifier(int input){
        this.modifier = input;
    }
    public String getDiceType(){
        return this.diceType;
    }
    public int getNumberOfDice(){
        return this.numberOfDice;
    }
    public int getModifier(){
        return this.modifier;
    }
    public void createToSave(){
        String saveable = "\nNew Collection\nDice Type: " + this.diceType;
        saveable += "\nNumber of Dice: " + this.numberOfDice;
        saveable += "\nModifier: " + this.modifier;
        this.toSave = saveable;
    }
    public String getToSave(){
        return this.toSave;
    }
}
