package sidben.villagertweaks.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.google.common.collect.Lists;
import scala.actors.threadpool.Arrays;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.handler.ConfigurationHandler;
import sidben.villagertweaks.helper.GolemEnchantment.EnchantmentType;
import sidben.villagertweaks.network.NetworkHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;



public class MagicHelper
{
    
    
    // Maximum quantity of enchants per pumpkin 
    public static int MaxEnchants = 3;
    
    public static String GolemEnchantmentsNBTKey = "golem_enchs";
    
    
    /**
     * Check if a pumpkin can be enchanted, what enchantments it would get and how much it would cost.
     * 
     * @param pumpkin The pumpkin
     * @param magicItem The item being combined
     * @param result The enchanted pumpkin
     * @param cost XP level cost
     * @return true if the item can be enchanted, false if should follow vanilla rules  
     */
    public static ResultCanEnchant canEnchant(ItemStack pumpkin, ItemStack magicItem) {
        
        // It's a pumpkin, check if can combine
        if (pumpkin.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && pumpkin.stackSize == 1) {
            ResultCanEnchant result = new ResultCanEnchant(); 
            
            // Gets a list of possible golem enchantments
            GolemEnchantment[] enchantCandidates = GolemEnchantment.convert(magicItem);
            GolemEnchantment[] currentEnchantments = GolemEnchantment.convert(pumpkin);
            ArrayList<GolemEnchantment> newEnchantments = new ArrayList<GolemEnchantment>();
            
            
            if (enchantCandidates.length > 0) 
            {
                result.cost = 0;
                
                
                LogHelper.info("    Golem Enchants candidates " + enchantCandidates.length);
                LogHelper.info("        " + Arrays.toString(enchantCandidates));
                LogHelper.info("    Current enchants " + currentEnchantments.length);
                LogHelper.info("        " + Arrays.toString(currentEnchantments));
                
                
                // Adds the "old" enchantments
                for (int i = 0; i < currentEnchantments.length; i++) {
                    if (newEnchantments.size() > MagicHelper.MaxEnchants) break;
                    GolemEnchantment auxEnchant = enchantCandidates[i];
                    
                    newEnchantments.add(auxEnchant);
                }                
                
                // Chooses what new enchantments can be combined
                for (int i = 0; i < enchantCandidates.length; i++) {
                    if (newEnchantments.size() > MagicHelper.MaxEnchants) break;
                    GolemEnchantment auxEnchant = enchantCandidates[i];
                    
                    if (auxEnchant.getCanBeCombined(newEnchantments)) {
                        newEnchantments.add(auxEnchant);
                        result.cost += auxEnchant.getXpBaseCost();
                    }
                }
                
                
                LogHelper.info("    New enchants to be added " + newEnchantments.size());
                

                
                // creates the item
                result.item = getEnchantedPumpkin(newEnchantments);

                // calculates extra penalties on the cost
                result.cost += ((newEnchantments.size() - 1) * 3);
                
                
                LogHelper.info("-- Magic Helper --");
                LogHelper.info("    cost " + result.cost);
                LogHelper.info("    output " + result.item);
                
                
                result.isValid = (newEnchantments.size() > 0);
                
                return result;
                
            } 
            

        }
        
        return null;
    }
    
    
    
