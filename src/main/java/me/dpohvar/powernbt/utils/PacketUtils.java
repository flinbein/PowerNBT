package me.dpohvar.powernbt.utils;

import org.bukkit.entity.Player;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public final class PacketUtils {

    public static final PacketUtils packetUtils = new PacketUtils();

    private PacketUtils(){}

    RefClass classCraftPlayer = getRefClass("{cb}.entity.CraftPlayer, {CraftPlayer}");
    RefClass classEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    RefClass classEntityPlayerMP = getRefClass("{nm}.entity.player.EntityPlayerMP, {EntityPlayerMP}, null");
    RefClass classNetworkElement = getRefClass(
            "{nms}.NetServerHandler," +
                    "{nms}.PlayerConnection," +
                    "{nm}.network.NetServerHandler," +
                    "{nm}.network.NetHandlerPlayServer," +
                    "{NetworkElement}"
    );






    RefField fieldNetworkElement = classEntityPlayerMP != null ?
            classEntityPlayerMP.findField(classNetworkElement) :
            classEntityPlayer.findField(classNetworkElement);



    RefMethod sendPacket = classNetworkElement.findMethodByParams("{nms}.Packet, {nm}.network.Packet, {nm}.network.packet.Packet, {Packet}");
    RefMethod getHandle = classCraftPlayer.findMethodByReturnType(classEntityPlayer);

    public void sendPacket(Player player, Object packet){
        Object entityPlayer = getHandle.of(player).call();
        Object network = fieldNetworkElement.of(entityPlayer).get();
        sendPacket.of(network).call(packet);
    }
}
