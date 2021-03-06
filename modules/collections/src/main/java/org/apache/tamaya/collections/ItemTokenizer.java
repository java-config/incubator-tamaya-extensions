/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.collections;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.ConversionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class that implements the tokenizing of the entries of a configuration value.
 */
final class ItemTokenizer {

    private static final Logger LOG = Logger.getLogger(ItemTokenizer.class.getName());

    private static final String ITEM_SEPARATOR = "item-separator";
    private static final String MAP_ENTRY_SEPARATOR = "map-entry-separator";
    private static final String ITEM_CONVERTER = "item-converter";
    private static final String DEFAULT_MAP_ENTRY_SEPARATOR = ":";
    private static final String DEFAULT_LIST_ITEM_SEPARATOR = ",";

    /**
     * Private singleton.
     */
    private ItemTokenizer(){}

    /**
     * Splits the given value using the given separator. Matcjhing is done by traversing the String value using
     * {@code indexOf} calls, one by one. The last unresolvable item (without any next separator token)
     * is added at the end of the createList.
     * @param value the value, not null.
     * @return the tokenized value as createList, in order of occurrence.
     */
    public static List<String> split(String value, ConversionContext ctx){
        String itemSeparator = ctx.getMeta().getOrDefault(ITEM_SEPARATOR, DEFAULT_LIST_ITEM_SEPARATOR);
        return split(value, itemSeparator);
    }

    /**
     * Splits the given value using the given separator. Matching is done by traversing the String value using
     * {@code indexOf} calls, one by one. The last unresolvable item (without any next separator token)
     * is added at the end of the list.
     * @param value the value, not null.
     * @param separator the separator to be used.
     * @return the tokenized value as list, in order of occurrence.
     */
    public static List<String> split(String value, final String separator) {
        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        int end = value.indexOf(separator,start);
        while(end>0) {
            if (value.charAt(end - separator.length()) != '\\') {
                String finalValue = value.substring(start, end);
                result.add(finalValue.replace("\\"+separator, separator));
                start = end + separator.length();
                end = value.indexOf(separator,start);
            }else{
                end = value.indexOf(separator,end + separator.length());
            }
        }
        if(start < value.length()){
            result.add(value.substring(start));
        }
        return result;
    }

    /**
     * Splits the given String value as a mapProperties entry, splitting it into key and value part with the given separator.
     * If the value cannot be split then {@code key = value = mapEntry} is used for further processing. key or value
     * parts are normally trimmed, unless they are enclosed with brackets {@code []}.
     * @param mapEntry the entry, not null.
     * @return an array of length 2, with the trimmed and parsed key/value pair.
     */
    public static String[] splitMapEntry(String mapEntry, ConversionContext ctx){
        String entrySeparator = ctx.getMeta().getOrDefault(MAP_ENTRY_SEPARATOR, DEFAULT_MAP_ENTRY_SEPARATOR);
        return splitMapEntry(mapEntry, entrySeparator);
    }

    /**
     * Splits the given String value as a mapProperties entry, splitting it into key and value part with the given separator.
     * If the value cannot be split then {@code key = value = mapEntry} is used for further processing. key or value
     * parts are normally trimmed, unless they are enmcosed with brackets {@code []}.
     * @param mapEntry the entry, not null.
     * @param separator the separator, not null.
     * @return an array of length 2, with the trimmed and parsed key/value pair.
     */
    public static String[] splitMapEntry(final String mapEntry, final String separator) {
        int index = mapEntry.indexOf(separator);
        String[] items;
        if(index<0) {
            items = new String[]{mapEntry, mapEntry};
        }else {
            items = new String[]{mapEntry.substring(0,index),
                                 mapEntry.substring(index+separator.length())};
        }
        if(items[0].trim().startsWith("[")){
            items[0]= items[0].trim();
            items[0] = items[0].substring(1);
        }else{
            items[0]= items[0].trim();
        }
        if(items[1].trim().endsWith("]")){
            items[1] = items[1].substring(0,items[1].length()-1);
        }else{
            items[1]= items[1].trim();
        }
        return items;
    }

    /**
     * Parses the given value into the required collection target type, defined by the context.
     * @param value the raw String value.
     * @return the parsed value, or null.
     */
    public static <T> T convertValue(String value, TypeLiteral<T> targetType, ConversionContext context) {
        String converterClass = context.getMeta().get(ITEM_CONVERTER);
        List<PropertyConverter<T>> valueConverters = new ArrayList<>(1);
        if (converterClass != null) {
            try {
                valueConverters.add((PropertyConverter<T>) Class.forName(converterClass).getConstructor()
                        .newInstance());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error convertion config to ArrayList type.", e);
            }
        }
        valueConverters.addAll(context.getConfiguration().getContext().getPropertyConverters(targetType));
        if (valueConverters.isEmpty()) {
            if(targetType.getRawType().equals(String.class)) {
                return (T)value;
            }
        } else {
            context = context.toBuilder()
                    .setTargetType(targetType).build();
            T result;
            for (PropertyConverter<T> conv : valueConverters) {
                try {
                    result = conv.convert(value, context);
                    if (result != null) {
                        return result;
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error convertion config to ArrayList type.", e);
                }
            }
        }
        LOG.log(Level.SEVERE, "Failed to convert collection value type for '" + value + "'.");
        return null;
    }

}
