package nycuro.commands.list;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.scheduler.Task;
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
        MechanicHandlers.fakeScoreboard.removePlayer((Player) commandSender);
        MechanicHandlers.taskHandler.cancel();
        MechanicHandlers.taskRepeatingHandler.cancel();
        if ((MechanicHandlers.coords.getOrDefault(commandSender.getName(), null) == null) ||
                (MechanicHandlers.coords.getOrDefault(commandSender.getName(), null).equals(false)))  {
            MechanicHandlers.coords.put(commandSender.getName(), true);
            API.getMessageAPI().sendCoordsSwitchMessage((Player) commandSender, true);
        } else if (MechanicHandlers.coords.getOrDefault(commandSender.getName(), null).equals(true)) {
            MechanicHandlers.coords.put(commandSender.getName(), false);
            API.getMessageAPI().sendCoordsSwitchMessage((Player) commandSender, false);
        }
        MechanicHandlers.taskHandler = API.getMainAPI().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                MechanicHandlers.fakeScoreboard.addPlayer((Player) commandSender);
            }
        }, 20 * 4);
        MechanicHandlers.taskRepeatingHandler = API.getMainAPI().getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (!((Player) commandSender).isOnline()) {
                    this.getHandler().cancel();
                    this.cancel();
                }
                API.getMechanicHandlers().addToScoreboard((Player) commandSender, MechanicHandlers.object);
                MechanicHandlers.fakeScoreboard.update();
            }
        },20 * 5, true);
        return true;
    }
}