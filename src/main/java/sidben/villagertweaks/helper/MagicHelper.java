package sidben.villagertweaks.helper;

import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;



public class MagicHelper
{
    
    
    // Maximum quantity of enchants per pumpkin 
    public static int MaxEnchants = 3;
    
    public static String GolemEnchantmentsNBTKey = "golem_enchs";
    
    
    
    
    /**
     * Check if a pumpkin can be enchanted, what enchantments it would get and how much would cost.
     * 
     * @param pumpkin The pumpkin
     * @param magicItem The item being combined
     * @param result The enchanted pumpkin
     * @param cost XP level cost
     * @return true if the item can be enchanted, false if should follow vanilla rules  
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ResultCanEnchant canEnchant(ItemStack pumpkin, ItemStack magicItem) {
        
        // It's a pumpkin, check if can combine
        if (pumpkin.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && pumpkin.stackSize == 1) {
            ResultCanEnchant result = new ResultCanEnchant();
            
            // Gets a list of possible golem enchantments
            GolemEnchantment[] golemEnchants = GolemEnchantment.convert(magicItem);
            
            if (golemEnchants.length > 0) 
            {
                result.item = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin), 1);
                result.cost = 0;
                
                
                LogHelper.info("    Golem Enchants " + golemEnchants.length);
                
                
                // Adds the first enchantments of the list to a NBT group
                NBTTagList geTagList = new NBTTagList();

                for (int i = 0; i < golemEnchants.length; i++) {
                    if (i >= MagicHelper.MaxEnchants) break;

                    GolemEnchantment auxEnchant = golemEnchants[i];
                    
                    LogHelper.info("    - " + i + ": " + auxEnchant);
                    
                    if (auxEnchant.getCanBeCombined()) 
                    {
                        // Enchantment can be combined, adds to the list
                        geTagList.appendTag(new NBTTagInt(auxEnchant.getId()));
                        result.cost += auxEnchant.getXpBaseCost();
                    } 
                    else 
                    {
                        // Enchantment can't be combined, override the loop so it only return 1 enchantment
                        geTagList = new NBTTagList();
                        geTagList.appendTag(new NBTTagInt(auxEnchant.getId()));
                        result.cost = auxEnchant.getXpBaseCost();
                        break;
                    }
                }
                
                result.item.setTagInfo(MagicHelper.GolemEnchantmentsNBTKey, geTagList);
                LogHelper.info("    -> " + geTagList);
                LogHelper.info("    -> # " + geTagList.tagCount());
                

                
                // NBT tag to remove the "Enchantments" from tooltip
                result.item.setTagInfo("HideFlags", new NBTTagInt(1));       

                // Adds a "fake" enchantment to make the pumpkin have an effect
                final Map pumpkinEnchants = EnchantmentHelper.getEnchantments(pumpkin);            
                pumpkinEnchants.put(Enchantment.infinity.effectId, 1);
                EnchantmentHelper.setEnchantments(pumpkinEnchants, result.item);

                
                // calculates extra penalties on the cost
                result.cost += ((geTagList.tagCount() - 1) * 3);
                
                
                LogHelper.info("-- Magic Helper --");
                LogHelper.info("    cost " + result.cost);
                LogHelper.info("    output " + result.item);
                
                
                result.isValid = true;
                
                return result;
                
            } 
            
            
            
            
            

                
               
                /*

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

                
                */


        }
        
        return null;
    }
    

}
