package sidben.villagertweaks.client.renderer.entity;


import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSnowmanHead;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderCustomSnowMan extends RenderSnowMan
{
    
    private static final ResourceLocation snowManTextures = new ResourceLocation("textures/entity/snowman.png");
    

    public RenderCustomSnowMan(RenderManager render) {
        super(render);
        
        // reject the original pumpkin layer and replace it with my own
        LayerRenderer targetLayer = null;
        
        for(Object layer : this.layerRenderers) {
            // NOTE: vanilla only has 1 layer, but maybe other mods adds more, so I seek the one I want
            if (layer instanceof LayerSnowmanHead) { 
                targetLayer = (LayerRenderer) layer;
                break;
            };
        }
        
        if (targetLayer != null) {
            this.removeLayer(targetLayer);
            this.addLayer(new LayerSnowmanEnchantedHead(this));
        }
        
    }

}
