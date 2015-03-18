package sidben.villagertweaks.handler;

import java.util.Iterator;
import java.util.Random;
import sidben.villagertweaks.ModVillagerTweaks;
import sidben.villagertweaks.client.particle.ParticleHelper;
import sidben.villagertweaks.client.particle.ParticlePotionEffect;
import sidben.villagertweaks.client.particle.ParticlePotionEffect.EffectType;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.init.MyAchievements;
import sidben.villagertweaks.network.ZombieVillagerProfessionMessage;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class PlayerEventHandler
{

    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {
        
        // TEMP CODE - TODO: remove
        // check if the player right-clicked a villager
        if (event.target instanceof EntityIronGolem) {
            
            // Only on client
            if (event.target.worldObj.isRemote) {
                final EntityIronGolem golem = (EntityIronGolem) event.target;
                World worldIn = event.target.worldObj;
                BlockPos pos = event.target.getPosition();
                final Random rand = golem.worldObj.rand;
                
                //worldIn.spawnParticle(EnumParticleTypes.NOTE, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.2D, (double)pos.getZ() + 0.5D, (double)10 / 24.0D, 0.0D, 0.0D, new int[0]);
                //golem.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, golem.posX + (rand.nextDouble() - 0.5D) * golem.width, golem.posY + golem.height + 0.5D, golem.posZ + (rand.nextDouble() - 0.5D) * golem.width, 0D, 0.1D, 0D, new int[0]);
                
                ParticlePotionEffect.EffectType[] pool = new ParticlePotionEffect.EffectType[12];
                pool[0] = EffectType.FIRE_RESISTANCE;
                pool[1] = EffectType.HEALTH_BOOST;
                pool[2] = EffectType.JUMP_BOOST;
                pool[3] = EffectType.REGENERATION;
                pool[4] = EffectType.RESISTANCE;
                pool[5] = EffectType.SPEED;
                pool[6] = EffectType.STRENGTH;
                pool[7] = EffectType.PROJECTILE_PROTECTION;
                pool[8] = EffectType.FIRE_PROTECTION;
                pool[9] = EffectType.BLAST_PROTECTION;
                pool[10] = EffectType.THORNS;
                pool[11] = EffectType.KNOCKBACK;
                
                EffectType effect = pool[rand.nextInt(12)];
                LogHelper.info(effect);
                
                ParticleHelper.spawnParticle(effect, golem.posX, golem.posY + golem.height, golem.posZ);
            }
            
        }        
        
        
        
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
            EntityZombie zombie = (EntityZombie)event.target;

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

 
    }
    
    
    
    
    @SubscribeEvent
    public void onPlayerMultiBlockPlace(MultiPlaceEvent event) 
    {

        int pumpkinAmount = 0;
        int ironAmount = 0;
        int snowAmount = 0;
        
        String customName = "";
        //Map pumpkinEnchants = null;
        
        
        // Check the replaced blocks to see if it contains golem materials
        for(Iterator<BlockSnapshot> i = event.getReplacedBlockSnapshots().iterator(); i.hasNext(); ) {
            BlockSnapshot b = i.next();
            
            if (b.getReplacedBlock().getBlock() == Blocks.pumpkin) pumpkinAmount += 1; 
            else if (b.getReplacedBlock().getBlock() == Blocks.iron_block) ironAmount += 1;
            else if (b.getReplacedBlock().getBlock() == Blocks.snow) snowAmount += 1;
        }
        
        if (ConfigurationHandler.onDebug) {
            LogHelper.info("onPlayerMultiBlockPlace()");
            LogHelper.info("    Found [" +pumpkinAmount+ "] pumpkins, [" +snowAmount+ "] snow and [" +ironAmount+ "] iron"); 
        }

        

        // Gets the custom info from the pumpkin
        if (pumpkinAmount == 1) {
            
            // Name
            ItemStack item = event.itemInHand;
            if (item.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && item.hasDisplayName()) customName = item.getDisplayName();
            
            /*
            // Enchantments
            pumpkinEnchants = EnchantmentHelper.getEnchantments(item);
            */

        }
        
        
        //----------------------------------------------------
        // Snowman pattern found
        //----------------------------------------------------
        if (pumpkinAmount == 1 && snowAmount == 2) {
            if (ConfigurationHandler.onDebug) LogHelper.info("    This player wanted to build a snowman at " + event.pos.toString());
            
            // Seek for a golem at that region
            EventTracker tracked = ServerInfoTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {
                
                // Check if the entity is alive and a snowman
                Entity target = event.world.getEntityByID(tracked.getEntityID());
                if (target instanceof EntitySnowman) {
                   
                    if (ConfigurationHandler.onDebug) LogHelper.info("    Snowman found, applying extra info");
                    
                    // Check if a custom name exists
                    if (customName != "") {
                        // Applies the custom name to the golem
                        target.setCustomNameTag(customName);
                        ((EntityLiving)target).enablePersistence();
                    }

                }
                
            }
            
        }


        //----------------------------------------------------
        // Iron Golem pattern found
        //----------------------------------------------------
        else if (pumpkinAmount == 1 && ironAmount == 4) {
            if (ConfigurationHandler.onDebug) {
                LogHelper.info("    This player built an iron golem at " + event.pos.toString());
            }

            // Seek for a golem at that region
            EventTracker tracked = ServerInfoTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {
                
                // Check if the entity is alive and a snowman
                Entity target = event.world.getEntityByID(tracked.getEntityID());
                if (target instanceof EntityIronGolem) {
                    
                    if (ConfigurationHandler.onDebug) LogHelper.info("    Iron Golem found, applying extra info");
                    
                    // Check if a custom name exists
                    if (customName != "") {
                        // Applies the custom name to the golem
                        target.setCustomNameTag(customName);
                    }
                    
                    /*
                    // Check if it's an enchanted pumpkin
                    if (pumpkinEnchants != null && pumpkinEnchants.size() > 0) {
                        
                        Iterator i = pumpkinEnchants.keySet().iterator();
                        while (i.hasNext())
                        {
                            int j = ((Integer)i.next()).intValue();
                            Enchantment enchantment = Enchantment.getEnchantmentById(j);
                            int level = ((Integer)pumpkinEnchants.get(Integer.valueOf(j))).intValue();
                            
                            if (enchantment == Enchantment.unbreaking) { 
                                LogHelper.info("    Adding resistance");
                                ((EntityLiving)target).addPotionEffect(new PotionEffect(Potion.resistance.id, 720000, 0));   // gives resistance boost for 600 minutes
                            }

                            else if (enchantment == Enchantment.sharpness) { 
                                LogHelper.info("    Adding strength");
                                ((EntityLiving)target).addPotionEffect(new PotionEffect(Potion.damageBoost.id, 720000, 0));   // gives resistance boost for 600 minutes
                            }

                            else if (enchantment == Enchantment.efficiency) { 
                                LogHelper.info("    Adding speed");
                                ((EntityLiving)target).addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 720000, 0));   // gives resistance boost for 600 minutes
                            }

                            else if (enchantment == Enchantment.infinity) { 
                                LogHelper.info("    Adding health");
                                ((EntityLiving)target).addPotionEffect(new PotionEffect(Potion.healthBoost.id, 720000, 14));   // gives resistance boost for 600 minutes - adds 60 health
                                ((EntityLiving)target).heal(160);
                            }
                            
                        }

                        LogHelper.info("    - Health: " + ((EntityLiving)target).getHealth());
                        LogHelper.info("    - Max Health: " + ((EntityLiving)target).getMaxHealth());
                        
                    }
                   
                    */

                }
                
            }
        
        }

        
    }
    
    
    
    
    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
    
        /*
         * Check if the player started tracking a zombie villager
         * (happens on server-side). 
         */
        if (event.target instanceof EntityZombie && !event.entity.worldObj.isRemote) {
            final EntityZombie zombie = (EntityZombie) event.target;

            if (zombie.isVillager()) {
   
                // Check if the zombie has special properties
                ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
                
                if (properties.getProfession() >= 0) {
                    // Sends a message to the player, with the zombie extra info
                    ModVillagerTweaks.NetworkWrapper.sendTo(
                            new ZombieVillagerProfessionMessage(zombie.getEntityId(), properties.getProfession()), 
                            (EntityPlayerMP) event.entityPlayer);
                }
    
            }
        }
    
    }

    


}
