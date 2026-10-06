package com.example.breakfree;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Google's official TEST banner ad unit ID. Replace with your own before publishing.
    private static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";

    private StorageHelper storage;

    private View contentLayout;
    private TextView tvDays, tvDaysLabel, tvStartDate;
    private TextView tvRankIcon, tvRankName, tvNextMilestone, tvDaysRemaining;
    private ProgressBar progressRank;
    private MaterialButton btnStartToday, btnSetDate, btnReset;
    private FrameLayout adContainer;
    private AdView adView;

    private ValueAnimator countAnimator;
    private ObjectAnimator progressAnimator;
    private boolean firstShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The app uses its own light design, so we keep it in light mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new StorageHelper(this);

        contentLayout = findViewById(R.id.contentLayout);
        tvDays = findViewById(R.id.tvDays);
        tvDaysLabel = findViewById(R.id.tvDaysLabel);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvRankIcon = findViewById(R.id.tvRankIcon);
        tvRankName = findViewById(R.id.tvRankName);
        tvNextMilestone = findViewById(R.id.tvNextMilestone);
        tvDaysRemaining = findViewById(R.id.tvDaysRemaining);
        progressRank = findViewById(R.id.progressRank);
        btnStartToday = findViewById(R.id.btnStartToday);
        btnSetDate = findViewById(R.id.btnSetDate);
        btnReset = findViewById(R.id.btnReset);
        adContainer = findViewById(R.id.adContainer);

        findViewById(R.id.btnHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnStartToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.setStartDate(DateHelper.today());
                updateUI(true);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReset();
            }
        });

        // Gentle fade-in animation when the screen opens
        contentLayout.setAlpha(0f);
        contentLayout.setTranslationY(40f);
        contentLayout.animate().alpha(1f).translationY(0f).setDuration(500).start();

        setupAds();
    }

    // ---------------------------------------------------------------
    // Screen update
    // ---------------------------------------------------------------

    private void updateUI(boolean animate) {
        long start = storage.getStartDate();
        int days = 0;

        if (start != StorageHelper.NO_START_DATE) {
            days = DateHelper.daysBetween(start, System.currentTimeMillis());
            tvStartDate.setText(getString(R.string.started_on, DateHelper.format(start)));
            btnStartToday.setVisibility(View.GONE);
            btnReset.setVisibility(View.VISIBLE);
            btnSetDate.setText(R.string.change_start_date);
        } else {
            tvStartDate.setText(R.string.no_streak_message);
            btnStartToday.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.GONE);
            btnSetDate.setText(R.string.set_start_date);
        }

        // Day counter
        tvDaysLabel.setText(getResources().getQuantityString(R.plurals.days_unit, days));

        // Rank card
        tvRankIcon.setText(RankHelper.getRankIcon(days));
        tvRankName.setText(RankHelper.getRankName(days));

        if (RankHelper.hasNextRank(days)) {
            int nextDays = RankHelper.getNextMilestoneDays(days);
            String nextDaysText = getResources()
                    .getQuantityString(R.plurals.days_count, nextDays, nextDays);
            tvNextMilestone.setText(getString(R.string.next_milestone,
                    RankHelper.getNextRankName(days), nextDaysText));

            int remaining = RankHelper.getDaysRemaining(days);
            tvDaysRemaining.setText(getResources()
                    .getQuantityString(R.plurals.days_to_go, remaining, remaining));
        } else {
            tvNextMilestone.setText(R.string.max_rank_reached);
            tvDaysRemaining.setText(R.string.max_rank_remaining);
        }

        int progress = RankHelper.getProgress(days);

        if (countAnimator != null) countAnimator.cancel();
        if (progressAnimator != null) progressAnimator.cancel();

        if (animate) {
            countAnimator = ValueAnimator.ofInt(0, days);
            countAnimator.setDuration(900);
            countAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvDays.setText(String.valueOf((int) animation.getAnimatedValue()));
                }
            });
            countAnimator.start();

            progressAnimator = ObjectAnimator.ofInt(progressRank, "progress", 0, progress);
            progressAnimator.setDuration(900);
            progressAnimator.start();
        } else {
            tvDays.setText(String.valueOf(days));
            progressRank.setProgress(progress);
        }
    }

    // ---------------------------------------------------------------
    // Start date
    // ---------------------------------------------------------------

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        long start = storage.getStartDate();
        if (start != StorageHelper.NO_START_DATE) {
            cal.setTimeInMillis(start);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker picker,
                                          int year, int month, int dayOfMonth) {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth, 0, 0, 0);
                        chosen.set(Calendar.MILLISECOND, 0);
                        storage.setStartDate(chosen.getTimeInMillis());
                        updateUI(true);
                        Toast.makeText(MainActivity.this,
                                R.string.start_date_saved, Toast.LENGTH_SHORT).show();
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        // The start date cannot be in the future.
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    // ---------------------------------------------------------------
    // Reset
    // ---------------------------------------------------------------

    private void confirmReset() {
        long start = storage.getStartDate();
        if (start == StorageHelper.NO_START_DATE) {
            return;
        }
        int days = DateHelper.daysBetween(start, System.currentTimeMillis());
        String daysText = getResources().getQuantityString(R.plurals.days_count, days, days);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_title)
                .setMessage(getString(R.string.reset_message, daysText))
                .setPositiveButton(R.string.reset, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        doReset();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doReset() {
        long start = storage.getStartDate();
        if (start == StorageHelper.NO_START_DATE) {
            return;
        }
        long now = System.currentTimeMillis();
        int days = DateHelper.daysBetween(start, now);

        storage.addHistoryItem(new HistoryItem(start, now, days, RankHelper.getRankName(days)));
        storage.clearStartDate();

        updateUI(true);
        Toast.makeText(this, R.string.streak_reset_done, Toast.LENGTH_LONG).show();
    }

    // ---------------------------------------------------------------
    // AdMob banner
    // ---------------------------------------------------------------

    private void setupAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Nothing needed here
            }
        });

        adView = new AdView(this);
        adView.setAdUnitId(TEST_BANNER_ID);
        adView.setAdSize(getAdaptiveAdSize());
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    // Adaptive banner: automatically fits the width of any phone or tablet.
    private AdSize getAdaptiveAdSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int adWidth = (int) (metrics.widthPixels / metrics.density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    // ---------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh every time the screen appears, so the day count is always correct.
        updateUI(firstShow);
        firstShow = false;
        if (adView != null) adView.resume();
    }

    @Override
    protected void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (countAnimator != null) countAnimator.cancel();
        if (progressAnimator != null) progressAnimator.cancel();
        if (adView != null) adView.destroy();
        super.onDestroy();
    }
}
