package org.skylight.executor;

import org.bukkit.craftbukkit.SpigotTimings;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;

public class WorldTimeUpdater {
    public static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static void updateTimeAsynchronously(int tickCounter){
         executor.execute(() -> {
             PlayerList list = MinecraftServer.getServerInst().getPlayerList();
             SpigotTimings.timeUpdateTimer.startTiming(); // Spigot
             // Send time updates to everyone, it will get the right time from the world the player is in.
             if (tickCounter % 20 == 0) {
                 for (int i = 0; i < list.getPlayers().size(); ++i) {
                     EntityPlayerMP entityplayer = (EntityPlayerMP) list.getPlayers().get(i);
                     entityplayer.connection.sendPacket(new SPacketTimeUpdate(entityplayer.world.getTotalWorldTime(), entityplayer.getPlayerTime(), entityplayer.world.getGameRules().getBoolean("doDaylightCycle"))); // Add support for per player time
                 }
             }
             SpigotTimings.timeUpdateTimer.stopTiming(); // Spigot
         });
    }
}
