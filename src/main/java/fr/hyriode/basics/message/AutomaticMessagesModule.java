package fr.hyriode.basics.message;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.basics.HyriBasics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by AstFaster
 * on 28/04/2023 at 18:36
 */
public class AutomaticMessagesModule {

    private static final int TIME_BETWEEN_MESSAGES = 5 * 60 * 20; // 5 minutes

    private final List<String> messages = Arrays.asList("cheater", "store", "helpers", "recruitment", "partner");

    private int currentIndex = 0;

    public AutomaticMessagesModule() {
        Collections.shuffle(this.messages);

        Bukkit.getScheduler().runTaskTimer(HyriBasics.get(), () -> {
            if (this.currentIndex >= this.messages.size()) {
                this.currentIndex = 0;
            }

            final HyriLanguageMessage message = HyriLanguageMessage.get("automatic-message." + this.messages.get(this.currentIndex));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message.getValue(player));
            }

            this.currentIndex++;
        }, TIME_BETWEEN_MESSAGES, TIME_BETWEEN_MESSAGES);
    }

}
