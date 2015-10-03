package com.carcache.carcache;

/**
 * Created by Sherman on 10/3/15.
 */
public class DeviceListItem {
    public String id;
    public String content;

    public DeviceListItem(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
