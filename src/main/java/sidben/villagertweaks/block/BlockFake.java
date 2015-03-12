package sidben.villagertweaks.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



/**
 * Fake villager blocks that can't be harvested.
 * 
 */
public class BlockFake extends BlockBasic
{
    
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockFake.EnumType.class);
    
    

    public BlockFake() {
        super(Material.rock);

        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setUnlocalizedName("fake_block");
        
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockFake.EnumType.STONE));
    }

    
    
    //----------------------------------------------------
    // Remove all drops
    //----------------------------------------------------
    
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        return 0;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }
    
    
    
    
    //----------------------------------------------------
    // Block states
    //----------------------------------------------------
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockFake.EnumType.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((BlockFake.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

    
    
    
    //----------------------------------------------------
    // Sub-types
    //----------------------------------------------------
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        BlockFake.EnumType[] aenumtype = BlockFake.EnumType.values();
        int i = aenumtype.length;

        for (int j = 0; j < i; ++j)
        {
            BlockFake.EnumType enumtype = aenumtype[j];
            list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
        }
    }
    
    public static enum EnumType implements IStringSerializable
    {
        STONE(0, "stone"),
        IRON(1, "iron");

        private static final BlockFake.EnumType[] META_LOOKUP = new BlockFake.EnumType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;

        private EnumType(int meta, String name)
        {
            this(meta, name, name);
        }

        private EnumType(int meta, String name, String unlocalizedName)
        {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockFake.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static
        {
            BlockFake.EnumType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                BlockFake.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }


    
}
