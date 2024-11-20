package de.zKinqJustinTW.setRank;

import de.zKinqJustinTW.setRank.commands.SetRankCommand;
import de.zKinqJustinTW.setRank.commands.TempRankCommand;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
    private LuckPerms luckPerms;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        config = getConfig();

        // Check if LuckPerms is installed
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            getLogger().info(ChatColor.GREEN + "Luckperms gefunden!");
        } else {
            getLogger().severe(ChatColor.RED + "LuckPerms nicht gefunden! Deaktiviere Plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        this.getCommand("setrank").setExecutor(new SetRankCommand(this));
        this.getCommand("temprank").setExecutor(new TempRankCommand(this));

        getLogger().info("SetRankPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SetRankPlugin has been disabled!");
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public String getKickMessage(boolean isTemporary, String rank, String duration) {
        String message = isTemporary ?
                config.getString("messages.kick.temporary", "You have been given a temporary rank. Please rejoin.") :
                config.getString("messages.kick.permanent", "You have been given a new rank. Please rejoin.");

        return message.replace("{rank}", rank).replace("{duration}", duration);
    }
}