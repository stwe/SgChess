/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Properties;

public class ConfigLoader {

    //-------------------------------------------------
    // Load
    //-------------------------------------------------

    public static void load(Class<?> configClass, String path) throws IOException, IllegalAccessException {
        Objects.requireNonNull(configClass, "configClass must not be null");
        Objects.requireNonNull(path, "path must not be null");

        InputStream in = ConfigLoader.class.getResourceAsStream(path);
        if (in == null) {
            throw new FileNotFoundException("Config file " + path + " not found.");
        }

        Properties properties = new Properties();
        properties.load(in);

        for (Field field : configClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.set(null, getValue(properties, field.getName(), field.getType()));
            }
        }
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    private static Object getValue(Properties properties, String name, Class<?> type) {
        var value = Objects.requireNonNull(properties).getProperty(Objects.requireNonNull(name));

        if (value == null) {
            throw new RuntimeException("Missing configuration value: " + name);
        }

        if (type == String.class) {
            return value;
        }

        if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        }

        if (type == int.class) {
            return Integer.parseInt(value);
        }

        if (type == float.class) {
            return Float.parseFloat(value);
        }

        if (type == double.class) {
            return Double.parseDouble(value);
        }

        throw new RuntimeException("Unknown configuration value type: " + type.getName());
    }
}
