package sidben.villagertweaks.handler;

import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.helper.LogHelper;


public class EntityEventHandler
{

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event)
    {

        // Break the method if the world is a client
        if (event.entity.worldObj.isRemote) {
            return;
        }


        if (ConfigurationHandler.onDebug) {
            LogHelper.info("Is zombie? [" + (event.entity instanceof EntityZombie) + "]");
            LogHelper.info("Emerald config enabled? [" + ConfigurationHandler.zombieVillagerDropEmerald + "]");
        }


        // It's a zombie!
        if (event.entity instanceof EntityZombie) {
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
        /*
         * // NOTE: This event only detects when a villager is killed by a zombie.
         * if (event.entity instanceof EntityZombie || event.entity instanceof EntityVillager) {
         * LogHelper.info("Entity death, type [" +event.entity.toString()+ "], dmg source [" +event.source.toString()+
         * "] type [" +event.source.damageType+ "]");
         * }
         */

        if (event.entity instanceof EntityVillager) {
            if (event.source instanceof EntityDamageSource) {
                if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityZombie) {

                    final EntityVillager villager = (EntityVillager) event.entity;
                    LogHelper.info("A zombie just killed this villager: [" + villager.toString() + "]");
                    LogHelper.info("Will he come back as a zombie? Find out on the next episode of Dragon Cube Z!");

                }
            }
        }
        // LogHelper.info("Entity death, type [" +event.entity.toString()+ "], dmg source [" +event.source.toString()+
        // "] type [" +event.source.damageType+ "]");
    }



    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {

        // Check if a zombie villager spawned (from villager) or if a villager spawned (from cured zombie)

        if (event.entity instanceof EntityZombie) {
            final EntityZombie zombie = (EntityZombie) event.entity;

            if (zombie.isVillager()) {
                LogHelper.info("Zombie Villager joined world: [" + event.entity.toString() + "]");
            }

        }

        else if (event.entity instanceof EntityVillager) {
            final EntityVillager villager = (EntityVillager) event.entity;

            if (!villager.isChild()) {
                LogHelper.info("Adult villager joined world: [" + event.entity.toString() + "]");
            }

        }

    }



    @SubscribeEvent
    public void onLivingUpdateEvent(LivingUpdateEvent event)
    {

        /*
         * if (event.entity instanceof EntityZombie) {
         * final EntityZombie zombie = (EntityZombie) event.entity;
         * LogHelper.info("zombie.conversionTime [" + zombie.conversionTime + "]");
         * LogHelper.info("zombie.conversionTime [" + zombie.getConversionTimeBoost() + "]");
         * }
         */


        // Check if a zombie is about to convert to villager
        if (event.entity instanceof EntityZombie) {

            final EntityZombie zombie = (EntityZombie) event.entity;

            // Based on the [onUpdate] event from zombies
            if (!zombie.worldObj.isRemote && zombie.isConverting()) {
                final int nextConversionTime = zombie.conversionTime - zombie.getConversionTimeBoost();
                if (nextConversionTime <= 0) {
                    LogHelper.info("Oh baby, this zombie is about to be cured! " + zombie.toString() + "");
                } else {
                    LogHelper.info("Zombie being cured in: [" + zombie.conversionTime + " -> " + nextConversionTime + "]");
                }
            }

        }


    }



}
