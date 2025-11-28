package com.example.cleantodustbin.cleanup;

import com.example.cleantodustbin.config.ModConfig;
import com.example.cleantodustbin.dustbin.DustbinManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.entity.Entity;

public class CleanupScheduler {
    
    private final DustbinManager dustbinManager;
    private final Timer timer;
    private long lastCleanupTime;
    private long cleanupInterval;
    private boolean isReminding;
    private int reminderCount;
    
    // 提醒时间点（秒）
    private static final int[] REMINDER_TIMES = {60, 30, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    
    public CleanupScheduler(DustbinManager dustbinManager) {
        this.dustbinManager = dustbinManager;
        this.timer = new Timer("CleanupScheduler", true);
        this.lastCleanupTime = System.currentTimeMillis();
        this.cleanupInterval = ModConfig.CLEANUP_INTERVAL.get() * 1000L;
        this.isReminding = false;
        this.reminderCount = 0;
        
        // 启动定时任务
        startCleanupTask();
    }
    
    /**
     * 启动清理任务
     */
    private void startCleanupTask() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkCleanup();
            }
        }, 0, 1000L); // 每秒检查一次
    }
    
    /**
     * 检查是否需要执行清理
     */
    private void checkCleanup() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastCleanup = currentTime - lastCleanupTime;
        
        // 检查是否需要开始提醒
        if (!isReminding) {
            long timeUntilCleanup = cleanupInterval - timeSinceLastCleanup;
            for (int remindTime : REMINDER_TIMES) {
                if (timeUntilCleanup <= remindTime * 1000L && timeUntilCleanup > (remindTime - 1) * 1000L) {
                    sendReminder(remindTime);
                    isReminding = true;
                    reminderCount = 1;
                    break;
                }
            }
        } 
        // 继续发送后续提醒
        else if (reminderCount < REMINDER_TIMES.length) {
            long timeUntilCleanup = cleanupInterval - timeSinceLastCleanup;
            int nextRemindTime = REMINDER_TIMES[reminderCount];
            if (timeUntilCleanup <= nextRemindTime * 1000L && timeUntilCleanup > (nextRemindTime - 1) * 1000L) {
                sendReminder(nextRemindTime);
                reminderCount++;
            }
        }
        
        // 检查是否需要执行清理
        if (timeSinceLastCleanup >= cleanupInterval) {
            executeCleanup();
            lastCleanupTime = currentTime;
            isReminding = false;
            reminderCount = 0;
        }
    }
    
    /**
     * 发送清理提醒
     * @param seconds 剩余秒数
     */
    private void sendReminder(int seconds) {
        String message = String.format(ModConfig.REMINDER_MESSAGE.get(), formatTime(seconds));
        broadcastMessage(message);
    }
    
    /**
     * 执行清理操作
     */
    private void executeCleanup() {
        int totalItems = 0;
        
        // 获取所有服务器世界
        Iterable<ServerLevel> levels = ServerLifecycleHooks.getCurrentServer().getAllLevels();
        
        for (ServerLevel level : levels) {
            // 获取所有掉落物实体
            List<ItemEntity> itemEntities = new ArrayList<>();
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    itemEntities.add((ItemEntity) entity);
                }
            }
            
            for (ItemEntity itemEntity : itemEntities) {
                // 将物品添加到垃圾桶
                dustbinManager.addItem(itemEntity.getItem());
                totalItems++;
                
                // 移除掉落物实体
                itemEntity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
        
        // 发送清理完成通知
        String message = String.format(ModConfig.CLEANUP_NOTIFICATION.get(), totalItems);
        broadcastMessage(message);
    }
    
    /**
     * 广播消息给所有玩家
     * @param message 要广播的消息
     */
    private void broadcastMessage(String message) {
        Component component = Component.literal(message);
        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(component, false);
    }
    
    /**
     * 格式化时间显示
     * @param seconds 秒数
     * @return 格式化后的时间字符串
     */
    private String formatTime(int seconds) {
        if (seconds >= 60) {
            return seconds / 60 + "分钟";
        } else if (seconds >= 30) {
            return "30秒";
        } else {
            return seconds + "秒";
        }
    }
    
    /**
     * 处理服务器tick事件，用于更新配置
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // 检查配置是否有变化
            long newInterval = ModConfig.CLEANUP_INTERVAL.get() * 1000L;
            if (newInterval != cleanupInterval) {
                cleanupInterval = newInterval;
            }
        }
    }
    
    /**
     * 关闭调度器
     */
    public void shutdown() {
        timer.cancel();
    }
}