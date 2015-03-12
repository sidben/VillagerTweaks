package sidben.villagertweaks.init;

import sidben.villagertweaks.block.BlockFake;
import net.minecraft.item.ItemMultiTexture;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class MyBlocks
{
    
    // Blocks instances
    public static final BlockFake fakeBlock = new BlockFake();

    
    public static void register() {
        //GameRegistry.registerBlock(fakeBlock, ItemMultiTexture.class, "fake_block");
        GameRegistry.registerBlock(fakeBlock, "fake_block");
    }
    
    // Need ItemBlock to get items with metadata
    // https://github.com/Mimer29or40/FirstMod/blob/79a11911245f4821e6b5eff80dd25afbe72ae6f6/src/main/java/com/mimer29or40/firstmod/item/ItemBlockWithVariants.java
    // http://www.minecraftforge.net/forum/index.php?topic=24619.0
    
}
