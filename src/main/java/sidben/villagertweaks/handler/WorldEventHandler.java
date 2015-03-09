package sidben.villagertweaks.handler;

import java.util.Iterator;
import java.util.Map;
import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import sidben.villagertweaks.tracker.ServerInfoTracker.EventType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class WorldEventHandler
{
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event)
    {

        LogHelper.info("onAnvilUpdate!");
        /*
        LogHelper.info("    " + event.getPhase());
        LogHelper.info("    [" + event.left.getItem() +  "] + [" + event.right.getItem()  + "], cost [" + event.cost + "], material cost [" + event.materialCost +  "]");
        */
        
        
        // Check for a pumpkin + enchanted book combo
        if (event.left.getItem() == Item.getItemFromBlock(Blocks.pumpkin)
                && event.left.stackSize == 1
                && event.right.getItem() == Items.enchanted_book
                && Items.enchanted_book.getEnchantments(event.right).tagCount() > 0) {

            
            Map pumpkinEnchants = EnchantmentHelper.getEnchantments(event.left);
            Map bookEnchants = EnchantmentHelper.getEnchantments(event.right);
            ItemStack result = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin), 1);
            int multipleEnchantPenalty;
            
            boolean isHarder = false;               // Unbreaking III   -> Resistance I
            boolean isBetter = false;               // Infinity I       -> Health Boost V
            boolean isFaster = false;               // Efficiency IV    -> Speed I
            boolean isStronger = false;             // Sharpness IV     -> Strength I


            
            // check if the book has valid enchantments
            Iterator i = bookEnchants.keySet().iterator();
            
            while (i.hasNext())
            {
                int j = ((Integer)i.next()).intValue();
                Enchantment enchantment = Enchantment.getEnchantmentById(j);
                int level = ((Integer)bookEnchants.get(Integer.valueOf(j))).intValue();
                
                if (enchantment == Enchantment.unbreaking && level >= 3) { isHarder = true; }
                if (enchantment == Enchantment.sharpness && level >= 4) { isStronger = true; }
                if (enchantment == Enchantment.efficiency && level >= 4) { isFaster = true; }
                if (enchantment == Enchantment.infinity && level >= 1) { isBetter = true; }
                
                LogHelper.info("    Book contains: " + enchantment.getName() + ", level " + level);
            }
            
            
            // calculates the cost
            event.cost = 0;
            multipleEnchantPenalty = 0;
            
            if (isHarder) { event.cost += 6; multipleEnchantPenalty += 1; }
            if (isBetter) { event.cost += 8; multipleEnchantPenalty += 1; }
            if (isFaster) { event.cost += 5; multipleEnchantPenalty += 1; }
            if (isStronger) { event.cost += 6; multipleEnchantPenalty += 1; }
            
            if (event.cost > 0 && multipleEnchantPenalty > 0) event.cost += (multipleEnchantPenalty * 3) + 1;       // Adds a maximum of 13
            
            if (event.name != "") event.cost += 1;
            
            //if (event.left.stackSize > 1) event.cost = event.cost * event.left.stackSize; 
            
            
            // Apply the enchantments
            if (isHarder) pumpkinEnchants.put(Enchantment.unbreaking.effectId, 1);
            if (isBetter) pumpkinEnchants.put(Enchantment.infinity.effectId, 1);
            if (isFaster) pumpkinEnchants.put(Enchantment.efficiency.effectId, 1);
            if (isStronger) pumpkinEnchants.put(Enchantment.sharpness.effectId, 1);
            
            EnchantmentHelper.setEnchantments(pumpkinEnchants, result);

            
            if (pumpkinEnchants.size() > 0) {
                event.output = result;
            } else {
                event.output = null;
            }
            
        }
        
        

    }
    

    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        LogHelper.info("==> World Loaded <== (is remote? " + event.world.isRemote + ")");
        
        if (!event.world.isRemote) {
            ServerInfoTracker.startTracking();
        }
    }

}
