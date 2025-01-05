package me.noname.mcskulls.events;

import me.noname.mcskulls.SkullApi;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Random;

public class DropEvent implements Listener {

    private final Random random = new Random();

    private final SkullApi skull;

    public DropEvent(SkullApi skull) {
        this.skull = skull;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack[] itemStacks = player.getInventory().getContents();
        int itemsToRemove = itemStacks.length / 2;

        for (int i = 0; i < itemStacks.length && itemsToRemove > 0; i++) {
            ItemStack itemStack = itemStacks[i];
            if (itemStack != null) {
                int amountToRemove = Math.min(itemStack.getAmount(), itemsToRemove);
                ItemStack itemToRemove = itemStack.clone();
                itemToRemove.setAmount(amountToRemove);
                player.getInventory().removeItem(itemToRemove);
                itemsToRemove -= amountToRemove;
            }
        }
    }
}
