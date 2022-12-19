package fr.hyriode.basics.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.packet.HyriChannel;
import fr.hyriode.api.party.HyriPartyInvitation;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 28/04/2022 at 07:32
 */
public class PartyModule {

    public PartyModule() {
        HyriAPI.get().getPubSub().subscribe(HyriChannel.PARTIES, new PartyReceiver(this));
        HyriAPI.get().getNetworkManager().getEventBus().register(new PartyListener());
    }

    public void onInvitation(HyriPartyInvitation invitation) {
        final Player receiver = Bukkit.getPlayer(invitation.getReceiver());

        if (receiver == null) {
            return;
        }

        final IHyriPlayer sender = IHyriPlayer.get(invitation.getSender());

        receiver.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_INVITATION_RECEIVED_MESSAGE.asString(receiver).replace("%player%", sender.getNameWithRank()))
                .append("\n")
                .append(BasicsMessage.BUTTON_ACCEPT.asString(receiver))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BasicsMessage.PARTY_ACCEPT_HOVER.asString(receiver))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p join " + sender.getName()))
                .append(" ")
                .append(BasicsMessage.BUTTON_DENY.asString(receiver))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BasicsMessage.PARTY_DENY_HOVER.asString(receiver))))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p deny " + sender.getName()))));
    }

    public static BaseComponent[] createMessage(Consumer<ComponentBuilder> append) {
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n").strikethrough(false);

        append.accept(builder);

        builder.append("\n").append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true).event((ClickEvent) null).event((HoverEvent) null);

        return builder.create();
    }
}
