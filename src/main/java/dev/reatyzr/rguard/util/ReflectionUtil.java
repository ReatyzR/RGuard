/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.util;

import io.netty.channel.Channel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ReflectionUtil {
    private static final String VERSION;
    private static final String NMS_PREFIX;
    private static final String OBC_PREFIX;
    private static final Map<String, Class<?>> CLASS_CACHE;
    private static final Map<String, Field> FIELD_CACHE;
    private static final Map<String, Method> METHOD_CACHE;

    private ReflectionUtil() {
    }

    public static String getVersion() {
        return VERSION;
    }

    public static Class<?> getNmsClass(String className) {
        return CLASS_CACHE.computeIfAbsent(className, name -> {
            try {
                return Class.forName(NMS_PREFIX + name);
            }
            catch (ClassNotFoundException e) {
                try {
                    return Class.forName(name);
                }
                catch (ClassNotFoundException ex) {
                    return null;
                }
            }
        });
    }

    public static Class<?> getObcClass(String className) {
        return CLASS_CACHE.computeIfAbsent(className, name -> {
            try {
                return Class.forName(OBC_PREFIX + name);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        });
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        String key = clazz.getName() + "." + fieldName;
        return FIELD_CACHE.computeIfAbsent(key, k -> {
            for (Class current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
                try {
                    Field field = current.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                }
                catch (NoSuchFieldException ignored) {
                    continue;
                }
            }
            return null;
        });
    }

    public static Object getFieldValue(Object object, String fieldName) {
        if (object == null) {
            return null;
        }
        Field field = ReflectionUtil.getField(object.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        try {
            return field.get(object);
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Field getFieldByType(Class<?> clazz, Class<?> type) {
        String key = clazz.getName() + ":" + type.getName();
        return FIELD_CACHE.computeIfAbsent(key, k -> {
            for (Class current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
                for (Field field : current.getDeclaredFields()) {
                    if (!type.isAssignableFrom(field.getType())) continue;
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        });
    }

    public static Object getFieldValueByType(Object object, Class<?> type) {
        if (object == null) {
            return null;
        }
        Field field = ReflectionUtil.getFieldByType(object.getClass(), type);
        if (field == null) {
            return null;
        }
        try {
            return field.get(object);
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Channel getChannel(Player player) {
        try {
            Player craftPlayer = player;
            Method getHandleMethod = player.getClass().getMethod("getHandle", new Class[0]);
            Object entityPlayer = getHandleMethod.invoke((Object)craftPlayer, new Object[0]);
            Object playerConnection = ReflectionUtil.getFieldValue(entityPlayer, "playerConnection");
            if (playerConnection == null) {
                return null;
            }
            Object networkManager = ReflectionUtil.getFieldValue(playerConnection, "networkManager");
            if (networkManager == null) {
                return null;
            }
            Field channelField = ReflectionUtil.getFieldByType(networkManager.getClass(), Channel.class);
            if (channelField != null) {
                return (Channel)channelField.get(networkManager);
            }
            return (Channel)ReflectionUtil.getFieldValue(networkManager, "channel");
        }
        catch (Exception e) {
            return null;
        }
    }

    static {
        CLASS_CACHE = new ConcurrentHashMap();
        FIELD_CACHE = new ConcurrentHashMap<String, Field>();
        METHOD_CACHE = new ConcurrentHashMap<String, Method>();
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = packageName.substring(packageName.lastIndexOf(46) + 1);
        NMS_PREFIX = "net.minecraft.server." + VERSION + ".";
        OBC_PREFIX = "org.bukkit.craftbukkit." + VERSION + ".";
    }
}

