package sidben.villagertweaks.client.particle;

import sidben.villagertweaks.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;



@SideOnly(Side.CLIENT)
public class ParticleTest extends EntityFX
{

    
    
    public ParticleTest(World world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(world, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);

        this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.1D;
        this.particleMaxAge = 16;
        this.noClip = false;
    }

    
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.8600000143051147D;
        this.motionY *= 0.8600000143051147D;
        this.motionZ *= 0.8600000143051147D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
    

    @Override
    public int getFXLayer()
    {
        return 3;
    }
    

    // particle rendering
    @Override
    public void func_180434_a(WorldRenderer worldrenderer, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
    {
        TextureManager renderer = Minecraft.getMinecraft().getTextureManager(); 
        final ResourceLocation field_110126_a = new ResourceLocation("textures/gui/container/inventory.png");
        
        // NOTE: effect icons are 18*18
        // 36 / 198
        /*        
        
        
        int iconID = 225;

        
        this.particleTextureIndexX = iconID % 16;
        this.particleTextureIndexY = iconID / 16;

        float f6 = ((float)this.particleTextureIndexX / 16.0F) - 1/16F;
        float f7 = f6 + 0.0624375F;
        float f8 = ((float)this.particleTextureIndexY / 16.0F) - 1/16F;
        float f9 = f8 + 0.0624375F;
        LogHelper.info("  (A)   " + f6 + ", " + f7 + ", " + f8 + ", " + f9);
*/

        float f6 = 0.140625F;
        float f7 = f6 + 0.0703125F;
        float f8 = 0.7734375F;
        float f9 = f8 + 0.0703125F;
        LogHelper.info("  (B)   " + f6 + ", " + f7 + ", " + f8 + ", " + f9 + " scale " + this.particleScale);
        

        float f10 = 0.1F * this.particleScale;
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_180434_3_ - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_180434_3_ - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_180434_3_ - interpPosZ);

        
        GlStateManager.disableLighting();
        renderer.bindTexture(field_110126_a);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldrenderer.startDrawingQuads();

        worldrenderer.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldrenderer.addVertexWithUV((double)(f11 - p_180434_4_ * f10 - p_180434_7_ * f10), (double)(f12 - p_180434_5_ * f10), (double)(f13 - p_180434_6_ * f10 - p_180434_8_ * f10), (double)f7, (double)f9);
        worldrenderer.addVertexWithUV((double)(f11 - p_180434_4_ * f10 + p_180434_7_ * f10), (double)(f12 + p_180434_5_ * f10), (double)(f13 - p_180434_6_ * f10 + p_180434_8_ * f10), (double)f7, (double)f8);
        worldrenderer.addVertexWithUV((double)(f11 + p_180434_4_ * f10 + p_180434_7_ * f10), (double)(f12 + p_180434_5_ * f10), (double)(f13 + p_180434_6_ * f10 + p_180434_8_ * f10), (double)f6, (double)f8);
        worldrenderer.addVertexWithUV((double)(f11 + p_180434_4_ * f10 - p_180434_7_ * f10), (double)(f12 - p_180434_5_ * f10), (double)(f13 + p_180434_6_ * f10 - p_180434_8_ * f10), (double)f6, (double)f9);

        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        
        
        // funcional (ish)
        /*
        GlStateManager.disableLighting();
        float f8 = 0.125F;
        float f9 = (float)(this.posX - interpPosX);
        float f10 = (float)(this.posY - interpPosY);
        float f11 = (float)(this.posZ - interpPosZ);
        //float f12 = this.worldObj.getLightBrightness(new BlockPos(this));
        float f12 = 1;
        renderer.bindTexture(field_110126_a);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldrenderer.startDrawingQuads();
        worldrenderer.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldrenderer.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 + f8), 0.0D, 1.0D);
        worldrenderer.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 + f8), 1.0D, 1.0D);
        worldrenderer.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 - f8), 1.0D, 0.0D);
        worldrenderer.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 - f8), 0.0D, 0.0D);
        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        */

        
        /*
        renderer.bindTexture(new ResourceLocation("textures/particle/particles.png"));
        worldrenderer.startDrawingQuads();
        
        super.func_180434_a(worldrenderer, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
        
        renderer.bindTexture(new ResourceLocation("textures/particle/particles.png"));
        worldrenderer.startDrawingQuads();
        */
    }

}
