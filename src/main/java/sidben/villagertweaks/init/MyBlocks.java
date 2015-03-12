package sidben.villagertweaks.init;

import sidben.villagertweaks.block.BlockFake;
import sidben.villagertweaks.item.ItemFakeBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class MyBlocks
{
    
    // Blocks instances
    public static final BlockFake fakeBlock = new BlockFake();

    
    public static void register() {
        GameRegistry.registerBlock(fakeBlock, ItemFakeBlock.class, "fake_block");
    }
    
   
}
