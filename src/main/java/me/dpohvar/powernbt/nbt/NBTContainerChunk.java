package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;

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
        List<String> s = new ArrayList<>();
        s.add("chunk");
        return s;
    }

    @Override
    public NBTCompound readTag() {
        return NBTManager.getInstance().read(chunk);
    }

    @Override
    public void writeTag(Object value) {
        if (value instanceof NBTCompound compound) {
            NBTManager.getInstance().write(chunk, compound);
        }
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
