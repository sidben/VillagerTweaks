package sidben.villagertweaks.client.particle;

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
    
    
    
    public static EntityFX spawnParticle(String particleName, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null)
        {
            int var14 = mc.gameSettings.particleSetting;
            
            ParticleTest particle = new ParticleTest(theWorld, xCoordIn, yCoordIn, zCoordIn, (float)xSpeedIn, (float)ySpeedIn, (float)zSpeedIn);
            mc.effectRenderer.addEffect(particle);
            return particle;
        }
        
        return null;
    }
    
}
