package org.skylight.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TimeStopCommand extends Command {
    public TimeStopCommand(){
        super("timestop");
    }

    /**
     * @description Stop the entities and tileEntities ticking
     * @param sender Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     * @return Always returns true
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.isOp()){
            sender.sendMessage(ChatColor.RED+"Waring !!! Entities  and TileEntities ticking will stop in 5 seconds!!!");
            new Thread(()->{
                try {
                    Thread.sleep(5000);
                    boolean stopped = net.minecraft.world.World.timeStopped.get();
                    if (stopped){
                        net.minecraft.world.World.timeStopped.set(!stopped);
                        sender.sendMessage(ChatColor.RED+"Time started");
                        return;
                    }
                    sender.sendMessage(ChatColor.RED+"Time stopped!");
                    net.minecraft.world.World.timeStopped.set(!stopped);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
        return true;
    }
}
