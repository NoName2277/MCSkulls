package me.noname.mcskulls.events;

import me.noname.mcskulls.Main;
import me.noname.mcskulls.SkullApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.SQLException;

public class SkullKillEntityByEntityEvent implements Listener {

    private final SkullApi skull;
    private final Economy eco;
    private final Main plugin;

    public SkullKillEntityByEntityEvent(SkullApi skull, Economy eco, Main plugin){
        this.skull = skull;
        this.eco = eco;
        this.plugin = plugin;
    }

    @EventHandler
    public void on(EntityDeathEvent event) throws SQLException {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getEntity().getKiller() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        Player killer = (Player) event.getEntity().getKiller();
        if(skull.hasSkull(victim)){
            skull.removeSkull(victim);
            eco.depositPlayer(killer, plugin.getConfig().getDouble("reward-value"));
            Bukkit.broadcastMessage("§aGracz §l" + killer.getDisplayName() + " §azabił agresywnego gracza nagroda §a§l" + plugin.getConfig().getDouble("reward-value") + " §a$");
        }else{
            skull.giveSkull(killer);
            killer.sendTitle("☠", "", 10, 10, 10);
            eco.withdrawPlayer(killer, plugin.getConfig().getDouble("punish-value"));
            Bukkit.broadcastMessage("§aGracz §l" + killer.getDisplayName() + " §azabił pokojowego gracza kara §a§l" + plugin.getConfig().getDouble("reward-value") + " §a$");
        }
    }
}
