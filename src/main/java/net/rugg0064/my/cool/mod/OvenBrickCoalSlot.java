package net.rugg0064.my.cool.mod;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class OvenBrickCoalSlot extends Slot
{
    public OvenBrickCoalSlot(Inventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        return stack.getItem() == Items.COAL;
    }

}
