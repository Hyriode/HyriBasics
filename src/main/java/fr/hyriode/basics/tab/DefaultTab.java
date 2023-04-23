package fr.hyriode.basics.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.HyriConstants;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.tab.Tab;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class DefaultTab extends Tab {

    private final Player player;

    public DefaultTab(Player player) {
        this.player = player;

        this.update();
    }

    public void update() {
        this.addLines();
        this.send(this.player);
    }

    private void addLines() {
        this.addHeaderLines();
        this.addFooterLines();
    }

    private void addHeaderLines() {
        final int ping = HyriAPI.get().getPlayerManager().getPing(this.player.getUniqueId());
        final String server = HyriAPI.get().getServer().getName();
        final int players = HyriAPI.get().getNetworkManager().getPlayerCounter().getPlayers();
        double tps = HyriAPI.get().getServer().getTPS();

        if (tps > 20) {
            tps = 20;
        }

        final String formattedTps = String.format("%.2f", tps).replace(",", ".");
        final String informationLine = BasicsMessage.TAB_INFORMATION.asString(this.player)
                .replace("%tps%", formattedTps)
                .replace("%ping%", String.valueOf(ping))
                .replace("%server%", server)
                .replace("%players%", String.valueOf(players));

        this.setBlankHeaderLine(0);
        this.setHeaderLine(1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + HyriConstants.SERVER_NAME + ChatColor.AQUA + ChatColor.ITALIC + ".fr");
        this.setBlankHeaderLine(2);
        this.setHeaderLine(3, informationLine);
        this.setBlankHeaderLine(4);
    }

    private void addFooterLines() {
        this.setBlankFooterLine(0);
        this.setFooterLine(1, this.createFooterLine(BasicsMessage.TAB_WEBSITE_FORUM.asString(this.player), HyriConstants.WEBSITE_URL));
        this.setFooterLine(2, this.createFooterLine(BasicsMessage.TAB_STORE.asString(this.player), HyriConstants.STORE_WEBSITE_URL));
        this.setFooterLine(3, this.createFooterLine(BasicsMessage.TAB_DISCORD.asString(this.player), HyriConstants.DISCORD_URL));
        this.setBlankFooterLine(4);
    }

    private String createFooterLine(String content, String url) {
        return " " + ChatColor.GRAY + content + ChatColor.AQUA + url + " ";
    }

}