package sidben.villagertweaks.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.helper.GolemEnchantment;



@SideOnly(Side.CLIENT)
public class LayerIronGolemGlint implements LayerRenderer
{

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final ModelIronGolem          golemModel     = new ModelIronGolem();
    private final RenderCrackedIronGolem  golemRender;
    private final Random                  rand           = new Random();
    private int                           displayTimer, displayMaxTime;
    private final float                   size           = 1.0005F;



    public LayerIronGolemGlint(RenderCrackedIronGolem render) {
        this.golemRender = render;
        randomizeTimers();
    }


    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }


    @Override
    public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.doRenderGlintLayer((EntityIronGolem) entity, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }


    public void doRenderGlintLayer(EntityIronGolem entity, float par1, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        final ExtendedGolem properties = ExtendedGolem.get(entity);
        boolean showGlint = false;
        boolean superGolem = false;
        
        
        /*
        sidben.villagertweaks.helper.LogHelper.info("doRenderGlintLayer()");
        sidben.villagertweaks.helper.LogHelper.info("    pos: " + entity.getPosition());
        sidben.villagertweaks.helper.LogHelper.info("    swingProgress (main model): " + this.golemRender.getMainModel().swingProgress);
        sidben.villagertweaks.helper.LogHelper.info("    ironGolemRightArm.rotateAngleX (main model): " + ((ModelIronGolem)this.golemRender.getMainModel()).ironGolemRightArm.rotateAngleX);
        sidben.villagertweaks.helper.LogHelper.info("    ironGolemLeftArm.rotateAngleX (main model): " + ((ModelIronGolem)this.golemRender.getMainModel()).ironGolemLeftArm.rotateAngleX);
        sidben.villagertweaks.helper.LogHelper.info("    isSwingInProgress (entity): " + entity.isSwingInProgress);
        sidben.villagertweaks.helper.LogHelper.info("    swingProgress (entity): " + entity.swingProgress);
        sidben.villagertweaks.helper.LogHelper.info("    limbSwing (entity): " + entity.limbSwing);
        sidben.villagertweaks.helper.LogHelper.info("    limbSwingAmount (entity): " + entity.limbSwingAmount);
        sidben.villagertweaks.helper.LogHelper.info("    getAttackTimer (entity): " + entity.getAttackTimer());
        sidben.villagertweaks.helper.LogHelper.info("    par1: " + par1);
        sidben.villagertweaks.helper.LogHelper.info("    par2: " + par2);
        sidben.villagertweaks.helper.LogHelper.info("    par3: " + par3);
        sidben.villagertweaks.helper.LogHelper.info("    par4: " + par4);
        sidben.villagertweaks.helper.LogHelper.info("    par5: " + par5);
        sidben.villagertweaks.helper.LogHelper.info("    par6: " + par6);
        sidben.villagertweaks.helper.LogHelper.info("    par7: " + par7);
        
        // NOTE: par1 matches entity.limbSwing, but looks like it's updated 1 tick later
         */
        
        
        if (properties != null) {

            // check if the golem has a unique enchant. This one can only be
            // applied solo, so the "random" method will always return it.
            final GolemEnchantment e = properties.getRandomEnchantment();
            if (e == GolemEnchantment.max) {
                showGlint = true;  // always shine
                superGolem = true;
            } else if (properties.getEnchantmentsAmount() > 0) {
                this.displayTimer++;
                showGlint = this.displayTimer > 0;

                if (this.displayTimer > this.displayMaxTime) {
                    randomizeTimers();
                }

                // LogHelper.info("--> " + this.displayTimer + " / " + this.displayMaxTime + " / " + this.size);
            }

        }


        // Renders the enchanted overlay
        if (!entity.isInvisible() && showGlint) {
            GlStateManager.depthMask(true);
            this.golemRender.bindTexture(RES_ITEM_GLINT);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            final float f7 = entity.ticksExisted + par3;
            GlStateManager.translate(f7 * 0.01F, f7 * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();

            /*
             * GlStateManager.enableAlpha();
             * GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
             */


            // GlStateManager.color(0.5F, 0.25F, 0.8F, 1.0F); // Vanilla enchanted color (128, 64, 204)
            // GlStateManager.color(0.1F, 0.234F, 0.7F, 1.0F); // Blue color
            if (superGolem) {
                GlStateManager.color(0.5F, 0.25F, 0.8F, 1.0F);
            } else {
                GlStateManager.color(0.6F, 0.6F, 0.6F, 1.0F);
            }
            GlStateManager.disableLighting();

            GlStateManager.blendFunc(GL11.GL_SRC_COLOR, 1);
            // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            // GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
            GlStateManager.scale(size, size, size);

            this.golemModel.setModelAttributes(this.golemRender.getMainModel());

            // copies the arm swing
            this.golemModel.ironGolemRightArm.rotateAngleX = ((ModelIronGolem)this.golemRender.getMainModel()).ironGolemRightArm.rotateAngleX; 
            this.golemModel.ironGolemLeftArm.rotateAngleX = ((ModelIronGolem)this.golemRender.getMainModel()).ironGolemLeftArm.rotateAngleX;
            
            this.golemModel.render(entity, par1, par2, par4, par5, par6, par7);

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }

    }



    private void randomizeTimers()
    {
        this.displayTimer = rand.nextInt(1500) - 3000;
        this.displayMaxTime = rand.nextInt(1000) + 800;
    }

}
