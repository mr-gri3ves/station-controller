package com.locator.stationcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.locator.stationcontroller.adapter.StationAdapter;
import com.locator.stationcontroller.db.QueryListener;
import com.locator.stationcontroller.db.Station;
import com.locator.stationcontroller.db.StationRepository;
import com.locator.stationcontroller.utils.Utils;
import com.locator.stationcontroller.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final StationRepository stationRepository = StationRepository.getInstance();
    private StationAdapter stationAdapter;
    private AlertDialog stationAddDialog;
    private BroadcastReceiver smsBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {


            startApp();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 0) {
            return;
        }
        if (grantResults.length == 0) {
            return;
        }
        startApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsBroadcastReceiver != null) {
            unregisterReceiver(smsBroadcastReceiver);
        }
    }

    private void setRecyclerView() {
        RecyclerView stationsRecyclerView = findViewById(R.id.stations_recycler_view);
        stationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        stationsRecyclerView.setAdapter(stationAdapter);
    }

    public void showAddStation(View view) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_station_create, null);
        stationAddDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_station)
                .setView(dialogView)
                .setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.cancel, null).show();
        stationAddDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = ((EditText) dialogView.findViewById(R.id.name_edit_text)).getText().toString();
            String phone1 = ((EditText) dialogView.findViewById(R.id.phone1_edit_text)).getText().toString();
            String phone2 = ((EditText) dialogView.findViewById(R.id.phone2_edit_text)).getText().toString();
            String sms1 = ((EditText) dialogView.findViewById(R.id.sms1_edit_text)).getText().toString();
            String sms2 = ((EditText) dialogView.findViewById(R.id.sms2_edit_text)).getText().toString();
            String sms3 = ((EditText) dialogView.findViewById(R.id.sms3_edit_text)).getText().toString();
            Station station = new Station(name, phone1, phone2, sms1, sms2, sms3);
            addStation(station);
        });

    }

    public void getStations() {
        stationRepository.getAll(stations -> {
            List<Station> syncedStations = Synchronizer.syncStations(this, stations);
            stationAdapter.setStations(syncedStations);
        });
    }

    public void addStation(@NonNull Station station) {
        Validator.Result result = Validator.validate(station);
        if (!result.isSuccess()) {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        Station syncedStation = Synchronizer.syncStation(this, station);
        stationRepository.insert(ids -> {
            if (ids.size() == 0) {
                Toast.makeText(this, "Station was not added", Toast.LENGTH_SHORT).show();
                return;
            }
            stationAdapter.addStation(syncedStation);
            stationAddDialog.dismiss();
        }, syncedStation);
    }

    public void updateStation(@NonNull Station station) {
        Validator.Result result = Validator.validate(station);
        if (!result.isSuccess()) {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        Station syncedStation = Synchronizer.syncStation(this, station);
        stationRepository.update(affectedCount -> {
            stationAdapter.updateStation(syncedStation);
        }, syncedStation);
    }

    public void deleteStation(@NonNull Station station) {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to delete station")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialog, which) -> {
                    stationRepository.delete(affectedRows -> {
                        if (affectedRows == 0) {
                            Toast.makeText(this, "Station was not deleted", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        stationAdapter.removeStation(station);
                    }, station);
                }).show();
    }

    public void sendSms(String message, String... phones) {
        if (phones.length == 1) {
            Utils.sendSms(phones[0], message, null, null);
            return;
        }
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(phones, 0, null)
                .setPositiveButton(R.string.ok, (dialog, whichButton) -> {
                    dialog.dismiss();
                    String chosenPhone = phones[((AlertDialog) dialog).getListView().getCheckedItemPosition()];
                    sendSms(message, chosenPhone);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void startApp() {
        stationAdapter = new StationAdapter(this);
        setRecyclerView();
        getStations();
        smsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                String phone = messages[0].getOriginatingAddress();
                String message = messages[0].getMessageBody();
                long date = messages[0].getTimestampMillis();

                stationRepository.getAllByPhone(phone, stations -> {
                    List<Station> syncedStations = Synchronizer.syncStationsWithSms(stations, phone, message, date);
                    if (syncedStations.isEmpty()) {
                        return;
                    }
                    stationRepository.update(integer -> {
                        if (integer != syncedStations.size()) {
                            Toast.makeText(context, "Some stations were not synced", Toast.LENGTH_SHORT).show();
                        }
                    }, stations.toArray(new Station[0]));
                    for (Station station : syncedStations) {
                        stationAdapter.updateStation(station);
                    }
                });
            }
        };
        registerReceiver(smsBroadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }
}