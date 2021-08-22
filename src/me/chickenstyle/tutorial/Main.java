package me.chickenstyle.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private static PacketReader packetReader;

    @Override
    public void onEnable() {
        instance = this;
        packetReader = new PacketReader();
        getCommand("test").setExecutor(new TestCommand());
        getServer().getPluginManager().registerEvents(NPCHandler.getListener(),this);
        getServer().getPluginManager().registerEvents(packetReader.getListener(), this);

        validatePlayers();
    }

    @Override
    public void onDisable() {
        invalidatePlayers();
        NPCHandler.getInstance().removeAllNPCS();
    }

    private void validatePlayers() {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                packetReader.inject(p);
            }
        }
    }

    private void invalidatePlayers() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            packetReader.uninject(p);
        }
    }

    public static Main getInstance() {
        return instance;
    }

}
