package sidben.villagertweaks.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.client.particle.ParticleHelper;
import sidben.villagertweaks.client.particle.ParticlePotionEffect;
import sidben.villagertweaks.client.particle.ParticlePotionEffect.EffectType;



//TODO: fix this mess of enchantment list types... :(



/**
 * Represents enchantments that can be applied to golems.
 * 
 */
public class GolemEnchantment
{

    private static final GolemEnchantment[] enchantmentsList     = new GolemEnchantment[16];

    public static final GolemEnchantment    max                  = new GolemEnchantment(0, null, EnchantmentType.REFRESH, "g_max", 30, true);
    public static final GolemEnchantment    speed                = new GolemEnchantment(1, EffectType.SPEED, EnchantmentType.PASSIVE, "g_speed", 5, false);
    public static final GolemEnchantment    protection           = new GolemEnchantment(2, EffectType.RESISTANCE, EnchantmentType.DEFENSE, "g_armor", 7, false);
    public static final GolemEnchantment    fireProtection       = new GolemEnchantment(3, EffectType.FIRE_PROTECTION, EnchantmentType.DEFENSE, "g_fire_armor", 6, false);
    public static final GolemEnchantment    projectileProtection = new GolemEnchantment(4, EffectType.PROJECTILE_PROTECTION, EnchantmentType.DEFENSE, "g_projectile_armor", 6, false);
    public static final GolemEnchantment    blastProtection      = new GolemEnchantment(5, EffectType.BLAST_PROTECTION, EnchantmentType.DEFENSE, "g_blast_armor", 5, false);
    public static final GolemEnchantment    strength             = new GolemEnchantment(6, EffectType.STRENGTH, EnchantmentType.PASSIVE, "g_strength", 6, false);
    public static final GolemEnchantment    knockback            = new GolemEnchantment(7, EffectType.KNOCKBACK, EnchantmentType.OFFENSE, "g_repulsor", 5, false);
    public static final GolemEnchantment    thorns               = new GolemEnchantment(8, EffectType.THORNS, EnchantmentType.DEFENSE, "g_spike", 5, false);
    public static final GolemEnchantment    unbreaking           = new GolemEnchantment(9, EffectType.HEALTH_BOOST, EnchantmentType.PASSIVE, "g_reinforced", 7, false);
    public static final GolemEnchantment    fire                 = new GolemEnchantment(10, EffectType.FIRE_RESISTANCE, EnchantmentType.OFFENSE, "g_flaming", 6, false);

    // TODO: Fire icon
    

    public static enum EnchantmentType {
        /** Applied when the golem attacks something */
        OFFENSE,

        /** Applied when the golem receives damage */
        DEFENSE,

        /** Applied when the golem joins the world */
        PASSIVE,

        /** Applied periodically */
        REFRESH
    }


    private final int                             id;
    private final ParticlePotionEffect.EffectType effect;
    private final String                          unlocalizedName;
    private final int                             xpBaseCost;
    private final boolean                         isUnique;
    private final EnchantmentType                 type;



    public static GolemEnchantment getEnchantmentById(int id)
    {
        return id >= 0 && id < enchantmentsList.length ? enchantmentsList[id] : null;
    }

    /**
     * Gets what would be the equivalent golem enchantment to a given vanilla enchantment.
     * 
     */
    public static GolemEnchantment convert(Enchantment enchantment, int level)
    {
        // Only accepts max level, or level 4 in case the max is 5.
        // TODO: revisit this idea, maybe lvl 4 books give less power
        if (level < enchantment.getMaxLevel() || (level < 4 && enchantment.getMaxLevel() == 5)) {
            return null;
        }

        // Convert the enchantment
        if (enchantment == Enchantment.baneOfArthropods || enchantment == Enchantment.sharpness || enchantment == Enchantment.smite || enchantment == Enchantment.power) {
            return strength;
        } else if (enchantment == Enchantment.blastProtection) {
            return blastProtection;
        } else if (enchantment == Enchantment.efficiency) {
            return speed;
        } else if (enchantment == Enchantment.fireAspect || enchantment == Enchantment.flame) {
            return fire;
        } else if (enchantment == Enchantment.fireProtection) {
            return fireProtection;
        } else if (enchantment == Enchantment.knockback || enchantment == Enchantment.punch) {
            return knockback;
        } else if (enchantment == Enchantment.projectileProtection) {
            return projectileProtection;
        } else if (enchantment == Enchantment.protection) {
            return protection;
        } else if (enchantment == Enchantment.thorns) {
            return thorns;
        } else if (enchantment == Enchantment.unbreaking) {
            return unbreaking;
        }

        return null;
    }

