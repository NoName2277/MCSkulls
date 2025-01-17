package me.noname.mcskulls.events;

import me.noname.mcskulls.Main;
import me.noname.mcskulls.SkullApi;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DropEvent implements Listener {


    private final SkullApi skullApi;
    private final Main plugin;

    public DropEvent(SkullApi skullApi, Main plugin) {
        this.skullApi = skullApi;
        this.plugin = plugin;
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        Location location = player.getBedSpawnLocation();
        if (location == null) {
            location = player.getLocation();
        }

        World world = location.getWorld();
        if (world == null) return;


        if (skullApi.hasSkull(player) || plugin.getConfig().getBoolean("dropFullItems", false)) {
            for (ItemStack item : contents) {
                if (item != null) {
                    world.dropItemNaturally(location, item);
                }
            }
            inventory.clear();
        } else {
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item != null && item.getAmount() > 1) {
                    int dropAmount = Math.max(1, item.getAmount() / 10);
                    int keepAmount = item.getAmount() - dropAmount;
                    item.setAmount(keepAmount);
                    ItemStack dropItem = item.clone();
                    dropItem.setAmount(dropAmount);
                    world.dropItemNaturally(location, dropItem);
                } else if (item != null && item.getAmount() == 1) {
                    contents[i] = null;
                    world.dropItemNaturally(location, item);
                }
            }
        }

        event.setKeepInventory(true);
        event.getDrops().clear();
    }




}
