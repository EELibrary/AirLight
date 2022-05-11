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
        EntityTickOverride.init(threads);
        if (executor == null) {
            ForkJoinPool.ForkJoinWorkerThreadFactory factory = task->{
                ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(task);
                thread.setName("Skylight TileEntity Ticker-" + threadId.getAndIncrement());
                thread.setDaemon(true);
                thread.setPriority(8);
                return thread;
            };
            executor = new ForkJoinPool(threads/2,factory,null,true);
        }
    }

    public static void onPreTick(){
        phaser = new Phaser();
        phaser.register();
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


    public static void onTickEnd(){
        phaser.arriveAndAwaitAdvance();
        phaser = null;
    }
}
