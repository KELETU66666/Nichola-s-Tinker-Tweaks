package keletu.tinkertweaks;

import keletu.tinkertweaks.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.IModifier;

import java.util.UnknownFormatConversionException;

@Mod.EventBusSubscriber(modid = KeletuTinkerTweaks.MODID)
public class CommonProxy {

  private static SoundEvent SOUND_LEVELUP = sound("levelup");

  public void playLevelupDing(EntityPlayer player) {
    Sounds.PlaySoundForPlayer(player, SOUND_LEVELUP, 1f, 1f);
  }

  public void sendStatsUpMessage(int level, ItemStack itemStack, EntityPlayer player) {
    ITextComponent textComponent;
	textComponent = new TextComponentString(TextFormatting.DARK_AQUA + String.format(Config.getStatsUpMessage() + TextFormatting.DARK_AQUA, level));
    player.sendStatusMessage(textComponent, false);
  }
  
  public void sendLevelUpMessage(int level, ItemStack itemStack, EntityPlayer player) {
    ITextComponent textComponent;
	if (Config.shouldUseConfigLevelupMessages()) {
		try {
			textComponent = new TextComponentString(TextFormatting.DARK_AQUA + String.format(Config.getLevelupMessage(level), itemStack.getDisplayName() + TextFormatting.DARK_AQUA, level));
		}
		catch (UnknownFormatConversionException e) {
			textComponent = new TextComponentString(TextFormatting.DARK_AQUA + String.format(Config.getGenericLevelupMessage(), itemStack.getDisplayName(), level) + TextFormatting.DARK_AQUA);
		}
	}
	else {
		// special message
		if(I18n.canTranslate("message.levelup." + level)) {
		  textComponent = new TextComponentString(TextFormatting.DARK_AQUA + I18n.translateToLocalFormatted("message.levelup." + level, itemStack.getDisplayName() + TextFormatting.DARK_AQUA));
		}
		// generic message
		else {
		  textComponent = new TextComponentString(TextFormatting.DARK_AQUA + I18n.translateToLocalFormatted("message.levelup.generic", itemStack.getDisplayName() + TextFormatting.DARK_AQUA, Tooltips.getLevelString(level)));
		}
	}
    player.sendStatusMessage(textComponent, false);
  }
  
  // Just like sendLevelUpMessage, except this message tells the player which modifier they got.
  public void sendModifierMessage(IModifier modifier, ItemStack itemStack, EntityPlayer player){
    ITextComponent textComponent;
	String pure_modifier = modifier.getLocalizedName();
	pure_modifier = pure_modifier.replaceAll(" ", "_");
	pure_modifier = pure_modifier.toLowerCase();
	if (Config.shouldUseConfigModifierMessages()) {
		textComponent = new TextComponentString(TextFormatting.DARK_AQUA + Config.getModifierMessage(pure_modifier));
	}
	else {
		// special message
		if(I18n.canTranslate("message.modifier." + pure_modifier)) {
		  textComponent = new TextComponentString(TextFormatting.DARK_AQUA + I18n.translateToLocalFormatted(("message.modifier." + pure_modifier), TextFormatting.DARK_AQUA));
		}
		// generic message
		else {
		  textComponent = new TextComponentString(TextFormatting.DARK_AQUA + I18n.translateToLocalFormatted("message.modifier.generic", itemStack.getDisplayName() + TextFormatting.DARK_AQUA));
		}
	}
    player.sendStatusMessage(textComponent, false);
  }
  
  private static SoundEvent sound(String name) {
    ResourceLocation location = new ResourceLocation(KeletuTinkerTweaks.MODID, name);
    SoundEvent event = new SoundEvent(location);
    event.setRegistryName(location);
    return event;
  }

  @SubscribeEvent
  public static void registerSoundEvent(RegistryEvent.Register<SoundEvent> event) {
    event.getRegistry().register(SOUND_LEVELUP);
  }
}
