////////////////////////////////////////////////////////////////////////////////////////////////////
// PlotSquared - A plot manager and world generator for the Bukkit API                             /
// Copyright (c) 2014 IntellectualSites/IntellectualCrafters                                       /
//                                                                                                 /
// This program is free software; you can redistribute it and/or modify                            /
// it under the terms of the GNU General Public License as published by                            /
// the Free Software Foundation; either version 3 of the License, or                               /
// (at your option) any later version.                                                             /
//                                                                                                 /
// This program is distributed in the hope that it will be useful,                                 /
// but WITHOUT ANY WARRANTY; without even the implied warranty of                                  /
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                   /
// GNU General Public License for more details.                                                    /
//                                                                                                 /
// You should have received a copy of the GNU General Public License                               /
// along with this program; if not, write to the Free Software Foundation,                         /
// Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA                               /
//                                                                                                 /
// You can contact us via: support@intellectualsites.com                                           /
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.intellectualcrafters.plot.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.PlotSquared;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.generator.SquarePlotWorld;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.util.ChunkManager;
import com.intellectualcrafters.plot.util.PlayerFunctions;
import com.intellectualcrafters.plot.util.PlotHelper;
import com.intellectualcrafters.plot.util.UUIDHandler;

public class DebugClear extends SubCommand {

    public DebugClear() {
        super(Command.DEBUGCLEAR, "Clear a plot using a fast experimental algorithm", "debugclear", CommandCategory.DEBUG, false);
    }

    @Override
    public boolean execute(final Player plr, final String... args) {
        if (plr == null) {
            // Is console
            if (args.length < 2) {
                PlotSquared.log("You need to specify two arguments: ID (0;0) & World (world)");
            } else {
                final PlotId id = PlotId.fromString(args[0]);
                final String world = args[1];
                if (id == null) {
                    PlotSquared.log("Invalid Plot ID: " + args[0]);
                } else {
                    if (!PlotSquared.isPlotWorld(world) || !(PlotSquared.getWorldSettings(world) instanceof SquarePlotWorld)) {
                        PlotSquared.log("Invalid plot world: " + world);
                    } else {
                        final Plot plot = PlotHelper.getPlot(Bukkit.getWorld(world), id);
                        if (plot == null) {
                            PlotSquared.log("Could not find plot " + args[0] + " in world " + world);
                        } else {
                            World bukkitWorld = Bukkit.getWorld(world);
                            Location pos1 = PlotHelper.getPlotBottomLoc(bukkitWorld, plot.id).add(1, 0, 1);
                            Location pos2 = PlotHelper.getPlotTopLoc(bukkitWorld, plot.id);
                            if (PlotHelper.runners.containsKey(plot)) {
                                PlayerFunctions.sendMessage(null, C.WAIT_FOR_TIMER);
                                return false;
                            }
                            PlotHelper.runners.put(plot, 1);
                            ChunkManager.regenerateRegion(pos1, pos2, new Runnable() {
                                @Override
                                public void run() {
                                    PlotHelper.runners.remove(plot);
                                    PlotSquared.log("Plot " + plot.getId().toString() + " cleared.");
                                    PlotSquared.log("&aDone!");
                                }
                            });
                        }
                    }
                }
            }
            return true;
        }

        if (!PlayerFunctions.isInPlot(plr) || !(PlotSquared.getWorldSettings(plr.getWorld()) instanceof SquarePlotWorld)) {
            return sendMessage(plr, C.NOT_IN_PLOT);
        }
        final Plot plot = PlayerFunctions.getCurrentPlot(plr);
        if (!PlayerFunctions.getTopPlot(plr.getWorld(), plot).equals(PlayerFunctions.getBottomPlot(plr.getWorld(), plot))) {
            return sendMessage(plr, C.UNLINK_REQUIRED);
        }
        if (((plot == null) || !plot.hasOwner() || !plot.getOwner().equals(UUIDHandler.getUUID(plr))) && !BukkitMain.hasPermission(plr, "plots.admin.command.debugclear")) {
            return sendMessage(plr, C.NO_PLOT_PERMS);
        }
        assert plot != null;
        World bukkitWorld = plr.getWorld();
        Location pos1 = PlotHelper.getPlotBottomLoc(bukkitWorld, plot.id).add(1, 0, 1);
        Location pos2 = PlotHelper.getPlotTopLoc(bukkitWorld, plot.id);
        if (PlotHelper.runners.containsKey(plot)) {
            PlayerFunctions.sendMessage(null, C.WAIT_FOR_TIMER);
            return false;
        }
        PlotHelper.runners.put(plot, 1);
        ChunkManager.regenerateRegion(pos1, pos2, new Runnable() {
            @Override
            public void run() {
                PlotHelper.runners.remove(plot);
                PlayerFunctions.sendMessage(plr, "&aDone!");
            }
        });

        // sign

        // wall

        return true;
    }
}
