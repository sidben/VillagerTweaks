package sidben.villagertweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;



public class EntityAILaunchFireworks extends EntityAIBase {

    
    private EntityVillager villager;
    private int delayCheck = 0;
    private int frequencyCheck = 50;            // Frequency in iterations that the villager will try to execute this action
    private int chance = 50;                    // Chance to execute action;
    
    
    
    public EntityAILaunchFireworks(EntityVillager par1EntityVillager)
    {
        this.villager = par1EntityVillager;
        this.setMutexBits(5);
    }

    
    @Override
    public boolean shouldExecute() {
        //System.out.println("AIFireworks.ShouldExecute?");

        
        if (!this.villager.isEntityAlive())
        {
            return false;
        }
        else if (this.villager.isInWater())
        {
            return false;
        }
        else if (!this.villager.onGround)
        {
            return false;
        }
        else if (this.villager.velocityChanged)
        {
            return false;
        }
        else if (this.villager.worldObj.isDaytime())
        {
            return false;
        }
        
        
        if (delayCheck > frequencyCheck){
            // Debug
            System.out.println("AIFireworks.ShouldExecute this time?");
            
            
            delayCheck = 0;
            int luckDraw = this.villager.worldObj.rand.nextInt(100); 
            
            if (luckDraw < chance) {
                // Debug
                System.out.println("AIFireworks.ShouldExecute!");

                return true;
            } else {
                // Debug
                System.out.println("AIFireworks.not yet (" +luckDraw+ ")");
                
            }

        } else {
            delayCheck++;
            
        }
            
        
        return false;
    }

    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        // Debug
        System.out.println("AIFireworks.StartExecute");
        System.out.println(" Side:       " + FMLCommonHandler.instance().getEffectiveSide());
        System.out.println(" World:      " + this.villager.worldObj.isRemote);
    }

    
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        // Debug
        System.out.println("AIFireworks.ContinueExecuting?");
        
        int a = this.villager.worldObj.rand.nextInt(10);
        if (a < 5) {
            // Debug
            System.out.println("  No!");
            return false;
        } else {
            // Debug
            System.out.println("  Yes!");
            return true;
        }
        
    }


    /**
     * Updates the task
     */
    public void updateTask()
    {
        // Debug
        System.out.println("AIFireworks.UpdateTask");
        
        
        
        double rocketX = this.villager.posX;
        double rocketY = this.villager.posY + 1;
        double rocketZ = this.villager.posZ;
        byte rocketFlight = 1;
        // int[] rocketColor = new int[1];
        int[] rocketColor = {14602026, 3887386};
        ItemStack rocketStack = new ItemStack(Item.firework);
        
        
        // NBT Info
        NBTTagCompound fireworksTagCompound = new NBTTagCompound("Fireworks");
        NBTTagList boomTagList = new NBTTagList("Explosions");
        NBTTagCompound boomTagCompound = new NBTTagCompound();

        boomTagCompound.setByte("Type", (byte) 0);
        boomTagCompound.setBoolean("Flicker", true);
        boomTagCompound.setIntArray("Colors", rocketColor);
        boomTagList.appendTag(boomTagCompound);

        fireworksTagCompound.setByte("Flight", rocketFlight);
        fireworksTagCompound.setTag("Explosions", boomTagList);

        rocketStack.stackTagCompound = new NBTTagCompound();
        rocketStack.stackTagCompound.setTag("Fireworks", fireworksTagCompound);
        

        EntityFireworkRocket rocket = new EntityFireworkRocket(this.villager.worldObj, rocketX, rocketY, rocketZ, rocketStack);
        this.villager.worldObj.spawnEntityInWorld(rocket);

        
    }
    
    
    /**
     * Resets the task
     */
    public void resetTask()
    {
        // Debug
        System.out.println("AIFireworks.ResetTask");
    }
    
}
