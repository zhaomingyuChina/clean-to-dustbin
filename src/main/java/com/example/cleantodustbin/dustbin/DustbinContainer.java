package com.example.cleantodustbin.dustbin;

import com.example.cleantodustbin.CleanToDustbin;
import com.example.cleantodustbin.command.DustbinCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class DustbinContainer extends AbstractContainerMenu {

    private final DustbinInventory dustbinInventory;
    private final int currentPage;
    private final int maxPages;

    public DustbinContainer(int containerId, Inventory playerInventory, DustbinInventory dustbinInventory, int currentPage, int maxPages) {
        super(MenuType.GENERIC_9x6, containerId);
        this.dustbinInventory = dustbinInventory;
        this.currentPage = currentPage;
        this.maxPages = maxPages;

        // 添加垃圾桶的槽位（6行9列）
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(dustbinInventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // 添加玩家背包的槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        // 添加玩家快捷栏的槽位
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 198));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.dustbinInventory.stillValid(player);
    }

    @Override
    public void clicked(int slotId, int mouseButton, ClickType clickType, Player player) {
        // 检查点击的是否是按钮槽位
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            // 检查是否是垃圾桶的槽位（前54个槽位）
            if (slot.container == this.dustbinInventory) {
                int dustbinSlotId = slot.getContainerSlot();
                // 处理上一页按钮（索引48）
                if (dustbinSlotId == 48 && currentPage > 0) {
                    // 取消事件，防止物品被移动
                    // 切换到上一页
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        com.example.cleantodustbin.command.DustbinCommand.openDustbinGUI(serverPlayer, currentPage - 1);
                    }
                    return;
                }
                // 处理下一页按钮（索引50）
                else if (dustbinSlotId == 50 && currentPage < maxPages - 1) {
                    // 取消事件，防止物品被移动
                    // 切换到下一页
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        com.example.cleantodustbin.command.DustbinCommand.openDustbinGUI(serverPlayer, currentPage + 1);
                    }
                    return;
                }
                // 处理告示牌槽位（索引49）
                else if (dustbinSlotId == 49) {
                    // 取消事件，防止物品被移动
                    return;
                }
            }
        }

        // 调用父类方法处理其他点击
        super.clicked(slotId, mouseButton, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 从垃圾桶移动到玩家背包
            if (index < 54) {
                if (!this.moveItemStackTo(itemstack1, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 从玩家背包移动到垃圾桶
            else if (!this.moveItemStackTo(itemstack1, 0, 54, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
