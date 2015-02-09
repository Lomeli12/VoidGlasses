package net.lomeli.voidglasses;

import net.minecraft.item.Item;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = VoidGlasses.MOD_ID, name = VoidGlasses.MOD_NAME, version = VoidGlasses.VERSION)
public class VoidGlasses {
    public static final int MAJOR = 1, MINOR = 0, REV = 0;
    public static final String MOD_ID = "voidglasses";
    public static final String MOD_NAME = "Void Glasses";
    public static final String VERSION = MAJOR + "." + MINOR + "." + REV;

    public static Item glasses;
    public static VersionChecker versionChecker;
    public static boolean checkForUpdates;

    @SidedProxy(serverSide = "net.lomeli.voidglasses.Proxy", clientSide = "net.lomeli.voidglasses.ClientProxy")
    public static Proxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();
        checkForUpdates = configuration.getBoolean("checkForUpdates", Configuration.CATEGORY_GENERAL, true, "Check for updates?");
        configuration.save();
        Logger.logBasic(event.getSourceFile().getParentFile().toString());
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }
}
