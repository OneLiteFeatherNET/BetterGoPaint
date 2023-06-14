/*
 * goPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) Arcaniax-Development
 * Copyright (C) Arcaniax team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.themeinerlp.bettergopaint;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.fastasyncworldedit.core.Fawe;
import dev.themeinerlp.bettergopaint.command.BrushCommand;
import dev.themeinerlp.bettergopaint.command.Handler;
import dev.themeinerlp.bettergopaint.command.ReloadCommand;
import dev.themeinerlp.bettergopaint.listeners.ConnectListener;
import dev.themeinerlp.bettergopaint.listeners.InteractListener;
import dev.themeinerlp.bettergopaint.listeners.InventoryListener;
import dev.themeinerlp.bettergopaint.objects.other.Settings;
import dev.themeinerlp.bettergopaint.objects.player.PlayerBrushManager;
import dev.themeinerlp.bettergopaint.utils.Constants;
import dev.themeinerlp.bettergopaint.utils.DisabledBlocks;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.ServerLib;

import java.io.File;
import java.util.function.Function;
import java.util.logging.Level;


public class BetterGoPaint extends JavaPlugin implements Listener {
    public static boolean plotSquaredEnabled;
    private static PlayerBrushManager manager;
    private static BetterGoPaint betterGoPaint;
    public ConnectListener connectListener;
    public InteractListener interactListener;
    public InventoryListener inventoryListener;
    public Handler cmdHandler;
    private AnnotationParser<CommandSender> annotationParser;

    public static BetterGoPaint getGoPaintPlugin() {
        return betterGoPaint;
    }

    public static PlayerBrushManager getBrushManager() {
        return manager;
    }

    public static boolean isPlotSquaredEnabled() {
        return plotSquaredEnabled;
    }

    public void reload() {
        BetterGoPaint.getGoPaintPlugin().reloadConfig();
        manager = new PlayerBrushManager();
        Settings.settings().reload(new File(getDataFolder(), "config.yml"));
    }

    public void onEnable() {

        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();
        ServerLib.isJavaSixteen();
        PaperLib.suggestPaper(this);

        betterGoPaint = this;
        Settings.settings().reload(new File(getDataFolder(), "config.yml"));
        enableBStats();
        enableCommandSystem();
        if (this.annotationParser != null) {
            annotationParser.parse(new ReloadCommand(this));
            annotationParser.parse(new BrushCommand(this));
        }


        manager = new PlayerBrushManager();

        connectListener = new ConnectListener(betterGoPaint);
        interactListener = new InteractListener(betterGoPaint);
        inventoryListener = new InventoryListener(betterGoPaint);
        cmdHandler = new Handler(betterGoPaint);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(connectListener, this);
        pm.registerEvents(interactListener, this);
        pm.registerEvents(inventoryListener, this);
        pm.registerEvents(cmdHandler, this);
        getCommand("gopaint").setExecutor(cmdHandler);
        DisabledBlocks.addBlocks();


    }

    private void enableCommandSystem() {
        try {
            PaperCommandManager<CommandSender> commandManager = PaperCommandManager.createNative(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator()
            );
            if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                commandManager.registerBrigadier();
                getLogger().info("Brigadier support enabled");
            }
            if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                commandManager.registerAsynchronousCompletions();
                getLogger().info("Async completion support enabled");
            }
            Function<ParserParameters, CommandMeta> commandMetaFunction = parserParameters ->
                    CommandMeta
                            .simple()
                            .with(CommandMeta.DESCRIPTION, parserParameters.get(StandardParameters.DESCRIPTION, "No description"))
                            .build();
            this.annotationParser = new AnnotationParser<>(commandManager, CommandSender.class, commandMetaFunction);

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Cannot init command manager");
        }

    }

    private void enableBStats() {
        Metrics metrics = new Metrics(this, Constants.BSTATS_ID);

        metrics.addCustomChart(new SimplePie(
                "faweVersion",
                () -> Fawe.instance().getVersion().toString()
        ));
    }

}
