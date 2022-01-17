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
}
