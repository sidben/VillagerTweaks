package sidben.villagertweaks.helper;

import net.minecraft.entity.EntityLivingBase;



/**
 * Represents how much damage a golem has.  
 *
 */
public enum EnumGolemHealth
{
    NORMAL(0.6F, 1.0F),
    DAMAGED(0.3F, 0.6F),
    HIGHLY_DAMAGED(-1.0F, 0.3F);
    
    
    
    protected final float min;
    protected final float max;
    
    private EnumGolemHealth(float minPercentage, float maxPercentage) {
        this.min = minPercentage;
        this.max = maxPercentage;
    }
    
    
    public static EnumGolemHealth getGolemHealth(EntityLivingBase entity) {
        float pct = 0;
        if (entity.getHealth() > 0) {
            pct = entity.getHealth() / entity.getMaxHealth();
        }
        
        if (pct > HIGHLY_DAMAGED.min && pct <= HIGHLY_DAMAGED.max) return HIGHLY_DAMAGED;
        if (pct > DAMAGED.min && pct <= DAMAGED.max) return DAMAGED;
        if (pct > NORMAL.min && pct <= NORMAL.max) return NORMAL;
        
        return NORMAL;
    }
    
}
