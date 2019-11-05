package com.zianderthalapps.d20diceroller.util;

import com.zianderthalapps.d20diceroller.DiceCollection;
import com.zianderthalapps.d20diceroller.SpecialCollection;
import com.zianderthalapps.d20diceroller.SpecialWithSubCollections;

import java.util.ArrayList;

public class JsonUtils {
    private static String DICE_COLLECTIONS_JSON_KEY = "dice";
    private static String SPECIAL_COLLECTIONS_JSON_KEY = "special_collections";
    private static String MOD_JSON_KEY = "\"modifier\"";
    private static String NUMBER_OF_DICE_KEY = "\"number_of_dice\"";
    private static String DICE_TYPE_KEY = "\"dice_type\"";
    private static String FILE_NAME_KEY = "\"file_name\"";

    public static String turnSpecialWithSubIntoJsonString(SpecialWithSubCollections specialWithSub){
        ArrayList<SpecialCollection> array = specialWithSub.getArray();
        StringBuilder temp = new StringBuilder("{ \n").append(FILE_NAME_KEY).append(" : ");
        temp.append("\"" + specialWithSub.getFileName() + "\", \n");
        temp.append(SPECIAL_COLLECTIONS_JSON_KEY).append(" : [");
        int i = 0;
        for(SpecialCollection special : array){
            if(i > 0){
                temp.append(", ").append(turnSpecialIntoJsonString(special));
            }else{
                temp.append(turnSpecialIntoJsonString(special));
            }
            i++;
        }
        temp.append(" ] \n}");
        return temp.toString();
    }

    public static String turnSpecialIntoJsonString(SpecialCollection special){
        ArrayList<DiceCollection> diceCollections = special.getSpecialCollection();
        StringBuilder temp = new StringBuilder("{ \n").append(FILE_NAME_KEY).append(" : ");
        temp.append("\"" + special.getFileName() + "\", \n");
        int i = 0;
        for(DiceCollection dice : diceCollections){
            if(i > 0){
                temp.append(", ").append(turnDiceCollectionIntoJsonString(dice));
            }else{
                temp.append(turnDiceCollectionIntoJsonString(dice));
            }
            i++;
        }
        temp.append(" ] \n}");
        return temp.toString();
    }
    public static String turnDiceCollectionIntoJsonString(DiceCollection dice){
        StringBuilder temp = new StringBuilder("{\n " + NUMBER_OF_DICE_KEY + " : " + " \"" + dice.getNumberOfDice() + "\"" );
        temp.append(", \n").append(DICE_TYPE_KEY).append(" : \"").append(dice.getDiceType()).append("\", \n");
        temp.append(MOD_JSON_KEY).append(" : \"").append(dice.getDiceType()).append("\", \n }");
        return temp.toString();
    }

}
