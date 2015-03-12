package sidben.villagertweaks.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import sidben.villagertweaks.client.renderer.entity.RenderCrackedIronGolem;
import sidben.villagertweaks.client.renderer.entity.RenderZombieVillager;
import sidben.villagertweaks.init.MyBlocks;
import sidben.villagertweaks.reference.Reference;


public class ClientProxy extends CommonProxy {

    
    @SuppressWarnings("unchecked")
    @Override
    public void initialize()
    {
        super.initialize();
        
        
        ItemModelMesher itemMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        

        // Block item renderers (for display on inventory)
        ModelBakery.addVariantName(Item.getItemFromBlock(MyBlocks.fakeBlock), getResourceName("fake_stone_block"));
        ModelBakery.addVariantName(Item.getItemFromBlock(MyBlocks.fakeBlock), getResourceName("fake_iron_block"));
        
        itemMesher.register(Item.getItemFromBlock(MyBlocks.fakeBlock), 0, new ModelResourceLocation(getResourceName("fake_stone_block"), "inventory"));
        itemMesher.register(Item.getItemFromBlock(MyBlocks.fakeBlock), 1, new ModelResourceLocation(getResourceName("fake_iron_block"), "inventory"));

        
        // Entity Renderers
        renderManager.entityRenderMap.remove(EntityIronGolem.class);
        renderManager.entityRenderMap.put(EntityIronGolem.class, new RenderCrackedIronGolem(renderManager));

        renderManager.entityRenderMap.remove(EntityZombie.class);
        renderManager.entityRenderMap.put(EntityZombie.class, new RenderZombieVillager(renderManager));

    }
    
    
    
    private String getResourceName(String name) {
        return Reference.ModID + ":" + name;
    }

}
