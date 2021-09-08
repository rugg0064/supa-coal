package net.rugg0064.my.cool.mod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

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
        this(syncId, playerInventory, new SimpleInventory(3));
        size = buf.readInt();
        this.inventory = new SimpleInventory(1 + (size * size));
        this.generateSlots(size, playerInventory);
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public BoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        super(MyCoolMod.BOX_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        //some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);

        if (inventory.size() == 3)
        {
            //If we are here then it means the client must have called the constructor
        } else
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
        //this.addSlot(new OvenBrickOutputSlot(inventory, 0, 152, 18));
        this.addSlot(new Slot(inventory, 0, 152, 18));

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                this.addSlot(new OvenBrickCoalSlot(inventory, 1 + i + (j * size), 8 + (j * 18), 18 + (i * 18)));
            }
        }

        //System.out.println(playerInventory.size());

        //Player inventory
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, 9 + j + i * 9, 8 + j * 18, 158 + i * 18));
            }
        }
        //Hotbar
        for (int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 216));
        }
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slot)
    {
        System.out.println("2 tried to insert into " + slot);
        return super.canInsertIntoSlot(stack, slot);
    }

    public boolean canInsertIntoSlot(Slot slot)
    {
        System.out.println("tried to insert into " + slot);
        return false;
        //return super.canInsertIntoSlot(slot);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        //System.out.println("onSlotClick");
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast)
    {
        System.out.printf("Starting a insert from %d to %d, backwards? %b%n", startIndex, endIndex, fromLast);
        int trueStartIndex = fromLast ? endIndex : startIndex;
        boolean changedSize = false;
        System.out.println("Starting the fill attempt");
        //First, go through and fill any existing stacks
        for(int i = trueStartIndex; stack.getCount() != 0 && (fromLast ? (i >= startIndex) : (i <= endIndex)); i += fromLast ? -1 : 1)
        {

            System.out.println("Index; " + i);
            System.out.println("stack count = " + stack.getCount());
            System.out.println("stack condition: " + (stack.getCount() != 0));
            System.out.println("Condition: " + (fromLast ? (i >= startIndex) : (i <= endIndex)));
            System.out.println("Whole condition: " + (stack.getCount() != 0 && (fromLast ? (i >= startIndex) : (i <= endIndex))));
            Slot curSlot = this.slots.get(i);
            ItemStack curItemStack = curSlot.getStack();
            if (!curItemStack.isEmpty() && ItemStack.canCombine(stack, curItemStack))
            {
                int maxStack = curItemStack.getMaxCount();
                int maxPossibleMove = maxStack - curItemStack.getCount();
                int whatsLess = maxPossibleMove < stack.getCount() ? maxPossibleMove : stack.getCount();
                if (whatsLess > 0)
                {
                    changedSize = true;
                }
                curItemStack.setCount(curItemStack.getCount() + whatsLess);
                stack.setCount(stack.getCount() - whatsLess);
            }
        }
        System.out.println("Current stackcount: " + stack.getCount());
        //If still not empty after combining stacks, try looking for an empty slot
        if(!stack.isEmpty())
        {
            System.out.println("Looking for an empty slot");
            for(int i = trueStartIndex; stack.getCount() != 0 && (fromLast ? (i >= startIndex) : (i <= endIndex)); i += fromLast ? -1 : 1)
            {
                System.out.println(stack.getCount());
                System.out.println("Checking: " + i);
                System.out.println("Condition: " + (fromLast ? i >= startIndex : i <= endIndex));
                Slot curSlot = this.slots.get(i);
                ItemStack curItemStack = curSlot.getStack();
                System.out.println("Is the original slot empty? " + stack.isEmpty());
                System.out.println("Is the current slot empty? " + curItemStack.isEmpty());
                if(curItemStack.isEmpty() && curSlot.canInsert(stack))
                {
                    curSlot.setStack(stack.copy());
                    stack.setCount(0);
                    changedSize = true;
                }
            }
        }

        return changedSize;
    }

    //Shift + click
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        ItemStack newStack = ItemStack.EMPTY;
        //Get the Slot object at invSlot index
        Slot slot = this.slots.get(invSlot);
        //Null if it's an invalid slot, hasStack means if there is an item there
        if (slot != null && slot.hasStack())
        {
            ItemStack originalStack = slot.getStack();
            boolean moveIntoBlock = invSlot >= this.inventory.size();
            if (moveIntoBlock)
            {
                this.insertItem(originalStack, 1, this.inventory.size() - 1, false);
            } else
            {
                this.insertItem(originalStack, this.inventory.size(), this.slots.size() - 1, true);
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

