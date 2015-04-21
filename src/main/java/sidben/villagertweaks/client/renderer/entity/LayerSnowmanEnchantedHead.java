package sidben.villagertweaks.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import sidben.villagertweaks.common.ExtendedGolem;
import sidben.villagertweaks.helper.GolemEnchantment;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.IBakedModel;


@SideOnly(Side.CLIENT)
public class LayerSnowmanEnchantedHead implements LayerRenderer
{

    private final RenderCustomSnowMan           golemRender;
    private final float                   size           = 0.625F;



    public LayerSnowmanEnchantedHead(RenderCustomSnowMan renderCustomSnowMan) {
        this.golemRender = renderCustomSnowMan;
    }


    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }


    @Override
    public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.doRenderGlintLayer((EntitySnowman) entity, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }


    public void doRenderGlintLayer(EntitySnowman entity, float par1, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        final ExtendedGolem properties = ExtendedGolem.get(entity);
        boolean showGlint = false;
        
        
        if (properties != null) {

            // check if the golem has a glint enchant (Max / Mighty).
            final GolemEnchantment e = properties.getRandomEnchantment();
            if (e == GolemEnchantment.max) {
                showGlint = true;
            }

        }


        // Renders the enchanted overlay
        if (!entity.isInvisible()) {
            ItemStack stack = new ItemStack(Blocks.pumpkin, 1);
            if (showGlint) stack.addEnchantment(Enchantment.infinity, 1);

            GlStateManager.pushMatrix();
            this.golemRender.func_177123_g().head.postRender(0.0625F);
            GlStateManager.translate(0.0F, -0.34375F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(size, -size, -size);
            Minecraft.getMinecraft().getItemRenderer().renderItem(entity, stack, ItemCameraTransforms.TransformType.HEAD);
            GlStateManager.popMatrix();

            

            /*
            GlStateManager.pushMatrix();
            this.golemRender.func_177123_g().head.postRender(0.0625F);
            GlStateManager.translate(0.0F, -(size/2), 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            
            
            IBakedModel model = this.itemRenderer.getItemModelMesher().getItemModel(stack);
            
            GlStateManager.depthMask(true);
            this.golemRender.bindTexture(RES_ITEM_GLINT);
            

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            final float f7 = entity.ticksExisted + par3;
            GlStateManager.translate(f7 * 0.01F, f7 * 0.01F, 0.0F);
            
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableBlend();
            
            //GlStateManager.color(0.5F, 0.25F, 0.8F, 1.0F);
            //GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
            GlStateManager.color(0.1F, 0.9F, 0.1F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_COLOR, 1);
            GlStateManager.scale(size, size, size);
            

            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.HEAD);
            this.itemRenderer.renderItem(stack, model);
            
            
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(false);
            

            
            GlStateManager.popMatrix();
             */
        }

    }




}
