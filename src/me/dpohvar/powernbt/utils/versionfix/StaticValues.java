package me.dpohvar.powernbt.utils.versionfix;

import static me.dpohvar.powernbt.utils.versionfix.VersionFix.fixClass;

public class StaticValues {

    public static final byte typeCompound = 10;
    public static final byte typeList = 9;

    public static final Class classNBTTagCompound = fixClass("net.minecraft.server.NBTTagCompound");
    public static final Class classNBTBase = fixClass("net.minecraft.server.NBTBase");
    public static final Class classPacket = fixClass("net.minecraft.server.Packet");

    public static final Class[] noInput = new Class[0];
    public static final Class[] oneNBTTagCompound = new Class[]{classNBTTagCompound};
    public static final Class[] onePacket = new Class[]{classPacket};
}
