package me.dpohvar.powernbt.utils.versionfix;

public interface XTileEntity extends VersionFix.FixInterface {

    public void a(Object compound);

    public void b(Object compound);

    public Object getUpdatePacket();
}
