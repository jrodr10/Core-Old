package nycuro.mechanic.handlers;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.scheduler.Task;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gt.creeperface.nukkit.scoreboardapi.scoreboard.*;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.*;
import nycuro.API;
import nycuro.Core;
import nycuro.api.JobsAPI;
import nycuro.database.Database;
import nycuro.database.objects.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class MechanicHandlers implements Listener {

    private Object2IntMap<String> timers = new Object2IntOpenHashMap<>();
    public static Object2ObjectMap<String, Boolean> coords = new Object2ObjectOpenHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
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
        FakeScoreboard fakeScoreboard = new FakeScoreboard();
        Objective object = new Objective("test", new ObjectiveCriteria("dummy", true));
        DisplayObjective newObject = new DisplayObjective(
                object,
                ObjectiveSortOrder.DESCENDING,
                ObjectiveDisplaySlot.SIDEBAR
        );

        object.setDisplayName("§3§lNycuRO §r§7» §bFactions");
        object.setScore(9, "    ", 9);
        object.setScore(8, "    ", 8);

        checkFaction(player, object);
        checkJob(player, object);
        checkMoney(player, object);
        checkLevel(player, object);
        checkForCoords(player, object);
        checkDiscord(player, object);

        fakeScoreboard.objective = newObject;
        API.getMainAPI().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!player.isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                fakeScoreboard.addPlayer(player);
            }
        }, 60, true);

        API.getMainAPI().getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!player.isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                checkFaction(player, object);
                checkJob(player, object);
                checkMoney(player, object);
                checkLevel(player, object);
                checkForCoords(player, object);
                checkDiscord(player, object);
                fakeScoreboard.update();
            }
        }, 80, 20, true);
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

    private void checkFaction(Player player, Objective objective) {
        Profile profile = Database.profile.get(player.getUniqueId());
        FPlayer fPlayers = FPlayers.i.get(player);
        Faction faction = fPlayers.getFaction();
        if (fPlayers.hasFaction()) {
            switch (profile.getLanguage()) {
                case 0:
                    objective.setScore(7, "  §3Factions: §8" + faction.getTag(), 7);
                    break;
                case 1:
                    objective.setScore(7, "  §3Factiune: §8" + faction.getTag(), 7);
                    break;
            }
        } else {
            objective.setScore(7, "", 7);
        }
    }

    private void checkJob(Player player, Objective objective) {
        Profile profile = Database.profile.get(player.getUniqueId());
        if (profile.getJob() != 0) {
            objective.setScore(6, "  §3Job: §8" + JobsAPI.jobs.get(profile.getJob()), 6);
        } else {
            objective.setScore(6, "", 6);
        }
    }


    private void checkMoney(Player player, Objective objective) {
        Profile profile = Database.profile.get(player.getUniqueId());
        objective.setScore(5, "  §3Coins: §8" + profile.getCoins(), 5);
    }

    private void checkLevel(Player player, Objective objective) {
        Profile profile = Database.profile.get(player.getUniqueId());
        switch (profile.getLanguage()) {
            case 0:
                objective.setScore(4, "  §3Level: §8" + player.getExperienceLevel(), 4);
                break;
            case 1:
                objective.setScore(4, "  §3Nivel: §8" + player.getExperienceLevel(), 4);
                break;
        }
    }

    private void checkForCoords(Player player, Objective objective) {
        try {
            if (coords.getOrDefault(player.getName(), null).equals(true)) {
                objective.setScore(3, "  §3Coords: §8" + (int) player.getX() + ":" + (int) player.getY() + ":" + (int) player.getZ(), 3);
            } else {
                objective.setScore(3, "", 3);
            }
        } catch (NullPointerException e) {
            // ignore
        }
    }

    private void checkDiscord(Player player, Objective objective) {
        Profile profile = Database.profile.get(player.getUniqueId());
        objective.setScore(2, "     ", 2);
        switch (profile.getLanguage()) {
            case 0:
                objective.setScore(1, "§7Discord: §3discord.nycuro.us", 1);
                break;
            case 1:
                objective.setScore(1, "§7Discord: §3discord.nycuro.us", 1);
                break;
        }
    }
}
