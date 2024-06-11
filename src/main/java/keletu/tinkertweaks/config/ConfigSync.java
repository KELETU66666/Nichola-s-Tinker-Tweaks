package keletu.tinkertweaks.config;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigSync {

  @SubscribeEvent
  @SideOnly(Side.SERVER)
  public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if(event.player instanceof EntityPlayerMP && FMLCommonHandler.instance().getSide().isServer()) {
      ConfigSyncPacket packet = new ConfigSyncPacket();
      KeletuTinkerTweaks.networkWrapper.network.sendTo(packet, (EntityPlayerMP) event.player);
    }
  }

}
