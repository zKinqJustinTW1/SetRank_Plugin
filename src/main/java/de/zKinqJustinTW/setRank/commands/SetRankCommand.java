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

import java.util.UUID;

public class SetRankCommand implements CommandExecutor {
    private final main plugin;

    public SetRankCommand(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("setrankplugin.setrank")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /setrank <player> <rank>");
            return true;
        }

        String playerName = args[0];
        String rankName = args[1];

        Player target = Bukkit.getPlayer(playerName);
        UUID uuid = target != null ? target.getUniqueId() : null;

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        plugin.getLuckPerms().getUserManager().modifyUser(uuid, user -> {
            // Remove all inherited groups
            user.data().clear(node -> node.getKey().startsWith("group."));

            // Add the new group
            user.data().add(InheritanceNode.builder(rankName).build());
        });

        sender.sendMessage(ChatColor.GREEN + "Rank " + rankName + " has been set for " + playerName + ".");

        if (target != null) {
            String kickMessage = plugin.getKickMessage(false, rankName, "");
            Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(kickMessage));
        }

        return true;
    }
}