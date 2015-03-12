package sidben.villagertweaks.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;



public class ItemFakeBlock extends ItemMultiTexture
{

    public ItemFakeBlock(Block block) {
        super(block, block, new String[] {"stone", "iron"});
    }

}
