package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatCommand extends HyriCommand<HyriBasics> {

    public ChatCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("chat")
                .withDescription("Change your current chat, or send a message to the specified chat")
                .withUsage("/chat set <chat> | /chat <chat> <message>")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();

        this.handleArgument(ctx, "set %input%", output -> {
            final String chat = output.get(String.class).toUpperCase();
            final HyriChatChannel channel = HyriChatChannel.valueOf(chat);

            if (channel == null) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_INVALID.asString(player));
                return;
            }

            final IHyriPlayer account = IHyriPlayer.get(playerId);

            if (!channel.hasAccess(account)) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_CANT_JOIN.asString(account));
                return;
            }

            if (account.getSettings().getChatChannel().equals(channel)) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_ALREADY_IN.asString(account));
                return;
            }

            player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_NOW_TALKING.asString(account).replace("%channel%", channel.name()));

            account.getSettings().setChatChannel(channel);
            account.update();
        });

        this.handleArgument(ctx, "%input% %sentence%", output -> {
            final String chat = output.get(0, String.class).toUpperCase();

            try {
                final HyriChatChannel channel = HyriChatChannel.valueOf(chat);

                HyriAPI.get().getChatChannelManager().sendMessage(channel, playerId, output.get(1, String.class), false);
            } catch (Exception e) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_INVALID.asString(player));
            }
        });
    }
}
