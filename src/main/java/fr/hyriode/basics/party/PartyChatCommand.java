package fr.hyriode.basics.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 29/04/2022 at 22:15
 */
public class PartyChatCommand extends HyriCommand<HyriBasics> {

    public PartyChatCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("partychat")
                .withAliases("pc")
                .withDescription("The command used to send a message in party chat")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/pc <message>"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriParty party = HyriAPI.get().getPartyManager().getPlayerParty(playerId);

        if (party == null) {
            player.spigot().sendMessage(PartyModule.createMessage(builder -> builder.append(BasicsMessage.PARTY_DOESNT_HAVE_SENDER_MESSAGE.asString(player))));
            return;
        }

        this.handleArgument(ctx, "%sentence%", output -> party.sendMessage(playerId, output.get(String.class)));
    }

}
