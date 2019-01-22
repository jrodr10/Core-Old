package nycuro.commands.list.mechanic;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import nycuro.API;
import nycuro.Core;
import nycuro.commands.PrincipalCommand;
import nycuro.database.Database;
import nycuro.database.objects.Profile;

import java.util.Map;
import java.util.UUID;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class SaveToDatabaseCommand extends PrincipalCommand {

    public SaveToDatabaseCommand() {
        super("savetodatabase", "DropParty Message Command!");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isOp()) return false;
        try {
            for (Map.Entry<UUID, Profile> map : Database.profile.entrySet()) {
                API.getDatabase().saveDatesPlayer(map.getKey(), map.getValue());
                System.out.println("Name: " + map.getKey() + " Value: " + map.getValue());
            }
        } finally {
            for (int j = 1; j <= 10; j++) {
                Core.log("Saved all!");
                System.out.println("Name: " + Database.scoreboardtimeName.get(j));
                System.out.println("Value: " + Database.scoreboardtimeValue.get(j));
            }
        }
        return false;
    }
}