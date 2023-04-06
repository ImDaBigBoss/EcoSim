package com.connexal.ecosim.command;

import com.connexal.ecosim.EcoSim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCentreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
            return true;
        }

        Player player = (Player) sender;

        int side = EcoSim.SIMULATION_WIDTH / 2;
        player.teleport(new Location(player.getWorld(), side + 0.5, EcoSim.HEIGHT_MAP[side][side], side + 0.5));

        return true;
    }
}
