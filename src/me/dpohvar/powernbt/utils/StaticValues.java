package me.dpohvar.powernbt.utils;

public class StaticValues {

    public static final Class classNBTBase = VersionFix.fixClass("net.minecraft.server.NBTBase");
    public static final Class classNBTTagByte = VersionFix.fixClass("net.minecraft.server.NBTTagByte");
    public static final Class classNBTTagShort = VersionFix.fixClass("net.minecraft.server.NBTTagShort");
    public static final Class classNBTTagInt = VersionFix.fixClass("net.minecraft.server.NBTTagInt");
    public static final Class classNBTTagLong = VersionFix.fixClass("net.minecraft.server.NBTTagLong");
    public static final Class classNBTTagFloat = VersionFix.fixClass("net.minecraft.server.NBTTagFloat");
    public static final Class classNBTTagDouble = VersionFix.fixClass("net.minecraft.server.NBTTagDouble");
    public static final Class classNBTTagByteArray = VersionFix.fixClass("net.minecraft.server.NBTTagByteArray");
    public static final Class classNBTTagString = VersionFix.fixClass("net.minecraft.server.NBTTagString");
    public static final Class classNBTTagList = VersionFix.fixClass("net.minecraft.server.NBTTagList");
    public static final Class classNBTTagCompound = VersionFix.fixClass("net.minecraft.server.NBTTagCompound");
    public static final Class classNBTTagIntArray = VersionFix.fixClass("net.minecraft.server.NBTTagIntArray");

    public static final Class classItemStack = VersionFix.fixClass("net.minecraft.server.ItemStack");

    public static final Class classPacket = VersionFix.fixClass("net.minecraft.server.Packet");
    public static final Class classCompressedStreamTools = VersionFix.fixClass("net.minecraft.server.NBTCompressedStreamTools");

    public static final Class classEntityPlayer = VersionFix.fixClass("net.minecraft.server.EntityPlayer");

    public static final Class[] noInput = new Class[0];
    public static final Class[] oneNBTTagCompound = new Class[]{classNBTTagCompound};
    public static final Class[] onePacket = new Class[]{classPacket};

}
