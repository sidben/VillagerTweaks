package sidben.villagertweaks;

import java.util.Random;

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
        
        
        /*
         * Fireworks Reference:
         * 
         * 
            Type:
                0   Small Ball
                1   Large Ball
                2   Star
                3   Creeper
                4   Burst
            
            Colors:
                White       15790320
                Light gray  11250603
                Gray        4408131
                Black       1973019
                Red         11743532
                Green       3887386
                Brown       5320730
                Blue        2437522
                Cyan        2651799
                Pink        14188952
                Lime        4312372
                Yellow      14602026
                Light Blue  6719955
                Magenta     12801229
                Orange      15435844
                Purple      8073150

         */
        
        Random r = this.villager.worldObj.rand;
        double rocketX = this.villager.posX;
        double rocketY = this.villager.posY + 1;
        double rocketZ = this.villager.posZ;
        byte rocketFlight = (byte) (r.nextInt(2)+1);
        byte rocketType = (byte) r.nextInt(5); 
        int rocketEffect = r.nextInt(100);
        boolean haveTwinkle = (rocketEffect > 10 && rocketEffect < 25);
        boolean haveTrail = (rocketEffect > 60 && rocketEffect < 68);
        // int[] possibleColors = {15790320, 11250603, 4408131, 1973019, 11743532, 3887386, 5320730, 2437522, 2651799, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 8073150};
        int[] possibleColors = {15790320, 11250603, 11743532, 2437522, 2651799, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 8073150};
        int[] rocketColor = {possibleColors[r.nextInt(possibleColors.length)]};
        ItemStack rocketStack = new ItemStack(Item.firework);
        
        
        // NBT Info
        NBTTagCompound fireworksTagCompound = new NBTTagCompound("Fireworks");
        NBTTagList boomTagList = new NBTTagList("Explosions");
        NBTTagCompound boomTagCompound = new NBTTagCompound();

        boomTagCompound.setByte("Type", (byte) rocketType);
        if (haveTwinkle) boomTagCompound.setBoolean("Flicker", true);
        if (haveTrail) boomTagCompound.setBoolean("Trail", true);
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
