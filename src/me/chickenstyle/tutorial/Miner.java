package me.chickenstyle.tutorial;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Miner extends NPCMob {

    public Miner(Location loc) {
        super("Miner",
                "ewogICJ0aW1lc3RhbXAiIDogMTYyNDIxNzMxMTgxOSwKICAicHJvZmlsZUlkIiA6ICI0ZTk0ZjQ1Yzc1OWM0MjkzYWYxNmZkZjIxOTIzN2E1ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NDg4NzBjYTc3YWE2OTlkMDE3MDkzYjgyNzc5ZDBhNTk4ZGM0ZjNmNDlkMzUxNTMyYWU0ZDk2MzNkY2MyYTVhIgogICAgfQogIH0KfQ==",
                "kmH2CtdpIXarfWpxQpTSrJim5xtU6STuq2s6TZcTffT4x4s3d/5MPVQc/K/neX9amXmXiFJh7ZM1Lla7vI4UOTbHvRcDmtLwpUflKC9s4K/muFifRE7qX490/lidM0IKPtbCyEYDdUYQtRA9a48qr0tHIHyq+c+nZ7fjJxMnQn8aKxnCu6E5AIJXoPZ0IsL0vKN6RkFhAtyNYSqA97nJ0/D4H1cm/Aa+sZmRKXw06k3elzluKqWKGnDtjZ5aentH1cMmNukVqOAnss+kF//6bmBvwBYQ4dshXZv/HKQPh2+n+gFV/t9Klh1C1seQnUw8XGqx8lgx9vt0nrjkM18Ee2jMmfwQUdLp2b+2LzR9jzIC17VETu1h8jIG9RrPriTKpSYwxtNmoYLcPoidfUuGVxt7L6DDmNxN0aWP7EEpGRKKkmuAYC49VlAU1giN4YefavkR2oiJ8fjOnbtFFYeZli5LZoiLGpqy/Xec1PZAxtUFhoXFaLEK0S5SXHwOy3SCQQKGZvZofrl8DDQVXbPIRPmTUtehFnymcQlqCPf1s/nklafu2Sv4527pyXR9Jmj778H+H9ceIOF/bZMUE1enIj9w7rKLAoytTGmVqL9gbaJ54pYyTKqy4XoOZ5lnr+tL9FKJioCzCfcoJPyoHluxSEgSub2MvEAwgD3w41vAnxI=",
                new MinerZombie(loc));

        getNPC().setSlot(EnumItemSlot.MAINHAND,CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_PICKAXE)));
        getNPC().setSlot(EnumItemSlot.OFFHAND,CraftItemStack.asNMSCopy(new ItemStack(Material.TORCH)));
        getNPC().setSlot(EnumItemSlot.HEAD,CraftItemStack.asNMSCopy(new ItemStack(Material.GOLDEN_HELMET)));
        getNPC().setSlot(EnumItemSlot.FEET,CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BOOTS)));


    }

    @Override
    public void performInteract(Player player, String interactType, EnumHand hand) {
        if (interactType.equalsIgnoreCase("INTERACT")) {
            if (hand == EnumHand.MAIN_HAND) {
                player.sendMessage("Hello Poggers!");
            }
        } else if (interactType.equalsIgnoreCase("ATTACK")) {
            ((CraftPlayer) player).getHandle().attack((getEntity()));

            for (Player online:getEntity().world.getWorld().getPlayers()) {
                PlayerConnection connection = ((CraftPlayer)online).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutAnimation(getNPC(),1));
            }
            Location loc = new Location(getEntity().getWorld().getWorld(),getEntity().locX(),getEntity().locY(),getEntity().locZ());


            getEntity().getWorld().getWorld().playSound(loc, Sound.ENTITY_PLAYER_HURT,1,1);

        }
    }

    private static class MinerZombie extends EntityZombie {
        public MinerZombie(Location loc) {
            super(EntityTypes.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());
            setSilent(true);
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((0.23000000417232513D*2));
            this.getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(69);
            this.getAttributeInstance(GenericAttributes.ARMOR).setValue(0);

            this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        }

        @Override
        protected boolean en() {
            return false;
        }
    }


}
