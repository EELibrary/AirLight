package org.spigotmc;

import com.google.common.base.Joiner;
import net.minecraft.server.MinecraftServer;
import com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.skylight.executor.EntityMainTickThread;
import org.skylight.executor.EntityMiscTickThread;

public class TicksPerSecondCommand extends Command
{

    public TicksPerSecondCommand(String name)
    {
        super( name );
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission( "bukkit.command.tps" );
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if ( !testPermission( sender ) )
        {
            return true;
        }

        // Paper start - Further improve tick handling
        double[] tps = org.bukkit.Bukkit.getTPS();
        String[] tpsAvg = new String[tps.length];

        for ( int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format( tps[i] );
        }

        sender.sendMessage( ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " + org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
        sender.sendMessage( ChatColor.GOLD + "Entity pool state: "+ ChatColor.GREEN + EntityMainTickThread.getPool().toString());
        sender.sendMessage( ChatColor.GOLD + "Entity misc pool state: "+ ChatColor.GREEN + EntityMiscTickThread.getPool().toString());
        // Paper end
        sender.sendMessage(ChatColor.GOLD + "Memory Useage: " +ChatColor.GREEN+ ( ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024 / 1024 ) + "MB/" + ( Runtime.getRuntime().maxMemory() / 1024 / 1024 ) + "MB");
        return true;
    }

    private static String format(double tps) // Paper - Made static
    {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }
}
