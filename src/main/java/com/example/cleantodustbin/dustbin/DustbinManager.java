package com.example.cleantodustbin.dustbin;

import com.example.cleantodustbin.config.ModConfig;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class DustbinManager {
    
    private final List<DustbinInventory> dustbinPages;
    private final int maxPages;
    private int currentPage;
    
    public DustbinManager() {
        this.maxPages = ModConfig.DUSTBIN_PAGES.get();
        this.dustbinPages = new ArrayList<>(maxPages);
        this.currentPage = 0;
        
        // 初始化每一页，每页54个格子（6x9）
        for (int i = 0; i < maxPages; i++) {
            dustbinPages.add(new DustbinInventory(54));
        }
    }
    
    /**
     * 添加物品到垃圾桶
     * @param stack 要添加的物品栈
     */
    public void addItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        
        ItemStack remaining = stack.copy();
        
        // 尝试将物品添加到每一页
        for (DustbinInventory inventory : dustbinPages) {
            remaining = inventory.insertItem(remaining);
            if (remaining.isEmpty()) {
                break;
            }
        }
        
        // 如果还有剩余物品，替换最后一页的最后一个格子
        if (!remaining.isEmpty()) {
            DustbinInventory lastInventory = dustbinPages.get(dustbinPages.size() - 1);
            lastInventory.setItem(lastInventory.getContainerSize() - 1, remaining);
        }
    }
    
    /**
     * 获取当前页面的Inventory
     * @return 当前页面的Inventory
     */
    public DustbinInventory getCurrentInventory() {
        return dustbinPages.get(currentPage);
    }
    
    /**
     * 获取指定页面的Inventory
     * @param page 页面索引（从0开始）
     * @return 指定页面的Inventory
     */
    public DustbinInventory getInventory(int page) {
        if (page < 0 || page >= dustbinPages.size()) {
            return dustbinPages.get(0);
        }
        return dustbinPages.get(page);
    }
    
    /**
     * 切换到指定页面
     * @param page 页面索引（从0开始）
     */
    public void switchPage(int page) {
        if (page >= 0 && page < dustbinPages.size()) {
            this.currentPage = page;
        }
    }
    
    /**
     * 切换到下一页
     */
    public void nextPage() {
        if (currentPage < maxPages - 1) {
            currentPage++;
        }
    }
    
    /**
     * 切换到上一页
     */
    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }
    
    /**
     * 获取当前页码
     * @return 当前页码（从0开始）
     */
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * 获取垃圾桶总页数
     * @return 总页数
     */
    public int getMaxPages() {
        return maxPages;
    }
    
    /**
     * 清空垃圾桶
     */
    public void clear() {
        for (DustbinInventory inventory : dustbinPages) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }
}