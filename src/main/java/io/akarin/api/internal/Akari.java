package io.akarin.api.internal;

import java.util.Queue;
import java.util.concurrent.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

@SuppressWarnings("restriction")
public abstract class Akari {
    /**
     * A common logger used by mixin classes
     */
    public final static Logger logger = LogManager.getLogger("Akarin");
    
    public static class AssignableThread extends Thread {
        public AssignableThread(Runnable run) {
            super(run);
        }
    }


    public static class AssignableFactory implements ThreadFactory {
        private final String threadName;
        private int threadNumber;
        
        public AssignableFactory(String name) {
            threadName = name;
        }
        
        @Override
        public Thread newThread(Runnable run) {
            Thread thread = new AssignableThread(run);
            thread.setName(StringUtils.replaceChars(threadName, "$", String.valueOf(threadNumber++)));
            thread.setPriority(8); // Fair
            return thread;
        }
    }
}
