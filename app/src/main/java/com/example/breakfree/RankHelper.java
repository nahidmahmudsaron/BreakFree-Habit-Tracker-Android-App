package com.example.breakfree;

public class RankHelper {

    public static final int[] MILESTONES = {
            1, 3, 7, 10, 14, 21, 30, 45, 60, 90, 120,
            150, 180, 210, 240, 270, 300, 330, 365, 400, 450, 500
    };

    public static final String[] NAMES = {
            "Beginner", "Starter", "Fighter", "Knight", "Guardian", "Warrior",
            "Champion", "Elite", "Master", "Veteran", "Conqueror", "Hero",
            "Legend", "Warlord", "Immortal", "Titan", "Grandmaster", "Overlord",
            "King", "Emperor", "Mythic", "BreakFree Legend"
    };

    public static final String[] ICONS = {
            "🌱", "⚡", "🏹", "🛡️", "🔰", "⚔️",
            "🏆", "💎", "🎯", "🎖️", "🚩", "🦅",
            "🌟", "🔥", "💫", "🗿", "🔮", "🌌",
            "👑", "🏯", "🐉", "🚀"
    };

    // Index of the highest rank reached, or -1 if the user has 0 days.
    public static int getRankIndex(int days) {
        int index = -1;
        for (int i = 0; i < MILESTONES.length; i++) {
            if (days >= MILESTONES[i]) {
                index = i;
            }
        }
        return index;
    }

    public static String getRankName(int days) {
        int index = getRankIndex(days);
        return index < 0 ? "No Rank Yet" : NAMES[index];
    }

    public static String getRankIcon(int days) {
        int index = getRankIndex(days);
        return index < 0 ? "🌑" : ICONS[index];
    }

    public static boolean hasNextRank(int days) {
        return getRankIndex(days) < MILESTONES.length - 1;
    }

    public static int getNextMilestoneDays(int days) {
        return MILESTONES[getRankIndex(days) + 1];
    }

    public static String getNextRankName(int days) {
        return NAMES[getRankIndex(days) + 1];
    }

    public static int getDaysRemaining(int days) {
        if (!hasNextRank(days)) {
            return 0;
        }
        return getNextMilestoneDays(days) - days;
    }

    // Progress toward the next rank, from 0 to 1000 (used by the progress bar).
    public static int getProgress(int days) {
        if (!hasNextRank(days)) {
            return 1000;
        }
        int index = getRankIndex(days);
        int previous = index < 0 ? 0 : MILESTONES[index];
        int next = MILESTONES[index + 1];
        return (days - previous) * 1000 / (next - previous);
    }
}
