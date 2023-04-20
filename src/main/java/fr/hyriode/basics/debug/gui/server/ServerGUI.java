package fr.hyriode.basics.debug.gui.server;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.server.select.ServerStateGUI;
import fr.hyriode.basics.debug.protocol.DebugProtocol;
import fr.hyriode.basics.debug.util.DevItemUtil;
import fr.hyriode.basics.debug.util.ServerStateWrapper;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.basics.util.ConfirmGUI;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.signgui.SignGUI;
import fr.hyriode.hyrame.utils.PrimitiveType;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:13
 */
public class ServerGUI extends HyriInventory {

    private int wantedSlots = -1;
    private HyggServer.State wantedState;

    private boolean stopping;

    private final HyriBasics plugin;
    private final String serverName;

    public ServerGUI(HyriBasics plugin, Player owner, String name, String serverName) {
        super(owner, name, 6 * 9);
        this.plugin = plugin;
        this.serverName = serverName;
        this.newUpdate(2 * 20L);

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(0, BasicsHead.MONITOR_ARROW_LEFT.asItemBuilder()
                .withName(ChatColor.DARK_AQUA + "Revenir en arrière")
                .withLore(ChatColor.GRAY + "Revenir au menu de", ChatColor.GRAY + "contrôle des serveurs.")
                .build(), event -> this.goBack());

        this.addItems();
    }

