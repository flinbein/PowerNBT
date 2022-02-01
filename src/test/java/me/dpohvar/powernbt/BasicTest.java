package me.dpohvar.powernbt;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.NBTContainerValue;
import me.dpohvar.powernbt.utils.query.NBTQuery;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicTest extends Assert {

    private static Object qGet(Object base, String selector) throws NBTTagNotFound {
        return new NBTContainerValue(base).getCustomTag(NBTQuery.fromString(selector));
    }

    private static Object qSet(Object base, String selector, Object value) throws NBTTagNotFound, NBTTagUnexpectedType {
        NBTContainerValue container = new NBTContainerValue(base);
        container.setCustomTag(NBTQuery.fromString(selector), value);
        return container.getObject();
    }

    private static Object qRemove(Object base, String selector) throws NBTTagNotFound {
        NBTContainerValue container = new NBTContainerValue(base);
        container.removeCustomTag(NBTQuery.fromString(selector));
        return container.getObject();
    }

    @Test
    public void testContainer(){
        LinkedHashMap<Object, Object> value = new LinkedHashMap<>();
        NBTContainerValue container = new NBTContainerValue(value);
        assertEquals(value, container.getObject());
    }

    @Test
    public void testGetJsonValue() throws NBTTagNotFound {
        NBTContainerValue container = new NBTContainerValue("{\"a\":12}");
        NBTQuery query = NBTQuery.fromString("#a");
        assertEquals((double) 12.0, container.getCustomTag(query));
    }

    @Test
    public void testSetJsonValue() throws NBTTagNotFound, NBTTagUnexpectedType {
        NBTContainerValue container = new NBTContainerValue("{\"a\":12}");
        NBTQuery query = NBTQuery.fromString("#a");
        container.setCustomTag(query, 34.0);
        assertEquals((double) 34.0, container.getCustomTag(query));
    }

    @Test
    public void testRemoveJsonValue() throws NBTTagNotFound {
        NBTContainerValue container = new NBTContainerValue("{\"a\":12}");
        NBTQuery query = NBTQuery.fromString("#a");
        container.removeCustomTag(query);
        assertEquals("{}", container.getObject());
    }

    @Test
    public void testRemoveJsonString() throws NBTTagNotFound {
        NBTContainerValue container = new NBTContainerValue("{\"a\":12}");
        NBTQuery query = NBTQuery.fromString("#");
        container.removeCustomTag(query);
        assertEquals("", container.getObject());
    }

    @Test
    public void testRemoveJsonValueString() throws NBTTagNotFound {
        LinkedHashMap<Object, Object> value = new LinkedHashMap<>();
        value.put("e", "{\"a\":12}");
        NBTContainerValue container = new NBTContainerValue(value);
        NBTQuery query = NBTQuery.fromString("e#a");
        container.removeCustomTag(query);
        Object modifiedObject = container.getObject();
        assertTrue(modifiedObject instanceof Map);
        assertEquals("{}", ((Map<?,?>)modifiedObject).get("e"));
    }

    @Test
    public void testByteArrayInsert() throws NBTTagNotFound, NBTTagUnexpectedType {
        NBTContainerValue container = new NBTContainerValue(new byte[]{1,2,3,4,5});
        NBTQuery query = NBTQuery.fromString("[3..1]");
        container.setCustomTag(query, new int[]{256+10, 256+20, 256+30});
        assertArrayEquals(new byte[]{1,30,20,10,4,5}, (byte[]) container.getObject());
    }

    @Test
    public void testByteArrayRemove() throws NBTTagNotFound {
        NBTContainerValue container = new NBTContainerValue(new byte[]{1,2,3,4,5});
        NBTQuery query = NBTQuery.fromString("[3..1]");
        container.removeCustomTag(query);
        assertArrayEquals(new byte[]{1,4,5}, (byte[]) container.getObject());
    }

    @Test
    public void testByteArrayAddToEnd() throws NBTTagNotFound, NBTTagUnexpectedType {
        NBTContainerValue container = new NBTContainerValue(new byte[]{1,2,3,4,5});
        NBTQuery query = NBTQuery.fromString("[..]");
        container.setCustomTag(query, new int[]{6, 7, 8});
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, (byte[]) container.getObject());
    }

    @Test
    public void testByteArrayAddToStart() throws NBTTagNotFound, NBTTagUnexpectedType {
        NBTContainerValue container = new NBTContainerValue(new byte[]{1,2,3,4,5});
        NBTQuery query = NBTQuery.fromString("[0..0]");
        container.setCustomTag(query, new int[]{6, 7, 8});
        assertArrayEquals(new byte[]{6, 7, 8, 1, 2, 3, 4, 5}, (byte[]) container.getObject());
    }


    @Test
    public void testComplexSetterToNull() throws NBTTagNotFound, NBTTagUnexpectedType {
        Object value = qSet(null, "x[0].foo.bar#baz#[4]fiz", 12);
        Object selected = qGet(value, "x[0].foo.bar#baz#[4]fiz");
        assertEquals(12.0, selected);
    }
}
