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
import sidben.villagertweaks.reference.Reference;


/**
 * Spawn particles with the potion effect icon.
 * Mainly used to display enchanted golems buffs.
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
        HEALTH_BOOST(0.140625F, 0.9140625F, 1),
        PROJECTILE_PROTECTION(0.0F, 0.0F, 2),
        FIRE_PROTECTION(unit * 1, 0.0F, 2),
        BLAST_PROTECTION(unit * 2, 0.0F, 2),
        THORNS(unit * 3, 0.0F, 2),
        KNOCKBACK(unit * 4, 0.0F, 2);


        public final Point2D iconCoordinates;
        public final int     textureID;


        private EffectType(float x, float y, int textureID) {
            this.iconCoordinates = new Point2D.Float(x, y);
            this.textureID = textureID;
        }

    }



    private final static float     unit           = 0.0703125F;                                                                 // Icons are 18x18 in a 256px page, so unit = 18/256
    private final EffectType       type;
    private final ResourceLocation vanillaEffects = new ResourceLocation("textures/gui/container/inventory.png");
    private final ResourceLocation modEffects     = new ResourceLocation(Reference.ModID + ":textures/particles/particles.png");
    private final TextureManager   renderer;
    private final double           maxY;


    protected ParticlePotionEffect(EffectType effect, TextureManager renderer, World world, double x, double y, double z) {
        super(world, x, y, z);
        this.type = effect;
        this.renderer = renderer;

        this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.1D;
        this.particleScale = 2.0F;
        this.particleMaxAge = 20;
        this.noClip = false;

        this.maxY = y + 0.45D;
    }



    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;


        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY >= this.maxY) {
            this.motionY = 0.0D;
        } else {
            this.motionY *= 0.8600000143051147D;
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
        final float minU = (float) this.type.iconCoordinates.getX();
        final float maxU = minU + unit;
        final float minV = (float) this.type.iconCoordinates.getY();
        final float maxV = minV + unit;


        final float f10 = 0.1F * this.particleScale;
        final float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * x - interpPosX);
        final float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * x - interpPosY);
        final float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * x - interpPosZ);


        GlStateManager.disableLighting();
        if (this.type.textureID == 1) {
            renderer.bindTexture(vanillaEffects);
        } else if (this.type.textureID == 2) {
            renderer.bindTexture(modEffects);
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldrenderer.startDrawingQuads();

        worldrenderer.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldrenderer.addVertexWithUV(f11 - y * f10 - ySpeed * f10, f12 - z * f10, f13 - xSpeed * f10 - zSpeed * f10, maxU, maxV);
        worldrenderer.addVertexWithUV(f11 - y * f10 + ySpeed * f10, f12 + z * f10, f13 - xSpeed * f10 + zSpeed * f10, maxU, minV);
        worldrenderer.addVertexWithUV(f11 + y * f10 + ySpeed * f10, f12 + z * f10, f13 + xSpeed * f10 + zSpeed * f10, minU, minV);
        worldrenderer.addVertexWithUV(f11 + y * f10 - ySpeed * f10, f12 - z * f10, f13 + xSpeed * f10 - zSpeed * f10, minU, maxV);

        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

    }


}
