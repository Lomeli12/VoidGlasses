package net.lomeli.voidglasses;

import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.Level;

import java.io.InputStreamReader;
import java.net.URL;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class VersionChecker {
    private boolean needsUpdate, isDirect, doneTelling;
    private final String updateJson = "https://raw.githubusercontent.com/Lomeli12/VoidGlasses/master/update.json";
    private String version, downloadURL, changeLog;

    public void checkForUpdates() {
        needsUpdate = false;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new URL(updateJson).openStream()));

            if (reader != null) {
                int major = 0, minor = 0, revision = 0;
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("major"))
                        major = reader.nextInt();
                    else if (name.equals("minor"))
                        minor = reader.nextInt();
                    else if (name.equals("revision"))
                        revision = reader.nextInt();
                    else if (name.equals("downloadURL"))
                        downloadURL = reader.nextString();
                    else if (name.equals("direct"))
                        isDirect = reader.nextBoolean();
                    else if (name.equals("changeLog"))
                        changeLog = reader.nextString();
                }
                reader.endObject();
                if (major > VoidGlasses.MAJOR)
                    needsUpdate = true;
                else if (major <= VoidGlasses.MAJOR) {
                    if (minor > VoidGlasses.MINOR)
                        needsUpdate = true;
                    else if (minor <= VoidGlasses.MINOR) {
                        if (revision > VoidGlasses.REV)
                            needsUpdate = true;
                    }
                }

                if (needsUpdate) {
                    version = major + "." + minor + "." + revision;
                    FMLLog.log(Level.INFO, translate("update.voidglasses.update"));
                    FMLLog.log(Level.INFO, translate("update.voidglasses.update.old") + " " + VoidGlasses.VERSION);
                    FMLLog.log(Level.INFO, translate("update.voidglasses.update.new") + " " + version);
                }
                reader.close();
            }
        } catch (Exception e) {
            Logger.logError("Failed to check for updates!");
        }
    }

    public void sendMessage() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("modDisplayName", VoidGlasses.MOD_NAME);
        tag.setString("oldVersion", VoidGlasses.VERSION);
        tag.setString("newVersion", version);
        tag.setString("updateUrl", downloadURL);
        tag.setBoolean("isDirectLink", isDirect);
        tag.setString("changeLog", changeLog);
        FMLInterModComms.sendMessage("VersionChecker", "addUpdate", tag);
    }

    public boolean needUpdate() {
        return needsUpdate;
    }

    public String translate(String unlocalized) {
        return StatCollector.translateToLocal(unlocalized);
    }

    @SubscribeEvent
    @SideOnly(CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (needUpdate() && event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null && !doneTelling) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (!version.isEmpty() && player != null) {
                player.addChatMessage(new ChatComponentTranslation("update.voidglasses.update"));
                player.addChatMessage(new ChatComponentText(translate("update.voidglasses.update.old") + " " + VoidGlasses.VERSION));
                player.addChatMessage(new ChatComponentText(translate("update.voidglasses.update.new") + " " + version));
                doneTelling = true;
            }
        }
    }
}
