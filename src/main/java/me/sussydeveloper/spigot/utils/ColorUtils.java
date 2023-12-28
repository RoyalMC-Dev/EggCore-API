package me.sussydeveloper.utils;

public class ColorUtils {

    public static String getRainbow(String message) {
        String[] colors = {"&4", "&c", "&6", "&e", "&a", "&b", "&d", "&5"};
        StringBuilder sb = new StringBuilder();
        int colorIndex = 0;
        for (int index = 0; index < message.length(); index++) {
            sb.append(colors[colorIndex]).append(message.charAt(index));
            colorIndex = (colorIndex + 1) % colors.length;
        }
        return ChatUtils.filter(sb.toString());
    }

}
