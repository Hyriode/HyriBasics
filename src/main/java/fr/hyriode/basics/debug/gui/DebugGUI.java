package fr.hyriode.basics.debug.gui;

import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.limbo.LimbosGUI;
import fr.hyriode.basics.debug.gui.proxy.ProxiesGUI;
import fr.hyriode.basics.debug.gui.server.ServersGUI;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 21:27
 */
public abstract class DebugGUI extends PaginatedInventory {

    public static final BiFunction<String, String, String> LORE_FORMATTER = (key, value) -> ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + key + ": " + ChatColor.AQUA + value;

    public static class Manager {

        private static Manager instance;

        private final List<Category> categories;

        public Manager() {
            instance = this;

            this.categories = new ArrayList<>();

            this.registerCategory(new Category(BasicsHead.BIRD_HOUSE_CYAN, "Menu principal", Arrays.asList("Accéder au menu principal du", "système d'aide au développement", "et de contrôle."), 0, MainGUI.class));
            this.registerCategory(new Category(BasicsHead.COIL_OF_WIRE, "Proxys", Arrays.asList("Accéder au menu de contrôle", "des proxies."), 2, ProxiesGUI.class));
            this.registerCategory(new Category(BasicsHead.COMPUTER, "Limbos", Arrays.asList("Accéder au menu de contrôle", "des limbos."), 3, LimbosGUI.class));
            this.registerCategory(new Category(BasicsHead.SERVER, "Serveurs", Arrays.asList("Accéder au menu de contrôle", "des serveurs."), 4, ServersGUI.class));
            this.registerCategory(new Category(BasicsHead.GAME, "Jeux", Arrays.asList("Accéder au menu de contrôle", "des jeux."), 6, ServersGUI.class));
            this.registerCategory(new Category(BasicsHead.DICE, "Jeux rotatifs", Arrays.asList("Accéder au menu de contrôle", "des jeux rotatifs."), 7, ServersGUI.class));
        }

        public void registerCategory(Category category) {
            this.categories.add(category);
        }

        public List<Category> getCategories() {
            return this.categories;
        }

        static Manager get() {
            return instance;
        }

    }

    public static class Category {

        private final ItemStack item;

        private final String name;
        private final List<String> lore;
        private final int slot;

        private final Class<? extends DebugGUI> guiClass;

        public Category(ItemStack item, String name, List<String> lore, int slot, Class<? extends DebugGUI> guiClass) {
            if (slot >= 9) {
                throw new IllegalArgumentException("Slot must be less than 9!");
            }
            this.item = item;
            this.name = name;
            this.lore = new ArrayList<>();
            this.slot = slot;
            this.guiClass = guiClass;

            for (String line : lore) {
                this.lore.add(ChatColor.GRAY + line);
            }
        }

        public Category(BasicsHead head, String name, List<String> lore, int slot, Class<? extends DebugGUI> guiClass) {
            this(ItemBuilder.asHead(head).build(), name, lore, slot, guiClass);
        }

        public ItemStack getItem() {
            return this.item;
        }

        public String getName() {
            return this.name;
        }

        public List<String> getLore() {
            return this.lore;
        }

        public int getSlot() {
            return this.slot;
        }

        public Class<? extends DebugGUI> getGuiClass() {
            return this.guiClass;
        }

    }

    protected boolean usingPages;

    protected final HyriBasics plugin;

    public DebugGUI(Player owner, String name, HyriBasics plugin) {
        super(owner, ChatColor.DARK_AQUA + "Debug " + ChatColor.DARK_GRAY +  "┃ " + ChatColor.GRAY + name, 6 * 9);
        this.plugin = plugin;

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        for (Category category : Manager.get().getCategories()) {
            this.setItem(category.getSlot(), new ItemBuilder(category.getItem())
                    .withName(ChatColor.DARK_AQUA + category.getName())
                    .withLore(category.getLore())
                    .build(), event -> this.openSubGUI(category.getGuiClass()));
        }
    }

    private void openSubGUI(Class<? extends DebugGUI> guiClass) {
        if (this.getClass() == guiClass) {
            return;
        }

        try {
            final Constructor<? extends DebugGUI> constructor = guiClass.getDeclaredConstructor(Player.class, HyriBasics.class);
            final DebugGUI gui = constructor.newInstance(this.owner, this.plugin);

            gui.open();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addDefaultPagesItems(45, 53);
    }

}
