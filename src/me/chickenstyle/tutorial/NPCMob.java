package me.chickenstyle.tutorial;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

public abstract class NPCMob{

    private final String[] skin;
    private final EntityPlayer npc;
    private final EntityLiving entity;
    private double lastX,lastY,lastZ;

    public NPCMob(String npcName, String playerName, EntityLiving entity) {
        this.skin = getSkin(playerName);
        this.entity = entity;
        this.npc = createNPC(npcName);
        Location loc = entity.getBukkitEntity().getLocation();
        setLastLocation(loc.getX(),loc.getY(),loc.getZ());
    }

    public NPCMob(String npcName, String texture,String signature, EntityLiving entity) {
        this.skin = new String[]{texture,signature};
        this.entity = entity;
        this.npc = createNPC(npcName);
        Location loc = entity.getBukkitEntity().getLocation();
        setLastLocation(loc.getX(),loc.getY(),loc.getZ());
    }

    public abstract void performInteract(Player player, String interactType, EnumHand hand);

    private EntityPlayer createNPC(String name) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = entity.getWorld().getWorld().getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);

        PlayerInteractManager manager = new PlayerInteractManager(world);

        try {
            Field gField = PlayerInteractManager.class.getDeclaredField("gamemode");
            gField.setAccessible(true);
            gField.set(manager,EnumGamemode.SURVIVAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        EntityPlayer npc = new EntityPlayer(server,world,profile,manager);

        double x = entity.locX();
        double y = entity.locY();
        double z = entity.locZ();

        float yaw = entity.yaw;

        float pitch = entity.pitch;

        npc.setLocation(x,y,z,yaw,pitch);


        profile.getProperties().put("textures",new Property("textures",skin[0],skin[1]));

        return npc;
    }

    private String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture,signature};
        } catch (Exception e) {
            return new String[] {"",""};
        }
    }

    public String[] getSkin() {
        return skin;
    }

    public EntityPlayer getNPC() {
        return npc;
    }

    public EntityLiving getEntity() {
        return entity;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public void setLastLocation(double lastX, double lastY, double lastZ) {
        this.lastX = lastX;
        this.lastY = lastY;
        this.lastZ = lastZ;
    }
}
