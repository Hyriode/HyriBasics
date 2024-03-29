package fr.hyriode.basics.command.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.network.IHyriNetwork;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class MaintenanceCommand extends HyriCommand<HyriBasics> {

    public MaintenanceCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("maintenance")
                .withDescription("The command used to enable/disable maintenance.")
                .withUsage(new CommandUsage().withStringMessage(player -> "/maintenance <on|off>"))
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("on", output -> {
            final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();

            network.getMaintenance().enable(player.getUniqueId(), null);
            network.update();

            player.sendMessage(BasicsMessage.COMMAND_MAINTENANCE_ON.asString(player));
        });

        ctx.registerArgument("off", output -> {
            final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();

            network.getMaintenance().disable();
            network.update();

            player.sendMessage(BasicsMessage.COMMAND_MAINTENANCE_OFF.asString(player));
        });

        super.handle(ctx);
    }

}
