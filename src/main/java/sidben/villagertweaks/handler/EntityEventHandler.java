package sidben.villagertweaks.handler;

import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.entity.ai.EntityAIUseBookshelf;
import sidben.villagertweaks.entity.ai.EntityAIUseFurnace;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.ClientInfoTracker;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker.EventType;


public class EntityEventHandler
{

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event)
    {

        // Look for a zombie
        if (event.entity instanceof EntityZombie && !event.entity.worldObj.isRemote) {
            final int targetEmeraldChance = 15 + 5 * event.lootingLevel;
            final int targetFeatherChance = 5;
            boolean haveIngot = false;
            int emeralds = 0;
            ItemStack droppedItem;
            final EntityZombie zombie = (EntityZombie) event.entity;



            // ----------------------------------------------------
            // Bonus Feather
            // ----------------------------------------------------

            // Will this zombie drop a feather?
            if (ConfigurationHandler.zombieDropFeather) {

                // Only regular adult zombies should drop feathers
                if (!zombie.isChild() && !zombie.isVillager()) {

                    final int chanceFeather = zombie.worldObj.rand.nextInt(100) + 1;
                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("A regular zombie died - chance do drop feather [" + chanceFeather + " <= " + targetFeatherChance + "]");
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

                    // Gets the current dropped items to see if the zombie will drop iron ingot
                    for (int i = 0; i < event.drops.size(); i++) {
                        droppedItem = event.drops.get(i).getEntityItem();
                        if (droppedItem != null) {
                            if (droppedItem.getItem() == Items.iron_ingot) {
                                haveIngot = true;
                            }
                        }
                    }
                    if (ConfigurationHandler.onDebug && haveIngot) {
                        LogHelper.info("A zombie villager died and is dropping an iron ingot, canceling emerald check");
                    }


                    // Never drops emeralds when dropping iron ingot
                    if (!haveIngot) {
                        // Will this zombie drop an emerald?
                        final int chanceEmerald = zombie.worldObj.rand.nextInt(100) + 1;
                        if (ConfigurationHandler.onDebug) {
                            LogHelper.info("A zombie villager died - chance do drop emerald [" + chanceEmerald + " <= " + targetEmeraldChance + "]");
                        }

                        if (chanceEmerald <= targetEmeraldChance) {
                            // drop 1 emerald, yay!
                            emeralds = 1;
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

                if (ConfigurationHandler.onDebug) {
                    LogHelper.info("A zombie just killed the villager [" + villager.getEntityId() + "] at [" + villager.getPosition() + "], profession [" + villager.getProfession() + "]");
                    LogHelper.info("Will he come back as a zombie? Find out on the next episode of Dragon Cube Z!");
                    LogHelper.info(" - Entity ToD " + MinecraftServer.getServer().getTickCounter());
                }

            }
        }


    }



    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {

        if (event.entity instanceof EntityZombie) {
            final EntityZombie zombie = (EntityZombie) event.entity;

            if (ConfigurationHandler.onDebug) {
                LogHelper.info("A zombie joined world: [" + zombie.toString() + "] - ");
                LogHelper.info("    ID: [" + zombie.getEntityId() + "], Is villager: [" + zombie.isVillager() + "]");
            }


            if (event.world.isRemote) {
                // Looks for info sent by the server that should be applied to the zombie (e.g. villager profession)
                // NOTE: On the client I don't check [isVillager] because the client don't have that info (yet?)
                ClientInfoTracker.SyncZombieMessage(zombie);

            } else {
                if (zombie.isVillager()) {

                    // Looks on the event tracker for a villager that just died
                    final EventTracker tracked = ServerInfoTracker.seek(EventType.VILLAGER, zombie.getPosition());
                    final ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);

                    if (tracked != null) {
                        if (ConfigurationHandler.onDebug) {
                            LogHelper.info("    Found info on the tracker, must copy to zombie");
                        }

                        // If found, copy the data from the villager
                        tracked.updateZombie(zombie, properties);

                        // Triggers the achievement
                        ServerInfoTracker.triggerVillagerInfectedAchievement(event.world, tracked.getEntityID());

                    } else {
                        if (ConfigurationHandler.onDebug) {
                            LogHelper.info("    No info on the tracker, assigning a random profession");
                        }

                        // If not, assign a random profession
                        properties.assignRandomProfessionIfNeeded();

                    }


                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("    Custom name [" + zombie.getCustomNameTag() + "]");
                        LogHelper.info("    Profession [" + properties.getProfession() + "]");
                    }


                }

            }

        }


        else if (event.entity instanceof EntityVillager && !event.world.isRemote) {
            final EntityVillager villager = (EntityVillager) event.entity;

            if (ConfigurationHandler.onDebug) {
                LogHelper.info("A villager joined world: [" + villager.toString() + "]");
                LogHelper.info("    ID: [" + villager.getEntityId() + "], Profession: [" + villager.getProfession() + "]");
            }


            //---------------------------------------------------------------------
            // Assign correct info to cured villagers
            //---------------------------------------------------------------------
            
            // Looks on the event tracker for a zombie that was cured
            final EventTracker tracked = ServerInfoTracker.seek(EventType.ZOMBIE, villager.getPosition());

            if (tracked != null) {
                if (ConfigurationHandler.onDebug) {
                    LogHelper.info("    Found info on the tracker, must copy to villager");
                }

                // If found, copy the data from the zombie
                tracked.updateVillager(villager);

                // Sends info to the special track list
                ServerInfoTracker.endedCuringZombie(tracked.getEntityID(), villager.getEntityId());

                // Triggers the achievement
                ServerInfoTracker.triggerZombieCuredAchievement(event.world, tracked.getEntityID());

            }

            
            //---------------------------------------------------------------------
            // Custom AI tasks
            //---------------------------------------------------------------------
            int priorityUseRandomBlock = 7;
            
            LogHelper.info("A villager joined world: [" + villager.toString() + "]");
            LogHelper.info("    ID: [" + villager.getEntityId() + "], Profession: [" + villager.getProfession() + "]");
            LogHelper.info("    --Adding custom AI--");
            
            villager.tasks.addTask(priorityUseRandomBlock, new EntityAIUseFurnace(villager, 0.6D));
            //villager.tasks.addTask(priorityUseRandomBlock, new EntityAIUseBookshelf(villager, 0.6D));
            

        }


        else if (event.entity instanceof EntityIronGolem && !event.world.isRemote) {
            final EntityIronGolem golem = (EntityIronGolem) event.entity;

            if (golem.isPlayerCreated()) {
                // Found an iron golem, adds to the tracker so pumpkin info can be applied later
                if (ConfigurationHandler.onDebug) {
                    LogHelper.info("A player created iron golem joined world: [" + golem.toString() + "]");
                }
                ServerInfoTracker.add(golem);
            }

        }


        else if (event.entity instanceof EntitySnowman && !event.world.isRemote) {
            final EntitySnowman golem = (EntitySnowman) event.entity;

            // Found an snow golem, adds to the tracker so pumpkin info can be applied later
            if (ConfigurationHandler.onDebug) {
                LogHelper.info("A snowman joined the world: [" + golem.toString() + "]");
            }
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

                final int nextConversionTime = zombie.conversionTime - zombie.getConversionTimeBoost();
                if (ConfigurationHandler.onDebug && nextConversionTime < 500) {
                    LogHelper.info("Zombie [" + zombie.getEntityId() + "] being cured in: [" + zombie.conversionTime + " -> " + nextConversionTime + "]");
                }

                // NOTE: if [conversionTime] is zero, the zombie already converted and it's too late to track
                if (nextConversionTime <= 0 && zombie.conversionTime > 0) {
                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("Oh baby, this zombie is about to be cured! " + zombie.toString() + " in tick " + MinecraftServer.getServer().getTickCounter());
                    }
                    ServerInfoTracker.add(zombie);
                }
            }

        }

        // Spawns smoke effects on damaged iron golems
        else if (event.entity instanceof EntityIronGolem) {
            final EntityIronGolem golem = (EntityIronGolem) event.entity;
            final Random rand = golem.worldObj.rand;

            if (golem.worldObj.isRemote) {

                float pct = 0;
                if (golem.getHealth() > 0) {
                    pct = golem.getHealth() / golem.getMaxHealth();
                }

                // Spawn smoke if he is highly damaged
                if (pct < 0.3F && rand.nextInt(60) == 0) {
                    final EnumParticleTypes particle = rand.nextInt(10) < 4 ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.CLOUD;

                    for (int i = 0; i < 2; ++i) {
                        golem.worldObj.spawnParticle(particle, golem.posX + (rand.nextDouble() - 0.5D) * golem.width, golem.posY + rand.nextDouble() * golem.height - 0.25D, golem.posZ + (rand.nextDouble() - 0.5D) * golem.width, 0D, 0.1D, 0D, new int[0]);
                    }
                }

            }
        }


    }



    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event)
    {

        // Adds the Extended Properties to zombies
        if (event.entity instanceof EntityZombie && ExtendedVillagerZombie.get((EntityZombie) event.entity) == null) {
            ExtendedVillagerZombie.register((EntityZombie) event.entity);
        }

    }



}
