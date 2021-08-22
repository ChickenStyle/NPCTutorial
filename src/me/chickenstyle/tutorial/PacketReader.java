package me.chickenstyle.tutorial;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.*;

public class PacketReader {

    Channel channel;
    private static Map<UUID, Channel> channels = new HashMap<>();

    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channels.put(player.getUniqueId(), channel);

        if (channel.pipeline().get("PacketInjector") != null)
            return;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) {
                arg.add(packet);
                int id = (int) getValue(packet,"a"); // NPC's id

                for (NPCMob mob : NPCHandler.getInstance().getNPCS()) {
                    if (mob.getNPC().getId() == id) { // checking if NPCHandler has the interacted npc
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            EnumHand hand = packet.c(); // may be null if the action is ATTACK
                            String action = packet.b().toString(); // Player's interaction type on the npc
                            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(69420D); //Ha ha funny number attack speed
                            player.saveData();
                            mob.performInteract(player, action,hand);
                            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4D);},0); //Setting attack speed back to normal
                            break;
                    }
                }

            }

        });
    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if (channel == null || channel.pipeline() == null) return;


        if (channel.pipeline().get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector");
    }

    public Listener getListener() {
        return new Listener() {

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                inject(e.getPlayer());
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                uninject(e.getPlayer());
            }

        };
    }

    private Object getValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);

            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
