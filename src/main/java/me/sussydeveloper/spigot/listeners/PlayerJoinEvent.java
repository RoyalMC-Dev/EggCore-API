package me.sussydeveloper.listeners;

import me.sussydeveloper.Main;
import me.sussydeveloper.commands.wallet.WalletData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {

    ConfigurationSection joinSection;

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event){

        joinSection = Main.getPlugin().getConfig().getConfigurationSection("join");
        Player p = event.getPlayer();

        WalletData data = new WalletData(p.getName(), p.getUniqueId().toString());
        data.createUser();

        for (String group : joinSection.getKeys(false)) {
            ConfigurationSection groupSection = joinSection.getConfigurationSection(group);
            String permission = groupSection.getString("permission");
            String message = groupSection.getString("message");

            if (p.hasPermission(permission)) {
                String finalMessage = message.replace("%player%", p.getDisplayName());
                event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', finalMessage));
                return;
            }
        }
        event.setJoinMessage(null);
    }
}
