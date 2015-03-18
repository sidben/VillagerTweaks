package sidben.villagertweaks.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.client.particle.ParticleHelper;
import sidben.villagertweaks.client.particle.ParticlePotionEffect;
import sidben.villagertweaks.client.particle.ParticlePotionEffect.EffectType;
import sidben.villagertweaks.reference.Reference;


/**
 * Represents enchantments that can be applied to golems.
 *
 */
public class GolemEnchantment
{
    /*
- Protection IV book = Resistance I buff (reduces damage 20%)
- Fire Protection IV = Fire Protection IV buff (reduces fire dmg by 60%)
- Proj. Protection IV = Projectile Protection II buff (reduces proj. dmg by 30%)
- Blast Protection IV = Blast Protection II buff (reduces explosion dmg by 30%)
- Sharpness, Smite, Bane of Arth. IV (or V?) = Strength buff (adds 30% base dmg)
- Fire Aspect II / Flame I = Golem sets attacked mobs on fire
- Knockback II / Punch II = Iron Golems get knockback resistance, Snow Golems get punch effect on snowballs
- Thorns III = Golem gets the Thorns effect
- Unbreaking III = Golem gets 50% more health
- Power V - Snow golems snowballs do a little dmg
     */

    
    private static final GolemEnchantment[] enchantmentsList = new GolemEnchantment[16];

    public static final GolemEnchantment speed = new GolemEnchantment(0, EffectType.SPEED, "speed", 5, true);
    public static final GolemEnchantment protection = new GolemEnchantment(1, EffectType.RESISTANCE, "protection", 5, true);
    public static final GolemEnchantment fireProtection = new GolemEnchantment(2, EffectType.FIRE_RESISTANCE, "fire_protection", 5, true);
    public static final GolemEnchantment projectileProtection = new GolemEnchantment(3, EffectType.PROJECTILE_PROTECTION, "projectile_protection", 5, true);
    public static final GolemEnchantment blastProtection = new GolemEnchantment(4, EffectType.BLAST_PROTECTION, "blast_protection", 5, true);
    public static final GolemEnchantment strength = new GolemEnchantment(5, EffectType.STRENGTH, "strength", 5, true);
    public static final GolemEnchantment knockback = new GolemEnchantment(6, EffectType.KNOCKBACK, "knockback", 5, true);
    public static final GolemEnchantment thorns = new GolemEnchantment(7, EffectType.THORNS, "thorns", 5, true);
    public static final GolemEnchantment unbreaking = new GolemEnchantment(8, EffectType.HEALTH_BOOST, "unbreaking", 5, true);
    public static final GolemEnchantment power = new GolemEnchantment(9, null, "power", 5, true);
    public static final GolemEnchantment fire = new GolemEnchantment(10, null, "fire", 5, true);
    public static final GolemEnchantment max = new GolemEnchantment(15, null, "max", 30, false);

    
    private final int id;
    private final ParticlePotionEffect.EffectType effect;
    private final String unlocalizedName;
    private final int xpBaseCost;
    private final boolean canBeCombined; 
    // TODO: affects iron or snow golem, use bit flag 1 = iron, 2 = snow, 3 = both
    
    
    public static GolemEnchantment getEnchantmentById(int id)
    {
        return id >= 0 && id < enchantmentsList.length ? enchantmentsList[id] : null;
    }
    
    /**
     * Gets what would be the equivalent golem enchantment to a given vanilla enchantment.
     * 
     */
    public static GolemEnchantment convert(Enchantment enchantment, int level) {
        // Only accepts max level, or level 4 in case the max is 5.
        // TODO: revisit this idea, maybe lvl 4 books give less power
        if (level < enchantment.getMaxLevel() || (level < 4 && enchantment.getMaxLevel() == 5)) return null;
        
        // Convert the enchantment
        if (enchantment == Enchantment.baneOfArthropods) { return strength; }
        else if (enchantment == Enchantment.blastProtection) { return blastProtection; }
        else if (enchantment == Enchantment.efficiency) { return speed; }
        else if (enchantment == Enchantment.fireAspect) { return fire; }
        else if (enchantment == Enchantment.fireProtection) { return fireProtection; }
        else if (enchantment == Enchantment.flame) { return fire; }
        else if (enchantment == Enchantment.knockback) { return knockback; }
        else if (enchantment == Enchantment.power) { return power; }
        else if (enchantment == Enchantment.projectileProtection) { return projectileProtection; }
        else if (enchantment == Enchantment.protection) { return protection; }
        else if (enchantment == Enchantment.punch) { return knockback; }
        else if (enchantment == Enchantment.sharpness) { return strength; }
        else if (enchantment == Enchantment.smite) { return strength; }
        else if (enchantment == Enchantment.thorns) { return thorns; }
        else if (enchantment == Enchantment.unbreaking) { return unbreaking; }
        
        return null;
    }

    /**
     * Calculates all the golem enchantments that could be applied using the given item. 
     * 
     */
    @SuppressWarnings("rawtypes")
    public static GolemEnchantment[] convert(ItemStack item) {
        ArrayList<GolemEnchantment> list = new ArrayList<GolemEnchantment>();
        

        // Enchanted book
        if (item.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(item).tagCount() > 0)
        {

            // check if the book has valid enchantments
            final Map bookEnchants = EnchantmentHelper.getEnchantments(item);
            final Iterator i = bookEnchants.keySet().iterator();

            while (i.hasNext()) {
                final int key = ((Integer)i.next()).intValue();
                final Enchantment enchantment = Enchantment.getEnchantmentById(key);
                final int level = ((Integer) bookEnchants.get(Integer.valueOf(key))).intValue();

                GolemEnchantment golemEnchant = GolemEnchantment.convert(enchantment, level);
                if (golemEnchant != null) {
                    list.add(golemEnchant);
                }
                
            }

        }

        // ...Notch's apple
        else if (item.getItem() == Items.golden_apple && item.getItemDamage() == 1) 
        {
            list.add(GolemEnchantment.max);
        }

        
        return list.toArray(new GolemEnchantment[list.size()]);
    }

    
    
    
    
    public GolemEnchantment(int id, EffectType effect, String name, int xpCost, boolean canCombine) {
        this.id = id;
        this.effect = effect;
        this.unlocalizedName = name;
        this.xpBaseCost = xpCost;
        this.canBeCombined = canCombine;
        
        if (enchantmentsList[this.id] != null)
        {
            throw new IllegalArgumentException("Duplicate golem enchantment id you silly!");
        }
        else
        {
            enchantmentsList[this.id] = this;
        }
    }
    
    
    public String getLocalizedName() {
        return StatCollector.translateToLocal(Reference.ModID + ":enchantment." + this.unlocalizedName);
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getXpBaseCost() {
        return this.xpBaseCost;
    }
    
    public boolean getCanBeCombined() {
        return this.canBeCombined;
    }
    

    
        
    /**
     * Spawn the particles of this enchantment at the given entity.
     * 
     */
    @SideOnly(Side.CLIENT)
    public void spawnParticles(Entity entity) {
        if (this.effect == null) return;
        ParticleHelper.spawnParticle(effect, entity.posX, entity.posY + entity.height, entity.posZ);
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
        r.append(", Can combine = ");
        r.append(this.canBeCombined);

        return r.toString();
    }

}
