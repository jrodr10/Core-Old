package nycuro;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import nycuro.abuse.handlers.AbuseHandlers;
import nycuro.ai.AiAPI;
import nycuro.api.*;
import nycuro.chat.handlers.ChatHandlers;
import nycuro.commands.list.*;
import nycuro.commands.list.economy.AddCoinsCommand;
import nycuro.commands.list.economy.GetCoinsCommand;
import nycuro.commands.list.economy.SetCoinsCommand;
import nycuro.commands.list.mechanic.TopCoinsCommand;
import nycuro.commands.list.mechanic.TopDeathsCommand;
import nycuro.commands.list.mechanic.TopKillsCommand;
import nycuro.commands.list.time.GetTimeCommand;
import nycuro.crate.CrateAPI;
import nycuro.crate.handlers.CrateHandlers;
import nycuro.database.Database;
import nycuro.dropparty.DropPartyAPI;
import nycuro.gui.handlers.GUIHandlers;
import nycuro.jobs.handlers.JobsHandlers;
import nycuro.kits.handlers.KitHandlers;
import nycuro.language.handlers.LanguageHandlers;
import nycuro.level.handlers.LevelHandlers;
import nycuro.mechanic.handlers.MechanicHandlers;
import nycuro.messages.handlers.MessageHandlers;
import nycuro.protection.handlers.ProtectionHandlers;
import nycuro.shop.BuyUtils;
import nycuro.shop.EnchantUtils;
import nycuro.shop.MoneyUtils;
import nycuro.shop.SellUtils;
import nycuro.tasks.BossBarTask;
import nycuro.tasks.CheckLevelTask;
import nycuro.tasks.SaveToDatabaseTask;
import nycuro.utils.MechanicUtils;
import nycuro.utils.RandomTPUtils;
import nycuro.utils.WarpUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class Core extends PluginBase {

    public static Object2ObjectMap<UUID, Long> startTime = new Object2ObjectOpenHashMap<>();
    public Object2ObjectMap<String, DummyBossBar> bossbar = new Object2ObjectOpenHashMap<>();

    public static void log(String s) {
        API.getMainAPI().getServer().getLogger().info(TextFormat.colorize("&a" + s));
    }

    public static void registerTops() {
        Database.getTopCoins();
        Database.getTopKills();
        Database.getTopDeaths();
    }

    public static String time(long time) {
        int hours = (int) TimeUnit.MILLISECONDS.toHours(time);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(time) - minutes * 60);
        return String.valueOf(hours + ":" + minutes + ":" + seconds);
    }

    @Override
    public void onLoad() {
        registerAPI();
        registerCommands();
    }

    @Override
    public void onEnable() {
        this.getLogger().info(String.valueOf(this.getDataFolder().mkdirs()));
        registerPlaceHolders();
        registerEvents();
        initDatabase();
        registerTasks();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelAllTasks();
        log("Cancelled All Tasks.");
    }

    private void initDatabase() {
        log("Init SQLite Database...");
        Database.connectToDatabase();
    }

    private void registerAPI() {
        API.mainAPI = this;
        API.mechanicAPI = new MechanicAPI();
        API.utilsAPI = new UtilsAPI();
        UtilsAPI.randomTPUtils = new RandomTPUtils();
        UtilsAPI.warpUtils = new WarpUtils();
        UtilsAPI.mechanicUtils = new MechanicUtils();
        API.kitsAPI = new KitsAPI();
        API.messageAPI = new MessageAPI();
        API.shopAPI = new ShopAPI();
        API.jobsAPI = new JobsAPI();
        ShopAPI.buyUtils = new BuyUtils();
        ShopAPI.sellUtils = new SellUtils();
        ShopAPI.moneyUtils = new MoneyUtils();
        API.aiAPI = new AiAPI();
        API.crateAPI = new CrateAPI();
        API.dropPartyAPI = new DropPartyAPI();
        ShopAPI.enchantUtils = new EnchantUtils();
        API.database = new Database();
        API.slotsAPI = new SlotsAPI();
    }

    private void registerCommands() {
        this.getServer().getCommandMap().register("setcoins", new SetCoinsCommand());
        this.getServer().getCommandMap().register("addcoins", new AddCoinsCommand());
        this.getServer().getCommandMap().register("onlinetime", new GetTimeCommand());
        this.getServer().getCommandMap().register("coins", new GetCoinsCommand());
        this.getServer().getCommandMap().register("topcoins", new TopCoinsCommand());
        this.getServer().getCommandMap().register("topkills", new TopKillsCommand());
        this.getServer().getCommandMap().register("topdeaths", new TopDeathsCommand());
        this.getServer().getCommandMap().register("spawnentities", new SpawnEntitiesCommand());
        this.getServer().getCommandMap().register("servers", new ServersCommand());
        this.getServer().getCommandMap().register("droppartymessage", new DropPartyMessageCommand());
        this.getServer().getCommandMap().register("spawnboss", new SpawnBossCommand());
        this.getServer().getCommandMap().register("kit", new KitCommand());
        this.getServer().getCommandMap().register("kits", new KitsCommand());
        this.getServer().getCommandMap().register("shop", new ShopCommand());
        this.getServer().getCommandMap().register("spawn", new SpawnCommand());
        this.getServer().getCommandMap().register("utils", new UtilsCommand());
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new AbuseHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new GUIHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new KitHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new LanguageHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new LevelHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new MechanicHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new MessageHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new ProtectionHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new JobsHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new CrateHandlers(), this);
        this.getServer().getPluginManager().registerEvents(new ChatHandlers(), this);
    }

    private void registerTasks() {
        this.getServer().getScheduler().scheduleRepeatingTask(new BossBarTask(), 20 * 5, true);
        this.getServer().getScheduler().scheduleRepeatingTask(new CheckLevelTask(), 20 * 20, true);
        this.getServer().getScheduler().scheduleDelayedTask(new SaveToDatabaseTask(), 20 * 60 * 60 * 3, true);
    }

    private void registerPlaceHolders() {
        PlaceholderAPI api = PlaceholderAPI.Companion.getInstance();
        api.staticPlaceholder("top1killsname", () -> Database.scoreboardkillsName.getOrDefault(1, " "));
        api.staticPlaceholder("top2killsname", () -> Database.scoreboardkillsName.getOrDefault(2, " "));
        api.staticPlaceholder("top3killsname", () -> Database.scoreboardkillsName.getOrDefault(3, " "));
        api.staticPlaceholder("top4killsname", () -> Database.scoreboardkillsName.getOrDefault(4, " "));
        api.staticPlaceholder("top5killsname", () -> Database.scoreboardkillsName.getOrDefault(5, " "));
        api.staticPlaceholder("top6killsname", () -> Database.scoreboardkillsName.getOrDefault(6, " "));
        api.staticPlaceholder("top7killsname", () -> Database.scoreboardkillsName.getOrDefault(7, " "));
        api.staticPlaceholder("top8killsname", () -> Database.scoreboardkillsName.getOrDefault(8, " "));
        api.staticPlaceholder("top9killsname", () -> Database.scoreboardkillsName.getOrDefault(9, " "));
        api.staticPlaceholder("top10killsname", () -> Database.scoreboardkillsName.getOrDefault(10, " "));

        api.staticPlaceholder("top1killscount", () -> Database.scoreboardkillsValue.getOrDefault(1, 0).toString());
        api.staticPlaceholder("top2killscount", () -> Database.scoreboardkillsValue.getOrDefault(2, 0).toString());
        api.staticPlaceholder("top3killscount", () -> Database.scoreboardkillsValue.getOrDefault(3, 0).toString());
        api.staticPlaceholder("top4killscount", () -> Database.scoreboardkillsValue.getOrDefault(4, 0).toString());
        api.staticPlaceholder("top5killscount", () -> Database.scoreboardkillsValue.getOrDefault(5, 0).toString());
        api.staticPlaceholder("top6killscount", () -> Database.scoreboardkillsValue.getOrDefault(6, 0).toString());
        api.staticPlaceholder("top7killscount", () -> Database.scoreboardkillsValue.getOrDefault(7, 0).toString());
        api.staticPlaceholder("top8killscount", () -> Database.scoreboardkillsValue.getOrDefault(8, 0).toString());
        api.staticPlaceholder("top9killscount", () -> Database.scoreboardkillsValue.getOrDefault(9, 0).toString());
        api.staticPlaceholder("top10killscount", () -> Database.scoreboardkillsValue.getOrDefault(10, 0).toString());

        api.staticPlaceholder("top1deathsname", () -> Database.scoreboarddeathsName.getOrDefault(1, " "));
        api.staticPlaceholder("top2deathsname", () -> Database.scoreboarddeathsName.getOrDefault(2, " "));
        api.staticPlaceholder("top3deathsname", () -> Database.scoreboarddeathsName.getOrDefault(3, " "));
        api.staticPlaceholder("top4deathsname", () -> Database.scoreboarddeathsName.getOrDefault(4, " "));
        api.staticPlaceholder("top5deathsname", () -> Database.scoreboarddeathsName.getOrDefault(5, " "));
        api.staticPlaceholder("top6deathsname", () -> Database.scoreboarddeathsName.getOrDefault(6, " "));
        api.staticPlaceholder("top7deathsname", () -> Database.scoreboarddeathsName.getOrDefault(7, " "));
        api.staticPlaceholder("top8deathsname", () -> Database.scoreboarddeathsName.getOrDefault(8, " "));
        api.staticPlaceholder("top9deathsname", () -> Database.scoreboarddeathsName.getOrDefault(9, " "));
        api.staticPlaceholder("top10deathsname", () -> Database.scoreboarddeathsName.getOrDefault(10, " "));

        api.staticPlaceholder("top1deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(1, 0).toString());
        api.staticPlaceholder("top2deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(2, 0).toString());
        api.staticPlaceholder("top3deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(3, 0).toString());
        api.staticPlaceholder("top4deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(4, 0).toString());
        api.staticPlaceholder("top5deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(5, 0).toString());
        api.staticPlaceholder("top6deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(6, 0).toString());
        api.staticPlaceholder("top7deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(7, 0).toString());
        api.staticPlaceholder("top8deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(8, 0).toString());
        api.staticPlaceholder("top9deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(9, 0).toString());
        api.staticPlaceholder("top10deathscount", () -> Database.scoreboarddeathsValue.getOrDefault(10, 0).toString());

        api.staticPlaceholder("top1coinsname", () -> Database.scoreboardcoinsName.getOrDefault(1, " "));
        api.staticPlaceholder("top2coinsname", () -> Database.scoreboardcoinsName.getOrDefault(2, " "));
        api.staticPlaceholder("top3coinsname", () -> Database.scoreboardcoinsName.getOrDefault(3, " "));
        api.staticPlaceholder("top4coinsname", () -> Database.scoreboardcoinsName.getOrDefault(4, " "));
        api.staticPlaceholder("top5coinsname", () -> Database.scoreboardcoinsName.getOrDefault(5, " "));
        api.staticPlaceholder("top6coinsname", () -> Database.scoreboardcoinsName.getOrDefault(6, " "));
        api.staticPlaceholder("top7coinsname", () -> Database.scoreboardcoinsName.getOrDefault(7, " "));
        api.staticPlaceholder("top8coinsname", () -> Database.scoreboardcoinsName.getOrDefault(8, " "));
        api.staticPlaceholder("top9coinsname", () -> Database.scoreboardcoinsName.getOrDefault(9, " "));
        api.staticPlaceholder("top10coinsname", () -> Database.scoreboardcoinsName.getOrDefault(10, " "));

        api.staticPlaceholder("top1coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(1, 0.0).toString());
        api.staticPlaceholder("top2coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(2, 0.0).toString());
        api.staticPlaceholder("top3coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(3, 0.0).toString());
        api.staticPlaceholder("top4coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(4, 0.0).toString());
        api.staticPlaceholder("top5coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(5, 0.0).toString());
        api.staticPlaceholder("top6coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(6, 0.0).toString());
        api.staticPlaceholder("top7coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(7, 0.0).toString());
        api.staticPlaceholder("top8coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(8, 0.0).toString());
        api.staticPlaceholder("top9coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(9, 0.0).toString());
        api.staticPlaceholder("top10coinscount", () -> Database.scoreboardcoinsValue.getOrDefault(10, 0.0).toString());

        api.staticPlaceholder("top1timename", () -> Database.scoreboardtimeName.getOrDefault(1, " "));
        api.staticPlaceholder("top2timename", () -> Database.scoreboardtimeName.getOrDefault(2, " "));
        api.staticPlaceholder("top3timename", () -> Database.scoreboardtimeName.getOrDefault(3, " "));
        api.staticPlaceholder("top4timename", () -> Database.scoreboardtimeName.getOrDefault(4, " "));
        api.staticPlaceholder("top5timename", () -> Database.scoreboardtimeName.getOrDefault(5, " "));
        api.staticPlaceholder("top6timename", () -> Database.scoreboardtimeName.getOrDefault(6, " "));
        api.staticPlaceholder("top7timename", () -> Database.scoreboardtimeName.getOrDefault(7, " "));
        api.staticPlaceholder("top8timename", () -> Database.scoreboardtimeName.getOrDefault(8, " "));
        api.staticPlaceholder("top9timename", () -> Database.scoreboardtimeName.getOrDefault(9, " "));
        api.staticPlaceholder("top10timename", () -> Database.scoreboardtimeName.getOrDefault(10, " "));

        api.staticPlaceholder("top1timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(1, (long) 0)));
        api.staticPlaceholder("top2timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(2, (long) 0)));
        api.staticPlaceholder("top3timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(3, (long) 0)));
        api.staticPlaceholder("top4timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(4, (long) 0)));
        api.staticPlaceholder("top5timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(5, (long) 0)));
        api.staticPlaceholder("top6timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(6, (long) 0)));
        api.staticPlaceholder("top7timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(7, (long) 0)));
        api.staticPlaceholder("top8timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(8, (long) 0)));
        api.staticPlaceholder("top9timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(9, (long) 0)));
        api.staticPlaceholder("top10timecount", () -> time(Database.scoreboardtimeValue.getOrDefault(10, (long) 0)));

        api.visitorSensitivePlaceholder("time_player", (p) -> Database.profile.get(p.getUniqueId()).getTime());
    }
}
