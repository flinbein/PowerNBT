package me.dpohvar.powernbt.utils.versionfix;

import net.minecraft.server.v1_4_5.NBTBase;

public interface XNBTBase extends VersionFix.FixInterface {

    public byte getTypeId();
    public String getName();
    public Object clone();

}
