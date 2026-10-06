package com.example.breakfree;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private StorageHelper storage;
    private List<HistoryItem> items;
    private HistoryAdapter adapter;

    private RecyclerView recyclerHistory;
    private TextView tvEmpty;
    private MaterialButton btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        storage = new StorageHelper(this);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        items = storage.getHistory();
        adapter = new HistoryAdapter(items);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClear();
            }
        });

        updateEmptyState();
    }

    private void confirmClear() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_history_title)
                .setMessage(R.string.clear_history_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storage.clearHistory();
                        items.clear();
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateEmptyState() {
        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
        btnClearHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
