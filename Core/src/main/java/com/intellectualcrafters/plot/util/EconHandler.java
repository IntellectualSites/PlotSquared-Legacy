package com.intellectualcrafters.plot.util;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.ConsolePlayer;
import com.intellectualcrafters.plot.object.OfflinePlotPlayer;
import com.intellectualcrafters.plot.object.PlotPlayer;

public abstract class EconHandler {

    private static EconHandler manager;

    public static EconHandler getEconHandler() {
        if (!Settings.Enabled_Components.ECONOMY) {
            return null;
        }
        if (manager != null) {
            return manager;
        }
        return manager = PS.get().IMP.getEconomyHandler();
    }

    public double getMoney(PlotPlayer player) {
        if (player instanceof ConsolePlayer) {
            return Double.MAX_VALUE;
        }
        return getBalance(player);
    }

    public abstract double getBalance(PlotPlayer player);

    public abstract void withdrawMoney(PlotPlayer player, double amount);

    public abstract void depositMoney(PlotPlayer player, double amount);

    public abstract void depositMoney(OfflinePlotPlayer player, double amount);

    public abstract boolean hasPermission(String world, String player, String perm);

    public boolean hasPermission(String player, String perm) {
        return hasPermission(null, player, perm);
    }
}
