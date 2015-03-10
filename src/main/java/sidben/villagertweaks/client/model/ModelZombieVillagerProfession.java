package sidben.villagertweaks.client.model;

import net.minecraft.client.model.ModelZombieVillager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class ModelZombieVillagerProfession extends ModelZombieVillager
{

    public ModelZombieVillagerProfession() {
        this(0.0F, 0.0F, false);
    }
    
    
    public ModelZombieVillagerProfession(float extension, float p_i1165_2_, boolean p_i1165_3_) {
        super(extension, p_i1165_2_, p_i1165_3_);

        // adds the robe
        this.bipedBody.setTextureOffset(36, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, extension + 0.5F);
        
        /*
         * NOTE:
         * 
         * [addBox] has the following signature:
         *     addBox(float offsetX, float offsetY, float offsetZ, int sizeX, int sizeY, int sizeZ, float extension)
         * 
         * Regular (biped) body box would be:
         *     addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, extension)
         *     
         */
        
    }


}
