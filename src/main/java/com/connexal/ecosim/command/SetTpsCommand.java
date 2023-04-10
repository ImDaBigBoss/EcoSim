package com.connexal.ecosim.command;

import com.connexal.ecosim.EcoSim;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetTpsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /settps <tps>");
            return true;
        }

        int tps;
        try {
            tps = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid number.");
            return true;
        }

        if (tps < 1) {
            sender.sendMessage(ChatColor.RED + "Please enter a number greater than 0.");
            return true;
        }

        EcoSim.ticksPerSecond = tps;

        return true;
    }
}
