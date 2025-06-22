package me.noname.mcskulls.commands;

import me.noname.mcskulls.Main;
import me.noname.mcskulls.SkullApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkullCommand implements CommandExecutor, TabCompleter {

    private final SkullApi skull;
    public SkullCommand(SkullApi skull){
        this.skull = skull;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && sender.hasPermission("skuls.admin")) {
            String name = args[1];
            Player target = Bukkit.getPlayerExact(name);
            if (target != null) {
                if (args[0].equalsIgnoreCase("daj")) {
                    if (!skull.hasSkull(target.getPlayer())) {
                        try {
                            skull.giveSkull(target.getPlayer());
                            target.sendTitle("☠", "", 10, 10, 10);
                            sender.sendMessage("§aPomyślnie dodano czaszkę graczowi §a§l" + target.getDisplayName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        sender.sendMessage("§cGracz o nicku §l" + target.getDisplayName() + " §cma już czaszkę");
                    }
                } else if (args[0].equalsIgnoreCase("usun")) {
                    if (skull.hasSkull(target.getPlayer())) {
                        try {
                            skull.removeSkull(target.getPlayer());
                            sender.sendMessage("§aPomyślnie usunięto czaszkę graczowi §a§l" + target.getDisplayName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        sender.sendMessage("§cGracz o nicku §l" + target.getDisplayName() + " §cnie ma czaszki!");
                    }
                }
            } else {
                sender.sendMessage("§cNie odnaleziono gracza o takim nicku");
            }
        } else if (args.length == 0) {
            if (skull.hasSkull((OfflinePlayer) sender)) {
                sender.sendMessage("§aMasz czaszkę!");
            } else {
                sender.sendMessage("§aNie masz aktualnie czaszki!");
            }
            return false;
        } else if (args.length == 1) {
            String playerName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (target != null) {
                if (skull.hasSkull(target)) {
                    sender.sendMessage("§aGracz " + target.getName() + " ma czaszkę!");
                } else {
                    sender.sendMessage("§aGracz " + target.getName() + " nie ma czaszki!");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Nie ma gracza o nicku " + playerName + "!");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            if (sender.hasPermission("skuls.admin") || sender.isOp()) {
                list.add("daj");
                list.add("usun");
                return list;
            }
            return null;
        }
        return null;
    }
}