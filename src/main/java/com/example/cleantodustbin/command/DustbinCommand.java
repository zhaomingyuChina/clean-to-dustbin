package com.example.cleantodustbin.command;

import com.example.cleantodustbin.CleanToDustbin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
public class DustbinCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ljt")
                .executes(DustbinCommand::openDustbin)
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(DustbinCommand::openDustbinWithPage))
                .then(Commands.literal("reload")
                        .requires(src -> src.hasPermission(2))
                        .executes(DustbinCommand::reloadConfig)));
    }
    
    private static int openDustbin(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            // 打开垃圾桶界面，默认第1页
            openDustbinGUI(player, 0);
            return 1;
        } else {
            source.sendFailure(Component.literal("只有玩家可以使用此命令！"));
            return 0;
        }
    }
    
    private static int openDustbinWithPage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            int page = IntegerArgumentType.getInteger(context, "page") - 1; // 转换为0-based索引
            openDustbinGUI(player, page);
            return 1;
        } else {
            source.sendFailure(Component.literal("只有玩家可以使用此命令！"));
            return 0;
        }
    }
    
    public static void openDustbinGUI(ServerPlayer player, int page) {
        // 获取垃圾桶管理器
        var dustbinManager = CleanToDustbin.instance.getDustbinManager();
        
        // 确保页码有效
        int maxPages = dustbinManager.getMaxPages();
        if (page < 0 || page >= maxPages) {
            page = 0;
        }
        
        // 切换到指定页面
        dustbinManager.switchPage(page);
        
        // 获取当前页面的Inventory
        var inventory = dustbinManager.getCurrentInventory();
        
        // 4号槽位：告示牌（索引49）- 任意木头告示牌
        ItemStack signItem = new ItemStack(net.minecraft.world.item.Items.OAK_SIGN);
        signItem.setHoverName(Component.literal("当前页码：" + (page + 1) + "/" + maxPages));
        inventory.setItem(49, signItem);
        
        // 根据当前页码显示或隐藏上一页和下一页按钮
        // 3号槽位：上一页按钮（索引48）- 红色玻璃板，第一页不显示
        if (page > 0) {
            ItemStack prevPageItem = new ItemStack(net.minecraft.world.item.Items.RED_STAINED_GLASS_PANE);
            prevPageItem.setHoverName(Component.literal("上一页"));
            inventory.setItem(48, prevPageItem);
        } else {
            inventory.setItem(48, ItemStack.EMPTY);
        }
        
        // 5号槽位：下一页按钮（索引50）- 红色玻璃板，最后一页不显示
        if (page < maxPages - 1) {
            ItemStack nextPageItem = new ItemStack(net.minecraft.world.item.Items.RED_STAINED_GLASS_PANE);
            nextPageItem.setHoverName(Component.literal("下一页"));
            inventory.setItem(50, nextPageItem);
        } else {
            inventory.setItem(50, ItemStack.EMPTY);
        }
        
        // 创建final变量，以便在lambda表达式中使用
        final int finalPage = page;
        final int finalMaxPages = maxPages;
        final var finalInventory = inventory;
        
        // 创建菜单提供者
        MenuProvider provider = new SimpleMenuProvider(
                (containerId, playerInv, p) -> {
                    // 使用我们自定义的容器
                    return new com.example.cleantodustbin.dustbin.DustbinContainer(containerId, playerInv, finalInventory, finalPage, finalMaxPages);
                },
                Component.literal("垃圾桶 - 第 " + (finalPage + 1) + "/" + finalMaxPages + " 页")
        );
        
        // 打开菜单
        player.openMenu(provider);
        
        // 发送提示消息
        player.sendSystemMessage(Component.literal("§a已打开垃圾桶第 " + (page + 1) + " 页！"));
        player.sendSystemMessage(Component.literal("§e点击玻璃板按钮切换页面，或使用 /ljt <页码> 命令"));
    }
    
    /**
     * 重新加载配置文件
     */
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            // 发送成功消息
            source.sendSuccess(() -> Component.literal("§a配置文件已成功重新加载！"), true);
            return 1;
        } catch (Exception e) {
            // 发送错误消息
            source.sendFailure(Component.literal("§c配置文件重新加载失败：" + e.getMessage()));
            return 0;
        }
    }
    
}