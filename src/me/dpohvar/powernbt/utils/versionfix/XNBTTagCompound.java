package me.dpohvar.powernbt.utils.versionfix;

public interface XNBTTagCompound extends XNBTBase {

    public void set(String s, Object nbtbase);
    public void remove(String s);
    public void setByte(String s, byte b0);
    public void setShort(String s, short short1);
    public void setInt(String s, int i);
    public void setLong(String s, long i);
    public void setFloat(String s, float f);
    public void setDouble(String s, double d0);
    public void setString(String s, String s1);
    public void setByteArray(String s, byte[] abyte);
    public void setIntArray(String s, int[] aint);
    public void setCompound(String s, Object compound);
    public void setBoolean(String s, boolean flag);
    public Object get(String s);
    public boolean hasKey(String s);



}
