package me.noname.mcskulls.events;

import me.noname.mcskulls.SkullApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class SkullJoinEvent implements Listener {

    private final SkullApi skull;

    public SkullJoinEvent(SkullApi skull){
        this.skull = skull;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        if (!skull.playerExists(player)) {
            skull.addPlayer(event.getPlayer());
            Bukkit.getLogger().info("dodano do bazy danych gracza o nicku  " + event.getPlayer().getDisplayName());
            skull.updateTabList(player);
        }
        skull.updateTabList(player);
    }
}


