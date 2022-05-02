package org.skylight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import oshi.SystemInfo;

public class SkStatusCommand extends Command {
    public SkStatusCommand() {
        super("skstatus");
    }
    private final SystemInfo si = new SystemInfo();
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        sender.sendMessage("CPU: " + si.getHardware().getProcessors()[0].getName()+"\n");
        sender.sendMessage("OS: " + si.getOperatingSystem().getFamily()+"\n");
        sender.sendMessage("Java: " + System.getProperty("java.version")+"\n");
        sender.sendMessage("Bukkit: " + org.bukkit.Bukkit.getVersion()+"\n");
        sender.sendMessage("Executors: "+"\n");
        Integer[] ids = net.minecraftforge.common.DimensionManager.getIDs(false);
        for(int id : ids){
            net.minecraft.world.World worldserver = (net.minecraft.world.World)net.minecraftforge.common.DimensionManager.getWorld(id);
            sender.sendMessage("  Dimension: "+id+"\n");
            sender.sendMessage("  World: "+worldserver.getWorldInfo().getWorldName()+"\n");
            sender.sendMessage("  Players: "+worldserver.playerEntities.size()+"\n");
            sender.sendMessage("  Entities: "+worldserver.loadedEntityList.size()+"\n");
            sender.sendMessage("  Tiles: "+worldserver.loadedTileEntityList.size()+"\n");
            sender.sendMessage("  TileEntities: "+worldserver.loadedTileEntityList.size()+"\n");
            sender.sendMessage("  Entities-Executor: "+worldserver.entityExecutor.toString()+"\n");
            sender.sendMessage("  Tile-Executor: "+worldserver.tileExecutor.toString()+"\n");
            sender.sendMessage("  WeatherEffects-Executor: "+worldserver.weatherExecutor.toString()+"\n");
        }
        return true;
    }
}
