package net.rugg0064.my.cool.mod.sblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.rugg0064.my.cool.mod.ImplementedInventory;
import net.rugg0064.my.cool.mod.MyCoolMod;

public class SBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory
{
    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);

    public SBlockEntity(BlockPos pos, BlockState state)
    {
        super(MyCoolMod.S_BLOCK_ENTITY, pos, state);
    }

    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new SBlockScreenHandler(syncId, playerInventory, this);
    }

    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    public DefaultedList<ItemStack> getItems()
    {
        return inventory;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        return nbt;
    }
}
