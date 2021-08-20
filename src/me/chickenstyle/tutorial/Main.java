package me.chickenstyle.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private PacketReader packetReader;

    @Override
    public void onEnable() {
        instance = this;
        packetReader = new PacketReader();
        getCommand("test").setExecutor(new TestCommand());
        getServer().getPluginManager().registerEvents(this,this);

        validatePlayers();
    }

    @Override
    public void onDisable() {
        invalidatePlayers();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        packetReader.inject(e.getPlayer());
        NPCHandler.getInstance().showNPCSOnJoin(e.getPlayer());

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        NPCHandler.getInstance().showNPCSOnJoin(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        packetReader.uninject(e.getPlayer());
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
