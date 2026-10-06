package com.example.breakfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<HistoryItem> items;

    public HistoryAdapter(List<HistoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvIcon.setText(RankHelper.getRankIcon(item.getDays()));
        holder.tvRank.setText(item.getRankName());
        holder.tvStart.setText(context.getString(R.string.history_started,
                DateHelper.format(item.getStartDate())));
        holder.tvEnd.setText(context.getString(R.string.history_ended,
                DateHelper.format(item.getEndDate())));
        holder.tvDays.setText(String.valueOf(item.getDays()));
        holder.tvDaysLabel.setText(context.getResources()
                .getQuantityString(R.plurals.days_unit, item.getDays()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvRank, tvStart, tvEnd, tvDays, tvDaysLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvHistoryIcon);
            tvRank = itemView.findViewById(R.id.tvHistoryRank);
            tvStart = itemView.findViewById(R.id.tvHistoryStart);
            tvEnd = itemView.findViewById(R.id.tvHistoryEnd);
            tvDays = itemView.findViewById(R.id.tvHistoryDays);
            tvDaysLabel = itemView.findViewById(R.id.tvHistoryDaysLabel);
        }
    }
}
