package org.skylight.executor;


import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Phaser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;


public class EntityTicker {
    /**
     * @author Prawa && PabuCommunity
     * @description This class is used to tick entities in the world. And is VERY important for the server to run.If you edit it please check more than one place in the code.
     */
    private static final Logger Logger = LogManager.getLogger();
    private static ExecutorService executor;
    private static Phaser phaser;
    private static AtomicInteger threadId = new AtomicInteger(0);

    public static void init(int threads) {
        if (executor == null) {
            ForkJoinPool.ForkJoinWorkerThreadFactory factory = task->{
                ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(task);
                thread.setName("Skylight Entity Ticker-" + threadId.getAndIncrement());
                thread.setDaemon(true);
                thread.setPriority(8);
                return thread;
            };
            executor = new ForkJoinPool(threads,factory,null,true);
        }
    }

    public static void onPreTick(){
        phaser = new Phaser();
        phaser.register();
    }

    public static void onEntityTick(net.minecraft.entity.Entity entity2,net.minecraft.world.World world){
        phaser.register();
        executor.execute(()->{
            try{
                if(World.timeStopped.get()){
                    return;
                }
                if(!world.entityLimiter.shouldContinue()){
                    return;
                }
                Entity entity3 = entity2.getRidingEntity();
                if (entity3 != null)
                {
                    if (!entity3.isDead && entity3.isPassenger(entity2)) {
                        return;
                    }
                    entity2.dismountRidingEntity();
                }
                if (!entity2.isDead && !(entity2 instanceof EntityPlayerMP))
                {
                    try
                    {
                        entity2.tickTimer.startTiming(); // Paper
                        net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(entity2);
                        world.updateEntity(entity2);
                        net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(entity2);
                        entity2.tickTimer.stopTiming(); // Paper
                    }
                    catch (Throwable throwable1) {throwable1.printStackTrace();}
                }

                if (entity2.isDead)
                {
                    int l1 = entity2.chunkCoordX;
                    int i2 = entity2.chunkCoordZ;
                    if (entity2.addedToChunk && world.isChunkLoaded(l1, i2, true))
                    {
                        world.getChunkFromChunkCoords(l1, i2).removeEntity(entity2);
                    }
                    world.loadedEntityList.remove(entity2); // CraftBukkit - Use field for loop variable
                    world.onEntityRemoved(entity2);
                }
            }finally{
                phaser.arriveAndDeregister();
            }
        });
    }

    public static void onWeatherEffectsTick(net.minecraft.entity.Entity entity,net.minecraft.world.World world){
        phaser.register();
        executor.execute(()->{
            try{
                if(World.timeStopped.get()){return;}
                // CraftBukkit start - Fixed an NPE
                if (entity == null) {
                    return;
                }
                // CraftBukkit end
                try {
                    if (entity.updateBlocked) return;
                    ++entity.ticksExisted;
                    entity.onUpdate();
                } catch (Throwable throwable2) {
                    throwable2.printStackTrace();
                }
                if (entity.isDead) {
                    world.weatherEffects.remove(entity);
                }
            }finally{
                phaser.arriveAndDeregister();
            }
        });
    }
    public static void onTileAdd(TileEntity tileentity1,World world){
        phaser.register();
        executor.execute(()->{
            try{
                if(World.timeStopped.get()){
                    return;
                }
                if (!tileentity1.isInvalid())
                {
                    if (!world.loadedTileEntityList.contains(tileentity1))
                    {
                        world.addTileEntity(tileentity1);
                    }

                    if (world.isBlockLoaded(tileentity1.getPos()))
                    {
                        Chunk chunk = world.getChunkFromBlockCoords(tileentity1.getPos());
                        IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                        chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                        world.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
                    }
                }
            }finally{
                phaser.arriveAndDeregister();
            }
        });
    }

    public static void onTileTick(TileEntity tileentity,World world){
        phaser.register();
        executor.execute(()->{
            try{
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
            }finally{
                phaser.arriveAndDeregister();
            }
        });
    }

    public static void onTickEnd(){
        phaser.arriveAndAwaitAdvance();
        phaser = null;
    }
}
