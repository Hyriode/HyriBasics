package fr.hyriode.basics.debug.util;

import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.hyriode.hyggdrasil.api.server.HyggServer.State;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 22:16
 */
public enum ServerStateWrapper {

    CREATING(0, State.CREATING, BasicsHead.LIGHT_SKY_BLUE, ChatColor.AQUA + "Création", Arrays.asList("Le serveur est en cours de", "création")),
    STARTING(1, State.STARTING, BasicsHead.YELLOW, ChatColor.YELLOW + "Démarrage", Collections.singletonList("Le serveur est en train de démarrer")),
    READY(2, State.READY, BasicsHead.LIME_GREEN, ChatColor.GREEN + "Prêt", Arrays.asList("Le serveur est prêt à", "recevoir des joueurs")),
    PLAYING(3, State.PLAYING, BasicsHead.ROSE_RED, ChatColor.RED + "En jeu", Arrays.asList("Une partie est en cours sur", "le serveur")),
    SHUTDOWN(4, State.SHUTDOWN, BasicsHead.BLACK, ChatColor.DARK_GRAY + "Arrêt", Arrays.asList("Le serveur est en train", "de s'arrêter")),
    IDLE(5, State.IDLE, BasicsHead.LIGHT_GRAY, ChatColor.GRAY + "Freeze", Arrays.asList("Le serveur ne répond plus depuis", "au moins " + (HyggdrasilAPI.TIMED_OUT_TIME / 1000) + " secondes"));

    private final int id;
    private final State initial;
    private final ItemStack item;
    private final String displayName;
    private final List<String> lore;

    ServerStateWrapper(int id, State initial, ItemStack item, String displayName, List<String> lore) {
        this.id = id;
        this.initial = initial;
        this.item = item;
        this.displayName = displayName;
        this.lore = new ArrayList<>();

        for (String line : lore) {
            this.lore.add(ChatColor.GRAY + line);
        }
    }

    ServerStateWrapper(int id, State initial, BasicsHead head, String displayName, List<String> lore) {
        this(id, initial, head.asItemBuilder().build(), displayName, lore);
    }

    public int getId() {
        return this.id;
    }

    public State getInitial() {
        return this.initial;
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public static ServerStateWrapper from(HyggServer.State serverState) {
        return valueOf(serverState.name());
    }

}
