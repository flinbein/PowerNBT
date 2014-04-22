package me.dpohvar.powernbt.utils;

import org.bukkit.entity.Player;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public final class PacketUtils {

    public static final PacketUtils packetUtils = new PacketUtils();

    private PacketUtils(){}

    RefClass classCraftPlayer = getRefClass("{CraftPlayer}, {cb}.entity.CraftPlayer");
    RefClass classEntityPlayer = getRefClass("{EntityPlayer}, {nms}.EntityPlayer, {nm}.entity.player.EntityPlayer");
    RefClass classEntityPlayerMP = getRefClass("{EntityPlayerMP}, {nm}.entity.player.EntityPlayerMP, null");
    RefClass classNetworkElement = getRefClass(
            "{NetworkElement} {nms}.PlayerConnection, {nm}.network.NetServerHandler, {nm}.network.NetHandlerPlayServer"
    );

    RefField fieldNetworkElement = ( isForge()?classEntityPlayerMP:classEntityPlayer ).findField(classNetworkElement);
    RefMethod sendPacket = classNetworkElement.findMethodByParams("{Packet}, {nms}.Packet, {nm}.network.Packet");
    RefMethod getHandle = classCraftPlayer.findMethodByReturnType(classEntityPlayer);

    public void sendPacket(Player player, Object packet){
        Object entityPlayer = getHandle.of(player).call();
        Object network = fieldNetworkElement.of(entityPlayer).get();
        sendPacket.of(network).call(packet);
    }
}
