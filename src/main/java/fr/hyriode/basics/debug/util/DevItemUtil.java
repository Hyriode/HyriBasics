package fr.hyriode.basics.debug.util;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:50
 */
public class DevItemUtil {

    public static ItemStack createServerItem(HyggServer server) {
        final BiFunction<String, String, String> loreFormatter = (key, value) -> ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + key + ": " + ChatColor.AQUA + value;
        final ServerStateWrapper state = ServerStateWrapper.from(server.getState());

        return new ItemBuilder(state.getItem())
                .withName(ChatColor.DARK_AQUA + server.getName())
                .withLore("",
                        ChatColor.DARK_GRAY + "Informations:",
                        loreFormatter.apply("Mode", server.getGameType() == null ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : server.getGameType()),
                        loreFormatter.apply("Map", server.getMap() == null ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : server.getMap()),
                        loreFormatter.apply("Etat", state.getDisplayName()),
                        loreFormatter.apply("Accessibilité", server.getAccessibility().name()),
                        "",
                        ChatColor.DARK_GRAY + "Chiffres:",
                        loreFormatter.apply("Slots", String.valueOf(server.getSlots())),
                        loreFormatter.apply("Joueurs", String.valueOf(server.getPlayers().size())),
                        loreFormatter.apply("Joueurs (jeu)", String.valueOf(server.getPlayingPlayers().size())))
                .build();
    }

    public static ItemStack createProxyItem(HyggProxy proxy) {
        final BiFunction<String, String, String> loreFormatter = (key, value) -> ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + key + ": " + ChatColor.AQUA + value;
        final ProxyStateWrapper state = ProxyStateWrapper.from(proxy.getState());

        return new ItemBuilder(state.getItem())
                .withName(ChatColor.DARK_AQUA + proxy.getName())
                .withLore("",
                        ChatColor.DARK_GRAY + "Informations:",
                        loreFormatter.apply("Etat", state.getDisplayName()),
                        loreFormatter.apply("Joueurs", String.valueOf(proxy.getPlayers().size())))
                .build();
    }

    public static ItemStack createLimboItem(HyggLimbo limbo) {
        final BiFunction<String, String, String> loreFormatter = (key, value) -> ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + key + ": " + ChatColor.AQUA + value;
        final LimboStateWrapper state = LimboStateWrapper.from(limbo.getState());

        return new ItemBuilder(state.getItem())
                .withName(ChatColor.DARK_AQUA + limbo.getName())
                .withLore("",
                        ChatColor.DARK_GRAY + "Informations:",
                        loreFormatter.apply("Type", limbo.getType().name()),
                        loreFormatter.apply("Etat", state.getDisplayName()),
                        loreFormatter.apply("Joueurs", String.valueOf(limbo.getPlayers().size())))
                .build();
    }

}
