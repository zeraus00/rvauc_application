package com.example.rfid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rfid.R;
import com.example.rfid.features.uniformcompliance.services.ViolationDTO;

import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViewHolder> {

    private final List<ViolationDTO> data;

    public ViolationAdapter(List<ViolationDTO> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, dayText, timeText, reasonsText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.txtDate);
            dayText = itemView.findViewById(R.id.txtDay);
            timeText = itemView.findViewById(R.id.txtTime);
            reasonsText = itemView.findViewById(R.id.txtReason);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_violation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViolationDTO record = data.get(position);

        holder.dateText.setText(record.date);
        holder.dayText.setText(record.day);
        holder.timeText.setText(record.time);
        holder.reasonsText.setText(String.join(", ", record.reasons));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