    /**
     * Returns a pumpkin with the enchantments defined.
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ItemStack getEnchantedPumpkin(ArrayList<GolemEnchantment> newEnchantments) 
    {
        ItemStack item = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin), 1);
        
        
        // Prepares the NBT tag with custom enchantments
        NBTTagList geTagList = new NBTTagList();

        // Adds the enchantments
        if (newEnchantments.size() > 0) {
            for (int i = 0; i < newEnchantments.size(); i++) {
                GolemEnchantment auxEnchant = newEnchantments.get(i);
                LogHelper.info("    - " + i + ": " + auxEnchant);

                geTagList.appendTag(new NBTTagInt(auxEnchant.getId()));
            }
        }
        
        item.setTagInfo(MagicHelper.GolemEnchantmentsNBTKey, geTagList);
        LogHelper.info("    -> " + geTagList);
        LogHelper.info("    -> # " + geTagList.tagCount());
       

        
        // NBT tag to remove the "Enchantments" from tooltip
        item.setTagInfo("HideFlags", new NBTTagInt(1));

        // Adds a "fake" enchantment to make the pumpkin have an effect
        final Map pumpkinEnchants = EnchantmentHelper.getEnchantments(item);
        pumpkinEnchants.put(Enchantment.infinity.effectId, 1);
        EnchantmentHelper.setEnchantments(pumpkinEnchants, item);
        
        // TODO: check stack.addEnchantment

        
        return item;
    }
    

    /**
     * Returns a pumpkin with the enchantment defined.
     * 
     */
    public static ItemStack getEnchantedPumpkin(GolemEnchantment newEnchantment) 
    {
        ArrayList<GolemEnchantment> list = new ArrayList<GolemEnchantment>();
        list.add(newEnchantment);
        return getEnchantedPumpkin(list);
    }
    
    
    
    
    /**
     * Applies all related effects on a golem that was just created
     * with a enchanted pumpkin.
     * 
     */
    public static void applyMagicEffectsOnNewlySpawnedGolem(EntityGolem golem, ItemStack enchantedPumpkin)
    {
        if (ConfigurationHandler.onDebug) {
            LogHelper.info("-- Enchanted Golem found at [" + golem.getPosition() + "], applying extra info");
        }
        
        MagicHelper.applyPumpkinExtraInfo(golem, enchantedPumpkin);
        MagicHelper.applyPassiveEffects(golem);
        MagicHelper.applyRefreshEffects(golem);
        
        // Heals the golem, if needed (so enchantments that raise health actually work)
        if (golem.getHealth() < golem.getMaxHealth()) {
            golem.heal(golem.getMaxHealth());
        }        
    }
    
    
    
    
    
