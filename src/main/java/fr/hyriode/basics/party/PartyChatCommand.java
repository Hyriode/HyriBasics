package fr.hyriode.basics.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 29/04/2022 at 22:15
 */
public class PartyChatCommand extends HyriCommand<HyriBasics> {

    public PartyChatCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("partychat")
                .withAliases("pc")
                .withDescription("The command used to send a message in party chat")
                .withUsage(new CommandUsage().withStringMessage(player -> "/pc <message>")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriParty party = HyriAPI.get().getPartyManager().getPlayerParty(playerId);

        if (party == null) {
            player.spigot().sendMessage(PartyModule.createMessage(builder -> builder.append(BasicsMessage.PARTY_DOESNT_HAVE_SENDER_MESSAGE.asString(player))));
            return;
        }

        ctx.registerArgument("%sentence%", output -> party.sendMessage(playerId, output.get(String.class)));

        super.handle(ctx);
    }

}
