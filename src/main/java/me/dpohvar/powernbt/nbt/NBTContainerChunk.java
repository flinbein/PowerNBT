package me.dpohvar.powernbt.nbt;

import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.utils.ChunkUtils.chunkUtils;

public class NBTContainerChunk extends NBTContainer<Chunk> {

    Chunk chunk;

    public NBTContainerChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getObject() {
        return chunk;
    }

    @Override
    public List<String> getTypes() {
        List<String> s = new ArrayList<String>();
        s.add("chunk");
        return s;
    }

    @Override
    public NBTTagCompound readTag() {
        NBTTagCompound tag = new NBTTagCompound();
        chunkUtils.readChunk(chunk, tag.getHandle());
        return tag;
    }

    @Override
    public void writeTag(NBTBase base) {
        chunkUtils.writeChunk(chunk, base.getHandle());
    }

    @Override
    protected Class<Chunk> getContainerClass() {
        return Chunk.class;
    }

    @Override
    public String toString(){
        return chunk.toString();
    }

}
