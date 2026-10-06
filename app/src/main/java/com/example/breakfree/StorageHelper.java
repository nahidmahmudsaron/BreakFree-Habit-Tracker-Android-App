package com.example.breakfree;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StorageHelper {

    public static final long NO_START_DATE = -1L;

    private static final String PREFS_NAME = "breakfree_prefs";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_HISTORY = "history";

    private final SharedPreferences prefs;

    public StorageHelper(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ----- Current streak -----

    public long getStartDate() {
        return prefs.getLong(KEY_START_DATE, NO_START_DATE);
    }

    public void setStartDate(long millis) {
        prefs.edit().putLong(KEY_START_DATE, millis).apply();
    }

    public void clearStartDate() {
        prefs.edit().remove(KEY_START_DATE).apply();
    }

    // ----- History -----

    public List<HistoryItem> getHistory() {
        List<HistoryItem> list = new ArrayList<>();
        String json = prefs.getString(KEY_HISTORY, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                list.add(new HistoryItem(
                        o.getLong("start"),
                        o.getLong("end"),
                        o.getInt("days"),
                        o.getString("rank")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Newest streak goes to the top of the list.
    public void addHistoryItem(HistoryItem item) {
        List<HistoryItem> list = getHistory();
        list.add(0, item);
        saveHistory(list);
    }

    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    private void saveHistory(List<HistoryItem> list) {
        JSONArray array = new JSONArray();
        try {
            for (HistoryItem item : list) {
                JSONObject o = new JSONObject();
                o.put("start", item.getStartDate());
                o.put("end", item.getEndDate());
                o.put("days", item.getDays());
                o.put("rank", item.getRankName());
                array.put(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply();
    }
}
