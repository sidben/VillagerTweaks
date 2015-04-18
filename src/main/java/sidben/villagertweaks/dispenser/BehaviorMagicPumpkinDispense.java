package sidben.villagertweaks.dispenser;

import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.helper.MagicHelper;
import sidben.villagertweaks.tracker.ServerInfoTracker;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;


public class BehaviorMagicPumpkinDispense extends BehaviorDefaultDispenseItem
{

    private boolean success = true;
    
    
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        LogHelper.info("== Dispensing a pumpkin  ==");
        
        World world = source.getWorld();
        BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
        BlockPumpkin blockpumpkin = (BlockPumpkin)Blocks.pumpkin;

        
        LogHelper.info("    at " + blockpos);

        
        if (world.isAirBlock(blockpos) && blockpumpkin.canDispenserPlace(world, blockpos))
        {
            if (!world.isRemote)
            {
                // Check if it's an enchanted pumpkin. If so, add to the tracker.
                if (stack.getTagCompound().getTagList(MagicHelper.GolemEnchantmentsNBTKey, 3) != null) {
                    ServerInfoTracker.add(stack, blockpos);
                }
                
                world.setBlockState(blockpos, blockpumpkin.getDefaultState(), 3);
            }

            --stack.stackSize;
        }
        else
        {
            this.success = false;
        }

        return stack;
    }

    
    protected void playDispenseSound(IBlockSource source)
    {
        if (this.success)
        {
            source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
        }
        else
        {
            source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
        }
    }
    
}
