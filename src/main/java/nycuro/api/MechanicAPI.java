package nycuro.api;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;
import nycuro.API;
import nycuro.ai.entity.BossEntity;
import nycuro.database.Database;
import nycuro.database.objects.Profile;
import nycuro.gui.list.ResponseFormWindow;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class MechanicAPI {

    public boolean isOnSpawn(Entity entity) {
        double x = entity.getLevel().getSpawnLocation().getX();
        double y = entity.getLevel().getSpawnLocation().getY();
        double z = entity.getLevel().getSpawnLocation().getZ();
        Vector3 vector3 = new Vector3(x, y, z);
        return entity.getLevel().getName().equalsIgnoreCase("world") && entity.getPosition().distance(vector3) <= 300 && !entity.getName().equals("NycuR0");
    }

    public boolean isOnPvP(Entity entity) {
        double x = entity.getLevel().getSpawnLocation().getX();
        double y = entity.getLevel().getSpawnLocation().getY();
        double z = entity.getLevel().getSpawnLocation().getZ();
        Vector3 vector3 = new Vector3(x, y, z);
        return entity.getLevel().getName().equalsIgnoreCase("pvp") && entity.getPosition().distance(vector3) <= 34 && !entity.getName().equals("NycuR0") && entity.getY() >= 75;
    }

    public boolean isOnBorder(Player player) {
        double x = player.getX();
        double z = player.getZ();
        return (x >= 10000 || x <= -10000) || (z >= 10000 || z <= -10000);
    }

    public void sendToSpawn(Player player) {
        double x = 113 + 0.5;
        double y = 73;
        double z = 64 + 0.5;
        Level level = API.getMainAPI().getServer().getDefaultLevel();
        if (!level.isChunkLoaded(113 >> 4, 64 >> 4)) {
            level.loadChunk(113 >> 4, 64 >> 4);
        }
        player.teleport(new Position(x, y, z, level));
        player.setImmobile(false);
    }

    /*public void spawnFireworks() {
        entities.forEach(Entity::spawnToAll);
    }*/

    public void spawnBoss() {
        for (Player player : API.getMainAPI().getServer().getOnlinePlayers().values()) {
            addDropPartyKey(player);
            new BossEntity();
            API.getMessageAPI().sendDropPartySpawnedMessage(player);
        }
    }

    public void spawnEntities() {
        Entity entities1 = Entity.createEntity(10, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 48 >> 4), API.getAiAPI().getChickenNBT());
        Entity entities2 = Entity.createEntity(11, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 57 >> 4), API.getAiAPI().getCowNBT());
        Entity entities3 = Entity.createEntity(12, API.getMainAPI().getServer().getDefaultLevel().getChunk(129 >> 4, 48 >> 4), API.getAiAPI().getPigNBT());
        Entity entities4 = Entity.createEntity(13, API.getMainAPI().getServer().getDefaultLevel().getChunk(120 >> 4, 48 >> 4), API.getAiAPI().getSheepNBT());
        entities1.spawnToAll();
        entities2.spawnToAll();
        entities3.spawnToAll();
        entities4.spawnToAll();
    }

    public void sendDropPartyMessageBroadcast(Player player) {
        API.getMessageAPI().sendDropPartyEventMessage(player);
    }

    private void addDropPartyKey(Player player) {
        Item dropPartyKey = Item.get(Item.TRIPWIRE_HOOK, 1, 1);
        Enchantment enchantment = Enchantment.get(Enchantment.ID_PROTECTION_ALL);
        enchantment.setLevel(1);
        dropPartyKey.setCustomName("DropParty Key");
        dropPartyKey.addEnchantment(enchantment);
        player.getInventory().addItem(dropPartyKey);
        API.getMessageAPI().sendDropPartyReceiveKeyMessage(player);
    }

    public void createBossBar(Player player) {
        API.getMainAPI().bossbar.put(player.getName(), new DummyBossBar.Builder(player)
                .text("bossbar")
                .length(100)
                .color(BlockColor.GREEN_BLOCK_COLOR)
                .build());
        player.createBossBar(API.getMainAPI().bossbar.get(player.getName()));
    }

    public void sendServersModal(Player player) {
        Profile profile = Database.profile.get(player.getUniqueId());
        int lang = profile.getLanguage();
        switch (lang) {
            case 0:
                FormWindowSimple serversMenu = new FormWindowSimple("Servers", "                       Hello!\n" +
                        "             Welcome to Servers!\n" +
                        "  Select what you want to do from now.");
                serversMenu.addButton(new ElementButton("How to be Partner?", new ElementButtonImageData("url", "https://i.imgur.com/nujWKR3.png")));
                serversMenu.addButton(new ElementButton("Close"));
                player.showFormWindow(new ResponseFormWindow(serversMenu, new Consumer<Map<Integer, Object>>() {
                    @Override
                    public void accept(Map<Integer, Object> response) {
                        if (!response.isEmpty()) {
                            switch (response.entrySet().iterator().next().getKey()) {
                                case 0:
                                    sendInfoServers(player);
                                    return;
                                case 1:
                                    break;
                            }
                        }
                    }
                }));
                break;
            case 1:
                serversMenu = new FormWindowSimple("Servers", "                      Salut!\n" +
                        "          Bine ai venit la Servere!\n" +
                        "     Alege ce doresti sa faci de acum.");
                serversMenu.addButton(new ElementButton("How to be Partner?", new ElementButtonImageData("url", "https://i.imgur.com/nujWKR3.png")));
                serversMenu.addButton(new ElementButton("Close"));
                player.showFormWindow(new ResponseFormWindow(serversMenu, new Consumer<Map<Integer, Object>>() {
                    @Override
                    public void accept(Map<Integer, Object> response) {
                        if (!response.isEmpty()) {
                            switch (response.entrySet().iterator().next().getKey()) {
                                case 0:
                                    sendInfoServers(player);
                                    return;
                                case 1:
                                    break;
                            }
                        }
                    }
                }));
                break;
        }
    }

    private void sendInfoServers(Player player) {
        FormWindowCustom infoMenu = new FormWindowCustom("Info Partner");
        Profile profile = Database.profile.get(player.getUniqueId());
        int lang = profile.getLanguage();
        switch (lang) {
            case 0:
                infoMenu.addElement(new ElementLabel("                      Hello!\n" +
                        "         Welcome to Partner Info!\n\n" +
                        "§c» §aHow can i be Partner?:\n" +
                        "§eFor be a Partner, you need just ask. Contact me on Discord.\n" +
                        "§eDiscord: §7NycuRO#6842\n\n" +
                        "§c» §aWhat benefits i have?: \n" +
                        "§eYour Server appereal here.\n" +
                        "§eYou can get a custom dns for your server.\n" +
                        "§eYour DNS need to be like that: §7{name}.nycuro.us\n\n" +
                        "§c» §aThanks: \n" +
                        "§eThanks so much for all persons who tried to be Partners and who wants.\n" +
                        "§eWe waiting with a message on Discord :).\n" +
                        "§eHave a nice day!"));
                break;
            case 1:
                infoMenu.addElement(new ElementLabel("                      Salut!\n" +
                        "      Bine ai venit la Info Partener!\n\n" +
                        "§c» §aCum pot deveni Partener?:\n" +
                        "§ePentru a deveni partener, trebuie doar sa ceri acest lucru. Contacteaza-ma pe Discord.\n" +
                        "§eDiscord: §7NycuRO#6842\n\n" +
                        "§c» §aCe beneficii am?:\n" +
                        "§eServerul tau va aparea aici.\n" +
                        "§eVei primi un DNS Custom pentru Serverul tau.\n" +
                        "§eAcesta va fi de forma: §7{name}.nycuro.us\n\n" +
                        "§c» §aMultumiri:\n" +
                        "§eVreau sa le multumesc tuturor persoanelor care au incercat sa fie Parteneri si care vor.\n" +
                        "§eVa asteptam cu un mesaj pe Discord :).\n" +
                        "§eO zi placuta!"));
                break;
        }
        player.showFormWindow(infoMenu);
    }

    public void sendModalContents(Player player) {
        FormWindowCustom serverMenu = new FormWindowCustom("Server Settings");
        serverMenu.setIcon("https://i.imgur.com/BXo8Cjp.png");
        int lang = 0;
        Profile profile = Database.profile.get(player.getUniqueId());
        if (profile != null) {
            lang = profile.getLanguage();
        }
        switch (lang) {
            case 0:
                serverMenu.addElement(new ElementLabel("§eHello!"));
                serverMenu.addElement(new ElementLabel("§eWelcome to Server Settings!"));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementDropdown("§c» §aLanguage:",
                        Arrays.asList("English", "Romana"), 0));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                break;
            case 1:
                serverMenu.addElement(new ElementLabel("§eSalut!"));
                serverMenu.addElement(new ElementLabel("§eBine ai venit la Server Settings!"));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementDropdown("§c» §aLanguage:",
                        Arrays.asList("English", "Romana"), 0));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                serverMenu.addElement(new ElementLabel("                                                                                    "));
                break;
        }
        player.addServerSettings(serverMenu);
    }
}
