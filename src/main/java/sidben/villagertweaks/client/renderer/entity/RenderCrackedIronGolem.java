package sidben.villagertweaks.client.renderer.entity;


import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderCrackedIronGolem extends RenderIronGolem
{

    public RenderCrackedIronGolem(RenderManager render) {
        super(render);
        this.addLayer(new LayerIronGolemDamage(this));
    }

}
