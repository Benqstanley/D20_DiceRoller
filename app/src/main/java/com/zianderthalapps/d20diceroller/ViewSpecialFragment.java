package com.zianderthalapps.d20diceroller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class ViewSpecialFragment extends Fragment {
    //TODO: figure out why adding to main is broken.
    //TODO: Enable editing
    public ViewSpecialFragment() {
        // Required empty public constructor
    }
    ContainerActivity containerActivity;
    File[] fileList;
    File directory;
    ArrayList<SpecialCollection> specialCollectionsListForViewScreen = new ArrayList<>();
    ArrayList<LinearLayout> specialCollectionRowsForViewScreen = new ArrayList<>();
    ArrayList<String> fileNamesToSendHome = new ArrayList<>();
    CheckBox selectAll;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        containerActivity = (ContainerActivity) getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_view_special, container, false);
        collectionLayout = (LinearLayout) rootView.findViewById(R.id.collection_view);
        context = getActivity();
        fileNamesToSendHome = containerActivity.getFileNamesForSpecialsToAddToHomeScreen();
        populateCollectionView();
        containerActivity.setOverflowButtonIcon(ContextCompat.getDrawable(containerActivity,R.drawable.ic_baseline_more_vert_24_whitepx));
        containerActivity.setIcon(0);
        Button addDiceSelectionButton = rootView.findViewById(R.id.add_selection);
        selectAll = rootView.findViewById(R.id.select_all);
        selectAll.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectAllRows();
                }else{
                    return;
                }
            }
        });
        addDiceSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerActivity.shedParent();
                buildSelection();
                containerActivity.navigate(R.id.nav_home);
            }
        });
        return rootView;
    }
    public void selectAllRows(){
        for(LinearLayout row : specialCollectionRowsForViewScreen){
            ((CheckBox) row.findViewById(R.id.special_collection_include_check_box)).setChecked(true);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = containerActivity.getMenuInflater();
        inflater.inflate(R.menu.view_special_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_special_options_delete_selection:
                deleteSelection();
                return true;
            case R.id.add_special_options_delete_everything:
                deleteEverything();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public File getFileFromFileName(String fName){
        return new File(getActivity().getFilesDir(), fName);
    }
    public SpecialCollection createSpecialFromFile(File file){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            SpecialCollection temp = (SpecialCollection) inputStream.readObject();
            inputStream.close();
            return temp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*The list of files is run through in a for loop. The specialCollection object is read from each file and thrown into the list
     * specialCollectionsListForHomeScreen. */
    public void createSpecialCollectionsListForViewScreen(){
        specialCollectionsListForViewScreen.clear();
        for(File file : fileList){
            SpecialCollection temp = createSpecialFromFile(file);
            if(temp!= null){
                specialCollectionsListForViewScreen.add(temp);
            }

        }
    }

    /*    populateCollectionView creates a list of the files saved in the app. It then calls createSpecialCollectionsListForViewScreen.
    If no saved special collections are found a message is displayed telling the user that no special collections are saved.
    If there are saved special collections then each is called for each one.*/
    public void populateCollectionView() {
        directory = getActivity().getFilesDir();
        fileList = directory.listFiles();
        if (fileList.length == 0) {
            containerActivity.displayToast("There Doesn't Seem To Be Anything Here");
            return;
        }
        createSpecialCollectionsListForViewScreen();
        containerActivity.setNewSpecial(false);
        specialCollectionRowsForViewScreen.clear();
        for (SpecialCollection dice : specialCollectionsListForViewScreen) {
            addCollectionRow(dice);
        }

        containerActivity.setSpecialCollectionRowsNeedShed(true);
    }
    LinearLayout collectionLayout;
    /*A LinearLayout is inflated so it has the format to be displayed. Then populateCollectionRow is called to fill in the details. Once the details are filled
     * in the layout is added to collectionLayout. It's also added to specialCollectionRowsForViewScreen for checking purposes.
     * Each LinearLayout is registered for a context menu that allows the user to edit or delete the specialCollection*/
    public void addCollectionRow(SpecialCollection dice) {
        LinearLayout specialCollectionRow = (LinearLayout) LayoutInflater.from(containerActivity.getApplicationContext()).inflate(R.layout.special_collection_row, null);
        populateCollectionRow(dice, specialCollectionRow);
        ((CheckBox) specialCollectionRow.findViewById(R.id.special_collection_include_check_box)).setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    selectAll.setChecked(false);
                }
            }
        });
        collectionLayout.addView(specialCollectionRow);
        specialCollectionRowsForViewScreen.add(specialCollectionRow);
        registerForContextMenu(specialCollectionRow);
    }
    /*  populateCollectionRow takes in a SpecialCollection and the LinearLayout that its info should be displayed in.
        The SpecialCollections info is added to the relevant TextViews.
    * */
    public void populateCollectionRow(SpecialCollection dice, LinearLayout row){
        TextView tv = row.findViewById(R.id.special_collection_details);
        tv.setText(dice.specialCollectionDetails());
        tv = row.findViewById(R.id.special_collection_name);
        tv.setText(dice.getPrintName());
    }
    View viewTouched;
    Context context;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = containerActivity.getMenuInflater();
        if(v.getId() == R.id.special_collection_row){
            inflater.inflate(R.menu.view_special_context_menu, menu);
        }else{
            inflater.inflate(R.menu.main_activity_dice_row_menu, menu);
        }

        viewTouched = v;
    }
    ArrayList<String> selectionToDelete = new ArrayList<>();
    ArrayList<LinearLayout> selectionRowsToDelete = new ArrayList<>();
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LinearLayout dice_row = (LinearLayout) viewTouched;
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteRowMenu(dice_row);
                return true;
            case R.id.menu_edit:
                int index = specialCollectionRowsForViewScreen.indexOf(dice_row);
                Log.e("Tag", "Index of: " + index);
                editSpecial(index);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /*Builds the users selection from the checkboxes.*/
    public void buildSelection(){
        selectionToDelete.clear();
        selectionRowsToDelete.clear();
        int n = specialCollectionRowsForViewScreen.size();
        CheckBox selectionBox;
        Boolean selected;
        LinearLayout row;
        for(int j = 0; j < n; j++){
            row = specialCollectionRowsForViewScreen.get(j);
            selectionBox = row.findViewById(R.id.special_collection_include_check_box);
            selected = selectionBox.isChecked();
            if(selected){
                fileNamesToSendHome.add(specialCollectionsListForViewScreen.get(j).getFileName());
                selectionToDelete.add(specialCollectionsListForViewScreen.get(j).getFileName());
                selectionRowsToDelete.add(specialCollectionRowsForViewScreen.get(j));
            }

        }
    }

    public void deleteSelection(){
        buildSelection();
        if(!selectionToDelete.isEmpty()){
            for(String fileName : selectionToDelete){
                File file = new File(context.getFilesDir(), fileName);
                if(file.exists()){
                    containerActivity.displayToast("Deleting File");
                    file.delete();
                }
            }
            for(LinearLayout row : selectionRowsToDelete){
                ((ViewManager) row.getParent()).removeView(row);
            }
            selectionToDelete.clear();
        }
    }
    public void deleteEverything(){
        for(File file : fileList){
            file.delete();
        }
        LinearLayout display = containerActivity.findViewById(R.id.collection_view);
        display.removeAllViews();
        containerActivity.setActionBarTitle(R.string.home);
        containerActivity.returnFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        containerActivity.displayToast("Everything has been destroyed");
    }
    public void editSpecial(int index){
        Log.e("ViewSpecial", "Entered editSpecial");
        containerActivity.setToEdit(specialCollectionsListForViewScreen.get(index));
        containerActivity.navigate(R.id.nav_edit);
    }
    public void deleteRowMenu(LinearLayout row){
        if(row.getId() == R.id.special_collection_row){
            String fileName = ((TextView) row.findViewById(R.id.special_collection_name)).getText().toString();
            String temp = "SpecialCollection" + fileName + ".txt";
            specialCollectionRowsForViewScreen.remove(row);
            File file = new File(context.getFilesDir(), temp);
            if(file.exists()){
                containerActivity.displayToast("Deleting File");
                file.delete();
            }
        }
        ((ViewManager) row.getParent()).removeView(row);
    }
}
