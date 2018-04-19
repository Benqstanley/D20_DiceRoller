package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddSpecialCollections extends AppCompatActivity {
    File[] fileList;
    Context context;
    File directory;
    FileInputStream fis;
    ArrayList<SpecialCollection> specialCollections = new ArrayList<>();
    ArrayList<LinearLayout> specialCollectionRows = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_special_collections);
        populateCollectionView();
    }
    public void deleteEverything(View view){
        for(File file : fileList){
            file.delete();
        }
    }
    public void populateCollectionView(){
        context = getApplicationContext();
        directory = context.getFilesDir();
        fileList = directory.listFiles();
        createSpecialCollectionsList();
        if(specialCollections.isEmpty()){
            displayToast("SpecialCollections is empty");
        }
        for(SpecialCollection dice : specialCollections){
            addCollectionRow(dice);
        }
    }
    public void createSpecialCollectionsList(){
        for(File file : fileList){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                SpecialCollection temp = (SpecialCollection) inputStream.readObject();
                specialCollections.add(temp);
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void addCollectionRow(SpecialCollection dice) {
        LinearLayout collectionLayout = findViewById(R.id.collection_view);
        LinearLayout specialCollectionRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.special_collection_row, null);
        populateCollectionRow(dice, specialCollectionRow);
        collectionLayout.addView(specialCollectionRow);
        specialCollectionRows.add(specialCollectionRow);
        registerForContextMenu(specialCollectionRow);
    }
    public void populateCollectionRow(SpecialCollection dice, LinearLayout row){
        TextView tv = row.findViewById(R.id.special_collection_details);
        tv.setText(dice.specialCollectionDetails());
        tv = row.findViewById(R.id.special_collection_name);
        tv.setText(dice.getName());
    }

    public void callMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    public void displayToast(CharSequence input){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, input, duration);
        toast.show();
    }
}
