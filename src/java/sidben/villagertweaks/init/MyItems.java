package sidben.villagertweaks.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.item.*;
import sidben.villagertweaks.reference.Reference;


@GameRegistry.ObjectHolder(Reference.ModID)
public class MyItems
{

    // Items instances



    // register the items
    public static void register()
    {
    }


    // register the renderers
    @SideOnly(Side.CLIENT)
    public static void registerRender()
    {
    }

}
