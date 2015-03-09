package sidben.villagertweaks.handler;

import java.util.Iterator;
import java.util.Map;
import sidben.villagertweaks.ModVillagerTweaks;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.network.ZombieVillagerProfessionMessage;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.SpecialEventsTracker;
import sidben.villagertweaks.tracker.SpecialEventsTracker.EventType;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PlayerEventHandler
{

    @SideOnly(Side.CLIENT)
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

                        // Cancel the regular event (trade GUI)
                        event.setCanceled(true);
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
        Map pumpkinEnchants = null;
        
        
        // Check the replaced blocks to see if it contains golem materials
        for(Iterator<BlockSnapshot> i = event.getReplacedBlockSnapshots().iterator(); i.hasNext(); ) {
            BlockSnapshot b = i.next();
            
            if (b.getReplacedBlock().getBlock() == Blocks.pumpkin) pumpkinAmount += 1; 
            else if (b.getReplacedBlock().getBlock() == Blocks.iron_block) ironAmount += 1;
            else if (b.getReplacedBlock().getBlock() == Blocks.snow) snowAmount += 1;
        }
        
        // LogHelper.info("    Found [" +pumpkinAmount+ "] pumpkins, [" +snowAmount+ "] snow and [" +ironAmount+ "] iron");
        

        // Gets the custom info from the pumpkin
        if (pumpkinAmount == 1) {
            
            // Name
            ItemStack item = event.itemInHand;
            if (item.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && item.hasDisplayName()) customName = item.getDisplayName();
            
            // Enchantments
            pumpkinEnchants = EnchantmentHelper.getEnchantments(item);

        }
        
        
        //
        // Snowman pattern found
        // 
        if (pumpkinAmount == 1 && snowAmount == 2) {
            LogHelper.info("    This player wanted to build a snowman at " + event.pos.toString());
            
            // Seek for a golem at that region
            EventTracker tracked = SpecialEventsTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {
                
                // Check if the entity is alive and a snowman
                Entity target = event.world.getEntityByID(tracked.getEntityID());
                LogHelper.info("    Looking for an entity with ID [" +tracked.getEntityID()+ "], found " + target);
                if (target instanceof EntitySnowman) {
                    
                    LogHelper.info("    It's a snowman, applying special effects");
                    
                    // Check if a custom name exists
                    if (customName != "") {
                    
                        // Applies the custom name to the golem
                        LogHelper.info("    Naming snowman [" +tracked.getEntityID()+ "] to [" +customName+ "]");
                        target.setCustomNameTag(customName);
                        
                    }

                }
                
            }
            
        }

        //
        // Iron Golem pattern found
        // 
        else if (pumpkinAmount == 1 && ironAmount == 4) {
            LogHelper.info("    This player built an iron golem at " + event.pos.toString());

            // Seek for a golem at that region
            EventTracker tracked = SpecialEventsTracker.seek(EventType.GOLEM, event.pos);
            if (tracked != null) {
                
                // Check if the entity is alive and a snowman
                Entity target = event.world.getEntityByID(tracked.getEntityID());
                LogHelper.info("    Looking for an entity with ID [" +tracked.getEntityID()+ "], found " + target);
                if (target instanceof EntityIronGolem) {
                    
                    LogHelper.info("    It's an iron golem, applying special effects");
                    
                    // Check if a custom name exists
                    if (customName != "") {
                    
                        // Applies the custom name to the golem
                        LogHelper.info("    Naming iron golem [" +tracked.getEntityID()+ "] to [" +customName+ "]");
                        target.setCustomNameTag(customName);
                        
                    }
                    
                   
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
                    

                }
                
            }
        
        }

        
    }
    
    
    
    @SubscribeEvent
    public void onPlayerBlockPlace(PlaceEvent event) 
    {
    }
    
    
    
    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        LogHelper.info("== onPlayerStartTracking ==");
        LogHelper.info("    " + event.entityPlayer);
        LogHelper.info("    " + event.target);

    
        /*
         * Check if the player started tracking a zombie villager
         * (happens on server-side). 
         */
        if (event.target instanceof EntityZombie && !event.entity.worldObj.isRemote) {
            final EntityZombie zombie = (EntityZombie) event.target;
    
            if (zombie.isVillager()) {
                LogHelper.info(" - Tracked a zombie villager -");
   
                // Check if the zombie has special properties
                ExtendedVillagerZombie properties = ExtendedVillagerZombie.get(zombie);
                LogHelper.info("    ID: [" + zombie.getEntityId() + "]");
                LogHelper.info("    PROFESSION: [" + properties.getProfession() + "]");
                
                if (properties.getProfession() >= 0) {
                    LogHelper.info("    --> notifying client");

                    // Sends a message to the player, with the zombie extra info
                    ModVillagerTweaks.NetworkWrapper.sendTo(
                            new ZombieVillagerProfessionMessage(zombie.getEntityId(), properties.getProfession()), 
                            (EntityPlayerMP) event.entityPlayer);
                }
    
            }
        }
    
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        LogHelper.info("== onPlayerLoggedIn ==");
    }

}
