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
public class SlotsCommand extends HyriCommand<HyriBasics> {

    public SlotsCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("slots")
                .withDescription("Edit network maximum slots")
                .withUsage(new CommandUsage().withStringMessage(player -> "/slots <amount>"))
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("%integer%", output -> {
            final int slots = output.get(Integer.class);
            final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();

            network.setSlots(slots);
            network.update();

            player.sendMessage(BasicsMessage.COMMAND_SLOTS_EDIT.asString(player)
                    .replace("%slots%", String.valueOf(slots)));
        });

        super.handle(ctx);
    }

}
