package sidben.villagertweaks.handler;

import java.util.Iterator;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.EventTracker;
import sidben.villagertweaks.tracker.SpecialEventsTracker;
import sidben.villagertweaks.tracker.SpecialEventsTracker.EventType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PlayerEventHandler
{

    // OBS: This method is called whenever a player interacts with something (right-click)
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

                }
                
            }
        
        }

        
    }
    
    
    
    @SubscribeEvent
    public void onPlayerBlockPlace(PlaceEvent event) 
    {
        /*
        LogHelper.info("onPlayerBlockPlace [" + event + "]");
        
        Block actualBlock = event.blockSnapshot.getCurrentBlock().getBlock();
        
        
        LogHelper.info("    actual block = [" + actualBlock + "]");
        */

        /*
        if (actualBlock == Blocks.pumpkin) {
            LogHelper.info("A pumpkin was placed at [" + event.pos.toString() + "]");
            
            ItemStack item = event.itemInHand;
            String customName = "";

            if (item.hasDisplayName()) {
                customName = item.getDisplayName();
            }

            
            //if (!event.world.isRemote) {
            //    SpecialEventsTracker.add(EventType.PUMPKIN, event.pos, customName, null);
            //}
            

        }
         */
    }
    

}
