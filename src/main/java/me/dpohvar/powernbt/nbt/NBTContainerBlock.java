package me.dpohvar.powernbt.nbt;

import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.utils.NBTBlockUtils.nbtBlockUtils;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerBlock extends NBTContainer<Block> {

    Block block;

    public NBTContainerBlock(Block block) {
        this.block = block;
    }

    public Block getObject() {
        return block;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("block", "block_" + block.getType().name());
    }

    @Override
    public NBTTagCompound readCustomTag() {
        NBTTagCompound tag = readTag();
        if (tag!=null) {
            List<String> ignores = plugin.getConfig().getStringList("ignore_get.block");
            if(ignores!=null) for(String s:ignores) tag.remove(s);
        }
        return tag;
    }

    public NBTTagCompound readTag() {
        NBTTagCompound base = new NBTTagCompound();
        nbtBlockUtils.readTag(block, base.getHandle());
        return base;
    }

    @Override
    public void writeTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        nbtBlockUtils.setTag(block,base.getHandle());
        nbtBlockUtils.update(block);
    }

    @Override
    public void writeCustomTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) base.clone();
        List<String> ignores = plugin.getConfig().getStringList("ignore_set.block");
        if(ignores!=null) for(String s:ignores) tag.remove(s);
        NBTTagCompound original = readTag();
        if(tag.getInt("x")==null)tag.put("x",original.get("x"));
        if(tag.getInt("y")==null)tag.put("y",original.get("y"));
        if(tag.getInt("z")==null)tag.put("z",original.get("z"));
        writeTag(tag);
    }

    @Override
    protected Class<Block> getContainerClass() {
        return Block.class;
    }

    @Override
    public String toString(){
        return "block:" + block.getType().toString();
    }

}
