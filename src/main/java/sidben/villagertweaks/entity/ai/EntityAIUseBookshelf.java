package sidben.villagertweaks.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;


/**
 * Makes librarian villagers "read" bookshelves. 
 *
 */
public class EntityAIUseBookshelf extends EntityAIMoveToBlock
{
    
    private final EntityVillager theVillager;
    
    

    public EntityAIUseBookshelf(EntityVillager villager, double p_i45889_2_) {
        super(villager, p_i45889_2_, 16);
        this.theVillager = villager;
    }

    
    @Override
    protected boolean func_179488_a(World worldIn, BlockPos p_179488_2_)
    {
        return false;
    }

}
