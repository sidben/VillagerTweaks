package sidben.villagertweaks.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.helper.MagicHelper;
import sidben.villagertweaks.init.MyAchievements;
import sidben.villagertweaks.network.NetworkHelper;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker.EventType;



public class PlayerEventHandler
{

    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {

        // check if the player right-clicked a villager
        if (event.target instanceof EntityVillager) {

            if (ConfigurationHandler.canNameVillagers) {

                // Check if the player is holding a Name Tag
                final ItemStack item = event.entityPlayer.inventory.getCurrentItem();
                if (item != null && item.getItem() == Items.name_tag) {

                    // Check if the tag have a name and the player is sneaking
                    if (item.hasDisplayName() && event.entityPlayer.isSneaking()) {

                        // Set the name to that villager
                        final EntityLiving villager = (EntityLiving) event.target;
                        villager.setCustomNameTag(item.getDisplayName());

                        // Consumes the item if not in Creative
                        if (!event.entityPlayer.capabilities.isCreativeMode) {
                            --item.stackSize;
                        }

                        // Gives the achievement
                        event.entityPlayer.triggerAchievement(MyAchievements.NameVillager);

                        // Cancel the regular event (trade GUI)
                        event.setCanceled(true);
                    }

                }

            }

        }


        // check if the player right-clicked a zombie
        else if (event.target instanceof EntityZombie) {
            final EntityZombie zombie = (EntityZombie) event.target;

            if (!zombie.worldObj.isRemote) {

                // Check if the player is holding a regular Golden Apple
                final ItemStack item = event.entityPlayer.inventory.getCurrentItem();
                if (item != null && item.getItem() == Items.golden_apple && item.getMetadata() == 0) {

                    // Check if the target is a zombie villager with weakness potion active
                    // Also check if the zombie isn't converting, I only want to track the
                    // player that started the conversion.
                    if (zombie.isVillager() && zombie.isPotionActive(Potion.weakness) && !zombie.isConverting()) {

                        // Sends info to the special track list
                        ServerInfoTracker.startedCuringZombie(event.entityPlayer.getEntityId(), zombie.getEntityId());

                    }

                }

            }

        }


        // check if the player right-clicked a Iron Golem
        else if (event.target instanceof EntityIronGolem) {

            if (event.target.worldObj.isRemote) {

                // Check if the player has an empty hand
                if (event.entityPlayer.inventory.getCurrentItem() == null) {
                    final EntityIronGolem golem = (EntityIronGolem) event.target;
                    final ExtendedGolem properties = ExtendedGolem.get(golem);

                    LogHelper.info("Clicked on a golem with empty hand");
                    
                    // Gets the golem custom enchantments
                    if (properties != null) {
                        final GolemEnchantment e = properties.getRandomEnchantment();
                        if (e != null) {
                            LogHelper.info("Spawning particle for " + e);
                            e.spawnParticles(golem);
                        }
                    }

                }

            }

        }



    }



    @SubscribeEvent
    public void onPlayerMultiBlockPlace(MultiPlaceEvent event)
    {

        int pumpkinAmount = 0;
        int ironAmount = 0;
        int snowAmount = 0;
        ItemStack pumpkin = null;


        // Check the replaced blocks to see if it contains golem materials
        for (BlockSnapshot b : event.getReplacedBlockSnapshots()) {
            if (b.getReplacedBlock().getBlock() == Blocks.pumpkin) {
                pumpkinAmount += 1;
            } else if (b.getReplacedBlock().getBlock() == Blocks.iron_block) {
                ironAmount += 1;
            } else if (b.getReplacedBlock().getBlock() == Blocks.snow) {
                snowAmount += 1;
            }
        }

        if (ConfigurationHandler.onDebug) {
            LogHelper.info("onPlayerMultiBlockPlace()");
            LogHelper.info("    Found [" + pumpkinAmount + "] pumpkins, [" + snowAmount + "] snow and [" + ironAmount + "] iron");
        }



        // Gets the custom info from the pumpkin
        if (pumpkinAmount == 1) {

            final ItemStack item = event.itemInHand;
            if (item.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
                pumpkin = item;
            }

        }


        // ----------------------------------------------------
        // Snowman pattern found
        // ----------------------------------------------------
        if (pumpkinAmount == 1 && snowAmount == 2) {
            if (ConfigurationHandler.onDebug) {
                LogHelper.info("    This player wanted to build a snowman at " + event.pos.toString());
            }

            // Seek for a golem at that region
            final EventTracker tracked = ServerInfoTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {

                // Check if the entity is alive and a snowman
                final Entity target = event.world.getEntityByID(tracked.getEntityID());
                if (target instanceof EntitySnowman) {

                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("    Snowman found, applying extra info");
                    }
                    MagicHelper.applyPumpkinExtraInfo((EntitySnowman) target, pumpkin);

                }

            }

        }


        // ----------------------------------------------------
        // Iron Golem pattern found
        // ----------------------------------------------------
        else if (pumpkinAmount == 1 && ironAmount == 4) {
            if (ConfigurationHandler.onDebug) {
                LogHelper.info("    This player built an iron golem at " + event.pos.toString());
            }

            // Seek for a golem at that region
            final EventTracker tracked = ServerInfoTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {

                // Check if the entity is alive and a iron golem
                final Entity target = event.world.getEntityByID(tracked.getEntityID());
                if (target instanceof EntityIronGolem) {
                    EntityIronGolem auxGolem = (EntityIronGolem) target;

                    if (ConfigurationHandler.onDebug) {
                        LogHelper.info("    Iron Golem found, applying extra info");
                    }
                    MagicHelper.applyPumpkinExtraInfo(auxGolem, pumpkin);
                    MagicHelper.applyPassiveEffects(auxGolem);
                    MagicHelper.applyRefreshEffects(auxGolem);
                    
                    // Heals the golem, if needed (so enchantments that raise health actually work)
                    if (auxGolem.getHealth() < auxGolem.getMaxHealth()) {
                        auxGolem.heal(auxGolem.getMaxHealth());
                    }
                    
                }

            }

        }


    }



    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event)
    {

        /*
         * Check if the player started tracking a zombie villager (happens on server-side).
         */
        if (event.target instanceof EntityZombie && !event.entity.worldObj.isRemote) {
            final EntityZombie zombie = (EntityZombie) event.target;

            if (zombie.isVillager()) {

                // Check if the zombie has special properties
                final ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
                if (properties != null) {
                    NetworkHelper.sendVillagerProfessionMessage(zombie.getEntityId(), properties, event.entityPlayer);
                }

            }
        }

        /*
         * Check if the player started tracking a golem (happens on server-side).
         */
        else if (event.target instanceof EntityIronGolem && !event.entity.worldObj.isRemote) {
            final EntityIronGolem golem = (EntityIronGolem) event.target;

            if (golem.isPlayerCreated()) {

                // Check if the golem has special properties
                final ExtendedGolem properties = ExtendedGolem.get(golem);
                if (properties != null) {
                    NetworkHelper.sendEnchantedGolemInfoMessage(golem.getEntityId(), properties, event.entityPlayer);
                }

            }
        } else if (event.target instanceof EntitySnowman && !event.entity.worldObj.isRemote) {
            final EntitySnowman golem = (EntitySnowman) event.target;

            // Check if the golem has special properties
            final ExtendedGolem properties = ExtendedGolem.get(golem);
            if (properties != null) {
                NetworkHelper.sendEnchantedGolemInfoMessage(golem.getEntityId(), properties, event.entityPlayer);
            }

        }


    }



}
