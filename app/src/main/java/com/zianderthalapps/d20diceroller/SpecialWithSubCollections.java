package com.zianderthalapps.d20diceroller;

import java.util.ArrayList;

public class SpecialWithSubCollections {
    //SpecialWithSubCollections is a SpecialCollection that has multiple SpecialCollections
    //Paid Feature
    ArrayList<SpecialCollection> mArrayOfSpecialCollections = new ArrayList<>();
    String fileName;
    Boolean roll = false;

    public void setRoll(Boolean input){
        this.roll = input;
    }
    public Boolean getRoll(){
        return this.roll;
    }

    public void setArray(ArrayList<SpecialCollection> input){
        this.mArrayOfSpecialCollections = input;
    }
    public String getPrintName(){
        int n = this.fileName.length();
        return this.fileName.substring(17, n-4);
    }

    public ArrayList<SpecialCollection> getArray(){
        return this.mArrayOfSpecialCollections;
    }
    public void setFileName(String input){
        this.fileName = input;
    }
    public String getFileName(){
        return this.fileName;
    }
    public String specialWithSubCollectionDetails(){
        StringBuilder temp = new StringBuilder();
        for(SpecialCollection special : this.mArrayOfSpecialCollections){
            temp.append(special.specialCollectionDetails());
            temp.append("\n");
        }
        return temp.toString();
    }
}

