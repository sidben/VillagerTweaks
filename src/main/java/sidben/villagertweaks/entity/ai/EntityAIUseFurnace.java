package sidben.villagertweaks.entity.ai;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import sidben.villagertweaks.helper.LogHelper;


/**
 * Makes blacksmith villagers activate furnaces.
 * 
 */
public class EntityAIUseFurnace extends EntityAIBase
{

    private final EntityVillager theVillager;
    private World theWorld;
    private int taskTimer;
    /*
    private double targetX;
    private double targetY;
    private double targetZ;
    */
    private final double movementSpeed;
    private BlockPos destinationBlock;          // Block where the villager will stand
    private BlockPos targetBlock;               // Block the villager will use
    private BlockPos lastTargetBlock;           // Last block used by the villager
    
    private int watchingBurnTimer;
    private int movingTimer;
    private static final int maxCookingTime = 300;     // Time to watch a furnace cook
    private boolean canClearFurnace;
    
    /*
     * Action type:
     * 
     * -1 = Not executing / idle
     *  0 = Walking to a furnace
     *  1 = Watching the furnace
     *  
     */
    private int action;
    
    
    
    
    public EntityAIUseFurnace(EntityVillager villager, double p_i45889_2_) {
        this.theVillager = villager;
        this.theWorld = villager.worldObj;
        this.movementSpeed = 0.6F;
        this.action = -1;
        // this.taskTimer = this.theVillager.getRNG().nextInt(200) + 100;
        this.taskTimer = 50;
        
        this.setMutexBits(5);           // No idea what this does, my guess would be priority of task or conflict with other tasks 
    }


    @Override
    public boolean shouldExecute()
    {
        LogHelper.info("shouldExecute() - Villager " + this.theVillager);
        LogHelper.info("    daytime = " + this.theVillager.worldObj.isDaytime());
        LogHelper.info("    action = " + this.action);
        LogHelper.info("    task timer = " + this.taskTimer);

        
        // Only executes at day
        if (!this.theVillager.worldObj.isDaytime())
        {
            return false;
        }
        else if (this.action >= 0) 
        {
            return false;
        }
        else 
        {
               
            if (this.taskTimer > 0) 
            {
                --this.taskTimer;
                return false;
            }
            else 
            {
                // this.taskTimer = 200 + this.theVillager.getRNG().nextInt(200);
                this.taskTimer = 50; 
                return this.hasTarget();
            }

        }


    }




    @Override
    public void startExecuting()
    {
        LogHelper.info("startExecuting() - Navigating to " + this.destinationBlock);

        this.theVillager.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY(), this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
        this.action = 0;
        this.movingTimer = 0;
        this.canClearFurnace = false;
    }

    @Override
    public boolean continueExecuting()
    {
        LogHelper.info("continueExecuting()");
        LogHelper.info("    action = " + this.action);
        LogHelper.info("    move timer = " + this.movingTimer);
        LogHelper.info("    burn timer = " + this.watchingBurnTimer);
        LogHelper.info("    villager = " + this.theVillager);
        LogHelper.info("    navigator has path = " + this.theVillager.getNavigator().noPath());
        LogHelper.info("    super = " + super.continueExecuting());
        
        // return !this.theVillager.getNavigator().noPath();

        // return (this.action == 0 || (this.action == 1 && this.watchingBurnTimer > 0)) && super.continueExecuting();
        //return this.action >= 0;
        return ((this.action == 0 && this.movingTimer < 1200) || (this.action == 1 && this.watchingBurnTimer > 0));
    }
    
