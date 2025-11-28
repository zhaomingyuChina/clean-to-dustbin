package com.example.cleantodustbin.dustbin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class DustbinInventory extends SimpleContainer {
    
    public DustbinInventory(int size) {
        super(size);
    }
    
    /**
     * 插入物品到Inventory
     * @param stack 要插入的物品栈
     * @return 未插入的物品栈
     */
    public ItemStack insertItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack toInsert = stack.copy();
        
        // 尝试合并到现有物品栈
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack cur = this.getItem(i);
            if (!cur.isEmpty() && ItemStack.isSameItemSameTags(cur, toInsert)) {
                int canMove = Math.min(cur.getMaxStackSize() - cur.getCount(), toInsert.getCount());
                if (canMove > 0) {
                    cur.grow(canMove);
                    toInsert.shrink(canMove);
                    this.setChanged();
                    if (toInsert.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        
        // 尝试放入空格子
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack cur = this.getItem(i);
            if (cur.isEmpty()) {
                int move = Math.min(toInsert.getCount(), toInsert.getMaxStackSize());
                ItemStack put = toInsert.copy();
                put.setCount(move);
                this.setItem(i, put);
                toInsert.shrink(move);
                this.setChanged();
                if (toInsert.isEmpty()) return ItemStack.EMPTY;
            }
        }
        
        return toInsert;
    }
    
    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        // 最后一行的特殊位置用于放置按钮，不允许玩家放置物品
        // 3号槽位：上一页按钮（索引48）
        // 4号槽位：告示牌（索引49）
        // 5号槽位：下一页按钮（索引50）
        if (index == 48 || index == 49 || index == 50) {
            return false;
        }
        return true;
    }
}