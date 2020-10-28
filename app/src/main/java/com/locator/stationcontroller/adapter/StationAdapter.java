package com.locator.stationcontroller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.locator.stationcontroller.MainActivity;
import com.locator.stationcontroller.R;
import com.locator.stationcontroller.db.Station;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IS_EDITING = 1;
    private static final int IS_VIEWING = 0;

    private final LayoutInflater inflater;
    private final ArrayList<Station> stations;

    private MainActivity activity;

    public StationAdapter(@NonNull MainActivity activity) {
        inflater = LayoutInflater.from(activity);
        stations = new ArrayList<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IS_VIEWING) {
            return new StationViewHolder(inflater.inflate(R.layout.item_station, parent, false));
        }
        return new StationEditViewHolder(inflater.inflate(R.layout.item_station_edit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Station station = stations.get(position);
        if (holder instanceof StationViewHolder) {
            StationViewHolder stationViewHolder = (StationViewHolder) holder;
            stationViewHolder.stationName.setText(station.getName());
            String voltageString = "V = " + station.getVoltage();
            stationViewHolder.voltage.setText(voltageString);
            stationViewHolder.r1.setChecked(station.isR1());
            stationViewHolder.r2.setChecked(station.isR2());
            stationViewHolder.edit.setOnClickListener((l) -> {
                station.setEditing(true);
                notifyItemChanged(position);
            });
            stationViewHolder.sms1Button.setOnClickListener(v -> {
                activity.sendSms(station.getSms1(), station.getPhone1(), station.getPhone2());
            });
            stationViewHolder.sms2Button.setOnClickListener(v -> {
                activity.sendSms(station.getSms2(), station.getPhone1(), station.getPhone2());
            });
            stationViewHolder.sms3Button.setOnClickListener(v -> {
                activity.sendSms(station.getSms3(), station.getPhone1(), station.getPhone2());
            });
        }
        if (holder instanceof StationEditViewHolder) {
            StationEditViewHolder stationEditViewHolder = (StationEditViewHolder) holder;
            stationEditViewHolder.nameEditText.setText(station.getName());
            stationEditViewHolder.tel1EditText.setText(station.getPhone1());
            stationEditViewHolder.tel2EditText.setText(station.getPhone2());
            stationEditViewHolder.sms1EditText.setText(station.getSms1());
            stationEditViewHolder.sms2EditText.setText(station.getSms2());
            stationEditViewHolder.sms3EditText.setText(station.getSms3());
            stationEditViewHolder.deleteImageView.setOnClickListener(v -> {
                activity.deleteStation(station);
            });
            stationEditViewHolder.cancelImageView.setOnClickListener(v -> {
                station.setEditing(false);
                notifyItemChanged(position);
            });
            stationEditViewHolder.doneImageView.setOnClickListener(v -> {
                station.setName(stationEditViewHolder.nameEditText.getText().toString());
                station.setPhone1(stationEditViewHolder.tel1EditText.getText().toString());
                station.setPhone2(stationEditViewHolder.tel2EditText.getText().toString());
                station.setSms1(stationEditViewHolder.sms1EditText.getText().toString());
                station.setSms2(stationEditViewHolder.sms2EditText.getText().toString());
                station.setSms3(stationEditViewHolder.sms3EditText.getText().toString());
                activity.updateStation(station);
            });
        }

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    @Override
    public int getItemViewType(int position) {
        Station station = stations.get(position);
        return station.isEditing() ? IS_EDITING : IS_VIEWING;
    }

    public void setStations(List<Station> stations) {
        this.stations.clear();
        this.stations.addAll(stations);
        notifyDataSetChanged();
    }

    public void addStation(@NonNull Station station) {
        this.stations.add(station);
        notifyItemInserted(this.stations.size() - 1);
    }

    public void removeStation(@NonNull Station station) {
        int index = -1;
        for(int i = 0 ;i < stations.size(); ++i){
            if(stations.get(i).equals(station)){
                index = i;
            }
        }

        if(index == -1){
            return;
        }

        this.stations.remove(index);
        notifyItemRemoved(index);
    }

    public void updateStation(@NonNull Station station) {
        int index = -1;
        for(int i = 0 ;i < stations.size(); ++i){
            if(stations.get(i).equals(station)){
                index = i;
            }
        }

        if(index == -1){
            return;
        }
        station.setEditing(false);
        this.stations.set(index,station);
        notifyItemChanged(index);;
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        private TextView stationName;
        private TextView voltage;
        private ToggleButton r1;
        private ToggleButton r2;
        private ImageView edit;
        private Button sms1Button;
        private Button sms2Button;
        private Button sms3Button;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.name_text_view);
            voltage = itemView.findViewById(R.id.voltage_text_view);
            r1 = itemView.findViewById(R.id.r1_toggle_button);
            r2 = itemView.findViewById(R.id.r2_toggle_button);
            edit = itemView.findViewById(R.id.edit_image_view);
            sms1Button = itemView.findViewById(R.id.send_sms1_button);
            sms2Button = itemView.findViewById(R.id.send_sms2_button);
            sms3Button = itemView.findViewById(R.id.send_sms3_button);
        }
    }

    public static class StationEditViewHolder extends RecyclerView.ViewHolder {
        private EditText nameEditText;
        private EditText tel1EditText;
        private EditText tel2EditText;
        private EditText sms1EditText;
        private EditText sms2EditText;
        private EditText sms3EditText;
        private ImageView deleteImageView;
        private ImageView cancelImageView;
        private ImageView doneImageView;

        public StationEditViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEditText = itemView.findViewById(R.id.name_edit_text);
            tel1EditText = itemView.findViewById(R.id.tel1_edit_text);
            tel2EditText = itemView.findViewById(R.id.tel2_edit_text);
            sms1EditText = itemView.findViewById(R.id.sms1_edit_text);
            sms2EditText = itemView.findViewById(R.id.sms2_edit_text);
            sms3EditText = itemView.findViewById(R.id.sms3_edit_text);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);
            cancelImageView = itemView.findViewById(R.id.cancel_image_view);
            doneImageView = itemView.findViewById(R.id.done_image_view);
        }
    }
}