    @Override
    public void resetTask()
    {
        LogHelper.info("resetTask() - Villager " + this.theVillager);
        super.resetTask();

        this.action = -1;
        this.canClearFurnace = false;
    }

    
    @Override
    public void updateTask()
    {
        LogHelper.info("updateTask()");
        LogHelper.info("    arrived " + this.arrived);
        /*
        LogHelper.info("    villager at " + this.theVillager.getPosition());
        LogHelper.info("    distance " + this.theVillager.getDistanceSqToCenter(this.destinationBlock));
        LogHelper.info("    distance 2 " + this.theVillager.getDistance(this.destinationBlock.getX(), this.destinationBlock.getY(), this.destinationBlock.getZ()));
        LogHelper.info("    distance 3 " + this.theVillager.getDistance(this.destinationBlock.getX(), this.theVillager.getPosition().getY(), this.destinationBlock.getZ()));
        LogHelper.info("    destination " + this.destinationBlock + " (" + this.destinationBlock.up() + ")");
        */
        LogHelper.info("    target " + this.targetBlock);


        //if (this.theVillager.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0D)
        if (this.theVillager.getDistance(this.destinationBlock.getX(), this.theVillager.getPosition().getY(), this.destinationBlock.getZ()) > 2.0D) // attempt to ignore Y
        {
            this.arrived = false;
            this.movingTimer++;
            
            if (this.movingTimer % 40 == 0)
            {
                this.theVillager.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY(), this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
            }
            if (this.movingTimer > 1200) {
                
            }

        }
        else
        {
            this.arrived = true;
            if (this.movingTimer > 0) this.movingTimer--;
            
            // Look at the furnace
            this.theVillager.getLookHelper().setLookPosition(this.targetBlock.getX() + 0.5D, (this.targetBlock.getY() + 1), this.targetBlock.getZ() + 0.5D, 10.0F, this.theVillager.getVerticalFaceSpeed());
            
            // Start the counter to watch the furnace
            if (this.action == 0) {
                this.action = 1;
                this.watchingBurnTimer = maxCookingTime;
                this.lastTargetBlock = this.targetBlock;
                
                // Use the furnace
                if (this.theWorld.getBlockState(this.targetBlock).getBlock() != Blocks.furnace) {
                    // not a furnace anymore, decrease timer
                    this.watchingBurnTimer = 10;                    
                }
                else if (!this.addItemsToFurnace(this.targetBlock)) {
                    // could not add items, decrease timer
                    this.watchingBurnTimer = this.theVillager.getRNG().nextInt(30) + 40;
                }
                else {
                    //this.theVillager.playSound("mob.villager.haggle", 1.0F, 1.0F);
                }
                
            }
            
        }
        
        if (this.action == 1) {
            this.watchingBurnTimer--;

            // removes the cooked charcoal
            if (this.watchingBurnTimer <= 0) {
                this.removeItemsFromFurnace(this.targetBlock);
            }
            
        }
        

        /*
         *  NOTE: Since I have to add an item to make the furnace work, I try to remove it so
         *  the furnace gets empty again and the player can't "farm" the villager for charcoal.
         *  
         *  A player still could add a hopper below the furnace and take the charcoal, but 
         *  even so, the amount produced is neglectable.
         *  
         *  I made sure to only add items when the furnace is completely empty and only remove 
         *  when the furnace just have the 1 cooked charcoal on the result slot, that way the 
         *  villager can't mess up player furnaces.
         */

    }
 
    
    
    private boolean arrived = false;
    
