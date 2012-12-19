package me.dpohvar.powernbt.utils.versionfix;

public interface XCraftWorld extends VersionFix.FixInterface {
    Object getTileEntityAt(final int x, final int y, final int z);
}
