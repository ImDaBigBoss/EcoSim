package com.connexal.ecosim.command;

import com.connexal.ecosim.mobs.SimMob;
import com.connexal.ecosim.mobs.SimMobs;
import com.connexal.ecosim.utils.MobSex;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class MobInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
            return true;
        }

        Player player = (Player) sender;

        Entity entity = player.getTargetEntity(10);
        if (entity == null) {
            player.sendMessage(ChatColor.RED + "You must be looking at a mob to use this command");
            return true;
        }

        SimMob mob = SimMobs.spawnedMobs.get(entity.getUniqueId());
        if (mob == null) {
            player.sendMessage(ChatColor.RED + "That is not a simulation mob");
            return true;
        }

        player.sendMessage(ChatColor.BOLD + "Mob Info:" + ChatColor.RESET + "\n" +
                ChatColor.WHITE + "  Type: " + ChatColor.GREEN + mob.getType().name() + "\n" +
                ChatColor.WHITE + "  Gender: " + ChatColor.GREEN + (mob.getSex() == MobSex.FEMALE ? "Female" : "Male") + "\n" +
                ChatColor.WHITE + "  Is gestating: " + ChatColor.GREEN + (mob.isGestating() ? "YES" : "NO") + "\n" +
                ChatColor.WHITE + "  Is blocked: " + ChatColor.GREEN + (mob.isBlocked() ? "YES" : "NO") + "\n" +
                ChatColor.WHITE + "  Age: " + ChatColor.GREEN + mob.getAge() + "\n" +
                ChatColor.WHITE + "  Can mate: " + ChatColor.GREEN + (mob.canMate() ? "YES" : "NO") + "\n" +
                ChatColor.WHITE + "  Hunger: " + ChatColor.GREEN + mob.getHunger() + "\n" +
                ChatColor.WHITE + "  Thirst: " + ChatColor.GREEN + mob.getThirst() + "\n");

        return true;
    }
}