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
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutput;

public class OvenBrickEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory
{
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private boolean validConfiguration;
    //Note: you can ONLY assume parent is valid, if isValidConfiguration == true
    BlockPos parentPos;
    //private OvenBrickEntity parent;
    private int size;

    public OvenBrickEntity(BlockPos pos, BlockState state)
    {
        super(MyCoolMod.OVEN_BRICK_ENTITY, pos, state);
        validConfiguration = false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, OvenBrickEntity be)
    {

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

    public BlockPos getParentPos()
    {
        return this.parentPos;
    }
    public void setParentPos(BlockPos newPos)
    {
        this.parentPos = newPos;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        System.out.println("readNbt called");
        System.out.println(this);
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        System.out.println(this.inventory.size());

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
            System.out.println("Has multiBlockSize");
            this.size = nbt.getInt("multiBlockSize");
        }
        else
        {
            System.out.println("Didn't set size, multiBlockSize wasn't found");
        }

        if(this.inventory.size() != (1 + size*size))
        {
            this.setSize(size);
        }

        System.out.println("Current size: " + size);
        System.out.println(nbt);
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        System.out.println("writeNbt called");
        System.out.println(this);
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        System.out.println(this.inventory.size());
        nbt.putInt("multiBlockSize", size);
        nbt.putBoolean("validConfiguration", validConfiguration);

        nbt.putBoolean("hasParent", parentPos != null);
        if(parentPos != null)
        {
            nbt.putInt("parentX", parentPos.getX());
            nbt.putInt("parentY", parentPos.getY());
            nbt.putInt("parentZ", parentPos.getZ());
        }

        System.out.println("Current size: " + size);
        System.out.println(nbt);
        return nbt;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
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
}
