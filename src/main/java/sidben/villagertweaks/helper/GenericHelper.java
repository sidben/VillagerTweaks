package sidben.villagertweaks.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;


/**
 * Miscellaneous helper methods. 
 *
 */
public class GenericHelper
{
    
    /**
     * Check if the given entity is a original Zombie (normal, baby or villager), 
     * and not a inherited class (like Zombie Pigman).
     *  
     */
    public static boolean isVanillaZombie(Entity entity) {
        if (entity == null) return false;
        if (entity instanceof EntityPigZombie) return false;
        return entity instanceof EntityZombie;
    }
    

}
