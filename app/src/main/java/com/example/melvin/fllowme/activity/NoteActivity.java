package com.example.melvin.fllowme.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.fragment.NoteListFragment;

/**
 * Created by Melvin on 2016/9/4.
 */
public class NoteActivity extends Activity {
    Double Long;
    Double Lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        NoteListFragment fragment = new NoteListFragment();
        Bundle bundle = new Bundle();
        Long = getIntent().getDoubleExtra("X", 0.0);
        Lat = getIntent().getDoubleExtra("Y", 0.0);
        bundle.putDouble("Long", Long);
        bundle.putDouble("Lat", Lat);
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().add(R.id.ll_note_container, fragment).commit();
    }
}
