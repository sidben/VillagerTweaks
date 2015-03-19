package sidben.villagertweaks.client.renderer.entity;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class LayerIronGolemGlint implements LayerRenderer
{
    
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final ModelIronGolem golemModel = new ModelIronGolem();
    private final RenderCrackedIronGolem  golemRender;
    private Random rand = new Random();
    private int displayTimer, displayMaxTime;
    private float size = 0.9F;



    public LayerIronGolemGlint(RenderCrackedIronGolem render) {
        this.golemRender = render;
        this.displayTimer = rand.nextInt(150) - 210;
        this.displayMaxTime = rand.nextInt(500) + 300;
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
        /*
        if (displayTimer > this.displayMaxTime) {
            this.displayTimer = rand.nextInt(150) - 400;
            this.displayMaxTime = rand.nextInt(50) + 200;
        }
        */

    }
    
    
    public void doRenderGlintLayer(EntityIronGolem entity, float par1, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        ExtendedGolem properties = ExtendedGolem.get(entity);
        boolean showGlint = false;
        float minSize = 0.95F;
        float maxSize = 1.0005F;
        float unit = 0.0001F;
        
        
        if (properties != null) {
            showGlint = true;
            this.displayTimer++;
            
            if (this.displayTimer > this.displayMaxTime && this.size > minSize) {
                this.size -= unit;
            }
            else if (this.displayTimer > 0 && this.size < maxSize) {
                this.size += unit;
            }
            
            if (this.size > maxSize) { 
                this.size = maxSize;
            }
            else if (this.size < minSize) {
                this.size = minSize;
            }
            
            if (this.size == minSize && this.displayTimer > this.displayMaxTime) {
                this.displayTimer = rand.nextInt(550) - 1000;
                this.displayMaxTime = rand.nextInt(2000) + 3000;
            }
            
            LogHelper.info("--> " + this.displayTimer + " / " + this.displayMaxTime + " / " +  this.size);
            
        }
        
        
        // Renders the enchanted overlay
        if (!entity.isInvisible() && showGlint) {
            GlStateManager.depthMask(true);
            this.golemRender.bindTexture(RES_ITEM_GLINT);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f7 = (float)entity.ticksExisted + par3;
            GlStateManager.translate(f7 * 0.01F, f7 * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            
            /*
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            */
            
            GlStateManager.color(0.5F, 0.25F, 0.8F, 1.0F);      // Enchanted color
            GlStateManager.disableLighting();
    
            GlStateManager.blendFunc(GL11.GL_SRC_COLOR, 1);
            // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            // GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
            GlStateManager.scale(size, size, size);
            
            this.golemModel.setModelAttributes(this.golemRender.getMainModel());
            this.golemModel.render(entity, par1, par2, par4, par5, par6, par7);
           
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }
        
    }

}



/*
final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

GlStateManager.depthMask(false);
GlStateManager.depthFunc(514);
GlStateManager.disableLighting();
GlStateManager.blendFunc(768, 1);

this.golemRender.bindTexture(RES_ITEM_GLINT);

GlStateManager.matrixMode(5890);
GlStateManager.pushMatrix();
GlStateManager.scale(8.0F, 8.0F, 8.0F);
float f = (float)(net.minecraft.client.Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
GlStateManager.translate(f, 0.0F, 0.0F);
GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);

this.golemRender.getMainModel().render(entity, p_177145_2_, p_177145_3_, p_177145_5_, p_177145_6_, p_177145_7_, p_177145_8_);

GlStateManager.popMatrix();
GlStateManager.matrixMode(5888);
GlStateManager.blendFunc(770, 771);
GlStateManager.enableLighting();
GlStateManager.depthFunc(515);
GlStateManager.depthMask(true);
*/