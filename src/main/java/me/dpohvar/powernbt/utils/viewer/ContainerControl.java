package me.dpohvar.powernbt.utils.viewer;

import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.query.NBTQuery;

public class ContainerControl {

    private final NBTContainer<?> container;
    private final NBTQuery query;
    private final boolean hasValue;
    private final Object value;
    private final String selector;
    private final NBTQuery accessQuery;
    private final boolean readonly;

    public ContainerControl(NBTContainer<?> container, NBTQuery query, Object value){
        this(container, query, true, value);
    }

    public ContainerControl(NBTContainer<?> container, NBTQuery query){
        this(container, query, false, null);
    }

    private ContainerControl(NBTContainer<?> container, NBTQuery query, boolean hasValue, Object value){
        this.container = container;
        this.query = query;
        this.hasValue = hasValue;
        this.value = value;
        this.selector = container.getSelector();
        this.readonly = this.selector == null || container.isObjectReadonly();
        NBTQuery containerSelectorQuery = container.getSelectorQuery();
        if (this.readonly) {
            this.accessQuery = null;
        } else if (containerSelectorQuery != null) {
            this.accessQuery = containerSelectorQuery.join(query);
        } else {
            this.accessQuery = query;
        }
    }

    public NBTContainer<?> getContainer() {
        return container;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public NBTQuery getQuery() {
        return query;
    }

    public Object getValue() {
        return value;
    }

    public NBTQuery getAccessQuery() {
        return accessQuery;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public String getSelector() {
        return selector;
    }

    public String getSelectorWithQuery() {
        if (this.selector == null) return null;
        return this.selector + " " + accessQuery;
    }
}
