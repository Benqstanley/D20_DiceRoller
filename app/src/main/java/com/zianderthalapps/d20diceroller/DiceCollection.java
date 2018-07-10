package com.zianderthalapps.d20diceroller;

import java.io.Serializable;

/**
 * Created by Ben on 4/18/2018.
 */

public class DiceCollection implements Serializable {
    private String diceType;
    private int numberOfDice;
    private int modifier;
    private boolean sum = false;
    private int tempRollTotal = 0;

    public void setTempRollTotal(int input){
        this.tempRollTotal = input;
    }
    public int getTempRollTotal(){
        return this.tempRollTotal;
    }

    public void setSum(boolean input){
        this.sum = input;
    }
    public Boolean getSum(){
        return this.sum;
    }

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


}
