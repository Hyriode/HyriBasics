package fr.hyriode.basics.nickname;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.IHyriNickname;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.util.Skin;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.ThreadUtil;
import fr.hyriode.hyrame.utils.UUIDFetcher;
import fr.hyriode.hyrame.utils.player.ProfileLoader;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 21/04/2022 at 21:16
 */
public class NicknameModule {

    private final NicknameLoader loader;

    public NicknameModule() {
        this.loader = new NicknameLoader();
        this.loader.load();

        HyriBasics.get().getServer().getPluginManager().registerEvents(new NicknameHandler(this), HyriBasics.get());
    }

    public void processNickname(Player player, String nick, String skinOwner, Skin skin, PlayerRank rankType, boolean mojangCheck) {
        if (!this.isNicknameAvailable(nick, mojangCheck) && !Objects.equals(HyriAPI.get().getPlayerManager().getNicknameManager().getPlayerUsingNickname(nick), player.getUniqueId())) {
            player.sendMessage(BasicsMessage.NICKNAME_PLAYER_EXISTS_MESSAGE.asString(player));
            return;
        }

        final UUID playerId = player.getUniqueId();
        final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);
        final IHyriNickname nickname = session.getNickname();

        if (nickname.getName() != null) {
            HyriAPI.get().getPlayerManager().getNicknameManager().removeUsedNickname(nickname.getName());
        }

        nickname.setName(nick);
        nickname.setSkinOwner(skinOwner);
        nickname.setSkin(skin);
        nickname.setRank(rankType);

        this.applyNickname(player, nickname.getName(), skin);

        nickname.update(playerId);
        session.update();

        HyriAPI.get().getPlayerManager().getNicknameManager().addUsedNickname(nick, playerId);

        player.sendMessage(BasicsMessage.NICKNAME_ADD_NICK_MESSAGE.asString(player).replace("%nickname%", nick));
    }

    public void processNickname(Player player) {
        this.processNickname(player, this.loader.getRandomNickname(), null, this.loader.getRandomSkin(), PlayerRank.PLAYER, false);
    }

    public void applyNickname(Player player, String nickname, String textureData, String textureSignature) {
        ThreadUtil.backOnMainThread(HyriBasics.get(), () -> {
            final GameProfile profile = PlayerUtil.setName(player, nickname);

            if (textureData != null && textureSignature != null) {
                profile.getProperties().clear();
                profile.getProperties().put("textures", new Property("textures", textureData, textureSignature));
            }

            PlayerUtil.reloadSkin(HyriBasics.get(), player);

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.hidePlayer(player);
                target.showPlayer(player);
            }
        });
    }

    public void applyNickname(Player player, String nickname, @NotNull Skin skin) {
        this.applyNickname(player, nickname, skin.getTextureData(), skin.getTextureSignature());
    }

    public void applyNickname(Player player, String nickname) {
        final GameProfile profile = new ProfileLoader(nickname).loadProfile();
        final Property textures = profile.getProperties().get("textures").iterator().next();

        this.applyNickname(player, nickname, textures.getValue(), textures.getSignature());
    }

    public void resetNickname(Player player) {
        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());
        final IHyriNickname nickname = session.getNickname();

        this.applyNickname(player, IHyriPlayer.get(player.getUniqueId()).getName()); // Set player nickname to its original name

        nickname.setName(null);
        nickname.setSkinOwner(null);
        nickname.setSkin(null);
        nickname.setRank(null);
        nickname.update(player.getUniqueId());
        session.update();

        HyriAPI.get().getPlayerManager().getNicknameManager().removeUsedNickname(player.getName());
    }

    public boolean isNicknameAvailable(String nickname, boolean mojangCheck) {
        return HyriAPI.get().getPlayerManager().getNicknameManager().isNicknameAvailable(nickname) // Check if nickname is used
                && HyriAPI.get().getPlayerManager().getPlayer(nickname) == null // Check if a player exists on Hyriode with the given name
                && (!mojangCheck || new UUIDFetcher().getUUID(nickname, true) == null); // Check Mojang
    }

    public Skin getPlayerSkin(String player) {
        final GameProfile skinProfile = new ProfileLoader(player).loadProfile();
        final Iterator<Property> properties = skinProfile.getProperties().get("textures").iterator();

        if (!properties.hasNext()) {
            return null;
        }

        final Property textures = properties.next();

        return new Skin(textures.getValue(), textures.getSignature());
    }

    public NicknameLoader getLoader() {
        return this.loader;
    }

}
