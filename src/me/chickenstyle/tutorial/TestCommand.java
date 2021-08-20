package me.chickenstyle.tutorial;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String s, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        NPCHandler.getInstance().addNPCMob(new Miner(player.getLocation()));


        return false;
    }
}
