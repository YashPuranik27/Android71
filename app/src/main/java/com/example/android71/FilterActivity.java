package com.example.android71;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FilterActivity extends DialogFragment {

    private AlbumCollection ac;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        ImageButton closeButton = view.findViewById(R.id.filter_button_close);
        Button searchButton = view.findViewById(R.id.filter_searchButton);

        AutoCompleteTextView per1 = view.findViewById(R.id.filter_personTag1);
        AutoCompleteTextView per2 = view.findViewById(R.id.filter_personTag2);
        AutoCompleteTextView loc1 = view.findViewById(R.id.filter_locationTag1);
        AutoCompleteTextView loc2 = view.findViewById(R.id.filter_locationTag2);

        Switch jointSwitch = view.findViewById(R.id.filter_jointSwitch);

        ac = AlbumCollection.getInstance();
        ArrayList<String> persons = ac.getPersonNames();
        ArrayList<String> locs = ac.getLocations();

        ArrayAdapter<String> personAdapter = new ArrayAdapter<String>
        (getActivity().getApplicationContext(), android.R.layout.select_dialog_item, persons);

        ArrayAdapter<String> locAdapter = new ArrayAdapter<String>
        (getActivity().getApplicationContext(), android.R.layout.select_dialog_item, locs);

        per1.setThreshold(1);
        per1.setAdapter(personAdapter);

        per2.setThreshold(1);
        per2.setAdapter(personAdapter);

        loc1.setThreshold(1);
        loc1.setAdapter(locAdapter);

        loc2.setThreshold(1);
        loc2.setAdapter(locAdapter);

        searchButton.setOnClickListener(v -> {
            String per1Val = per1.getText().toString().trim();
            String per2Val = per2.getText().toString().trim();
            String loc1Val = loc1.getText().toString().trim();
            String loc2Val = loc2.getText().toString().trim();

            ArrayList<Photo> per1Matches = ac.returnPhotosWithPersonTag(per1Val);
            ArrayList<Photo> per2Matches = ac.returnPhotosWithPersonTag(per2Val);
            ArrayList<Photo> loc1Matches = ac.returnPhotosWithLocationTag(loc1Val);
            ArrayList<Photo> loc2Matches = ac.returnPhotosWithLocationTag(loc2Val);

            boolean doingLoc = !loc1Val.equals("") || !loc2Val.equals("");
            boolean doingPer = !per1Val.equals("") || !per2Val.equals("");

            if(!doingLoc && !doingPer)
                return;

            if(doingPer && per1Val.equals(""))
                per1Matches = per2Matches;
            if(doingPer && per2Val.equals(""))
                per2Matches = per1Matches;
            if(doingLoc && loc1Val.equals(""))
                loc1Matches = loc2Matches;
            if(doingLoc && loc2Val.equals(""))
                loc2Matches = loc1Matches;

            ArrayList<Photo> culledList = new ArrayList<>();
            if(jointSwitch.isChecked()) {//OR MODE
                culledList.addAll(per1Matches);
                culledList.addAll(per2Matches);
                culledList.addAll(loc1Matches);
                culledList.addAll(loc2Matches);
                culledList = new ArrayList<>(culledList.stream().distinct().collect(Collectors.toList()));
            } else { //AND MODE
                culledList = per1Matches;
                if(doingLoc && !doingPer)
                    culledList = loc1Matches;

                if(doingLoc) {
                    culledList.retainAll(loc1Matches);
                    culledList.retainAll(loc2Matches);
                }
                if(doingPer){
                    culledList.retainAll(per1Matches);
                    culledList.retainAll(per2Matches);
                }
            }

            ac.setSearchResults(culledList);

            String query = String.join((jointSwitch.isChecked() ? "|" : "&"));

            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);
            startActivity(intent);
        });

        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }
}