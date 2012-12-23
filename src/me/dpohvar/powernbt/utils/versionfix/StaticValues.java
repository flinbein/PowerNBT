package me.dpohvar.powernbt.utils.versionfix;

import static me.dpohvar.powernbt.utils.versionfix.VersionFix.fixClass;

public class StaticValues {

    public static final byte typeCompound = 10;
    public static final byte typeList = 9;

    public static final Class classNBTBase = fixClass("net.minecraft.server.NBTBase");
    public static final Class classNBTTagEnd = fixClass("net.minecraft.server.NBTTagEnd");
    public static final Class classNBTTagByte = fixClass("net.minecraft.server.NBTTagByte");
    public static final Class classNBTTagShort = fixClass("net.minecraft.server.NBTTagShort");
    public static final Class classNBTTagInt = fixClass("net.minecraft.server.NBTTagInt");
    public static final Class classNBTTagLong = fixClass("net.minecraft.server.NBTTagLong");
    public static final Class classNBTTagFloat = fixClass("net.minecraft.server.NBTTagFloat");
    public static final Class classNBTTagDouble = fixClass("net.minecraft.server.NBTTagDouble");
    public static final Class classNBTTagByteArray = fixClass("net.minecraft.server.NBTTagByteArray");
    public static final Class classNBTTagString = fixClass("net.minecraft.server.NBTTagString");
    public static final Class classNBTTagList = fixClass("net.minecraft.server.NBTTagList");
    public static final Class classNBTTagCompound = fixClass("net.minecraft.server.NBTTagCompound");
    public static final Class classNBTTagIntArray = fixClass("net.minecraft.server.NBTTagIntArray");

    public static final Class classPacket = fixClass("net.minecraft.server.Packet");
    public static final Class classCompressedStreamTools = fixClass("net.minecraft.server.NBTCompressedStreamTools");

    public static final Class[] noInput = new Class[0];
    public static final Class[] oneNBTTagCompound = new Class[]{classNBTTagCompound};
    public static final Class[] onePacket = new Class[]{classPacket};
}
