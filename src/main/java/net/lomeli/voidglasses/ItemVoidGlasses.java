package net.lomeli.voidglasses;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemVoidGlasses extends ItemArmor {
    private static final ArmorMaterial material = EnumHelper.addArmorMaterial("glasses", VoidGlasses.MOD_ID + ":voidglasses", -1, new int[]{0, 0, 0, 0}, 0);

    public ItemVoidGlasses() {
        super(material, 0, 0);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setUnlocalizedName(VoidGlasses.MOD_ID + ".glasses");
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        for (int i = 0; i < ClientProxy.shaderList.size(); i++) {
            ItemStack stack = new ItemStack(itemIn);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("shaderIndex", i);
            stack.setTagCompound(tag);
            subItems.add(stack);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        int i = getShaderFromStack(stack);
        if (i != -1 && i < ClientProxy.shaderList.size()) {
            Object shader = ClientProxy.shaderList.get(i);
            if (shader != null)
                tooltip.add(String.format(StatCollector.translateToLocal("voidglasses.shader"), shader.toString()));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return VoidGlasses.MOD_ID + ":textures/glasses.png";
    }

    public static int getShaderFromStack(ItemStack stack) {
        if (stack != null && stack.getItem() != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("shaderIndex"))
            return stack.getTagCompound().getInteger("shaderIndex");
        return -1;
    }
}
