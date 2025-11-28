package com.example.cleantodustbin.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // 清理周期（秒）
    public static final ForgeConfigSpec.IntValue CLEANUP_INTERVAL;
    
    // 垃圾桶页数
    public static final ForgeConfigSpec.IntValue DUSTBIN_PAGES;
    
    // 提醒文字模板
    public static final ForgeConfigSpec.ConfigValue<String> REMINDER_MESSAGE;
    
    // 清理通知模板
    public static final ForgeConfigSpec.ConfigValue<String> CLEANUP_NOTIFICATION;
    
    static {
        BUILDER.push("Clean To Dustbin Configuration");
        
        CLEANUP_INTERVAL = BUILDER
                .comment("清理周期，单位：秒")
                .defineInRange("cleanupInterval", 3600, 60, 86400);
        
        DUSTBIN_PAGES = BUILDER
                .comment("垃圾桶页数")
                .defineInRange("dustbinPages", 5, 1, 20);
        
        REMINDER_MESSAGE = BUILDER
                .comment("清理提醒消息模板，%s将被替换为剩余时间")
                .define("reminderMessage", "§e掉落物清理将在 %s 后开始！");
        
        CLEANUP_NOTIFICATION = BUILDER
                .comment("清理完成通知模板，%d将被替换为清理的物品数量")
                .define("cleanupNotification", "§a已清理 %d 个掉落物，使用 /ljt 打开垃圾桶查看！");
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}