package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatCommand extends HyriCommand<HyriBasics> {

    public ChatCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("chat")
                .withDescription("Change your current chat, or send a message to the specified chat")
                .withUsage(new CommandUsage().withStringMessage(player -> "/chat set <chat> | /chat <chat> <message>")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final UUID playerId = player.getUniqueId();

        ctx.registerArgument("set %input%", "/chat set <chat>", output -> {
            try {
                final String chat = output.get(String.class).toUpperCase();
                final HyriChatChannel channel = HyriChatChannel.valueOf(chat);

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
            } catch (Exception e) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_INVALID.asString(player));
            }
        });

        ctx.registerArgument("%input% %sentence%", "/chat <chat> <message>", output -> {
            final String chat = output.get(0, String.class).toUpperCase();

            try {
                final HyriChatChannel channel = HyriChatChannel.valueOf(chat);

                HyriAPI.get().getChatChannelManager().sendMessage(channel, playerId, output.get(1, String.class), false);
            } catch (Exception e) {
                player.sendMessage(BasicsMessage.COMMAND_CHAT_CHANNEL_INVALID.asString(player));
            }
        });

        super.handle(ctx);
    }
}
