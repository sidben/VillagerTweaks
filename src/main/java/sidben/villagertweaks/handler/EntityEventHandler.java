package sidben.villagertweaks.handler;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sidben.villagertweaks.ModVillagerTweaks;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.network.ZombieVillagerProfessionMessage;
import sidben.villagertweaks.tracker.ClientInfoTracker;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker.EventType;


public class EntityEventHandler
{

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event)
    {


        // It's a zombie!
        if (event.entity instanceof EntityZombie && !event.entity.worldObj.isRemote) {
            final int targetEmeraldChance = 15 + 5 * event.lootingLevel;
            final int targetFeatherChance = 5;
            boolean haveIngot = false;
            int emeralds = 0;
            ItemStack droppedItem;
            final Random rand = new Random();
            final EntityZombie zombie = (EntityZombie) event.entity;

            if (ConfigurationHandler.onDebug) {
                LogHelper.info("is zombie child? [" + zombie.isChild() + "]");
                LogHelper.info("is zombie villager? [" + zombie.isVillager() + "]");
                LogHelper.info("Looting Level [" + event.lootingLevel + "]");
            }


            // ----------------------------------------------------
            // Bonus Feather
            // ----------------------------------------------------

            // Will this zombie drop a feather?
            if (ConfigurationHandler.zombieDropFeather) {

                // Only regular adult zombies should drop feathers
                if (!zombie.isChild() && !zombie.isVillager()) {

                    final int chanceFeather = rand.nextInt(100) + 1;
                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("Regular zombie - Chance do drop feather = [" + chanceFeather + " < " + targetFeatherChance + "]");
                    }
                    if (chanceFeather <= targetFeatherChance) {
                        // Adds 1 feather to the drop list
                        final EntityItem itemFeather = new EntityItem(zombie.worldObj, zombie.posX, zombie.posY, zombie.posZ);
                        final ItemStack stackFeather = new ItemStack(Items.feather, 1, 0);
                        itemFeather.getDataWatcher().updateObject(10, stackFeather);
                        event.drops.add(itemFeather);
                    }

                }

            }



            // ----------------------------------------------------
            // Bonus Emerald
            // ----------------------------------------------------

            if (ConfigurationHandler.zombieVillagerDropEmerald) {

                // Only adult villager zombies will be affected, and only if killed by players
                // OBS: removed requirement for player kill (event.recentlyHit)
                if (!zombie.isChild() && zombie.isVillager()) {

                    // Gets the current dropped items
                    for (int i = 0; i < event.drops.size(); i++) {
                        droppedItem = event.drops.get(i).getEntityItem();
                        if (droppedItem != null) {
                            if (droppedItem.getItem() == Items.iron_ingot) {
                                haveIngot = true;
                            }
                        }
                    }
                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("Villager zombie - Have ingot? [" + haveIngot + "]");
                    }


                    // Never drops emeralds when dropping iron ingot
                    if (!haveIngot) {
                        // Will this zombie drop an emerald?
                        final int chanceEmerald = rand.nextInt(100) + 1;
                        if (ConfigurationHandler.onDebug) {
                            LogHelper.info("Villager zombie - Chance do drop emerald = [" + chanceEmerald + " < " + targetEmeraldChance + "]");
                        }
                        if (chanceEmerald <= targetEmeraldChance) {
                            emeralds = 1; // drop 1 emerald, yay!
                        }
                    }


                    // Adds the emeralds to the drop list
                    if (emeralds > 0) {
                        final EntityItem item = new EntityItem(zombie.worldObj, zombie.posX, zombie.posY, zombie.posZ);
                        final ItemStack stack = new ItemStack(Items.emerald, emeralds, 0);
                        item.getDataWatcher().updateObject(10, stack);
                        event.drops.add(item);
                    }

                }

            }

        }



    }



    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (event.entity instanceof EntityVillager) {
            if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityZombie) {
                
                // A villager was killed by a zombie and may be zombified. Adds to the tracker for future check.
                final EntityVillager villager = (EntityVillager) event.entity;
                ServerInfoTracker.add(villager);

                LogHelper.info("A zombie just killed this villager: [" + villager.toString() + "] at [" + villager.getPosition() + "], profession [" + villager.getProfession() + "]");
                LogHelper.info("Will he come back as a zombie? Find out on the next episode of Dragon Cube Z!");
                LogHelper.info(" - Entity ToD " + MinecraftServer.getServer().getTickCounter());

            }
        }

    
    }



    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        /*
        if (event.entity instanceof EntityZombie) {
            LogHelper.info("== onEntityJoinWorld ==");
            LogHelper.info("   remote " + event.world.isRemote);
            LogHelper.info("   " + event.entity);
            
            EntityZombie z = (EntityZombie) event.entity;
            LogHelper.info("    IsVillager " + z.isVillager());
            LogHelper.info("    Name " + z.getCustomNameTag());
            LogHelper.info("    ID " + z.getEntityId());

            LogHelper.info("== /onEntityJoinWorld ==");
        }
        */
        

        
        
        
            
        if (event.entity instanceof EntityZombie) {
            final EntityZombie zombie = (EntityZombie) event.entity;

            LogHelper.info("Zombie joined world: [" + zombie.toString() + "]");
            LogHelper.info("    ID: [" + zombie.getEntityId() + "]");
            LogHelper.info("    Villager: [" + zombie.isVillager() + "]");
            
            
            if (event.world.isRemote) {
                LogHelper.info("    CLIENT - Looking for special info");
                ClientInfoTracker.SyncZombieMessage(zombie);
                // NOTE: On the client I don't check [isVillager] because the client don't have that info
                
            }
            else {
                
                LogHelper.info("    Entity ToB " + MinecraftServer.getServer().getTickCounter());

                
                if (zombie.isVillager()) {
                    LogHelper.info("    SERVER - define a profession");

                    ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);

                    
                    // Looks on the event tracker for a villager that just died
                    EventTracker tracked = ServerInfoTracker.seek(EventType.VILLAGER, zombie.getPosition());
                    LogHelper.info("    tracked = " + tracked);
                    
                    
                    if (tracked != null) {
                        // If found, copy the data from the villager
                        tracked.updateZombie(zombie, properties);

                        // Triggers the achievement
                        ServerInfoTracker.triggerVillagerInfectedAchievement(event.world, tracked.getEntityID());

                    } else {
                        // If not, assign a random profession
                        properties.assignRandomProfessionIfNeeded();

                    }
                    
                    
                    LogHelper.info("    ZOMBIE NAME " + zombie.getCustomNameTag());
                    LogHelper.info("    ZOMBIE PROFESSION " + properties.getProfession());

                    
                }
                
            }

        }

        
        else if (event.entity instanceof EntityVillager && !event.world.isRemote) {
            final EntityVillager villager = (EntityVillager) event.entity;

            LogHelper.info("Villager joined world: [" + villager.toString() + "]");

            
            // Looks on the event tracker for a zombie that was cured
            EventTracker tracked = ServerInfoTracker.seek(EventType.ZOMBIE, villager.getPosition());
            LogHelper.info("    tracked = " + tracked);

            if (tracked != null) {
                // If found, copy the data from the zombie
                tracked.updateVillager(villager);
                
                // Sends info to the special track list
                ServerInfoTracker.endedCuringZombie(tracked.getEntityID(), villager.getEntityId());

                // Triggers the achievement
                ServerInfoTracker.triggerZombieCuredAchievement(event.world, tracked.getEntityID());
                
            }

        }

        
        else if (event.entity instanceof EntityIronGolem && !event.world.isRemote) {
            final EntityIronGolem golem = (EntityIronGolem) event.entity;

            if (golem.isPlayerCreated()) {
                LogHelper.info("Player built Iron Golem joined world: [" + golem.toString() + "]");
                ServerInfoTracker.add(golem);
            }

        }

        
        else if (event.entity instanceof EntitySnowman && !event.world.isRemote) {
            final EntitySnowman golem = (EntitySnowman) event.entity;

            LogHelper.info("Snowman joined the world: [" + golem.toString() + "]");
            ServerInfoTracker.add(golem);

        }



    }



    @SubscribeEvent
    public void onLivingUpdateEvent(LivingUpdateEvent event)
    {

        // Check if a zombie is about to convert to villager
        if (event.entity instanceof EntityZombie) {
            final EntityZombie zombie = (EntityZombie) event.entity;

            
            // Based on the [onUpdate] event from zombies
            if (!zombie.worldObj.isRemote && zombie.isConverting()) {
                
                // FOR DEBUG PURPOSES ONLY!!!!
                if (zombie.conversionTime > 1000) zombie.conversionTime = 1000;
                
                final int nextConversionTime = zombie.conversionTime - zombie.getConversionTimeBoost();
                LogHelper.info("Zombie [" + zombie.getEntityId() + "] being cured in: [" + zombie.conversionTime + " -> " + nextConversionTime + "]");
               
                // NOTE: if [conversionTime] is zero, the zombie already converted and it's too late to track
                if (nextConversionTime <= 0 && zombie.conversionTime > 0) {
                    LogHelper.info("Oh baby, this zombie is about to be cured! " + zombie.toString() + " in tick " + MinecraftServer.getServer().getTickCounter());
                    ServerInfoTracker.add(zombie);
                }
            }

        }
        
        else if (event.entity instanceof EntityIronGolem) {
            final EntityIronGolem golem = (EntityIronGolem) event.entity;
            Random rand = golem.worldObj.rand;

            if (golem.worldObj.isRemote) {

                float pct = 0;
                if (golem.getHealth() > 0) pct = golem.getHealth() / golem.getMaxHealth();

                // Spawn smoke if he is damaged
                if (pct < 0.3F) {
                    if (rand.nextInt(60) == 0) {
                        EnumParticleTypes particle = rand.nextInt(10) < 4 ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.CLOUD;
                        
                        for (int i = 0; i < 2; ++i)
                        {
                            golem.worldObj.spawnParticle(particle, golem.posX + (rand.nextDouble() - 0.5D) * (double)golem.width, golem.posY + rand.nextDouble() * (double)golem.height - 0.25D, golem.posZ + (rand.nextDouble() - 0.5D) * (double)golem.width, 0D, 0.1D, 0D, new int[0]);
                        }
                    }
                }
                
            }
        }


    }


    


    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {

        // Adds the Extended Properties to zombies
        if (event.entity instanceof EntityZombie && ExtendedVillagerZombie.get((EntityZombie)event.entity) == null) {
            // LogHelper.info(" -- Adding extended properties to Zombie [" + event.entity.getEntityId() + "]");
            ExtendedVillagerZombie.register((EntityZombie) event.entity);
        }
        
    }
    


}
