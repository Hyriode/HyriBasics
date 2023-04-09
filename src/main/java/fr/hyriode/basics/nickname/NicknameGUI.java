package fr.hyriode.basics.nickname;

import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.util.Skin;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.basics.util.GUIDesign;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.signgui.SignGUI;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.ThreadUtil;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/04/2022 at 08:03
 */
public class NicknameGUI extends HyriInventory {

    private static final List<PlayerRank> RANKS = Arrays.asList(PlayerRank.PLAYER, PlayerRank.VIP, PlayerRank.VIP_PLUS, PlayerRank.EPIC);

    private String currentNickname;
    private boolean randomNickname;
    private Skin currentSkin;
    private String currentSkinOwner;
    private PlayerRank currentRank;

    private int rankIndex;

    private final NicknameModule nicknameModule;

    public NicknameGUI(Player owner, NicknameModule nicknameModule, String currentNickname, String currentSkinOwner, Skin currentSkin, PlayerRank currentRank) {
        super(owner, name(owner, "gui.nickname.title"), 6 * 9);
        this.nicknameModule = nicknameModule;
        this.currentNickname = currentNickname;
        this.randomNickname = true;
        this.currentSkinOwner = currentSkinOwner;
        this.currentSkin = currentSkin;
        this.currentRank = currentRank;
        this.rankIndex = RANKS.indexOf(this.currentRank);

        // Design pattern
        this.applyDesign(GUIDesign.DOUBLE_LINES);

        // Validate item
        this.setItem(49, new ItemBuilder(Material.STAINED_GLASS, 1, 5).withName(BasicsMessage.NICKNAME_GUI_APPLY_NAME.asString(this.owner)).build(), event -> {
            ThreadUtil.ASYNC_EXECUTOR.execute(() -> {
                if (this.currentSkinOwner != null && this.currentSkin == null) {
                    this.currentSkin = this.nicknameModule.getPlayerSkin(this.currentSkinOwner);
                }

                if (this.currentSkin == null) {
                    this.currentSkin = this.nicknameModule.getLoader().getRandomSkin();
                }

                ThreadUtil.backOnMainThread(HyriBasics.get(), () -> this.nicknameModule.processNickname(this.owner, this.currentNickname, this.currentSkinOwner, this.currentSkin, this.currentRank, !this.randomNickname));

                this.owner.closeInventory();
            });
        });

        this.addItems();
    }

    public NicknameGUI(Player owner, NicknameModule nicknameModule, String currentSkinOwner, Skin currentSkin, PlayerRank currentRank) {
        this(owner, nicknameModule, nicknameModule.getLoader().getRandomNickname(), currentSkinOwner, currentSkin, currentRank);
    }

    private void addItems() {
        this.addNameItems();
        this.addSkinItems();
        this.addRankItems();
    }

    private void addNameItems() {
        final ItemStack nickname = new ItemBuilder(Material.BOOK)
                .withName(BasicsMessage.NICKNAME_GUI_NICK_NAME.asString(this.owner))
                .withLore(ListReplacer.replace(BasicsMessage.NICKNAME_GUI_NICK_LORE.asList(this.owner), "%nickname%", this.currentNickname).list())
                .build();

        this.setItem(20, nickname, event -> this.openNameGUI());

        final ItemStack random = ItemBuilder.asHead(BasicsHead.DICE)
                .withName(BasicsMessage.NICKNAME_GUI_RANDOM_NAME.asString(this.owner))
                .build();

        this.setItem(29, random, event -> {
            this.currentNickname = this.nicknameModule.getLoader().getRandomNickname();
            this.randomNickname = true;
            this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

            this.addNameItems();
        });
    }

    private void openNameGUI() {
        new SignGUI((player, lines) -> {
            final String first = lines[0];

            if (first.length() > 3 && first.length() <= 16 && !first.contains(" ") && first.matches("[a-zA-Z0-9_]*")) {
                this.currentNickname = first;
                this.randomNickname = false;
                this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

                this.addNameItems();
            } else {
                player.sendMessage(HyrameMessage.INVALID_ARGUMENT.asString(this.owner).replace("%arg%", first));
            }

            this.open();
        }).withLines("", "^^^^^^^^", "Nick", "").open(this.owner);
    }

    private void addSkinItems() {
        final ItemStack skin = ItemBuilder.asHead(HyrameHead.SIMPLISTIC_STEVE)
                .withName(BasicsMessage.NICKNAME_GUI_SKIN_NAME.asString(this.owner))
                .withLore(ListReplacer.replace(BasicsMessage.NICKNAME_GUI_SKIN_LORE.asList(this.owner), "%skin%", this.currentSkinOwner != null ? this.currentSkinOwner : "-").list())
                .build();

        this.setItem(22, skin, event -> this.openSkinGUI());

        final ItemStack random = ItemBuilder.asHead(BasicsHead.DICE)
                .withName(BasicsMessage.NICKNAME_GUI_RANDOM_SKIN.asString(this.owner))
                .build();

        this.setItem(31, random, event -> {
            this.currentSkin = this.nicknameModule.getLoader().getRandomSkin();
            this.currentSkinOwner = null;
            this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

            this.addSkinItems();
        });
    }

    private void openSkinGUI() {
        new SignGUI((player, lines) -> {
            final String first = lines[0];

            if (first.length() > 0 && !first.contains(" ")) {
                this.currentSkinOwner = first;
                this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

                this.addSkinItems();
            } else {
                player.sendMessage(ChatColor.RED + HyrameMessage.INVALID_ARGUMENT.asString(this.owner).replace("%arg%", first));
            }

            this.open();
        }).withLines("", "^^^^^^^^", "Skin", "").open(this.owner);
    }

    private void addRankItems() {
        final List<String> lore = BasicsMessage.NICKNAME_GUI_RANK_LORE.asList(this.owner);
        final List<String> ranks = new ArrayList<>();

        for (PlayerRank rankType : RANKS) {
            final String prefix = rankType == PlayerRank.PLAYER ? BasicsMessage.PLAYER_RANK.asString(this.owner): rankType.getDefaultPrefix();

            ranks.add(" " + (rankType == this.currentRank ? ChatColor.WHITE + "▶ " + prefix : prefix));
        }

        lore.addAll(lore.indexOf("%ranks%"), ranks);
        lore.remove("%ranks%");

        final ItemStack item = new ItemBuilder(Material.NAME_TAG)
                .withName(BasicsMessage.NICKNAME_GUI_RANK_NAME.asString(this.owner))
                .withLore(lore)
                .build();

        this.setItem(24, item, event -> {
            if (this.rankIndex == RANKS.size() - 1) {
                this.rankIndex = 0;
            } else {
                this.rankIndex++;
            }

            this.currentRank = RANKS.get(this.rankIndex);
            this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

            this.addRankItems();
        });

        final ItemStack random = ItemBuilder.asHead(BasicsHead.DICE)
                .withName(BasicsMessage.NICKNAME_GUI_RANDOM_RANK.asString(this.owner))
                .build();

        this.setItem(33, random, event -> {
            PlayerRank rank = this.generateRandomRank();

            while (rank == this.currentRank) {
                rank = this.generateRandomRank();
            }

            this.currentRank = rank;

            this.rankIndex = RANKS.indexOf(this.currentRank);
            this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

            this.addRankItems();
        });
    }

    private PlayerRank generateRandomRank() {
        return RANKS.get(ThreadLocalRandom.current().nextInt(RANKS.size()));
    }

}
