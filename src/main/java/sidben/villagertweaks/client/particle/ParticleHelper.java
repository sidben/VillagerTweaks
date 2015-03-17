package sidben.villagertweaks.client.particle;

import sidben.villagertweaks.client.particle.ParticlePotionEffect.EffectType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;


@SideOnly(Side.CLIENT)
public class ParticleHelper
{

    private static Minecraft mc = Minecraft.getMinecraft();
    private static World theWorld = mc.theWorld;
    private static TextureManager renderEngine = mc.renderEngine;
    
    
    
    public static EntityFX spawnParticle(EffectType effect, double xCoord, double yCoord, double zCoord) {
        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null)
        {
            /*
             * This is from the CaveJohnson212's tutorial, I haven't looked into it but looks like
             * it avoids rendering particles if the player isn't looking and if the particles
             * config are set to none.
             * 
             * NOTE: I'll ignore particle setting for now, TODO: revisit this code
             */
            /*
            int var14 = mc.gameSettings.particleSetting;
            if (var14 == 1 && theWorld.rand.nextInt(3) == 0)
            {
                var14 = 2;
            }
            */

            double var15 = mc.getRenderViewEntity().posX - xCoord;
            double var17 = mc.getRenderViewEntity().posY - yCoord;
            double var19 = mc.getRenderViewEntity().posZ - zCoord;
            double var22 = 16.0D;
            
            if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
            {
                return null;
            }
            /*
            else if (var14 > 1)
            {
                return null;
            }
            */
            
            
            // Creates a new particle and display in the world
            ParticlePotionEffect particle = new ParticlePotionEffect(effect, renderEngine, theWorld, xCoord, yCoord, zCoord);
            mc.effectRenderer.addEffect(particle);
            return particle;
        }
        
        return null;
    }
    
}
