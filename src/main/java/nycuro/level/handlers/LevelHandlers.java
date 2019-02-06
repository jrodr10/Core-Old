package nycuro.level.handlers;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.scheduler.Task;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import nycuro.API;
import nycuro.api.JobsAPI;
import nycuro.database.Database;
import nycuro.database.objects.Profile;
import nycuro.mechanic.handlers.MechanicHandlers;


/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class LevelHandlers implements Listener {

    @EventHandler
    public void onFPlayerJoinFaction(FPlayerJoinEvent event) {
        FPlayer fPlayer = event.getFPlayer();
        Player player = fPlayer.getPlayer();
        Faction faction = event.getFaction();
        MechanicHandlers.fakeScoreboard.removePlayer(player);
        MechanicHandlers.taskHandler.cancel();
        MechanicHandlers.taskRepeatingHandler.cancel();
        Conf.prefixAdmin = "**";
        Conf.prefixMod = "*";
        int level = Database.profile.get(player.getUniqueId()).getLevel();
        int job = Database.profile.get(player.getUniqueId()).getJob();
        if (job == 0) {
            player.setNameTag("§a[§c" + level + "§a] §7" + fPlayer.getRole().getPrefix() + faction.getTag() + " §3" + fPlayer.getName());
        } else {
            player.setNameTag("§7[§e" + JobsAPI.jobs.get(job) + "§7] " + "§a[§c" + level + "§a] §7" + fPlayer.getRole().getPrefix() + faction.getTag() + " §3" + fPlayer.getName());
        }
        MechanicHandlers.taskHandler = API.getMainAPI().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                MechanicHandlers.fakeScoreboard.addPlayer(player);
            }
        }, 20 * 4);
        MechanicHandlers.taskRepeatingHandler = API.getMainAPI().getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!player.isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                API.getMechanicHandlers().addToScoreboard(player, MechanicHandlers.object);
                MechanicHandlers.fakeScoreboard.update();
            }
        },20 * 5, true);
    }

    /*@EventHandler
    public void onCreatureSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (API.getMechanicAPI().isOnSpawn(entity) || API.getMechanicAPI().isOnPvP(entity)) {
            event.setCancelled();
        }
    }*/

    @EventHandler
    public void onFPlayerLeaveFaction(FPlayerLeaveEvent event) {
        FPlayer fPlayer = event.getFPlayer();
        Player player = fPlayer.getPlayer();
        if (!fPlayer.isOnline()) return;
        MechanicHandlers.fakeScoreboard.removePlayer(player);
        MechanicHandlers.taskHandler.cancel();
        MechanicHandlers.taskRepeatingHandler.cancel();
        int level = Database.profile.get(player.getUniqueId()).getLevel();
        int job = Database.profile.get(player.getUniqueId()).getJob();
        if (job == 0) {
            player.setNameTag("§a[§c" + level + "§a] §7" + player.getName());
        } else {
            player.setNameTag("§7[§e" + JobsAPI.jobs.get(job) + "§7] " + "§a[§c" + level + "§a] §7" + player.getName());
        }
        MechanicHandlers.taskHandler = API.getMainAPI().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                MechanicHandlers.fakeScoreboard.addPlayer(player);
            }
        }, 20 * 4);
        MechanicHandlers.taskRepeatingHandler = API.getMainAPI().getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!player.isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                API.getMechanicHandlers().addToScoreboard(player, MechanicHandlers.object);
                MechanicHandlers.fakeScoreboard.update();
            }
        },20 * 5, true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = FPlayers.i.get(player);
        Faction faction = fPlayer.getFaction();
        Conf.prefixAdmin = "**";
        Conf.prefixMod = "*";
        int level = 0;
        int job = 0;
        Profile profile = Database.profile.get(player.getUniqueId());
        if (profile != null) {
            level = profile.getLevel();
            job = profile.getJob();
        }
        if (job == 0) {
            if (fPlayer.hasFaction()) {
                player.setNameTag("§a[§c" + level + "§a] §7" + fPlayer.getRole().getPrefix() + faction.getTag() + " §3" + fPlayer.getName());
            } else {
                player.setNameTag("§a[§c" + level + "§a] §7" + player.getName());
            }
        } else {
            if (fPlayer.hasFaction()) {
                player.setNameTag("§7[§e" + JobsAPI.jobs.get(job) + "§7] " + "§a[§c" + level + "§a] §7" + fPlayer.getRole().getPrefix() + faction.getTag() + " §3" + fPlayer.getName());
            } else {
                player.setNameTag("§7[§e" + JobsAPI.jobs.get(job) + "§7] " + "§a[§c" + level + "§a] §7" + player.getName());
            }
        }
    }
}
