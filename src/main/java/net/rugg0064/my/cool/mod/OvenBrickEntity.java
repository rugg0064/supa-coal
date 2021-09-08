package net.rugg0064.my.cool.mod;

import jdk.jshell.spi.ExecutionControl;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutput;

public class OvenBrickEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Inventory, SidedInventory
{
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private boolean validConfiguration;
    //Note: you can ONLY assume parent is valid, if isValidConfiguration == true
    OvenBrickEntity parent;
    BlockPos parentPos;

    //private OvenBrickEntity parent;
    private int size;

    public OvenBrickEntity(BlockPos pos, BlockState state)
    {
        super(MyCoolMod.OVEN_BRICK_ENTITY, pos, state);
        validConfiguration = false;
        this.parent = null;
    }

    public static void tick(World world, BlockPos pos, BlockState state, OvenBrickEntity be)
    {
        //System.out.println(be.getWorld().getBlockEntity(be.parentPos) instanceof OvenBrickEntity);
        //System.out.println(this.getWorld().getBlockEntity(parentPos));
    }

    public void spewContents()
    {

    }

    public void setSize(int newSize)
    {
        this.size = newSize;
        this.spewContents();
        this.inventory = DefaultedList.ofSize(1 + (size*size), ItemStack.EMPTY);
        markDirty();
    }
    public int getSize()
    {
        return this.size;
    }
    public boolean isValidConfiguration()
    {
        return validConfiguration;
    }
    public void setValidConfiguration(boolean value)
    {
        this.validConfiguration = value;
    }

    public OvenBrickEntity getParent()
    {
        if(this.parent == null)
        {
            if(this.getWorld() != null)
            {
                BlockEntity ent = this.getWorld().getBlockEntity(parentPos);
                if(ent instanceof OvenBrickEntity)
                {
                    this.parent = (OvenBrickEntity) ent;
                    this.inventory = this.parent.inventory;
                }
                else
                {
                    this.parent = null;
                }
            }
        }
        return this.parent;
    }

    public BlockPos getParentPos()
    {
        return this.parentPos;
    }
    public void setParentPos(BlockPos newPos)
    {
        this.parentPos = newPos;
        this.getParent();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        //System.out.println("readNbt called");
        //System.out.println(this);
        super.readNbt(nbt);
        //System.out.println(this.inventory.size());

        if(nbt.get("hasParent") != null)
        {
            if(nbt.getBoolean("hasParent"))
            {
                this.parentPos = new BlockPos( nbt.getInt("parentX"), nbt.getInt("parentY"), nbt.getInt("parentZ") );
            }
        }

        if(nbt.get("validConfiguration") != null)
        {
            this.validConfiguration = nbt.getBoolean("validConfiguration");
        }

        if(nbt.get("multiBlockSize") != null)
        {
            //System.out.println("Has multiBlockSize");
            this.setSize(nbt.getInt("multiBlockSize"));
        }
        else
        {
            //System.out.println("Didn't set size, multiBlockSize wasn't found");
        }
        System.out.println("ASD!: " + this.parentPos);
        if(this.parentPos != null)
        {
            System.out.println("parent pos is not null");
            System.out.printf("%s %s%n", this.world, this.getWorld());
            this.getParent();
            /*
            if(this.getWorld() != null)
            {
                System.out.println("The world is not null");
                BlockEntity parentEntity = this.world.getBlockEntity(this.parentPos);
                System.out.println(parentEntity);
                if(parentEntity != null && parent instanceof OvenBrickEntity)
                {
                    this.parent = (OvenBrickEntity) parentEntity;
                    this.inventory = this.parent.inventory;
                    System.out.println(this.parent);
                }
            }
            */
        }
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        //System.out.println("writeNbt called");
        //System.out.println(this);
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        //System.out.println(this.inventory.size());
        nbt.putInt("multiBlockSize", size);
        nbt.putBoolean("validConfiguration", validConfiguration);

        nbt.putBoolean("hasParent", parentPos != null);
        if(parentPos != null)
        {
            nbt.putInt("parentX", parentPos.getX());
            nbt.putInt("parentY", parentPos.getY());
            nbt.putInt("parentZ", parentPos.getZ());
        }

        //System.out.println("Current size: " + size);
        //System.out.println(nbt);
        return nbt;
    }

    @Override
    public int[] getAvailableSlots(Direction side)
    {
        if(this.getParent() != this)
        {
            return getParent().getAvailableSlots(side);
        }
        else
        {
            if(side == Direction.DOWN)
            {
                return new int[]{0};
            }
            else
            {
                int[] outInt = new int[size*size];
                for(int i = 0; i < outInt.length; i++)
                {
                    outInt[i] = i + 1;
                }
                return outInt;
            }
        }

    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
    {
        return slot != 0 && this.isValidConfiguration() && stack.getItem() == Items.COAL;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir)
    {
        return dir == Direction.DOWN && this.isValidConfiguration() && stack.getItem() == MyCoolMod.COAL_COKE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BoxScreenHandler(syncId, inv, this);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(size);
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int size()
    {
        if(this.getParent() != this)
        {
            return this.getParent().size();
        }
        else
        {
            return getItems().size();
        }
    }

    @Override
    public boolean isEmpty()
    {
        if(this.getParent() != this)
        {
            return this.getParent().isEmpty();
        }
        else
        {
            for (int i = 0; i < size(); i++)
            {
                ItemStack stack = getStack(i);
                if (!stack.isEmpty())
                {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public ItemStack getStack(int slot)
    {
        if(this.getParent() != this)
        {
            return this.getParent().getStack(slot);
        }
        else
        {
            return getItems().get(slot);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        if(this.getParent() != this)
        {
            return this.getParent().removeStack(slot, amount);
        }
        else
        {
            ItemStack result = Inventories.splitStack(getItems(), slot, amount);
            if (!result.isEmpty())
            {
                markDirty();
            }
            return result;
        }
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        if(this.getParent() != this)
        {
            return this.getParent().removeStack(slot);
        }
        else
        {
            return Inventories.removeStack(getItems(), slot);
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        if(this.getParent() != this)
        {
            this.getParent().setStack(slot, stack);
        }
        else
        {
            getItems().set(slot, stack);
            if (stack.getCount() > getMaxCountPerStack())
            {
                stack.setCount(getMaxCountPerStack());
            }
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        //Yes this one doesnt need the parenting but whatever
        if(this.getParent() != this)
        {
            return this.getParent().canPlayerUse(player);
        }
        else
        {
            return true;
        }
    }

    @Override
    public void clear()
    {
        if(this.getParent() != this)
        {
            this.getParent().clear();
        }
        else
        {
            getItems().clear();
        }

    }
}
