package sidben.villagertweaks.handler;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sidben.villagertweaks.helper.LogHelper;
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

        // Check for a pumpkin + enchanted book combo
        if (event.left.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && event.left.stackSize == 1 && event.right.getItem() == Items.enchanted_book
                && Items.enchanted_book.getEnchantments(event.right).tagCount() > 0) {


            final Map pumpkinEnchants = EnchantmentHelper.getEnchantments(event.left);
            final Map bookEnchants = EnchantmentHelper.getEnchantments(event.right);
            final ItemStack result = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin), 1);
            int multipleEnchantPenalty;

            boolean isHarder = false;               // Unbreaking III -> Resistance I
            boolean isBetter = false;               // Infinity I -> Health Boost V
            boolean isFaster = false;               // Efficiency IV -> Speed I
            boolean isStronger = false;             // Sharpness IV -> Strength I



            // check if the book has valid enchantments
            final Iterator i = bookEnchants.keySet().iterator();

            while (i.hasNext()) {
                final int j = ((Integer) i.next()).intValue();
                final Enchantment enchantment = Enchantment.getEnchantmentById(j);
                final int level = ((Integer) bookEnchants.get(Integer.valueOf(j))).intValue();

                if (enchantment == Enchantment.unbreaking && level >= 3) {
                    isHarder = true;
                }
                if (enchantment == Enchantment.sharpness && level >= 4) {
                    isStronger = true;
                }
                if (enchantment == Enchantment.efficiency && level >= 4) {
                    isFaster = true;
                }
                if (enchantment == Enchantment.infinity && level >= 1) {
                    isBetter = true;
                }

                LogHelper.info("    Book contains: " + enchantment.getName() + ", level " + level);
            }


            // calculates the cost
            event.cost = 0;
            multipleEnchantPenalty = 0;

            if (isHarder) {
                event.cost += 6;
                multipleEnchantPenalty += 1;
            }
            if (isBetter) {
                event.cost += 8;
                multipleEnchantPenalty += 1;
            }
            if (isFaster) {
                event.cost += 5;
                multipleEnchantPenalty += 1;
            }
            if (isStronger) {
                event.cost += 6;
                multipleEnchantPenalty += 1;
            }

            if (event.cost > 0 && multipleEnchantPenalty > 0) {
                event.cost += (multipleEnchantPenalty * 3) + 1;       // Adds a maximum of 13
            }

            if (event.name != "") {
                event.cost += 1;
            }

            // if (event.left.stackSize > 1) event.cost = event.cost * event.left.stackSize;


            // Apply the enchantments
            if (isHarder) {
                pumpkinEnchants.put(Enchantment.unbreaking.effectId, 1);
            }
            if (isBetter) {
                pumpkinEnchants.put(Enchantment.infinity.effectId, 1);
            }
            if (isFaster) {
                pumpkinEnchants.put(Enchantment.efficiency.effectId, 1);
            }
            if (isStronger) {
                pumpkinEnchants.put(Enchantment.sharpness.effectId, 1);
            }

            EnchantmentHelper.setEnchantments(pumpkinEnchants, result);
            
            
            // NBT tag to remove the "Enchantments" from tooltip
            result.setTagInfo("HideFlags", new NBTTagInt(1));


            if (pumpkinEnchants.size() > 0) {
                event.output = result;
            } else {
                event.output = null;    // vanilla behavior will execute 
            }

        }


    }



    /**
     * Fired when the player removes a "repaired" item from the Anvil's Output slot.
     * 
     */
    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event)
    {
        Item item1 = event.left.getItem();
        Item item2 = event.right.getItem();
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
    }


    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (!event.world.isRemote) {
            ServerInfoTracker.startTracking();
        }
    }

}
