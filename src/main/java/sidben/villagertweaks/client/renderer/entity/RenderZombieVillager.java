package sidben.villagertweaks.client.renderer.entity;


import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.villagertweaks.client.model.ModelZombieVillagerProfession;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.reference.Reference;
import com.google.common.collect.Lists;



@SideOnly(Side.CLIENT)
public class RenderZombieVillager extends RenderBiped
{

    private static final ResourceLocation       vanillaZombie         = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation       vanillaZombieVillager = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
    private static final ResourceLocation       zombieFarmer          = new ResourceLocation(Reference.ModID + ":textures/entity/zombie_villager_farmer.png");
    private static final ResourceLocation       zombieLibrarian       = new ResourceLocation(Reference.ModID + ":textures/entity/zombie_villager_librarian.png");
    private static final ResourceLocation       zombieCleric          = new ResourceLocation(Reference.ModID + ":textures/entity/zombie_villager_cleric.png");
    private static final ResourceLocation       zombieSmith           = new ResourceLocation(Reference.ModID + ":textures/entity/zombie_villager_smith.png");
    private static final ResourceLocation       zombieButcher         = new ResourceLocation(Reference.ModID + ":textures/entity/zombie_villager_butcher.png");

    private final ModelBiped                    field_82434_o;
    private final ModelZombieVillagerProfession zombieVillagerProfessionModel;
    private final List                          field_177121_n;
    private final List                          field_177122_o;


    @SuppressWarnings("unchecked")
    public RenderZombieVillager(RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelZombie(), 0.5F, 1.0F);
        final LayerRenderer layerrenderer = (LayerRenderer) this.layerRenderers.get(0);
        this.field_82434_o = this.modelBipedMain;
        this.zombieVillagerProfessionModel = new ModelZombieVillagerProfession();
        this.addLayer(new LayerHeldItem(this));
        final LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
        {
            @Override
            protected void func_177177_a()
            {
                this.field_177189_c = new ModelZombie(0.5F, true);
                this.field_177186_d = new ModelZombie(1.0F, true);
            }
        };
        this.addLayer(layerbipedarmor);
        this.field_177122_o = Lists.newArrayList(this.layerRenderers);

        if (layerrenderer instanceof LayerCustomHead) {
            this.removeLayer(layerrenderer);
            this.addLayer(new LayerCustomHead(this.zombieVillagerProfessionModel.bipedHead));
        }

        this.removeLayer(layerbipedarmor);
        this.addLayer(new LayerVillagerArmor(this));
        this.field_177121_n = Lists.newArrayList(this.layerRenderers);
    }

    public void func_180579_a(EntityZombie p_180579_1_, double p_180579_2_, double p_180579_4_, double p_180579_6_, float p_180579_8_, float p_180579_9_)
    {
        this.func_82427_a(p_180579_1_);
        super.doRender(p_180579_1_, p_180579_2_, p_180579_4_, p_180579_6_, p_180579_8_, p_180579_9_);
    }

    private void func_82427_a(EntityZombie zombie)
    {
        if (zombie.isVillager()) {
            this.mainModel = this.zombieVillagerProfessionModel;
            this.layerRenderers = this.field_177121_n;
        } else {
            this.mainModel = this.field_82434_o;
            this.layerRenderers = this.field_177122_o;
        }

        this.modelBipedMain = (ModelBiped) this.mainModel;
    }

    protected void rotateCorpse(EntityZombie p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
        if (p_77043_1_.isConverting()) {
            p_77043_3_ += (float) (Math.cos(p_77043_1_.ticksExisted * 3.25D) * Math.PI * 0.25D);
        }

        super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
        this.func_180579_a((EntityZombie) entity, x, y, z, p_76986_8_, partialTicks);
    }

    @Override
    protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
        this.rotateCorpse((EntityZombie) p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
        this.func_180579_a((EntityZombie) entity, x, y, z, p_76986_8_, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        final EntityZombie zombie = (EntityZombie) entity;
        ExtendedVillagerZombie properties;
    
        if (zombie.isVillager()) {

            properties = (ExtendedVillagerZombie)zombie.getExtendedProperties(ExtendedVillagerZombie.id);
            int profession = properties.getProfession();

            switch(profession) {
                case 0:
                    return zombieFarmer;
                    
                case 1:
                    return zombieLibrarian;
                    
                case 2:
                    return zombieCleric;

                case 3:
                    return zombieSmith;

                case 4:
                    return zombieButcher;
                    
                default:
                    return vanillaZombieVillager;
            }
            
        } 
        else {
            return vanillaZombie;
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
        this.func_180579_a((EntityZombie) entity, x, y, z, p_76986_8_, partialTicks);
    }


}