    private void addItems() {
        final HyggServer server = this.getServer();

        if (server == null) {
            this.owner.sendMessage(ChatColor.RED + "Le serveur que vous regardiez n'existe plus!");

            this.goBack();
            return;
        }

        // Server information
        this.setItem(4, DevItemUtil.createServerItem(server));

        // Connexion
        this.setItem(25, BasicsHead.ENDER_PEARL.asItemBuilder()
                .withName(ChatColor.GREEN + "Connexion (normale)")
                .withLore(
                        ChatColor.GRAY + "Envoi une requête de connexion",
                        ChatColor.GRAY + "pour rejoindre le serveur.",
                        ChatColor.GRAY + "Cette dernière peut être",
                        ChatColor.GRAY + "refusée (full, état, etc).",
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour se connecter au serveur")
                .build(),
                event -> {
                    HyriAPI.get().getServerManager().sendPlayerToServer(this.owner.getUniqueId(), this.serverName);

                    this.owner.sendMessage(ChatColor.GREEN + "Connexion en cours vers '" + this.serverName + "'...");
                });

        // Stop
        this.setItem(21, BasicsHead.RED_BUTTON.asItemBuilder()
                .withName(ChatColor.RED + "Stopper le serveur")
                .withLore(
                        ChatColor.GRAY + "Le serveur sera arrêtée en",
                        ChatColor.GRAY + "envoyant une requête à Hyggdrasil",
                        "",
                        ChatColor.DARK_AQUA + "Clic gauche pour stopper le serveur",
                        ChatColor.RED + "Clic droit pour stopper le serveur sans évacuation")
                .build(),
                event -> {
                    if (this.stopping) {
                        this.owner.sendMessage(ChatColor.RED + "'" + this.serverName + "' est déjà en train de s'arrêter!");
                        return;
                    }

                    new ConfirmGUI(this.owner)
                            .withConfirmCallback(e -> {
                                this.stopping = true;

                                final ClickType clickType = event.getClick();

                                if (clickType.isLeftClick()) {
                                    this.evacuatePlayers(server);

                                    this.owner.sendMessage(ChatColor.GREEN + "'" + this.serverName + "' en cours d'évacuation avant d'arrêter le serveur... (" + server.getPlayers().size() + " joueurs à évacuer)");

                                    Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, this::stopServer, 5 * 20L);
                                } else if (clickType.isRightClick()) {
                                    this.stopServer();
                                }
                            })
                            .withCancelCallback(e -> {
                                this.owner.sendMessage(ChatColor.RED + "Action annulée.");
                                this.open();
                            })
                            .open();
                });

        // Slots
        this.setItem(22, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .withName(ChatColor.DARK_AQUA + "Slots")
                .withLore(
                        ChatColor.GRAY + "Change les slots disponibles",
                        ChatColor.GRAY + "sur le serveur",
                        "",
                        ChatColor.DARK_GRAY + "Actuel: " + ChatColor.AQUA + (this.wantedSlots <= -1 ? server.getSlots() : this.wantedSlots),
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour modifier")
                .build(),
                event -> new SignGUI((player, strings) -> {
                    final String slotsStr = strings[0];

                    if (PrimitiveType.INTEGER.isValid(slotsStr)) {
                        this.wantedSlots = PrimitiveType.INTEGER.parse(slotsStr);

                        player.sendMessage(ChatColor.GREEN + "Slots du serveur '" + this.serverName + "' modifiés.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Nombre invalide!");
                    }

                    this.addItems();
                    this.open();
                }).withLines("", "^^^^^^^^", "Slots", "disponibles").open(this.owner));

        // State
        final ServerStateWrapper state = ServerStateWrapper.from(this.wantedState == null ? server.getState() : HyggServer.State.valueOf(this.wantedState.name()));

        this.setItem(23, new ItemBuilder(state.getItem())
                .withName(ChatColor.DARK_AQUA + "Etat du serveur")
                .withLore(
                        ChatColor.GRAY + "Force le changement d'état",
                        ChatColor.GRAY + "du serveur",
                        "",
                        ChatColor.DARK_GRAY + "Actuel: " + state.getDisplayName(),
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour changer")
                .build(),
                event -> new ServerStateGUI(this.owner, newState -> {
                    this.wantedState = newState;

                    this.addItems();
                    this.open();
                }).open());

        // Evacuation
        this.setItem(31, BasicsHead.GRAY_RECYCLING_BIN.asItemBuilder()
                .withName(ChatColor.RED + "Evacuer le serveur")
                .withLore(
                        ChatColor.GRAY + "Déplace tous les joueurs présents",
                        ChatColor.GRAY + "sur le serveur vers un autre serveur",
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour évacuer")
                .build(),
                event -> {
                    this.evacuatePlayers(server);

                    this.owner.sendMessage(ChatColor.GREEN + "'" + this.serverName + "' en cours d'évacuation... (" + server.getPlayers().size() + " joueurs à évacuer)");
                });

        // Confirm changes
        this.setItem(49, new ItemBuilder(Material.STAINED_GLASS, 1, 5)
                .withName(ChatColor.GREEN + "Appliquer les modifications")
                .withLore(
                        ChatColor.GRAY + "Applique les changements effectués",
                        ChatColor.GRAY + "sur le serveur (slots, état, etc)")
                .build(),
                event -> {
                    if (this.getServer() == null) {
                        this.owner.sendMessage(ChatColor.RED + "'" + this.serverName + "' n'existe plus!");
                        this.goBack();
                        return;
                    }
                    final DebugProtocol protocol = this.plugin.getDebugProtocol();

                    if (this.wantedSlots >= 0) {
                        protocol.editServerSlots(this.serverName, this.wantedSlots);
                        this.wantedSlots = -1;
                    }

                    if (this.wantedState != null) {
                        protocol.editServerState(this.serverName, this.wantedState);
                        this.wantedState = null;
                    }

                    this.owner.sendMessage(ChatColor.GREEN + "Modifications appliquées pour '" + this.serverName + "'");
                });
    }

    private void evacuatePlayers(HyggServer server) {
        final Set<UUID> players = server.getPlayers();

        for (UUID player : players) {
            HyriAPI.get().getPlayerManager().connectPlayer(player, HyriAPI.get().getLobbyAPI().getBestLobby().getName());
        }
    }

    private void stopServer() {
        this.owner.sendMessage(ChatColor.RED + "Arrêt en cours de '" + this.serverName + "'...");

        HyriAPI.get().getServerManager().removeServer(this.serverName, () -> {
            this.owner.sendMessage(ChatColor.GREEN + "'" + this.serverName + "' a été correctement arrêté.");

            ThreadUtil.backOnMainThread(this.plugin, this::goBack);
        });
    }

    private void goBack() {
        new ServersGUI(this.owner, this.plugin).open();
    }

    @Override
    public void update() {
        this.addItems();
    }

    private HyggServer getServer() {
        return HyriAPI.get().getServerManager().getServer(this.serverName);
    }

}
