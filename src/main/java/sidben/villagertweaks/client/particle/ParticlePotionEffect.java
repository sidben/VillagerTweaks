package sidben.villagertweaks.client.particle;

import java.awt.geom.Point2D;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Spawn particles with the potion effect icon.
 *
 */
@SideOnly(Side.CLIENT)
public class ParticlePotionEffect extends EntityFX
{

    public enum EffectType {
        SPEED(0.0F, 0.7734375F, 1),                 // NOTE: I'm too lazy to come up with a math formula so I hard-coded this icon coordinates
        STRENGTH(0.28125F, 0.7734375F, 1),
        JUMP_BOOST(0.140625F, 0.84375F, 1),
        REGENERATION(0.4921875F, 0.7734375F, 1),
        RESISTANCE(0.421875F, 0.84375F, 1),
        FIRE_RESISTANCE(0.4921875F, 0.84375F, 1),
        HEALTH_BOOST(0.140625F, 0.9140625F, 1);
               
        
        public final Point2D iconCoordinates;
        public final int textureID; 
        
        
        private EffectType(float x, float y, int textureID) {
            this.iconCoordinates = new Point2D.Float(x, y);
            this.textureID = textureID;
        }
        
    }
    
    
    
    
    private final static float unit = 0.0703125F;       // Icons are 18x18 in a 256px page, so unit = 18/256
    private final EffectType type;
    private final ResourceLocation vanillaEffects = new ResourceLocation("textures/gui/container/inventory.png");
    private final TextureManager renderer;
    
    
    protected ParticlePotionEffect(EffectType effect, TextureManager renderer, World world, double x, double y, double z) {
        super(world, x, y, z);
        this.type = effect;
        this.renderer = renderer;
        
        this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.1D;
        this.particleScale = 1.4F;
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
    public void func_180434_a(WorldRenderer worldrenderer, Entity entity, float x, float y, float z, float xSpeed, float ySpeed, float zSpeed)
    {
        float minU = (float)this.type.iconCoordinates.getX();
        float maxU = minU + unit;
        float minV = (float)this.type.iconCoordinates.getY();
        float maxV = minV + unit;
        

        float f10 = 0.1F * this.particleScale;
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x - interpPosZ);

        
        GlStateManager.disableLighting();
        if (this.type.textureID == 1) {
            renderer.bindTexture(vanillaEffects);
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldrenderer.startDrawingQuads();

        worldrenderer.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldrenderer.addVertexWithUV((double)(f11 - y * f10 - ySpeed * f10), (double)(f12 - z * f10), (double)(f13 - xSpeed * f10 - zSpeed * f10), (double)maxU, (double)maxV);
        worldrenderer.addVertexWithUV((double)(f11 - y * f10 + ySpeed * f10), (double)(f12 + z * f10), (double)(f13 - xSpeed * f10 + zSpeed * f10), (double)maxU, (double)minV);
        worldrenderer.addVertexWithUV((double)(f11 + y * f10 + ySpeed * f10), (double)(f12 + z * f10), (double)(f13 + xSpeed * f10 + zSpeed * f10), (double)minU, (double)minV);
        worldrenderer.addVertexWithUV((double)(f11 + y * f10 - ySpeed * f10), (double)(f12 - z * f10), (double)(f13 + xSpeed * f10 - zSpeed * f10), (double)minU, (double)maxV);

        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        
    }
    
    
}
