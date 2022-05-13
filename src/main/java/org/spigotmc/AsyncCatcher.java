package org.spigotmc;

import org.skylight.executor.ThreadManager;

public class AsyncCatcher
{
    public static void catchOp(String reason) {
        if (ThreadManager.is_off_main_thread()) {
            throw new IllegalStateException("Called from async thread! " + reason);
        }
    }
}
