package sidben.villagertweaks.handler;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.helper.MagicHelper;
import sidben.villagertweaks.helper.ResultCanEnchant;
import sidben.villagertweaks.init.MyAchievements;
import sidben.villagertweaks.tracker.ServerInfoTracker;


public class WorldEventHandler
{


    /**
     * Fired when a player places items in both the left and right slots of a anvil.
     * 
     */
    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event)
    {
        
        // Check if the items can be combined to create an enchanted pumpkin
        ResultCanEnchant result = MagicHelper.canEnchant(event.left, event.right);
        
        if (result != null && result.isValid) 
        {
            event.output = result.item;
            event.cost = result.cost;
        } 
        else 
        {
            // vanilla behavior will execute
            event.output = null;     
        }

    }



    /**
     * Fired when the player removes a "repaired" item from the Anvil's Output slot.
     * 
     */
    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event)
    {
        Item item1 = event.left == null ? null : event.left.getItem();
        Item item2 = event.right == null ? null : event.right.getItem();
        Item pumpkin = Item.getItemFromBlock(Blocks.pumpkin);
        
        // Check if the player combined a pumpkin and an enchanted book
        if (event.output.getItem() == pumpkin &&
                (item1 == pumpkin || item1 == Items.enchanted_book) &&
                (item2 == pumpkin || item2 == Items.enchanted_book)) {
            
            // Gives the achievement
            event.entityPlayer.triggerAchievement(MyAchievements.EnchantPumpkin);
            
        }

    }


    
    /**
     * This event is fired in {@link ItemStack#getTooltip(EntityPlayer, boolean)}, which in turn is called from it's respective GUIContainer.
     * 
     */
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        /*
        if (event.itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            LogHelper.info("onItemTooltip()");
            LogHelper.info("    tooltip " + event.toolTip.size());
            LogHelper.info("    tooltip " + event.toolTip);
        }
        */
        // TODO: change rarity of golden pumpkin (can be done?)
    }


    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (!event.world.isRemote) {
            ServerInfoTracker.startTracking();
        }
    }

}
