package net.rugg0064.my.cool.mod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BoxScreenHandler extends ScreenHandler
{
    private Inventory inventory;
    public int size;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public BoxScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        //3 is an impossible value, so it is our marker to not generate in our shared constructor
        this(syncId, playerInventory, new SimpleInventory( 3 ));
        size = buf.readInt();
        this.inventory = new SimpleInventory(1 + (size*size));
        this.generateSlots(size, playerInventory);
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public BoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory )
    {
        super(MyCoolMod.BOX_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        //some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);

        if(inventory.size() == 3)
        {
            //If we are here then it means the client must have called the constructor
        }
        else
        {
            //If we are here then it means the server must have called the constructor
            int cubedSize = inventory.size() - 1;
            int size = (int) Math.sqrt(cubedSize);
            this.generateSlots(size, playerInventory);
        }
    }

    public void generateSlots(int size, PlayerInventory playerInventory)
    {
        //Entity's inventory
        //Place the output right away, as it is always the same position
        this.addSlot(new Slot(inventory, 0,  152, 18));

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

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
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

