package nycuro.api;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import nycuro.API;
import nycuro.database.Database;
import nycuro.database.objects.Profile;
import nycuro.gui.list.ResponseFormWindow;

import java.util.Map;
import java.util.function.Consumer;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class JobsAPI {

    public static Object2ObjectMap<Integer, String> jobs = new Object2ObjectArrayMap<>();

    static {
        jobs.put(0, "");
        jobs.put(1, "LumberJack");
        jobs.put(2, "Miner");
        jobs.put(3, "Farmer");
        jobs.put(4, "Killer");
        jobs.put(5, "Hunter");
    }

    private void sendInfoMessageJobs(Player player) {
        FormWindowCustom infoMenu = new FormWindowCustom("Info Jobs");
        infoMenu.addElement(new ElementLabel(API.getMessageAPI().sendInfoMessageJobs(player)));
        player.showFormWindow(infoMenu);
    }

    public void getJob(Player player) {
        FPlayer fPlayers = FPlayers.i.get(player);
        Faction faction = fPlayers.getFaction();
        Conf.prefixAdmin = "**";
        Conf.prefixMod = "*";
        Profile profile = Database.profile.get(player.getUniqueId());
        int level = profile.getLevel();
        FormWindowSimple jobsMenu = new FormWindowSimple("Jobs", API.getMessageAPI().sendJobPrincipalModal(player));
        jobsMenu.addButton(new ElementButton("LumberJack", new ElementButtonImageData("url", "https://i.imgur.com/uWmtrax.png")));
        jobsMenu.addButton(new ElementButton("Miner", new ElementButtonImageData("url", "https://i.imgur.com/XFCYdCz.png")));
        jobsMenu.addButton(new ElementButton("Farmer", new ElementButtonImageData("url", "https://i.imgur.com/otMDlEU.png")));
        jobsMenu.addButton(new ElementButton("Killer", new ElementButtonImageData("url", "https://i.imgur.com/YHkAa4q.png")));
        jobsMenu.addButton(new ElementButton("Hunter", new ElementButtonImageData("url", "https://i.imgur.com/HpwAZvq.png")));
        jobsMenu.addButton(new ElementButton("Info", new ElementButtonImageData("url", "https://i.imgur.com/nujWKR3.png")));
        jobsMenu.addButton(new ElementButton("Without Job", new ElementButtonImageData("url", "https://i.imgur.com/YXBNPBc.png")));
        jobsMenu.addButton(new ElementButton("Close"));
        player.showFormWindow(new ResponseFormWindow(jobsMenu, new Consumer<Map<Integer, Object>>() {
            @Override
            public void accept(Map<Integer, Object> response) {
                if (!response.isEmpty()) {
                    switch (response.entrySet().iterator().next().getKey()) {
                        case 0:
                            profile.setJob(1);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§7[§eLumberJack§7] " + "§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendReceiveJobMessage(player);
                            return;
                        case 1:
                            profile.setJob(2);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§7[§eMiner§7] " + "§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendReceiveJobMessage(player);
                            return;
                        case 2:
                            profile.setJob(3);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§7[§eFarmer§7] " + "§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendReceiveJobMessage(player);
                            return;
                        case 3:
                            profile.setJob(4);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§7[§eKiller§7] " + "§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendReceiveJobMessage(player);
                            return;
                        case 4:
                            profile.setJob(5);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§7[§eHunter§7] " + "§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendReceiveJobMessage(player);
                            return;
                        case 5:
                            sendInfoMessageJobs(player);
                            return;
                        case 6:
                            profile.setJob(0);
                            if (fPlayers.hasFaction()) {
                                player.setNameTag("§a[§c" + level + "§a] §7" + fPlayers.getRole().getPrefix() + faction.getTag() + " §3" + fPlayers.getName());
                            }
                            API.getMessageAPI().sendWithoutJobMessage(player);
                            return;
                        case 7:
                            break;
                    }
                }
            }
        }));
    }
}
