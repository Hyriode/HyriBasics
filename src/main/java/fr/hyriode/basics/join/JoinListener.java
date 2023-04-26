package fr.hyriode.basics.join;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leaderboard.IHyriLeaderboard;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriTransactionContent;
import fr.hyriode.api.player.model.modules.IHyriTransactionsModule;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 25/04/2023 at 21:19
 */
public class JoinListener extends HyriListener<HyriBasics> {

    public JoinListener(HyriBasics plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        HyriAPI.get().getScheduler().runAsync(() -> {
            final Player player = event.getPlayer();
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());

            // Leaderboard fix
            final IHyriLeaderboard leaderboard = NetworkLeveling.LEADERBOARD.get();
            final double experience = account.getNetworkLeveling().getExperience();

            if (experience != leaderboard.getScore(HyriLeaderboardScope.TOTAL, playerId)) {
                leaderboard.setScore(playerId, experience);
            }

            // Give temporarily reward
            final IHyriTransactionsModule transactions = account.getTransactions();

            if (!transactions.has("temporarily", "timed-out-reward")) {
                final int hyodes = this.randomHyodes();

                transactions.add("temporarily", "timed-out-reward", new TemporarilyTransaction(hyodes));
                account.update();

                player.sendMessage(HyriLanguageMessage.get("message.timed-out-reward").getValue(account).replace("%hyodes%", String.valueOf(hyodes)));
            }
        });
    }

    private int randomHyodes() {
        return 50 * ThreadLocalRandom.current().nextInt(0, 10);
    }

    public static class TemporarilyTransaction implements IHyriTransactionContent {

        private int hyodes;

        public TemporarilyTransaction(int hyodes) {
            this.hyodes = hyodes;
        }

        @Override
        public void save(MongoDocument document) {
            document.append("hyodes", this.hyodes);
        }

        @Override
        public void load(MongoDocument document) {
            this.hyodes = document.getInteger("hyodes");
        }

    }

}
