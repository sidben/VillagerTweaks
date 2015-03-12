package sidben.villagertweaks.block;


import sidben.villagertweaks.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;



public abstract class BlockBasic extends Block
{

    protected BlockBasic(Material material) {
        super(material);
    }

    
    //----------------------------------------------------
    // Block name
    //----------------------------------------------------

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s:%s", Reference.ModID.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
    
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

}
