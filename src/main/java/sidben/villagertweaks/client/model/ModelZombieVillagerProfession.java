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
        // ModelBiped body ->         bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i1149_1_);
        
    }


}
