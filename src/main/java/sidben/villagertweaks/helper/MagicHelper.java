package sidben.villagertweaks.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import scala.actors.threadpool.Arrays;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.helper.GolemEnchantment.EnchantmentType;
import sidben.villagertweaks.network.NetworkHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;



public class MagicHelper
{
    
    
    // Maximum quantity of enchants per pumpkin 
    public static int MaxEnchants = 3;
    
    public static String GolemEnchantmentsNBTKey = "golem_enchs";
    
    
    // TODO: allow an enchanted pumpkin to get more enchants - remember to apply the penalty
    
    /**
     * Check if a pumpkin can be enchanted, what enchantments it would get and how much it would cost.
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
    
    
    
    /**
     * Applies the extra info (name, enchantments) in the pumpkin to the golem created with it.
     * 
     */
    public static void applyPumpkinExtraInfo(EntityIronGolem golem, ItemStack pumpkin) {
        if (golem == null || pumpkin == null) return;
        
        int[] pumpkinEnchants = getEnchantmentIds(pumpkin);
        String customName = pumpkin.getDisplayName();
        if (customName.equals(Blocks.pumpkin.getLocalizedName())) customName = "";
        

        // Check if a custom name exists
        if (customName != "") {
            // Applies the custom name to the golem
            golem.setCustomNameTag(customName);
        }
        
        // Check if it's an enchanted pumpkin
        if (pumpkinEnchants != null && pumpkinEnchants.length > 0) {
            final ExtendedGolem properties = ExtendedGolem.get(golem);
            if (properties == null) {
                LogHelper.warn("Could not load extened properties for Iron Golem ID " + golem.getEntityId() + ", enchantments won't be applied.");
            } else {
                properties.setEnchantments(pumpkinEnchants);
                LogHelper.info("--> Applying enchantments on golem " + golem.getEntityId() + ": " + Arrays.toString(pumpkinEnchants));
                
                NetworkHelper.sendEnchantedGolemInfoMessage(golem, properties);
            }
        }

    }
    
    
    public static void applyPumpkinExtraInfo(EntitySnowman golem, ItemStack pumpkin) {
        if (golem == null || pumpkin == null) return;
     
        String customName = pumpkin.getDisplayName();

        
        // Check if a custom name exists
        if (customName != "") {
            // Applies the custom name to the golem
            golem.setCustomNameTag(customName);
            golem.enablePersistence();
        }
        

    }
    
    
    
    
    
    /**
     * Returns all valid golem enchantments in the given pumpkin.
     * 
     */
    public static int[] getEnchantmentIds(ItemStack itemStack) {
        int[] enchants = null;
        
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            // Get a tagList of type 3 (integers)
            NBTTagList tag = itemStack.hasTagCompound() ? itemStack.getTagCompound().getTagList(MagicHelper.GolemEnchantmentsNBTKey, 3) : null;
            
            // Loads the enchantments
            if (tag != null && !tag.hasNoTags()) {
                enchants = new int[tag.tagCount()]; 
                for (int i = 0; i < enchants.length; i++) {
                    int enchId = ((NBTTagInt)tag.get(i)).getInt();
                    enchants[i] = enchId;
                }
            }
            
        }
        
        return enchants;
    }
    
    
    
    
    
    public static List<String> getPumpkinToolip(ItemStack itemStack, List<String> originalTooltip) {
        ArrayList<String> toolTip = Lists.newArrayList(originalTooltip);
        GolemEnchantment[] enchants = null;
        boolean hasSuperRare = false;
        
        
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {

            // Get a tagList of type 3 (integers)
            NBTTagList tag = itemStack.hasTagCompound() ? itemStack.getTagCompound().getTagList(MagicHelper.GolemEnchantmentsNBTKey, 3) : null;
            
            // Loads the enchantments
            if (tag != null && !tag.hasNoTags()) {
                enchants = new GolemEnchantment[tag.tagCount()]; 
                for (int i = 0; i < enchants.length; i++) {
                    int enchId = ((NBTTagInt)tag.get(i)).getInt();
                    GolemEnchantment e = GolemEnchantment.getEnchantmentById(enchId);
                    enchants[i] = e;
                    
                    if (e != null && e == GolemEnchantment.max) {
                        hasSuperRare = true;
                    }
                }
            }
            
            // Customize the tooltip
            if (enchants != null && enchants.length > 0) {
                if (hasSuperRare) {
                    toolTip.set(0, EnumChatFormatting.LIGHT_PURPLE + toolTip.get(0));
                } else {
                    toolTip.set(0, EnumChatFormatting.YELLOW + toolTip.get(0));
                }

                // adds the names in reverse order so they are displayed in the correct order
                for (int i = enchants.length - 1; i >= 0; i--) {
                    if (enchants[i] != null) {
                        toolTip.add(1, enchants[i].getLocalizedName());
                    }
                }
            }

            
        }

        return toolTip;
    }
    
    
    
    
    
    
    
    public static void applyPassiveEffects(GolemEnchantment enchantment, Entity golem) {
        if (enchantment.getType() != EnchantmentType.PASSIVE) return;
        
        // TODO: implement
    }


    public static void applyAttackEffects(GolemEnchantment enchantment, Entity golem, Entity target) {
        if (enchantment.getType() != EnchantmentType.OFFENSE) return;
        
        // TODO: implement
    }
    
    
    public static void applyDefenseEffects(GolemEnchantment enchantment, Entity golem, DamageSource source) {
        if (enchantment.getType() != EnchantmentType.DEFENSE) return;
        
        // TODO: implement
    }

    
    public static void applyRefreshEffects(GolemEnchantment enchantment, Entity golem) {
        if (enchantment.getType() != EnchantmentType.REFRESH) return;
        
        // TODO: implement
    }


}
