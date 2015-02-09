package net.lomeli.voidglasses;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends Proxy {
    private boolean shaderActivated;
    private int setIndex = -1;
    public static List<ResourceLocation> shaderList;

    @Override
    public void preInit() {
        super.preInit();
        if (!OpenGlHelper.areShadersSupported())
            Logger.logWarning(StatCollector.translateToLocal("voidglasses.shadernotsupported"));
        for (ResourceLocation shader : EntityRenderer.shaderResourceLocations) {
            shaderList.add(shader);
        }
        //populateList();
    }

    @Override
    public void init() {
        super.init();
        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(VoidGlasses.versionChecker);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(VoidGlasses.glasses, 0, new ModelResourceLocation("voidglasses:voidGlasses", "inventory"));
    }

    public void populateList() {
        File modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
        File shaderFolder = new File(modsFolder, "vg_shaders");
        if (shaderFolder.exists()) {
            File[] folderFiles = shaderFolder.listFiles();
            if (folderFiles != null && folderFiles.length > 0) {
                for (File file : folderFiles) {
                    if (file != null && FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("json"))
                        shaderList.add(new ResourceLocation(file.getAbsolutePath()));
                }
            }
        } else
            shaderFolder.mkdir();
    }

    public void activateShader(int shaderIndex) {
        if (OpenGlHelper.areShadersSupported() && shaderIndex < EntityRenderer.shaderResourceLocations.length) {
            if (Minecraft.getMinecraft().entityRenderer.theShaderGroup != null)
                Minecraft.getMinecraft().entityRenderer.theShaderGroup.deleteShaderGroup();
            Minecraft.getMinecraft().entityRenderer.theShaderGroup = null;
            ResourceLocation shader = shaderList.get(shaderIndex);
            if (shader != null) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(String.format(StatCollector.translateToLocal("voidglasses.shaderActivate"), shader.toString())));
                Minecraft.getMinecraft().entityRenderer.loadShader(shader);
                shaderActivated = true;
                setIndex = shaderIndex;
            } else {
                shaderActivated = false;
                setIndex = -1;
            }
        }
    }

    public void disableShaders() {
        Minecraft.getMinecraft().entityRenderer.theShaderGroup = null;
        shaderActivated = false;
        setIndex = -1;
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player != null && event.side == Side.CLIENT && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            if (shaderActivated && Minecraft.getMinecraft().entityRenderer.theShaderGroup == null) {
                ResourceLocation shader = shaderList.get(setIndex);
                if (shader != null)
                    Minecraft.getMinecraft().entityRenderer.loadShader(shader);
            }

            ItemStack stack = player.inventory.getStackInSlot(39);
            if (stack != null && stack.getItem() != null) {
                if (stack.getItem() == VoidGlasses.glasses) {
                    int i = ItemVoidGlasses.getShaderFromStack(stack);
                    if (i != -1) {
                        if (shaderActivated) {
                            if (setIndex != i)
                                activateShader(i);
                        } else
                            activateShader(i);
                    } else if (shaderActivated)
                        disableShaders();
                }
            } else if (shaderActivated)
                disableShaders();
        }
    }
}
