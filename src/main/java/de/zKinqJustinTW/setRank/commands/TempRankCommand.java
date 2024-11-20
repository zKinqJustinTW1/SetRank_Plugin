package de.zKinqJustinTW.setRank.commands;

import de.zKinqJustinTW.setRank.main;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class TempRankCommand implements CommandExecutor {
    private final main plugin;

    public TempRankCommand(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("setrankplugin.temprank")) {
            sender.sendMessage(ChatColor.RED + "Keine Rechte.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /temprank <player> <rank> <duration>");
            return true;
        }

        String playerName = args[0];
        String rankName = args[1];
        String durationString = args[2];

        Player target = Bukkit.getPlayer(playerName);
        UUID uuid = target != null ? target.getUniqueId() : null;

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden.");
            return true;
        }

        Duration duration;
        try {
            duration = parseDuration(durationString);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Ungültiges Format. Verwenden Sie 's' für Sekunden, 'm' für Minuten, 'h' für Stunden oder 'd' für Tage..");
            return true;
        }

        plugin.getLuckPerms().getUserManager().modifyUser(uuid, user -> {
            // Add the temporary group
            user.data().add(InheritanceNode.builder(rankName).expiry(duration).build());
        });

        sender.sendMessage(ChatColor.GREEN + "Temporary rank " + rankName + " has been set for " + playerName + " for " + durationString + ".");

        if (target != null) {
            String kickMessage = plugin.getKickMessage(true, rankName, durationString);
            Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(kickMessage));
        }

        return true;
    }

    private Duration parseDuration(String durationString) {
        int length = Integer.parseInt(durationString.substring(0, durationString.length() - 1));
        char unit = durationString.charAt(durationString.length() - 1);

        switch (unit) {
            case 's':
                return Duration.ofSeconds(length);
            case 'm':
                return Duration.ofMinutes(length);
            case 'h':
                return Duration.ofHours(length);
            case 'd':
                return Duration.ofDays(length);
            default:
                throw new IllegalArgumentException("Invalid duration unit");
        }
    }
}