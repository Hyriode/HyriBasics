package fr.hyriode.basics.command.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class WhitelistCommand extends HyriCommand<HyriBasics> {

    public WhitelistCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("hyriwhitelist")
                .withDescription("Give whitelist access to someone")
                .withAliases("hyriwl")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/hyriwhitelist add <player>")
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "add %input%", output -> {
            final String playerName = output.get(String.class);

            if (HyriAPI.get().getPlayerManager().getWhitelistManager().isWhitelisted(playerName)) {
                player.sendMessage(BasicsMessage.COMMAND_WHITELIST_ALREADY_IN.asString(player));
                return;
            }

            HyriAPI.get().getPlayerManager().getWhitelistManager().whitelistPlayer(playerName);

            player.sendMessage(BasicsMessage.COMMAND_WHITELIST_ADDED.asString(player));
        });
    }

}
