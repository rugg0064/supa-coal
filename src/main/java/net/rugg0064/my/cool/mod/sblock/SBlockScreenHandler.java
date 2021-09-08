package net.rugg0064.my.cool.mod.sblock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.rugg0064.my.cool.mod.MyCoolMod;

public class SBlockScreenHandler extends ScreenHandler
{
    private Inventory inventory;

    public SBlockScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory( 10 ));
    }

    public SBlockScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory )
    {
        super(MyCoolMod.S_BLOCK_SCREEN_HANDLER, syncId);
        checkSize(inventory, 10);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0,  152, 18));
        int size = 3;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                this.addSlot(new Slot(inventory, 1 + j + (i * size), 8 + (i * 18), 18 + (j * 18)));
            }
        }

        System.out.println(playerInventory.size());

        //Player inventory
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, 9 + j + i * 9,  8 + j * 18, 158 + i * 18));
            }
        }
        //Hotbar
        for (int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 216));
        }
    }

    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack())
        {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size())
            {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false))
            {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty())
            {
                slot.setStack(ItemStack.EMPTY);
            } else
            {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
