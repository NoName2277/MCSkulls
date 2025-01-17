package me.noname.mcskulls;

import me.noname.mcskulls.commands.SkullCommand;
import me.noname.mcskulls.events.DropEvent;
import me.noname.mcskulls.events.SkullJoinEvent;
import me.noname.mcskulls.events.SkullKillEntityByEntityEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Main extends JavaPlugin implements Listener {

    private Economy eco;
    private SkullApi skull;

    public void onEnable() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            skull = new SkullApi(getDataFolder().getAbsolutePath() + "/skull.db");

            SkullPlaceholder skullPlaceholder = new SkullPlaceholder(skull);
            skullPlaceholder.register();

            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                eco = rsp.getProvider();
            } else {
                getLogger().severe("Vault economy not found! Make sure you have a compatible economy plugin.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            getServer().getPluginManager().registerEvents(new SkullJoinEvent(skull), this);
            getServer().getPluginManager().registerEvents(new DropEvent(skull, this), this);
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(new SkullKillEntityByEntityEvent(skull, eco, this), this);
            getCommand("czaszki").setExecutor(new SkullCommand(skull));
            getCommand("czaszki").setTabCompleter(new SkullCommand(skull));
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (skull != null) {
            try {
                skull.closeCon();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
