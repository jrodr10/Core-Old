package nycuro.commands.list;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import nycuro.API;
import nycuro.commands.PrincipalCommand;
import nycuro.mechanic.handlers.MechanicHandlers;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class CoordsCommand extends PrincipalCommand {

    public CoordsCommand() {
        super("coords", "Arata coordonatele la Scoreboard");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if ((MechanicHandlers.coords.getOrDefault(commandSender.getName(), null) == null) ||
                (MechanicHandlers.coords.getOrDefault(commandSender.getName(), null).equals(false)))  {
            MechanicHandlers.coords.put(commandSender.getName(), true);
            API.getMessageAPI().sendCoordsSwitchMessage((Player) commandSender, true);
        } else if (MechanicHandlers.coords.getOrDefault(commandSender.getName(), null).equals(true)) {
            MechanicHandlers.coords.put(commandSender.getName(), false);
            API.getMessageAPI().sendCoordsSwitchMessage((Player) commandSender, false);
        }
        return true;
    }
}