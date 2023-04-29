package fr.hyriode.basics;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.chat.channel.IHyriChatChannelHandler;
import fr.hyriode.basics.afk.AFKModule;
import fr.hyriode.basics.announcement.AnnouncementListener;
import fr.hyriode.basics.booster.BoosterModule;
import fr.hyriode.basics.chat.DefaultChatHandler;
import fr.hyriode.basics.chat.channel.GlobalChannelHandler;
import fr.hyriode.basics.chat.channel.PartnerChannelHandler;
import fr.hyriode.basics.chat.channel.PartyChannelHandler;
import fr.hyriode.basics.debug.protocol.DebugProtocol;
import fr.hyriode.basics.friend.FriendModule;
import fr.hyriode.basics.leveling.LevelingModule;
import fr.hyriode.basics.message.AutomaticMessagesModule;
import fr.hyriode.basics.message.PrivateMessageModule;
import fr.hyriode.basics.nickname.NicknameModule;
import fr.hyriode.basics.party.PartyModule;
import fr.hyriode.basics.tab.TabModule;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * Created by AstFaster
 * on 07/12/2022 at 14:52
 */
public class HyriBasics extends JavaPlugin implements IPluginProvider {

    private static final String PACKAGE = "fr.hyriode.basics";
    private static final String[] HEADER_LINES = new String[] {
            "  _  _          _ ___          _       ",
            " | || |_  _ _ _(_) _ ) __ _ __(_)__ ___",
            " | __ | || | '_| | _ \\/ _` (_-< / _(_-<",
            " |_||_|\\_, |_| |_|___/\\__,_/__/_\\__/__/",
            "       |__/                            "
    };

    private static HyriBasics instance;

    private IHyrame hyrame;
    private FriendModule friendModule;
    private PartyModule partyModule;
    private BoosterModule boosterModule;
    private LevelingModule levelingModule;
    private NicknameModule nicknameModule;
    private PrivateMessageModule privateMessageModule;
    private TabModule tabModule;
    private AFKModule afkModule;
    private DebugProtocol debugProtocol;
    private AutomaticMessagesModule automaticMessagesModule;

    @Override
    public void onEnable() {
        instance = this;

        for (String line : HEADER_LINES) {
            log(line);
        }

        this.hyrame = HyrameLoader.load(this);
        this.friendModule = new FriendModule();
        this.partyModule = new PartyModule();
        this.boosterModule = new BoosterModule();
        this.levelingModule = new LevelingModule();
        this.nicknameModule = new NicknameModule();
        this.privateMessageModule = new PrivateMessageModule();
        this.tabModule = new TabModule();
        this.afkModule = new AFKModule();
        this.debugProtocol = new DebugProtocol();
        this.automaticMessagesModule = new AutomaticMessagesModule();

        // Register HyriAPI events listeners
        HyriAPI.get().getNetworkManager().getEventBus().register(new AnnouncementListener());

        // Register default chat handler
        this.hyrame.getChatManager().registerHandler(100, new DefaultChatHandler());

        // Register chat channels
        final BiConsumer<HyriChatChannel, IHyriChatChannelHandler> channelRegistry = (channel, handler) -> HyriAPI.get().getChatChannelManager().registerHandler(channel, handler);

        channelRegistry.accept(HyriChatChannel.GLOBAL, new GlobalChannelHandler());
        channelRegistry.accept(HyriChatChannel.PARTY, new PartyChannelHandler());
        channelRegistry.accept(HyriChatChannel.PARTNER, new PartnerChannelHandler());
    }

    @Override
    public void onDisable() {

    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.GREEN + "[HyriBasics] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static HyriBasics get() {
        return instance;
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public FriendModule getFriendModule() {
        return this.friendModule;
    }

    public PartyModule getPartyModule() {
        return this.partyModule;
    }

    public BoosterModule getBoosterModule() {
        return this.boosterModule;
    }

    public LevelingModule getLevelingModule() {
        return this.levelingModule;
    }

    public NicknameModule getNicknameModule() {
        return this.nicknameModule;
    }

    public PrivateMessageModule getPrivateMessageModule() {
        return this.privateMessageModule;
    }

    public TabModule getTabModule() {
        return this.tabModule;
    }

    public AFKModule getAFKModule() {
        return this.afkModule;
    }

    public DebugProtocol getDebugProtocol() {
        return this.debugProtocol;
    }

    public AutomaticMessagesModule getAutomaticMessagesModule() {
        return this.automaticMessagesModule;
    }

    @Override
    public JavaPlugin getPlugin() {
        return HyriBasics.get();
    }

    @Override
    public String getId() {
        return "hyribasics";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
