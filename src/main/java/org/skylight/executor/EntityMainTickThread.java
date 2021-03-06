package org.skylight.executor;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import org.skylight.executor.forkjoin.ParallelListTraverse;

public class EntityMainTickThread {
    private static final AtomicInteger threadId = new AtomicInteger(0);
    private static ForkJoinPool pool;
    public static final AtomicInteger active = new AtomicInteger(0);
    private static int coreCount = Runtime.getRuntime().availableProcessors();

    public static void tickEntities(List<Entity> entities){
        List<Entity> list = new ArrayList<>(entities);
        pool.submit(new ParallelListTraverse<Entity>(list,coreCount,EntityMainTickThread::tickEntity)).join();
    }

    public static void tickTiles(List<TileEntity> entities){
        List<TileEntity> list = new ArrayList<>(entities);
        pool.submit(new ParallelListTraverse<TileEntity>(list,coreCount,EntityMainTickThread::onTileTick)).join();
    }

    public static void init(int threads){
        ForkJoinPool.ForkJoinWorkerThreadFactory factory = task->{
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(task);
            thread.setName("Skylight Parallel Entity Main Ticker-" + threadId.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(8);
            return thread;
        };
        pool = new ForkJoinPool(threads,factory,null,true);
        coreCount = threads;
    }

    private static void tickEntity(Entity entity2) {
        World world = entity2.world;    // Get the world
        if (World.timeStopped.get()) {return;}
        if (!world.entityLimiter.shouldContinue()) {return;}
        Entity entity3 = entity2.getRidingEntity();
        if (entity3 != null) {
            if (!entity3.isDead && entity3.isPassenger(entity2)) {
                return;
            }
            entity2.dismountRidingEntity();
        }
        if (!entity2.isDead && !(entity2 instanceof EntityPlayerMP)) {
            try {
                entity2.tickTimer.startTiming(); // Paper
                net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(entity2);
                world.updateEntity(entity2);
                net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(entity2);
                entity2.tickTimer.stopTiming(); // Paper
            } catch (Throwable throwable1) {
                throwable1.printStackTrace();
            }
        }

        if (entity2.isDead) {
            int l1 = entity2.chunkCoordX;
            int i2 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && world.isChunkLoaded(l1, i2, true)) {
                world.getChunkFromChunkCoords(l1, i2).removeEntity(entity2);
            }
            world.loadedEntityList.remove(entity2); // CraftBukkit - Use field for loop variable
            world.onEntityRemoved(entity2);
        }
    }

    private static void onTileTick(TileEntity tileentity) {
        World world = tileentity.world;    // Get the world
        if(World.timeStopped.get()){return;}
        if (!world.tileLimiter.shouldContinue()) {return;}
        // Spigot start
        if (tileentity == null) {return;}
        // Spigot end
        if (!tileentity.isInvalid() && tileentity.hasWorld()) {
            BlockPos blockpos = tileentity.getPos();
            if (world.isBlockLoaded(blockpos, false) && world.getWorldBorder().contains(blockpos)) //Forge: Fix TE's getting an extra tick on the client side....
            {
                try {
                    tileentity.tickTimer.startTiming(); // Spigot
                    net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileentity);
                    ((ITickable) tileentity).update();
                    net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileentity);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                // Spigot start
                finally {
                    tileentity.tickTimer.stopTiming();
                }
                // Spigot end
            }
        }
        if (tileentity.isInvalid()) {
            world.tickableTileEntities.remove(tileentity);
            world.loadedTileEntityList.remove(tileentity);
            if (world.isBlockLoaded(tileentity.getPos())) {
                Chunk chunk = world.getChunkFromBlockCoords(tileentity.getPos());
                if (chunk.getTileEntity(tileentity.getPos(), net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK) == tileentity)
                    chunk.removeTileEntity(tileentity.getPos());
            }
        }
    }
    public static ForkJoinPool getPool()
    {
        return pool;
    }
}
