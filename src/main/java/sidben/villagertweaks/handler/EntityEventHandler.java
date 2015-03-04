package sidben.villagertweaks.handler;

import java.util.Random;
import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


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
            LogHelper.info("Is zombie? [" +(event.entity instanceof EntityZombie)+ "]");
            LogHelper.info("Emerald config enabled? [" +ConfigurationHandler.zombieVillagerDropEmerald+ "]");
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
                LogHelper.info("is zombie child? [" +zombie.isChild()+ "]");
                LogHelper.info("is zombie villager? [" +zombie.isVillager()+ "]");
                LogHelper.info("Looting Level [" +event.lootingLevel+ "]");
            }
            

            // ----------------------------------------------------
            // Bonus Feather
            // ----------------------------------------------------

            // Will this zombie drop a feather?
            if (ConfigurationHandler.zombieDropFeather) {

                // Only regular adult zombies should drop feathers 
                if (!zombie.isChild() && !zombie.isVillager()) {
                    
                    final int chanceFeather = rand.nextInt(100) + 1;
                    if (ConfigurationHandler.onDebug) LogHelper.info("Regular zombie - Chance do drop feather = [" +chanceFeather+ " < " +targetFeatherChance+ "]");
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
                    if (ConfigurationHandler.onDebug) LogHelper.info("Villager zombie - Have ingot? [" +haveIngot+ "]");
    
    
                    // Never drops emeralds when dropping iron ingot
                    if (!haveIngot) {
                        // Will this zombie drop an emerald?
                        final int chanceEmerald = rand.nextInt(100) + 1;
                        if (ConfigurationHandler.onDebug) LogHelper.info("Villager zombie - Chance do drop emerald = [" +chanceEmerald+ " < " +targetEmeraldChance+ "]");
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

}
