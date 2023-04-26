package fr.hyriode.basics.debug.gui.server;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.server.ILobbyAPI;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.server.select.ServerGameTypeGUI;
import fr.hyriode.basics.debug.gui.server.select.ServerMapGUI;
import fr.hyriode.basics.debug.gui.server.select.ServerTypeGUI;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyggdrasil.api.server.HyggServerCreationInfo;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 17:42
 */
public class ServerCreateGUI extends HyriInventory {

    private IHyriGameInfo gameInfo;
    private String gameName;
    private String gameType;
    private IHyriWorld map;

    private boolean lobby;

    public ServerCreateGUI(Player owner, HyriBasics plugin) {
        super(owner, ChatColor.DARK_GRAY + "Démmarer un serveur", 5 * 9);

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(36, 44, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(0, BasicsHead.MONITOR_ARROW_LEFT.asItemBuilder()
                .withName(ChatColor.DARK_AQUA + "Revenir en arrière")
                .withLore(ChatColor.GRAY + "Revenir au menu de", ChatColor.GRAY + "contrôle des serveurs.")
                .build(), event -> new ServersGUI(this.owner, plugin).open());

        // Global information
        this.setItem(4, BasicsHead.SERVER.asItemBuilder()
                .withName(ChatColor.DARK_AQUA + "Créer un serveur")
                .withLore(ChatColor.GRAY + "Démarre un serveur avec les", ChatColor.GRAY + "paramètres souhaités")
                .build());

        // Create server
        this.setItem(40, new ItemBuilder(Material.STAINED_GLASS, 1, 5)
                .withName(ChatColor.GREEN + "Créer le serveur")
                .withLore(ChatColor.GRAY + "Envoi une requête pour", ChatColor.GRAY + "démarrer le serveur souhaité")
                .build(),
                event -> {
                    if (this.gameName == null) {
                        this.owner.sendMessage(ChatColor.RED + "Type de serveur invalide!");
                        return;
                    }

                    if (this.gameType == null && !this.lobby) {
                        this.owner.sendMessage(ChatColor.RED + "Type de jeu invalide!");
                        return;
                    }

                    final long before = System.currentTimeMillis();

                    this.owner.sendMessage(ChatColor.GREEN + "Serveur en cours de création... (type: " + this.gameName + "; type de jeu: " + this.gameType + "; map: " + this.map.getName());

                    HyriAPI.get().getServerManager().createServer(new HyggServerCreationInfo(this.gameName)
                            .withGameType(this.gameType)
                            .withMap(this.map == null ? null : this.map.getName()),
                            server -> this.owner.sendMessage(ChatColor.GREEN + "'" + server.getName() + "' démarré après " + (System.currentTimeMillis() - before) + "ms."));

                    new ServersGUI(this.owner, plugin).open();
                });

        this.addItems();
    }

    private void addItems() {
        // Server type
        this.setItem(21, new ItemBuilder(Material.COMPASS)
                .withName(ChatColor.DARK_AQUA + "Type de serveur")
                .withLore(
                        ChatColor.GRAY + "Ex: lobby, bedwars",
                        "",
                        ChatColor.DARK_GRAY + "Actuel: " + ChatColor.AQUA + (this.gameName == null ? "Aucun" : this.gameName),
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour modifier")
                .build(),
                event -> {
                    final List<IHyriGameInfo> gameInfos = HyriAPI.get().getGameManager().getGamesInfo();

                    new ServerTypeGUI(this.owner, gameInfos, gameInfo -> {
                        this.gameInfo = gameInfo;
                        this.gameName = gameInfo.getName();
                        this.map = null;
                        this.gameType = null;
                        this.lobby = false;

                        this.addItems();
                        this.open();
                    }, () -> {
                        this.gameInfo = null;
                        this.gameName = ILobbyAPI.TYPE;
                        this.lobby = true;

                        this.addItems();
                        this.open();
                    }).open();
                });

        // Game type
        this.setItem(22, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .withName(ChatColor.DARK_AQUA + "Type de jeu")
                .withLore(
                        ChatColor.GRAY + "Ex: default, Solo, Normal",
                        "",
                        ChatColor.DARK_GRAY + "Actuel: " + ChatColor.AQUA + (this.gameType == null ? "Aucun" : this.gameType),
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour modifier")
                .build(),
                event -> {
                    if (this.gameName == null) {
                        this.owner.sendMessage(ChatColor.RED + "Il faut d'abord séléctionner un type de serveur avant de choisir le type de jeu!");
                        return;
                    }

                    if (this.lobby) {
                        this.owner.sendMessage(ChatColor.RED + "Action impossible pour un serveur 'lobby'!");
                        return;
                    }

                    new ServerGameTypeGUI(this.owner, this.gameInfo.getTypes(), gameType -> {
                        this.gameType = gameType;
                        this.map = null;

                        this.addItems();
                        this.open();
                    }).open();
                });

        // Map
        this.setItem(23, new ItemBuilder(Material.PAPER)
                .withName(ChatColor.DARK_AQUA + "Carte")
                .withLore(
                        ChatColor.GRAY + "Ex: Eternal, default, Endstone",
                        "",
                        ChatColor.DARK_GRAY + "Actuel: " + ChatColor.AQUA + (this.map == null ? "Aucun" : this.map),
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour modifier")
                .build(),
                event -> {
                    if (this.gameName == null) {
                        this.owner.sendMessage(ChatColor.RED + "Il faut d'abord séléctionner un type de serveur avant de choisir le type de jeu!");
                        return;
                    }

                    if (this.gameType == null && !this.lobby) {
                        this.owner.sendMessage(ChatColor.RED + "Il faut d'abord séléctionner un type de jeu avant de choisir la carte!");
                        return;
                    }

                    final List<IHyriWorld> maps = this.gameType != null ? HyriAPI.get().getWorldManager().getWorlds(this.gameInfo.getName(), this.gameType) : HyriAPI.get().getWorldManager().getWorlds(this.lobby ? ILobbyAPI.TYPE : this.gameInfo.getName());

                    new ServerMapGUI(this.owner, maps, map -> {
                        this.map = map;

                        this.addItems();
                        this.open();
                    }).open();
                });
    }

}
