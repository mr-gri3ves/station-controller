package com.locator.stationcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.locator.stationcontroller.db.QueryListener;
import com.locator.stationcontroller.db.Station;
import com.locator.stationcontroller.db.StationRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}