package com.example.cleantodustbin;

import com.example.cleantodustbin.config.ModConfig;
import com.example.cleantodustbin.dustbin.DustbinManager;
import com.example.cleantodustbin.command.DustbinCommand;
import com.example.cleantodustbin.cleanup.CleanupScheduler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("cleantodustbin")
public class CleanToDustbin {
    
    public static final String MODID = "cleantodustbin";
    public static CleanToDustbin instance;
    private DustbinManager dustbinManager;
    private CleanupScheduler cleanupScheduler;
    
    public CleanToDustbin() {
        instance = this;
        
        // Register config
        ModLoadingContext.get().registerConfig(Type.SERVER, ModConfig.SPEC);
        
        // Get event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        
        // Initialize managers
        dustbinManager = new DustbinManager();
        cleanupScheduler = new CleanupScheduler(dustbinManager);
        
        // Register events
        forgeEventBus.register(this);
        forgeEventBus.register(cleanupScheduler);
        
        // Log mod initialization
        System.out.println("Clean To Dustbin mod initialized!");
    }
    
    /**
     * 注册命令
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DustbinCommand.register(event.getDispatcher());
    }
    
    public DustbinManager getDustbinManager() {
        return dustbinManager;
    }
    
    public CleanupScheduler getCleanupScheduler() {
        return cleanupScheduler;
    }
}