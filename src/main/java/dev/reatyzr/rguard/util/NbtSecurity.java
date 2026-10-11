/*
 * Decompiled with CFR 0.152.
 */
package dev.reatyzr.rguard.util;

import dev.reatyzr.rguard.settings.Settings;
import dev.reatyzr.rguard.util.ReflectionUtil;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class NbtSecurity {
    private NbtSecurity() {
    }

    public static boolean validateNbtCompound(Object nbtTagCompound, Settings settings) {
        if (nbtTagCompound == null) {
            return true;
        }
        AtomicInteger totalTags = new AtomicInteger(0);
        return NbtSecurity.checkCompoundDepth(nbtTagCompound, 0, totalTags, settings);
    }

    private static boolean checkCompoundDepth(Object compound, int currentDepth, AtomicInteger totalTags, Settings settings) {
        if (compound == null) {
            return true;
        }
        if (currentDepth > settings.getNbtMaxDepth()) {
            return false;
        }
        if (totalTags.incrementAndGet() > settings.getNbtMaxTags()) {
            return false;
        }
        Object mapObject = ReflectionUtil.getFieldValue(compound, "map");
        if (!(mapObject instanceof Map)) {
            return true;
        }
        Map map = (Map)mapObject;
        if (map.size() > settings.getNbtMaxTags()) {
            return false;
        }
        for (Object rawEntry : map.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) rawEntry;
            int[] ints;
            byte[] bytes;
            String str;
            Object dataObj;
            String valueClassName;
            String keyStr;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String && (keyStr = (String)key).length() > 256) {
                return false;
            }
            if (value == null || !((valueClassName = value.getClass().getSimpleName()).equals("NBTTagCompound") ? !NbtSecurity.checkCompoundDepth(value, currentDepth + 1, totalTags, settings) : (valueClassName.equals("NBTTagList") ? !NbtSecurity.checkList(value, currentDepth + 1, totalTags, settings) : (valueClassName.equals("NBTTagString") ? (dataObj = ReflectionUtil.getFieldValue(value, "data")) instanceof String && (str = (String)dataObj).length() > settings.getNbtMaxStringLength() : (valueClassName.equals("NBTTagByteArray") ? (dataObj = ReflectionUtil.getFieldValue(value, "data")) instanceof byte[] && (bytes = (byte[])dataObj).length > settings.getNbtMaxTotalSizeBytes() : valueClassName.equals("NBTTagIntArray") && (dataObj = ReflectionUtil.getFieldValue(value, "data")) instanceof int[] && (ints = (int[])dataObj).length > settings.getNbtMaxTotalSizeBytes() / 4))))) continue;
            return false;
        }
        return true;
    }

    private static boolean checkList(Object nbtTagList, int currentDepth, AtomicInteger totalTags, Settings settings) {
        if (nbtTagList == null) {
            return true;
        }
        if (currentDepth > settings.getNbtMaxDepth()) {
            return false;
        }
        if (totalTags.incrementAndGet() > settings.getNbtMaxTags()) {
            return false;
        }
        Object listObject = ReflectionUtil.getFieldValue(nbtTagList, "list");
        if (!(listObject instanceof Collection)) {
            return true;
        }
        Collection collection = (Collection)listObject;
        if (collection.size() > settings.getNbtMaxListElements()) {
            return false;
        }
        for (Object item : collection) {
            String str;
            Object dataObj;
            String itemClassName;
            if (item == null || !((itemClassName = item.getClass().getSimpleName()).equals("NBTTagCompound") ? !NbtSecurity.checkCompoundDepth(item, currentDepth + 1, totalTags, settings) : (itemClassName.equals("NBTTagList") ? !NbtSecurity.checkList(item, currentDepth + 1, totalTags, settings) : itemClassName.equals("NBTTagString") && (dataObj = ReflectionUtil.getFieldValue(item, "data")) instanceof String && (str = (String)dataObj).length() > settings.getNbtMaxStringLength()))) continue;
            return false;
        }
        return true;
    }

    public static boolean validateBookPages(Object pagesTagList, Settings settings) {
        if (pagesTagList == null) {
            return true;
        }
        Object listObject = ReflectionUtil.getFieldValue(pagesTagList, "list");
        if (!(listObject instanceof Collection)) {
            return true;
        }
        Collection collection = (Collection)listObject;
        if (collection.size() > settings.getNbtMaxBookPages()) {
            return false;
        }
        for (Object pageObj : collection) {
            String pageText;
            Object dataObj;
            if (pageObj == null || !((dataObj = ReflectionUtil.getFieldValue(pageObj, "data")) instanceof String) || (pageText = (String)dataObj).length() <= settings.getNbtMaxBookPageLength()) continue;
            return false;
        }
        return true;
    }
}