    private final int rangeHorizontal = 12;
    private final int rangeVertical = 3;
    private final int attemptsToFindTarget = 30;

    
    
    
    private boolean hasTarget()
    {
        Vec3 vec = this.findAvailableFurnace();
        
        if (vec == null)
        {
            LogHelper.info("    I have no target - Villager " + this.theVillager);
            return false;
        }
        else
        {
            LogHelper.info("    I have a valid target at - Villager " + this.theVillager);
            this.destinationBlock = new BlockPos(vec);
            return true;
        }
    }
    
    
    private Vec3 findAvailableFurnace()
    {
        BlockPos villagerPos = new BlockPos(this.theVillager.posX, this.theVillager.getEntityBoundingBox().minY, this.theVillager.posZ);

        
        // This check all blocks, more reliable, but will cause lag?
        // Using a spiral algorithm (ref - stackoverflow.com/questions/398299/looping-in-a-spiral)
        int x = 0, z = 0, dx = 0, dz = -1;
        int swap;
        
        for (int i = 0; i < (rangeHorizontal * rangeHorizontal); i++)
        {
            if ((-rangeHorizontal/2 < x) && (x <= rangeHorizontal/2) && (-rangeHorizontal/2 < z) && (z <= rangeHorizontal/2)) {

                for (int y = 0; y < rangeVertical; y++)     // check a "column" of blocks
                {
                    // Check the given block
                    BlockPos blockpos = villagerPos.add(x, y, z);
                    if (lastTargetBlock != null && blockpos.distanceSq(lastTargetBlock) < 1) {
                        LogHelper.info("== Ignoring last target block ==");
                    }
                    else {
                        Vec3 destination = this.checkBlockAt(blockpos);
                        if (destination != null) {
                            this.targetBlock = blockpos;
                            return destination;
                        }
                    }
                }

            }
            
            if( (x == z) || ((x < 0) && (x == -z)) || ((x > 0) && (x == 1-z))) {
                swap = dx; 
                dx = -dz; 
                dz = swap;
            }   
            
            x += dx;
            z += dz;
        }
        

        
        /*
        // This will look randomly, hard to find a single block 
        Random random = this.theVillager.getRNG();
        BlockPos villagerPos = new BlockPos(this.theVillager.posX, this.theVillager.getEntityBoundingBox().minY, this.theVillager.posZ);

        for (int i = 0; i < attemptsToFindTarget; ++i)
        {
            BlockPos blockpos = villagerPos.add(random.nextInt(rangeHorizontal * 2) - rangeHorizontal, random.nextInt(rangeVertical + 1) - 1, random.nextInt(rangeHorizontal) - rangeHorizontal);
            Vec3 possibleTarget = this.checkBlockAt(blockpos);
            if (possibleTarget != null) { 
                return possibleTarget;
            }
        }
        */

        return null;
    }
    
    
    /**
     * Check if the block at the given position is of the valid type and 
     * if the block is a valid spot. 
     * 
     * @return The position facing the block.
     * 
     */
    protected Vec3 checkBlockAt(BlockPos pos) 
    {
        IBlockState state = this.theWorld.getBlockState(pos);
        Block block = state.getBlock();

        LogHelper.info("    Looking at: " + pos + ": " + block);
        

        // Looks for a furnace
        if (block == Blocks.furnace) {
            EnumFacing facing = (EnumFacing)state.getValue(BlockFurnace.FACING);

            LogHelper.info("Found a furnace @ " + pos + " facing " + facing);
            

            // TODO: check hopper code for optimization
            // return func_145893_b(this.getWorld(), (double)(this.pos.getX() + enumfacing.getFrontOffsetX()), (double)(this.pos.getY() + enumfacing.getFrontOffsetY()), (double)(this.pos.getZ() + enumfacing.getFrontOffsetZ()));
            
            // Check if the block in front (of the face) is air 
            if (facing == EnumFacing.NORTH) {
                return checkBlockInFront(pos.north());
            }
            else if (facing == EnumFacing.SOUTH) {
                return checkBlockInFront(pos.south());
            }
            else if (facing == EnumFacing.WEST) {
                return checkBlockInFront(pos.west());
            }
            else if (facing == EnumFacing.EAST) {
                return checkBlockInFront(pos.east());
            }
            
            
        }
        
        return null;
    }
    
    
    /**
     * Used to check the block in front of the target, to see if the villager can stand there.
     * 
     */
    protected Vec3 checkBlockInFront(BlockPos pos) {
        if (this.theWorld.isAirBlock(pos)) {
            return new Vec3((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());    
        }
        
        return null;
    }
    
    
    
    
    /**
     * Adds one log and 1 charcoal to use the furnace.
     * 
     */
    private boolean addItemsToFurnace(BlockPos pos) 
    {
        TileEntity tileentity = this.theWorld.getTileEntity(pos);
        
        if (tileentity instanceof TileEntityFurnace) {
            TileEntityFurnace tileFurnace = (TileEntityFurnace) tileentity; 
            ItemStack source = tileFurnace.getStackInSlot(0);
            ItemStack fuel = tileFurnace.getStackInSlot(1);
            ItemStack result = tileFurnace.getStackInSlot(2);
            
            
            LogHelper.info("Adding items to furnace at: " + pos);
            LogHelper.info("    source: " + source);
            LogHelper.info("    fuel: " + fuel);
            LogHelper.info("    result: " + result);
            
            
            // check if the furnace is empty
            if (source == null && fuel == null && result == null)
            {
                // furnace empty, adds item
                tileFurnace.setInventorySlotContents(0, new ItemStack(Blocks.log, 1, 0));
                tileFurnace.setInventorySlotContents(1, new ItemStack(Items.coal, 1, 1));
                
                this.theVillager.playSound("mob.villager.yes", 1.0F, 1.0F);
                
                this.canClearFurnace = true;
                
                return true;
            }
            else 
            {
                // Furnace has items, can't add
                this.theVillager.playSound("mob.villager.no", 0.9F, 0.7F);
                
                return false;
            }
            
        } 
        
        return false;
    }
    
    /**
     * Removes the cooked charcoal from the furnace.
     * 
     */
    private void removeItemsFromFurnace(BlockPos pos) 
    {
        if (!this.canClearFurnace) return;
        
        TileEntity tileentity = this.theWorld.getTileEntity(pos);
        
        if (tileentity instanceof TileEntityFurnace) {
            TileEntityFurnace tileFurnace = (TileEntityFurnace) tileentity; 
            ItemStack source = tileFurnace.getStackInSlot(0);
            ItemStack fuel = tileFurnace.getStackInSlot(1);
            ItemStack result = tileFurnace.getStackInSlot(2);
            

            LogHelper.info("Removing items from furnace at: " + pos);
            LogHelper.info("    source: " + source);
            LogHelper.info("    fuel: " + fuel);
            LogHelper.info("    result: " + result);
            /*
            if (result != null) {
                LogHelper.info("    result 2: " + (result == new ItemStack(Items.coal, 1, 1)));
                LogHelper.info("    result 3: " + (result.isItemEqual(new ItemStack(Items.coal, 1, 1))));
                LogHelper.info("    result 4: " + (result.isItemEqual(new ItemStack(Items.coal, 5, 1))));
                LogHelper.info("    result 4: " + (result(new ItemStack(Items.coal, 5, 1))));
            }
            */

            
            
            // check if the furnace only have 1 coal on the result and remove it
            if (source == null && fuel == null && result != null && 
                    result.isItemEqual(new ItemStack(Items.coal, 1, 1)) &&
                    result.stackSize == 1)
            {
                // furnace empty, adds item
                tileFurnace.setInventorySlotContents(2, null);
            }
        } 

    }
    

}
