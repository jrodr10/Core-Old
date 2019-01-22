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

    private static ArrayList<Boolean> blocked = new ArrayList<>();
    private static ArrayList<Boolean> teleported = new ArrayList<>();
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
                if (!level.getChunk(x >> 4, z >> 4).isLoaded()) {
                    blocked.add((int) player.getId(), true);
                } else {
                    player.teleport(new Vector3(x, 255, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                    player.teleport(highestBlockPosition(level, x, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                    API.getMessageAPI().sendRandomTPMessage(player, x, z);
                    taskHandler.cancel();
                }
                if (blocked.get((int) player.getId())) {
                    API.getMainAPI().getServer().getScheduler().scheduleRepeatingTask(new Task() {
                        @Override
                        public void onRun(int i) {
                            if (!blocked.get((int) player.getId())) {
                                player.teleport(new Vector3(x, 255, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                                player.teleport(highestBlockPosition(level, x, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                                API.getMessageAPI().sendRandomTPMessage(player, x, z);
                                teleported.add((int) player.getId(), true);
                            }
                            if (teleported.get((int) player.getId())) {
                                if (!this.getHandler().isCancelled()) {
                                    this.getHandler().cancel();
                                }
                                if (!taskHandler.isCancelled()) {
                                    taskHandler.cancel();
                                }
                            }
                        }
                    }, 1, true);
                }
            }
        }, 10);
    }

    private Vector3 highestBlockPosition(Level level, int x, int z) {
        BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
        return new Vector3(x, chunk.getHighestBlockAt(x & 0x0f, z & 0xf) + 1, z);
    }
}
