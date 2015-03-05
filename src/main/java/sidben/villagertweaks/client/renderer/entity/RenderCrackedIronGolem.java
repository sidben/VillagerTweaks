package sidben.villagertweaks.client.renderer.entity;


import sidben.villagertweaks.helper.LogHelper;
import sidben.villagertweaks.reference.Reference;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class RenderCrackedIronGolem extends RenderIronGolem
{

    private static final ResourceLocation vanilaIronGolem = new ResourceLocation("textures/entity/iron_golem.png");
    private static final ResourceLocation alt1IronGolem = new ResourceLocation(Reference.ModID + ":textures/entity/damaged1_iron_golem.png");
    private static final ResourceLocation alt2IronGolem = new ResourceLocation(Reference.ModID + ":textures/entity/damaged2_iron_golem.png");
    
    
    public RenderCrackedIronGolem(RenderManager p_i46133_1_) {
        super(p_i46133_1_);
    }


    protected ResourceLocation getEntityTexture(EntityIronGolem entity)
    {
        float pct = 0;
        if (entity.getHealth() > 0) pct = entity.getHealth() / entity.getMaxHealth();
        
        // LogHelper.info("  [Golem Life: " + entity.getHealth() + "/" + entity.getMaxHealth() + " = " + pct + " pc]");
        
        
        if (pct > 0.6F) {
            // LogHelper.info("  - Healthy");
            return vanilaIronGolem;
        }
        else if (pct > 0.3F) {
            // LogHelper.info("  - Damaged");
            return alt1IronGolem;
        }
        else {
            // LogHelper.info("  - Broken");
            return alt2IronGolem;
        }
    }
    
}
