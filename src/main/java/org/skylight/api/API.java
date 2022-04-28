package org.skylight.api;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.bukkit.entity.Minecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public final class API {
    public static void forceKickPlayer(String name){
        if(name!=null) {
            try {
                EntityPlayerMP entityplayermp = MinecraftServer.getServerInst().getPlayerList().getPlayerByUsername(name);
                if (entityplayermp != null) {
                    entityplayermp.connection.getNetworkManager().forceDisconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void doNotHandlePlayerPacket(String name){
        if(name!=null) {
            try {
                EntityPlayerMP entityplayermp = MinecraftServer.getServerInst().getPlayerList().getPlayerByUsername(name);
                if (entityplayermp != null) {
                    entityplayermp.connection.getNetworkManager().handlePacket.set(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void spawnFakePlayer(String name){
    }

    public static void enablePPSLimitor(int pps){
        net.minecraft.network.NetworkManager.enablePPSLimitor = true;
        net.minecraft.network.NetworkManager.ppsl = pps;
    }
}
