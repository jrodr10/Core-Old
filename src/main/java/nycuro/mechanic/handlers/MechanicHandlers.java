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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import nycuro.API;
import nycuro.Core;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class MechanicHandlers implements Listener {

    private Object2IntMap<String> timers = new Object2IntOpenHashMap<>();

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

    @EventHandler
    public void onHurt(EntityDamageEvent event) {
        Player player = (event.getEntity() instanceof Player) ? (Player) event.getEntity() : null;
        if (player == null) return;
        Player damager = null;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            if (ev instanceof EntityDamageByChildEntityEvent) {
                EntityDamageByChildEntityEvent evc = (EntityDamageByChildEntityEvent) ev;
                if (evc.getDamager() instanceof Player) damager = (Player) evc.getDamager();
            } else if (ev.getDamager() instanceof Player) damager = (Player) ev.getDamager();

            if (damager == null) return;
            /** Credits: @Nora. Thanks! */
            if (!API.getMechanicAPI().isOnSpawn(damager)) {
                if (!API.getMechanicAPI().isOnSpawn(player)) {
                    API.getMessageAPI().sendHitBowMessage(player, damager);
                    if (event.getDamage() >= player.getHealth()) {
                        event.setCancelled();
                        player.setHealth(20);
                        player.getFoodData().setLevel(20);
                        player.teleport(player.getServer().getDefaultLevel().getSpawnLocation());
                        player.removeAllEffects();
                        player.getInventory().clearAll();
                        API.getMessageAPI().sendDeadMessage(player, damager);
                    }
                    if (player.getPosition().getY() < 0) {
                        event.setCancelled();
                        player.setHealth(20);
                        player.getFoodData().setLevel(20);
                        player.teleport(player.getServer().getDefaultLevel().getSpawnLocation());
                        player.removeAllEffects();
                        player.getInventory().clearAll();
                    }
                }
            }
        }
    }
}
