package nycuro.mechanic.handlers;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gt.creeperface.nukkit.scoreboardapi.scoreboard.*;
import it.unimi.dsi.fastutil.objects.*;
import nycuro.API;
import nycuro.Core;
import nycuro.api.JobsAPI;
import nycuro.database.Database;
import nycuro.database.objects.Profile;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class MechanicHandlers implements Listener {

    private Object2IntMap<String> timers = new Object2IntOpenHashMap<>();
    public static Object2ObjectMap<String, Boolean> coords = new Object2ObjectOpenHashMap<>();
    public static Objective object;
    public static TaskHandler taskHandler;
    public static TaskHandler taskRepeatingHandler;
    public static FakeScoreboard fakeScoreboard;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        coords.put(player.getName(), false);
        API.getDatabase().playerExist(player, bool -> {
            if (!bool) {
                API.getDatabase().addNewPlayer(player);
            }
        });
        API.getMainAPI().getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                String username = player.getName();
                Integer playerTime = timers.getOrDefault(username, 1);
                switch (playerTime) {
                    case 1:
                        API.getMessageAPI().sendFirstJoinTitle(player);
                        break;
                    case 2:
                        API.getMechanicAPI().sendModalContents(player);
                        API.getMessageAPI().sendSecondJoinTitle(player);
                        break;
                    case 3:
                        API.getMessageAPI().sendThreeJoinTitle(player);
                        break;
                    default:
                        API.getMainAPI().getServer().getScheduler().cancelTask(this.getTaskId());
                }
                timers.put(username, playerTime + 1);
            }
        }, 20, 20 * 3, true);
        if (Core.startTime.get(player.getUniqueId()) != null) {
            Core.startTime.replace(player.getUniqueId(), System.currentTimeMillis());
        } else {
            Core.startTime.put(player.getUniqueId(), System.currentTimeMillis());
        }

        addScoreboard(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Core.startTime.remove(player.getUniqueId());
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        String message = event.getMessage();
        if (message.equalsIgnoreCase("జ్ఞ\u200Cా")) {
            API.getMessageAPI().sendAbuseMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    public void addScoreboard(Player player) {
        fakeScoreboard = new FakeScoreboard();
        object = new Objective("test", new ObjectiveCriteria("dummy", true));
        DisplayObjective newObject = new DisplayObjective(
                object,
                ObjectiveSortOrder.DESCENDING,
                ObjectiveDisplaySlot.SIDEBAR
        );

        addToScoreboard(player, object);

        fakeScoreboard.objective = newObject;
        taskHandler = API.getMainAPI().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                fakeScoreboard.addPlayer(player);
            }
        }, 20 * 4);

        taskRepeatingHandler = API.getMainAPI().getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!player.isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                addToScoreboard(player, object);
                fakeScoreboard.update();
            }
        },20 * 5, true);
    }

    public void addToScoreboard(Player player, Objective object) {
        object.setDisplayName("§3§lNycuRO §r§7» §bFactions");
        object.setScore(1, String.valueOf(""), 13);
        object.setScore(2, String.valueOf(""), 12);

        Profile profile = Database.profile.get(player.getUniqueId());
        FPlayer fPlayers = FPlayers.i.get(player);
        Faction faction = fPlayers.getFaction();

        try {
            if (fPlayers.hasFaction()) {
                object.setScore(3, "  §3Factions: §8" + faction.getTag(), 11);
                object.setScore(4, "  §3Kills: §8" + profile.getKills(), 10);
                object.setScore(5, "  §3Deaths: §8" + profile.getDeaths(), 9);
                object.setScore(6, "  §3OnlineTime: §8" + Core.time(profile.getTime()), 8);
                if (profile.getJob() != 0) {
                    object.setScore(7, "  §3Job: §8" + JobsAPI.jobs.get(profile.getJob()), 7);
                    object.setScore(8, "  §3Coins: §8" + Core.round(profile.getCoins(), 2), 6);
                    object.setScore(9, "  §3Level: §8" + player.getExperienceLevel(), 5);
                    if ((coords.getOrDefault(player.getName(), null).equals(false)) || (coords.getOrDefault(player.getName(), null) == null)) {
                        object.setScore(10, String.valueOf(""), 4);
                        object.setScore(11, "§7Discord: §3discord.nycuro.us", 3);
                    } else if (coords.getOrDefault(player.getName(), null).equals(true)) {
                        object.setScore(10, "  §3Coords: §8" + (int) player.getX() + ":" + (int) player.getY() + ":" + (int) player.getZ(), 4);
                        object.setScore(11, String.valueOf(""), 3);
                        object.setScore(12, "§7Discord: §3discord.nycuro.us", 2);
                    }
                } else {
                    object.setScore(7, "  §3Coins: §8" + Core.round(profile.getCoins(), 2), 7);
                    object.setScore(8, "  §3Level: §8" + player.getExperienceLevel(), 6);
                    if ((coords.getOrDefault(player.getName(), null).equals(false)) || (coords.getOrDefault(player.getName(), null) == null)) {
                        object.setScore(9, String.valueOf(""), 5);
                        object.setScore(10, "§7Discord: §3discord.nycuro.us", 4);
                    } else if (coords.getOrDefault(player.getName(), null).equals(true)) {
                        object.setScore(9, "  §3Coords: §8" + (int) player.getX() + ":" + (int) player.getY() + ":" + (int) player.getZ(), 5);
                        object.setScore(10, String.valueOf(""), 4);
                        object.setScore(11, "§7Discord: §3discord.nycuro.us", 3);
                    }
                }
            } else {
                object.setScore(3, "  §3Kills: §8" + profile.getKills(), 11);
                object.setScore(4, "  §3Deaths: §8" + profile.getDeaths(), 10);
                object.setScore(5, "  §3OnlineTime: §8" + Core.time(profile.getTime()), 9);
                if (profile.getJob() != 0) {
                    object.setScore(6, "  §3Job: §8" + JobsAPI.jobs.get(profile.getJob()), 8);
                    object.setScore(7, "  §3Coins: §8" + Core.round(profile.getCoins(), 2), 7);
                    object.setScore(8, "  §3Level: §8" + player.getExperienceLevel(), 6);
                    if ((coords.getOrDefault(player.getName(), null).equals(false)) || (coords.getOrDefault(player.getName(), null) == null)) {
                        object.setScore(9, String.valueOf(""), 5);
                        object.setScore(10, "§7Discord: §3discord.nycuro.us", 4);
                    } else if (coords.getOrDefault(player.getName(), null).equals(true)) {
                        object.setScore(9, "  §3Coords: §8" + (int) player.getX() + ":" + (int) player.getY() + ":" + (int) player.getZ(), 5);
                        object.setScore(10, String.valueOf(""), 4);
                        object.setScore(11, "§7Discord: §3discord.nycuro.us", 3);
                    }
                } else {
                    object.setScore(6, "  §3Coins: §8" + Core.round(profile.getCoins(), 2), 8);
                    object.setScore(7, "  §3Level: §8" + player.getExperienceLevel(), 7);
                    if ((coords.getOrDefault(player.getName(), null).equals(false)) || (coords.getOrDefault(player.getName(), null) == null)) {
                        object.setScore(8, String.valueOf(""), 6);
                        object.setScore(9, "§7Discord: §3discord.nycuro.us", 5);
                    } else if (coords.getOrDefault(player.getName(), null).equals(true)) {
                        object.setScore(8, "  §3Coords: §8" + (int) player.getX() + ":" + (int) player.getY() + ":" + (int) player.getZ(), 6);
                        object.setScore(9, String.valueOf(""), 5);
                        object.setScore(10, "§7Discord: §3discord.nycuro.us", 4);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
