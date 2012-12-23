package me.dpohvar.powernbt.utils.versionfix;

public interface XNBTBase extends VersionFix.FixInterface {

    public byte getTypeId();

    public String getName();

    public Object clone();

    public Object setName(String s);

}
