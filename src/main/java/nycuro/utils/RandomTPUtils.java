package nycuro.utils;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import nycuro.API;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class RandomTPUtils {

    private static TaskHandler taskHandler;

    /**
     * Credits: @SupremeMortal. Thanks bro for RandomTP
     */
    public void getSafeLocationSpawn(Player player, int radius) {
        int x = (int) (radius * Math.cos(ThreadLocalRandom.current().nextDouble()));
        int z = (int) (radius * Math.sin(ThreadLocalRandom.current().nextDouble()));
        Level level = API.getMainAPI().getServer().getDefaultLevel();
        level.loadChunk(x >> 4, z >> 4);
        taskHandler = player.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                player.getServer().getScheduler().scheduleRepeatingTask(new Task() {
                    @Override
                    public void onRun(int i) {
                        level.getChunk(x >> 4, z >> 4).isLoaded();
                    }
                }, 1, true);
                if (level.getChunk(x >> 4, z >> 4).isLoaded()) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        // ignore
                    } finally {
                        player.setImmobile(true);
                        player.teleport(new Vector3(x, 255, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                        player.teleport(highestBlockPosition(level, x, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                        player.setImmobile(false);
                        if (!taskHandler.isCancelled()) {
                            taskHandler.cancel();
                            this.cancel();
                            this.getHandler().cancel();
                        }
                    }
                }
            }
        }, 10, true);
    }

    private Vector3 highestBlockPosition(Level level, int x, int z) {
        BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
        return new Vector3(x, chunk.getHighestBlockAt(x & 0x0f, z & 0xf) + 1, z);
    }
}
