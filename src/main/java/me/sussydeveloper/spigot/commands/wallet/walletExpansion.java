package me.sussydeveloper.commands.wallet;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sussydeveloper.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class walletExpansion extends PlaceholderExpansion {

    Main plugin;

    public walletExpansion(Main plugin){
        this.plugin = plugin;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "EggCore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VerifiedFemboy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player != null){
            if(params.equalsIgnoreCase("wallet")){
                WalletData walletData = WalletData.Cache.getWalletDataCache().get(player.getUniqueId().toString());
                return walletData.getWalletFormatted() + " zl";
            }
        }
        return super.onPlaceholderRequest(player, params);
    }
}
