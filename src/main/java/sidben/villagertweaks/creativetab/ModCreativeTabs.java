package sidben.villagertweaks.creativetab;

import java.util.ArrayList;
import java.util.List;
import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.MagicHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModCreativeTabs extends CreativeTabs 
{

    public ModCreativeTabs(String unlocalizedName) {
        super(unlocalizedName);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
        return Items.emerald;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List itemList) {
         
        //--- Adds pumpkins enchantments
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.speed));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.protection));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.fireProtection));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.projectileProtection));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.blastProtection));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.strength));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.knockback));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.thorns));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.unbreaking));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.fire));
        itemList.add(MagicHelper.getEnchantedPumpkin(GolemEnchantment.max));
        
    }
         
         
}
