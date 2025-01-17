package me.noname.mcskulls;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class SkullPlaceholder extends PlaceholderExpansion {
    private final SkullApi connection;
    public SkullPlaceholder(SkullApi connection){
        this.connection = connection;
    }
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("skull")) {
            return connection.tab(player); 
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return "mcskulls";
    }

    @Override
    public String getAuthor() {
        return "noname";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }


}
