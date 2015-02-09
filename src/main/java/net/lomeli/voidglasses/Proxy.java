package net.lomeli.voidglasses;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;

import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Proxy {
    public void preInit() {
        VoidGlasses.versionChecker = new VersionChecker();
        if (VoidGlasses.checkForUpdates)
            VoidGlasses.versionChecker.checkForUpdates();
        ClientProxy.shaderList = new ArrayList<ResourceLocation>();
        VoidGlasses.glasses = new ItemVoidGlasses();
        GameRegistry.registerItem(VoidGlasses.glasses, "voidGlasses");
    }

    public void init() {
        for (int i = 0; i < ClientProxy.shaderList.size(); i++) {
            ItemStack stack = new ItemStack(VoidGlasses.glasses);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("shaderIndex", i);
            stack.setTagCompound(tag);
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(stack, 0, 1, 2));
            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(stack, 0, 1, 2));
            ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(stack, 0, 1, 2));
            ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(stack, 0, 1, 2));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(new WeightedRandomChestContent(stack, 0, 1, 2));
        }
    }
}
