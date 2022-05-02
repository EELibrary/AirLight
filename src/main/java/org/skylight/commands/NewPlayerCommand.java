package org.skylight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.skylight.api.API;

public class NewPlayerCommand extends Command {
    public NewPlayerCommand() {
        super("newplayer");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 1&& sender instanceof Player) {
            if (sender.isOp()) {
               // API.spawnFakePlayer(args[0],net.minecraftforge.common.DimensionManager.getWorld(0));
            }
        }
        return true;
    }
}
