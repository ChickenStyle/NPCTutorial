package me.chickenstyle.tutorial;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) throws Exception {
                arg.add(packet);
                readPacket(player, packet);
            }

        });
    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if (channel == null || channel.pipeline() == null) return;


        if (channel.pipeline().get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector");
    }

    public void readPacket(Player player, Packet<?> packet) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            String action = getValue(packet,"action").toString().toUpperCase();
            int id = (int) getValue(packet, "a");
            for (NPCMob mob : NPCHandler.getInstance().getNPCS()) {
                if (mob.getNPC().getId() == id) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(69420D);
                        player.saveData();
                        mob.performInteract(player, action,(EnumHand) getValue(packet,"d"));
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4D);},0);

                }
            }

        }

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
