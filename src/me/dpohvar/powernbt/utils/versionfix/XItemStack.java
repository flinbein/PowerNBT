package me.dpohvar.powernbt.utils.versionfix;


public interface XItemStack extends VersionFix.FixInterface {
    public org.bukkit.Material getType();

    public int getTypeId();

    public void setTypeId(int type);

    public int getAmount();

    public void setAmount(int amount);

    public void setDurability(short durability);

    public short getDurability();

    public int getMaxStackSize();

    public Object getHandle();

    public Object getTag();

    public void setTag(Object tag);

    public Object clone();
}
