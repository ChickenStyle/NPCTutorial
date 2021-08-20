package me.chickenstyle.tutorial;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class NPCHandler {

    private static NPCHandler instance = null;

    private final List<NPCMob> npcs;

    private final BukkitTask npcMoveRunnable;

    private NPCHandler() {
        this.npcs = new LinkedList<>();
        this.npcMoveRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                List<NPCMob> mobsToRemove = new ArrayList<>();
                for (NPCMob mob:npcs) {
                    if (mob.getEntity().isAlive()) {

                        update(mob);

                        double mobX = mob.getEntity().locX();
                        double mobY = mob.getEntity().locY();
                        double mobZ = mob.getEntity().locZ();

                        double lastX = mob.getLastX();
                        double lastY = mob.getLastY();
                        double lastZ = mob.getLastZ();

                            double getX = mobX - lastX;
                            double getY = mobY - lastY;
                            double getZ = mobZ - lastZ;

                            move(mob.getNPC(),getX,getY,getZ,mob.getEntity().yaw, mob.getEntity().pitch);

                            mob.setLastLocation(mobX,mobY,mobZ);

                    } else {

                        mob.getNPC().damageEntity(DamageSource.GENERIC,mob.getNPC().getMaxHealth());
                        removeNPCPacket(mob.getNPC());
                        mobsToRemove.add(mob);
                    }
                }
                for (NPCMob mob:mobsToRemove) {npcs.remove(mob);}

            }
        }.runTaskTimer(Main.getInstance(),0,2);
    }

    private void update(NPCMob mob) {

        for (Player player:mob.getEntity().getWorld().getWorld().getPlayers()) {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(mob.getNPC(),(byte)(mob.getEntity().yaw *256/360)));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(mob.getNPC().getId(),(byte)(mob.getEntity().yaw*256/360),(byte)(mob.getEntity().pitch*256/360),true));

        }
    }

    private void move(EntityPlayer npc,double x,double y,double z,float yaw,float pitch) {
        for (Player player:npc.getWorld().getWorld().getPlayers()) {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(npc.getId(),(short)(x*4096),(short)(y*4096),(short)(z*4096),(byte)(yaw*256/360),(byte)(pitch*256/360),true));
        }
    }

    private void removeNPCPacket(EntityPlayer npc) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
    }

    public void addNPCMob(NPCMob mob) {

        mob.getEntity().world.addEntity(mob.getEntity(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        addNPCPacket(mob);
        npcs.add(mob);
    }

    public void showNPCSOnJoin(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (NPCMob mob:npcs) {
                    showNPCPacket(player,mob);
                }
            }
        }.runTaskLater(Main.getInstance(),1);
    }

    private void showNPCPacket(Player player,NPCMob mob) {
        EntityPlayer npc = mob.getNPC();
        EntityLiving entity = mob.getEntity();

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte)(mob.getEntity().yaw * 256/360)));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));

        for (EnumItemSlot slot:EnumItemSlot.values()) {
            if (npc.getEquipment(slot).getItem() != Item.getById(0)) {
                connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),slot, npc.getEquipment(slot)));
            }
        }


        new BukkitRunnable(){

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
            }
        }
        }.runTaskLater(Main.getInstance(),1);
    }

    private void addNPCPacket(NPCMob mob) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showNPCPacket(player,mob);
        }
    }


    public List<NPCMob> getNPCS() {
        return npcs;
    }

    public static NPCHandler getInstance() {
        instance = instance == null ? new NPCHandler() : instance;
        return instance;
    }

}
