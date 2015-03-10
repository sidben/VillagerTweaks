package sidben.villagertweaks.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.reference.Reference;



@SideOnly(Side.CLIENT)
public class LayerIronGolemDamage implements LayerRenderer
{

    private static final ResourceLocation golemLittleDamage = new ResourceLocation(Reference.ModID + ":textures/entity/iron_golem_damaged_stage_1.png");
    private static final ResourceLocation golemBigDamage    = new ResourceLocation(Reference.ModID + ":textures/entity/iron_golem_damaged_stage_2.png");
    private final RenderCrackedIronGolem  golemRender;



    public LayerIronGolemDamage(RenderCrackedIronGolem render) {
        this.golemRender = render;
    }



    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }


    @Override
    public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.doRenderDamageLayer((EntityIronGolem) entity, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }


    public void doRenderDamageLayer(EntityIronGolem entity, float p_177145_2_, float p_177145_3_, float p_177145_4_, float p_177145_5_, float p_177145_6_, float p_177145_7_, float p_177145_8_)
    {
        if (!entity.isInvisible()) {
            // Gets the golem percentage life left
            float pct = 0;
            if (entity.getHealth() > 0) {
                pct = entity.getHealth() / entity.getMaxHealth();
            }


            if (pct >= 0.3F && pct < 0.6F) {
                // 30% to 60% = Golem a bit damaged
                this.golemRender.bindTexture(golemLittleDamage);
                this.golemRender.getMainModel().render(entity, p_177145_2_, p_177145_3_, p_177145_5_, p_177145_6_, p_177145_7_, p_177145_8_);

            } else if (pct < 0.3F) {
                // Below 30% = Golem very damaged
                this.golemRender.bindTexture(golemBigDamage);
                this.golemRender.getMainModel().render(entity, p_177145_2_, p_177145_3_, p_177145_5_, p_177145_6_, p_177145_7_, p_177145_8_);

            }


        }
    }

}
