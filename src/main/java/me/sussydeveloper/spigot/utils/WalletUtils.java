package me.sussydeveloper.utils;

import me.sussydeveloper.commands.wallet.WalletData;

public class WalletUtils {

    public static String getTargetUUID(String target) {
        return WalletData.Cache.findUUIDByPlayerName(target);
    }

    public static Float getFloatValue(String value){
        try{
            return Float.parseFloat(value);
        } catch (NumberFormatException e){
            return 0.0F;
        }
    }

    public static WalletData getWalletDataByUUID(String uuid){
        return WalletData.Cache.getWalletDataCache().get(uuid);
    }
}