    /**
     * Calculates all the golem enchantments that could be applied using the given item.
     * 
     */
    @SuppressWarnings("rawtypes")
    public static GolemEnchantment[] convert(ItemStack item)
    {
        final ArrayList<GolemEnchantment> list = new ArrayList<GolemEnchantment>();
        GolemEnchantment[] arrayList = null;


        // Enchanted book
        if (item.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(item).tagCount() > 0) {

            // check if the book has valid enchantments
            final Map enchantmentsList = EnchantmentHelper.getEnchantments(item);
            final Iterator i = enchantmentsList.keySet().iterator();

            while (i.hasNext()) {
                final int key = ((Integer) i.next()).intValue();
                final Enchantment enchantment = Enchantment.getEnchantmentById(key);
                final int level = ((Integer) enchantmentsList.get(Integer.valueOf(key))).intValue();

                final GolemEnchantment golemEnchant = GolemEnchantment.convert(enchantment, level);
                if (golemEnchant != null) {
                    list.add(golemEnchant);
                }

            }

        }

        // Enchanted pumpkin
        if (item.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {

            // check if the pumpkin has valid enchantments
            int[] pumpkinEnchants = MagicHelper.getEnchantmentIds(item);
            
            if (pumpkinEnchants != null && pumpkinEnchants.length > 0) {
                arrayList = convertToEnchantArray(pumpkinEnchants);
            }

        }

        // ...Notch's apple
        else if (item.getItem() == Items.golden_apple && item.getItemDamage() == 1) {
            list.add(GolemEnchantment.max);
        }


        if (arrayList == null) {
            arrayList = list.toArray(new GolemEnchantment[list.size()]);
        }
        return arrayList;
    }


    /**
     * Gets a list of all valid enchantments with the given IDs.
     * 
     */
    public static GolemEnchantment[] convertToEnchantArray(int[] ids)
    {
        final ArrayList<GolemEnchantment> list = convertToEnchantList(ids);
        return list.toArray(new GolemEnchantment[list.size()]);
    }

    /**
     * Gets a list of all valid enchantments with the given IDs.
     * 
     */
    public static ArrayList<GolemEnchantment> convertToEnchantList(int[] ids) {
        final ArrayList<GolemEnchantment> list = new ArrayList<GolemEnchantment>();


        if (ids != null && ids.length > 0) {
            for (final int id : ids) {
                final GolemEnchantment golemEnchant = GolemEnchantment.getEnchantmentById(id);
                if (golemEnchant != null) {
                    list.add(golemEnchant);
                }
            }
        }

        return list;
    }


    /**
     * Gets a list of IDs from the given enchantments list.
     * 
     */
    public static int[] convert(GolemEnchantment[] enchantments)
    {
        if (enchantments == null || enchantments.length <= 0) {
            return new int[0];
        }

        final int[] ids = new int[enchantments.length];
        for (int i = 0; i < ids.length; i++) {
            final GolemEnchantment e = enchantments[i];
            if (e != null) {
                ids[i] = e.getId();
            }
        }

        return ids;
    }

    
    
    /**
     * Checks if this enchantment can be added to the given list.
     * 
     */
    public boolean getCanBeCombined(GolemEnchantment[] currentEnchantments)
    {
        if (currentEnchantments == null || currentEnchantments.length <= 0) 
        {
            return true;
        }
        else
        {
            if (this.isUnique) {
                return false;
            }
            else {
                // Only allows 1 enchantment of each type
                for (GolemEnchantment e : currentEnchantments) {
                    if (e.type == this.type) return false;
                    if (e.isUnique) return false;
                }
                return true;
            }
        }
    }

    /**
     * Checks if this enchantment can be added to the given list.
     * 
     */
    public boolean getCanBeCombined(List<GolemEnchantment> currentEnchantments)
    {
        if (currentEnchantments == null || currentEnchantments.size() <= 0) 
        {
            return true;
        }
        else
        {
            if (this.isUnique) {
                return false;
            }
            else {
                // Only allows 1 enchantment of each type
                for (GolemEnchantment e : currentEnchantments) {
                    if (e.type == this.type) return false;
                    if (e.isUnique) return false;
                }
                return true;
            }
        }
    }

    


    


    public GolemEnchantment(int id, EffectType effect, EnchantmentType type, String name, int xpCost, boolean isUnique) {
        this.id = id;
        this.effect = effect;
        this.type = type;
        this.unlocalizedName = name;
        this.xpBaseCost = xpCost;
        this.isUnique = isUnique;

        if (enchantmentsList[this.id] != null) {
            throw new IllegalArgumentException("Duplicate golem enchantment id you silly!");
        } else {
            enchantmentsList[this.id] = this;
        }
    }


    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("enchantment." + this.unlocalizedName);
    }

    public int getId()
    {
        return this.id;
    }

    public int getXpBaseCost()
    {
        return this.xpBaseCost;
    }

    /**
     * Checks is the enchantment is something that must be applied alone. 
     */
    public boolean getIsUnique()
    {
        return this.isUnique;
    }

    public EnchantmentType getType()
    {
        return this.type;
    }



    /**
     * Spawn the particles of this enchantment at the given entity.
     * 
     */
    @SideOnly(Side.CLIENT)
    public void spawnParticles(EntityLiving entity)
    {
        if (this.effect == null) {
            return;
        }
        if (entity.isInvisible()) {
            return;
        }
        final Random rand = entity.getRNG();
        final double range = 0.15D;
        final double randomX = (rand.nextDouble() * (range * 2)) - range;
        final double randomY = (rand.nextDouble() * (range * 2)) - range;
        final double randomZ = (rand.nextDouble() * (range * 2)) - range;

        ParticleHelper.spawnParticle(effect, entity.posX + randomX, entity.posY + entity.height - randomY, entity.posZ + randomZ);
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Enchantment ID = ");
        r.append(this.id);
        r.append(", Effect = ");
        if (this.effect == null) {
            r.append("NULL");
        } else {
            r.append(this.effect.toString());
        }
        r.append(", XP cost = ");
        r.append(this.xpBaseCost);
        r.append(", Is unique = ");
        r.append(this.isUnique);

        return r.toString();
    }


}
