package fr.hyriode.basics.nickname;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.util.Skin;
import fr.hyriode.basics.HyriBasics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 16/12/2022 at 18:34
 */
public class NicknameLoader {

    private final File nicknamesFile = new File(HyriBasics.get().getDataFolder(), "nicknames.txt");
    private final File skinsFile = new File(HyriBasics.get().getDataFolder(), "skins.txt");

    private final List<String> nicknames = new ArrayList<>();
    private final List<Skin> skins = new ArrayList<>();

    public void load() {
        this.loadNicknames();
        this.loadSkins();
    }

    private void loadNicknames() {
        HyriBasics.log("Loading random nicknames...");

        if (!this.nicknamesFile.exists()) {
            try {
                this.nicknamesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(this.nicknamesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.nicknames.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HyriBasics.log("Loaded random nicknames (" + this.nicknames.size() + ").");
    }

    private void loadSkins() {
        HyriBasics.log("Loading random skins...");

        if (!this.skinsFile.exists()) {
            try {
                this.skinsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(this.skinsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] splitted = line.split(";");

                if (splitted.length == 2) {
                    this.skins.add(new Skin(splitted[0], splitted[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HyriBasics.log("Loaded random skins (" + this.skins.size() + ").");
    }

    public List<String> getNicknames() {
        return this.nicknames;
    }

    public List<Skin> getSkins() {
        return this.skins;
    }

    public String getRandomNickname() {
        String nickname = null;
        while (nickname == null) {
            nickname = this.nicknames.get(ThreadLocalRandom.current().nextInt(this.nicknames.size()));

            if (!HyriAPI.get().getPlayerManager().getNicknameManager().isNicknameAvailable(nickname)) {
                nickname = null;
            }
        }
        return nickname;
    }

    public Skin getRandomSkin() {
        return this.skins.get(ThreadLocalRandom.current().nextInt(this.skins.size()));
    }

}
