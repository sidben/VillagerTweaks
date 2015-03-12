package sidben.villagertweaks.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import sidben.villagertweaks.helper.LogHelper;


/**
 * Makes blacksmith villagers activate furnaces.
 * 
 */
public class EntityAIUseFurnace extends EntityAIMoveToBlock
{

    private final EntityVillager theVillager;
    
    /*
     * Action type:
     * 
     * -1 = Not executing
     *  0 = Activating furnace
     *  1 = Watching it burn
     *  
     */
    private int action;
    
    private int watchingBurnTimer;

    
    
    
    public EntityAIUseFurnace(EntityVillager villager, double p_i45889_2_) {
        super(villager, p_i45889_2_, 16);
        this.theVillager = villager;
    }


    @Override
    public boolean shouldExecute()
    {
        // Only executes at day
        if (!this.theVillager.worldObj.isDaytime())
        {
            return false;
        }

        
        // field_179496_a is the counter
        if (this.field_179496_a <= 0)
        {
            if (!this.theVillager.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
            {
                return false;
            }
            
            this.action = -1;
        }

        boolean r = super.shouldExecute();
        if (r) LogHelper.info("shouldExecute(): " + r + " - Villager " + this.theVillager);
        return r;
    }

    @Override
    public boolean continueExecuting()
    {
        boolean r = this.action >= 0 && super.continueExecuting();
        if (r) LogHelper.info("continueExecuting() " + r + " - Villager " + this.theVillager);        
        return r;
    }

    @Override
    public void startExecuting()
    {
        LogHelper.info("startExecuting() - Villager " + this.theVillager);
        super.startExecuting();
    }

    @Override
    public void resetTask()
    {
        LogHelper.info("resetTask() - Villager " + this.theVillager);
        super.resetTask();
    }

    
    @Override
    public void updateTask()
    {
        super.updateTask();
        
        if (this.func_179487_f())
        {
            World world = this.theVillager.worldObj;
            BlockPos blockpos = this.destinationBlock;
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            LogHelper.info("updateTask() - Villager " + this.theVillager);
            LogHelper.info("    action: " + action);
            LogHelper.info("    " + (block instanceof BlockFurnace));

        
            if (this.action == 0 && block instanceof BlockFurnace)
            {
                world.destroyBlock(blockpos, true);
            }
            
            
            this.action = -1;
            this.field_179496_a = 10;
        }
        
        else {
            LogHelper.info("updateTask() - func_179487_f = false");

        }
    }
    

    @Override
    protected boolean func_179488_a(World worldIn, BlockPos position)
    {
        // This method randmly (?) scans nearby blocks
        
        Block block = worldIn.getBlockState(position).getBlock();
        

//        LogHelper.info("func_179488_a()");
//        LogHelper.info("    Block at: " + position + " = " + block);
        
        if (block == Blocks.furnace) {
//            LogHelper.info("    It's a furnace");

            this.action = 0;
            return true;
            
        }

        else if (block == Blocks.lit_furnace) {
//            LogHelper.info("    It's a LIT furnace");
            
            this.action = 1;
            return true;
        }


        return false;
    }

}
