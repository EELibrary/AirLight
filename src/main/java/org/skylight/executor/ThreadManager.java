package org.skylight.executor;

import org.bukkit.Bukkit;

import java.util.List;
import java.util.Vector;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.CraftServer;

public class ThreadManager {
    public static List<Thread> server_workers = new Vector<>();
    private static List<Thread> thread_whitelist = new Vector<>();
    public static boolean is_off_main_thread(){
        Thread currentThread = Thread.currentThread();
        if(thread_whitelist.contains(currentThread) || server_workers.contains(currentThread)){
            return false;
        }
        if (!Bukkit.isPrimaryThread() && !((MinecraftServer)((CraftServer) Bukkit.getServer()).getServer()).isCallingFromMinecraftThread()) {
            return true;
        }
        return false;
    }
    @Deprecated
    public static void addAsyncCatcherWhiteList(Thread thread){
        thread_whitelist.add(thread);
    }
    @Deprecated
    public static void addAsyncCatcherWhiteList(){
        thread_whitelist.add(Thread.currentThread());
    }
    @Deprecated
    public static void removeAsyncCatcherWhiteList(Thread thread){
        thread_whitelist.remove(thread);
    }

    @Deprecated
    public static void removeAsyncCatcherWhiteList(){
        thread_whitelist.remove(Thread.currentThread());
    }

    public static void postToMainThread(Runnable runnable){
        net.minecraft.server.MinecraftServer.getServerInst().processQueue.add(runnable);
    }
}
