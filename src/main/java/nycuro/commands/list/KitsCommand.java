package nycuro.commands.list;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import nycuro.API;
import nycuro.commands.PrincipalCommand;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class KitsCommand extends PrincipalCommand {

    public KitsCommand() {
        super("kits", "List Kits");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = (Player) commandSender;
        switch (strings.length) {
            case 0:
                API.getMessageAPI().sendKitsMessage(player);
                return true;
            default:
                API.getMessageAPI().sendExceptionKitsMessage(player);
                return true;
        }
    }
}