    /**
     * Applies the extra info (name, enchantments) in the pumpkin to the golem created with it.
     * 
     */
    public static void applyPumpkinExtraInfo(EntityGolem golem, ItemStack pumpkin) {
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
    
    

    
    private static final double healthBoostAmountIron = 0.6D;
    private static final double healthBoostAmountSnow = 3.0D;
    private static final double speedAmount = 0.25D;
    
    private static final AttributeModifier attReinforcedIron = new AttributeModifier(UUID.fromString("179aa5d9-b25e-4f17-8bd5-44e6f39b8d5c"), "golem_reinforced", healthBoostAmountIron, 2);
    private static final AttributeModifier attReinforcedSnow = new AttributeModifier(UUID.fromString("179aa5d9-b25e-4f17-8bd5-44e6f39b8d5c"), "golem_reinforced", healthBoostAmountSnow, 2);
    private static final AttributeModifier attQuick = new AttributeModifier(UUID.fromString("2139955f-7a5d-4e69-9e44-fc8f9b214a77"), "golem_quick", speedAmount, 2);
    
    
    
    
    /**
     * Apply passive enchantments, that modifies base stats permanently.
     * 
     */
    public static void applyPassiveEffects(EntityGolem golem) {
        
        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get((EntityGolem) golem);
        
        // 0 = Iron golem, 1 = snow golem
        int type = (golem instanceof EntityIronGolem) ? 0 : (golem instanceof EntitySnowman) ? 1 : -1;   
        
        
        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            LogHelper.info("== applyPassiveEffects() - " + golem.getEntityId() + " ==");
            
            for (GolemEnchantment e : properties.getEnchantments()) {
                if (e != null && e.getType() == EnchantmentType.PASSIVE) 
                {

                    /*---------------------------------------------------------------
                     * Unbreaking / Reinforced
                     * Adds health.  
                     *---------------------------------------------------------------*/
                    if (e == GolemEnchantment.unbreaking) {
                        
                        IAttributeInstance iattributeinstance = ((EntityLivingBase)golem).getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth);
                        
                        if (iattributeinstance != null)
                        {
                            if (type == 0) {
                                iattributeinstance.removeModifier(attReinforcedIron);
                                iattributeinstance.applyModifier(attReinforcedIron);
                            } else if (type == 1) {
                                iattributeinstance.removeModifier(attReinforcedSnow);
                                iattributeinstance.applyModifier(attReinforcedSnow);
                            }
                        }
                    
                    }
                    
                    
                    /*---------------------------------------------------------------
                     * Speed / Quick
                     * Makes faster.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.speed) {
                        
                        IAttributeInstance iattributeinstance = ((EntityLivingBase)golem).getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);

                        if (iattributeinstance != null)
                        {
                            iattributeinstance.removeModifier(attQuick);
                            iattributeinstance.applyModifier(attQuick);
                        }
                    
                    }

                    
                    /*---------------------------------------------------------------
                     * Strength
                     * Makes stronger.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.strength) {

                        // Special Note: The iron golems DO NOT do damage based on strength,
                        // they use a hard-coded formula and do between 7 to 21 damage
                    
                    }
                    
                } //if (e is passive enchantment)

            } // for

        } // if (properties are valid)
        
    }


    
    
    /**
     * Apply offensive enchantments, that adds some special effect to the attack without
     * directly adding damage.
     * 
     */
    public static void applyAttackEffects(Entity golem, Entity target) {

        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get((EntityGolem) golem);
        
        // 0 = Iron golem, 1 = snow golem
        int type = (golem instanceof EntityIronGolem) ? 0 : (golem instanceof EntitySnowman) ? 1 : -1;   

        // Must have a target
        if (target == null) return;
        
        
        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            LogHelper.info("== applyAttackEffects() - " + golem.getEntityId() + " ==");
            
            for (GolemEnchantment e : properties.getEnchantments()) {
                if (e != null && e.getType() == EnchantmentType.OFFENSE) 
                {

                    /*---------------------------------------------------------------
                     * Knockback
                     * Pushes mobs (higher and/or back)  
                     *---------------------------------------------------------------*/
                    if (e == GolemEnchantment.knockback) {

                        float motionHori = 0F;
                        float motionVert = 0F; 
                        
                        if (type == 0) {
                            motionHori = 1.5F;
                            motionVert = 0.2F;
                        }
                        else if (type == 1) {
                            motionHori = 1.3F;
                        }

                        // code ref: EntityMob.attackEntityAsMob and EntityIronGolem.attackEntityAsMob
                        target.motionX += (-MathHelper.sin(golem.rotationYaw * (float)Math.PI / 180.0F) * motionHori);
                        target.motionY += motionVert;
                        target.motionZ += (MathHelper.cos(golem.rotationYaw * (float)Math.PI / 180.0F) * motionHori);

                    }

                    
                    /*---------------------------------------------------------------
                     * Flaming
                     * Sets mobs on fire  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.fire) {
                        
                        // vanilla fire aspect = 4
                        if (type == 0) { 
                            target.setFire(6);  
                        } 
                        else if (type == 1) {
                            // snowman don't set mobs on fire
                        }
                    
                    }
                    
                    
                } //if (e is attack enchantment)

            } // for

        } // if (properties are valid)
        
    }
    

    
    /**
     * Apply enchantments that modifies damage dealt in each attack.
     * 
     */
    public static float applyDamagingEffects(Entity golem, Entity target, float ammount) {
        float realDamage = ammount;
        
        
        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get((EntityGolem) golem);

        // 0 = Iron golem, 1 = snow golem
        int type = (golem instanceof EntityIronGolem) ? 0 : (golem instanceof EntitySnowman) ? 1 : -1;   

        
        LogHelper.info("== applyDamagingEffects() - " + golem.getEntityId() + " ==");
       
        
        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            for (GolemEnchantment e : properties.getEnchantments()) {
                if (e != null) 
                {

                    /*---------------------------------------------------------------
                     * Mighty
                     * do more damage (simulate strength potion).
                     *---------------------------------------------------------------*/
                    if (e == GolemEnchantment.max) {
                        if (type == 0) {
                            realDamage *= 1.3F;
                        } else if (type == 1 && ammount == 0) {
                            // snowball with no damage, causes fixed damage of 1
                            realDamage = 1F;
                        }
                        // Note: Mighty snow golem don't add dmg if the snowball already does damage.
                    }
                    
                    
                    /*---------------------------------------------------------------
                     * Strength (special case)
                     * Do more damage.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.strength) {

                        if (type == 0) {
                            // original damage = 7 to 22, my version does 16 to 28
                            realDamage = (float)(16 + golem.worldObj.rand.nextInt(12));
                        } else if (type == 1) {
                            if (ammount == 0) {
                                // snowball with no damage, causes fixed damage of 1
                                realDamage = 1F;
                            } else if (ammount > 0) {
                                // snowball with damage (example: Blazes), simulate strength potion
                                realDamage *= 1.3F;
                            }
                            
                            // chance for 1 extra damage (20% chance)
                            if (realDamage > 0 && golem.worldObj.rand.nextInt(10) < 2) {
                                realDamage += 1F;
                            }
                            
                        }
                    
                    }
                    
                } //if (e is attack enchantment)

            } // for

        } // if (properties are valid)
        
        
        
        // Debug
        if (ConfigurationHandler.onDebug) {
            LogHelper.info("Applying attack (damage modifier) enchantments on golem");
            LogHelper.info("    golem: " + golem);
            LogHelper.info("    target: " + target);
            LogHelper.info("    damage value: " + ammount + " -> " + realDamage);
        }        
        
        
        return realDamage;
    }
    

    
    
    /**
     * Check effects that can cancel a source of damage.
     * 
     */
    public static boolean applyDamageCancelEffects(Entity golem, DamageSource source) {
        boolean shouldCancel = false;
        
        
        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get((EntityGolem) golem);

        
        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            LogHelper.info("== applyDamageCancelEffects() - " + golem.getEntityId() + " ==");            
            
            for (GolemEnchantment e : properties.getEnchantments()) {

                /*---------------------------------------------------------------
                 * Mighty
                 * Super golem, has a chance to ignore projectile.
                 *---------------------------------------------------------------*/
                if (e == GolemEnchantment.max) {
                    
                    if (!golem.worldObj.isDaytime() && source.isProjectile()) {

                        if (EnumGolemHealth.getGolemHealth((EntityLivingBase)golem) == EnumGolemHealth.HIGHLY_DAMAGED) {
                            // high damage, chance of 60%
                            shouldCancel = (golem.worldObj.rand.nextInt(10) < 6);
                            
                        } 
                        else if (EnumGolemHealth.getGolemHealth((EntityLivingBase)golem) == EnumGolemHealth.DAMAGED) {
                            // some damage, chance of 20%
                            shouldCancel = (golem.worldObj.rand.nextInt(10) < 2);
                            
                        }
                        
                    }
                        
                }

            } // for

        } // if (properties are valid)

        
        return shouldCancel;
    }
    
    
    
    /**
     * Apply defensive enchantments that modifies damage received.
     * 
     */
    public static float applyDefenseEffects(Entity golem, DamageSource source, float ammount) {
        float originalDamage = ammount;
        float realDamage = 0F;
        float damageModifier = 0F;
        
        
        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get((EntityGolem) golem);
        

        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            LogHelper.info("== applyAttackEffects() - " + golem.getEntityId() + " ==");
            
            // Ref for armor enchants: http://minecraft.gamepedia.com/Armor#Enchantments
            
            for (GolemEnchantment e : properties.getEnchantments()) {
                if (e != null && e.getType() == EnchantmentType.DEFENSE) 
                {

                    /*---------------------------------------------------------------
                     * Protection
                     * Generic protection against all types of damage.  
                     *---------------------------------------------------------------*/
                    if (e == GolemEnchantment.protection) {
                        
                        /*
                         * As a reference, 1 piece of armor with Protection IV reduces damage by 20%.
                         * 4 pieces of armor reduces damage by 80%, the max cap.
                         * 
                         * Since the golem uses 1 Protection IV book, but cannot "equip" multiple
                         * pieces or armor, I hard-coded the reduction at 30%.   
                         */
                        
                        damageModifier = 0.3F;
                    
                    }

                    
                    /*---------------------------------------------------------------
                     * Blast Protection
                     * Protection against explosions.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.blastProtection && source.isExplosion()) {
                        
                        /*
                         * As a reference, 1 piece of armor with Blast Protection IV would
                         * reduce damage by 44%, but it's capped at 20%.
                         * 
                         * To make it simple I set the reduction to 50%.   
                         */
                        
                        damageModifier = 0.5F;
                    
                    }

                    
                    /*---------------------------------------------------------------
                     * Fire Protection
                     * Protection against fire/lava.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.fireProtection && source.isFireDamage()) {
                        
                        /*
                         * As a reference, 1 piece of armor with Fire Protection IV would
                         * reduce damage by 36%, but it's capped at 20%.
                         * 
                         * I decided on 35%, a little more than regular protection, but
                         * since I can't reduce the fire timer I added a 30% chance to 
                         * instantly extinguish the golem.
                         */
                        
                        damageModifier = 0.35F;
                        if (golem.isBurning()) {
                            int raffle = golem.worldObj.rand.nextInt(10);
                            if (raffle < 3) golem.extinguish();
                        }
                    
                    }

                    
                    /*---------------------------------------------------------------
                     * Projectile Protection
                     * Protection against ranged attacks.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.projectileProtection && source.isProjectile()) {
                        
                        /*
                         * The rules for projectile protection are the same as the ones
                         * for blast protection, but I feel that arrow are a much higher
                         * menace for golems, since the knockback can make them take many
                         * shots before reaching the target.
                         * 
                         * For that reason I set the reduction to 60%.   
                         */
                        
                        damageModifier = 0.6F;
                    
                    }

                    
                    /*---------------------------------------------------------------
                     * Thorns
                     * Damages attacker.  
                     *---------------------------------------------------------------*/
                    else if (e == GolemEnchantment.thorns) {
                        
                        /*
                         * Note: Default Thorns damage is 1 to 4, with 15% chance per level.
                         * 
                         * On the enchantment, the penalty is the armor loses more durability, so 
                         * I made the golem loses 15% more health. The chance is of 60%.
                         */

                        Entity attacker = null;

                        // Finds the attacker
                        if (source.damageType == "mob") {
                            attacker = source.getSourceOfDamage();
                        }
                        else if (source instanceof EntityDamageSourceIndirect) {     // e.g. projectile attack
                            attacker = ((EntityDamageSourceIndirect)source).getEntity();
                        }
                        
                        
                        // Causes damage (with a chance, and only if the golem was in fact damaged)
                        if (attacker != null && golem.worldObj.rand.nextInt(20) < 12 && ammount >= 1)
                        {
                            int thornsDamage = 2 + golem.worldObj.rand.nextInt(4);
                            
                            attacker.attackEntityFrom(DamageSource.causeThornsDamage(golem), thornsDamage);
                            attacker.playSound("damage.thorns", 0.5F, 1.0F);
                            
                            damageModifier = -0.15F;
                        }
                        
                    
                    }
                    
                    
                } //if (e is attack enchantment)

            } // for

        } // if (properties are valid)

        
        
        
        realDamage = originalDamage * (1.0F - damageModifier);
        if (realDamage > 1F) {
            // round down
            realDamage = (float) Math.floor(realDamage);
        }
        
        
        // Debug
        if (ConfigurationHandler.onDebug) {
            LogHelper.info("Applying defense enchantments on golem");
            LogHelper.info("    golem: " + golem);
            LogHelper.info("    damage source: " + source);
            LogHelper.info("    damage value: " + originalDamage + " -> " + realDamage);
        }
        
        
        return realDamage;
    }


    
    
    /**
     * Apply enchantments that are refreshed after a certain time.
     * 
     */
    public static void applyRefreshEffects(EntityGolem golem) {

        // Load the properties
        ExtendedGolem properties = ExtendedGolem.get(golem);
        
        // 0 = Iron golem, 1 = snow golem
        int type = (golem instanceof EntityIronGolem) ? 0 : (golem instanceof EntitySnowman) ? 1 : -1;   

        

        if (properties != null && properties.getEnchantmentsAmount() > 0) 
        {
            LogHelper.info("== applyRefreshEffects() - " + golem.getEntityId() + " ==");            
            LogHelper.info("    Day: " + golem.worldObj.isDaytime());
            LogHelper.info("    Dimension: " + golem.dimension);
            
            for (GolemEnchantment e : properties.getEnchantments()) {
                if (e != null && e.getType() == EnchantmentType.REFRESH) 
                {

                    /*---------------------------------------------------------------
                     * Mighty
                     * Super golem, has speed, resistance and absorption by night,
                     * regeneration by day.
                     * 
                     * NOTE: End = day, Nether = night.
                     *---------------------------------------------------------------*/
                    if (e == GolemEnchantment.max) {
                        
                        if (golem.worldObj.isDaytime()) {
                            if (type == 0) {
                                // Iron Golem
                                if (EnumGolemHealth.getGolemHealth(golem) == EnumGolemHealth.HIGHLY_DAMAGED) {
                                    refreshPotionEffect(golem, Potion.regeneration, 2);
                                } else {
                                    refreshPotionEffect(golem, Potion.regeneration, 1);
                                }
                            }
                            else if (type == 1) {
                                // Snow Golem
                                refreshPotionEffect(golem, Potion.regeneration, 2);
                                refreshPotionEffect(golem, Potion.absorption, 1);
                                refreshPotionEffect(golem, Potion.resistance, 1);
                            }
                            
                        }
                        else {
                            if (type == 0) {
                                // Iron Golem
                                if (EnumGolemHealth.getGolemHealth(golem) == EnumGolemHealth.HIGHLY_DAMAGED) {
                                    refreshPotionEffect(golem, Potion.resistance, 2);
                                } else {
                                    refreshPotionEffect(golem, Potion.resistance, 1);
                                }
                                refreshPotionEffect(golem, Potion.moveSpeed, 1);
                                refreshPotionEffect(golem, Potion.absorption, 4);
                            }
                            else if (type == 1) {
                                // Snow Golem
                                refreshPotionEffect(golem, Potion.resistance, 2);
                                refreshPotionEffect(golem, Potion.moveSpeed, 1);
                                refreshPotionEffect(golem, Potion.absorption, 4);
                            }
                            
                        }
                    
                    }

                    
                } //if (e is attack enchantment)

            } // for

        } // if (properties are valid)

    }
    
    
    
    
    /**
     * Adds a potion to the given entity for 30 seconds or
     * refresh the effect if the time is lower than 20 seconds.
     * 
     */
    private static void refreshPotionEffect(EntityLivingBase entity, Potion potion, int amplifier) {
        if (!entity.isPotionActive(potion) || entity.getActivePotionEffect(potion).getDuration() < 400) {
            entity.addPotionEffect(new PotionEffect(potion.id, 600, amplifier));
        }
    }


}
