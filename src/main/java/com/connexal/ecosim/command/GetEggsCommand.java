package com.connexal.ecosim.command;

import com.connexal.ecosim.mobs.SimMobType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class GetEggsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
            return true;
        }

        Player player = (Player) sender;

        for (SimMobType type : SimMobType.values()) {
            Material egg = Material.valueOf(type.getEntityType().name().toUpperCase(Locale.ROOT) + "_SPAWN_EGG");
            player.getWorld().dropItem(player.getLocation(), new ItemStack(egg));
        }

        return true;
    }
}
