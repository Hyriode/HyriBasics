package fr.hyriode.basics.debug.command;

import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.MainGUI;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 19:57
 */
public class DebugCommand extends HyriCommand<HyriBasics> {

    public DebugCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("hyridev")
                .withAliases("dev", "hyridebug", "debug")
                .withUsage(new CommandUsage().withStringMessage(player -> "/hyridev"))
                .withDescription("Command used to manage HyriDev plugin")
                .withPermission(player -> player.getRank().isSuperior(StaffRank.DEVELOPER)));
    }

    @Override
    public void handle(CommandContext ctx) {
        new MainGUI(ctx.getSender(), this.plugin).open();
    }

}
