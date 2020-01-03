package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SnowGolem extends WalkingMonster {

    public static final int NETWORK_ID = 21;

    public SnowGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setFriendly(true);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(4);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 23 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55) {
            this.attackDelay = 0;

            double f = 1.2;
            double yaw = this.yaw + Utils.rand(-12.0, 12.0);
            double pitch = this.pitch + Utils.rand(-7.0, 7.0);
            Location location = new Location(this.x + (-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5), this.y + this.getEyeHeight(),
                    this.z + (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5), yaw, pitch, this.level);
            Entity k = Entity.createEntity("Snowball", location, this);
            if (k == null) {
                return;
            }

            EntitySnowball snowball = (EntitySnowball) k;
            snowball.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));

            Vector3 motion = new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f).multiply(f);
            snowball.setMotion(motion);

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), snowball, f);
            this.server.getPluginManager().callEvent(ev);

            EntityProjectile projectile = ev.getProjectile();
            if (ev.isCancelled()) {
                if (this.stayTime > 0 || this.distance(this.target) <= ((this.getWidth() + 0.0d) / 2 + 0.05) * nearbyDistanceMultiplier()) projectile.close();
            } else if (projectile != null) {
                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
                this.server.getPluginManager().callEvent(launch);
                if (launch.isCancelled()) {
                    if (this.stayTime > 0 || this.distance(this.target) <= ((this.getWidth() + 0.0d) / 2 + 0.05) * nearbyDistanceMultiplier()) projectile.close();
                } else {
                    projectile.spawnToAll();
                    this.level.addSound(this, Sound.MOB_SNOWGOLEM_SHOOT);
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        for (int i = 0; i < Utils.rand(0, 15); i++) {
            drops.add(Item.get(Item.SNOWBALL, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return "Snow Golem";
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 10;
    }
}
