package com.zalo.hackathon.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by chiennd on 22/05/2017.
 */
public class MapUtils {

    /**
     * Convert {x: {y: z}} --> {y: {x: z}}
     */
    public static <U, I, V> Map<I, Map<U, V>> reverse(Map<U, Map<I, V>> uiMap, CombineFunc<V> combineFunc) {
        Map<I, Map<U, V>> iuMap = new HashMap<>();

        for (U u : uiMap.keySet()) {
            for (I i : uiMap.get(u).keySet()) {
                if (!iuMap.containsKey(i)) {
                    iuMap.put(i, new HashMap<>());
                }

                if (!iuMap.get(i).containsKey(u)) {
                    iuMap.get(i).put(u, uiMap.get(u).get(i));
                } else {
                    iuMap.get(i).put(u, combineFunc.combine(iuMap.get(i).get(u), uiMap.get(u).get(i)));
                }
            }
        }

        return iuMap;
    }

    /**
     * Print to file with weighted map : {x : {y: weight}}
     *
     * @param uiMap: input
     * @param path:  output file
     * @param topK:  top weight of that key
     * @param <U>:   generic type
     * @param <I>:   generic type
     * @param <V>:   generic type
     * @throws IOException: File Exception
     */
    public static <U, I, V extends Comparable<? super V>> void printToFile(Map<U, Map<I, V>> uiMap, String path, Integer topK) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(path));
        for (U user : uiMap.keySet()) {
            Map<I, V> originMap = uiMap.get(user);

            if (originMap.size() == 0) {
                continue;
            }

            Map<I, V> sortedMap = sortByValue(originMap, false);

            int count = 0;
            for (Map.Entry<I, V> entry : sortedMap.entrySet()) {
                fileWriter.write(user + "\t" + entry.getKey() + "\t" + entry.getValue() + "\ttype_2\n");
                count += 1;

                if (topK != null && count == topK) {
                    break;
                }
            }
        }

        fileWriter.close();
    }

    public static <U, I, V extends Comparable<? super V>> void printToFileOneLine(Map<U, Map<I, V>> uiMap, String path, Integer topK, boolean isAccending) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(path));
        for (U user : uiMap.keySet()) {

            if (uiMap.get(user).size() == 0) {
                continue;
            }

            Map<I, V> originMap = uiMap.get(user);
            Map<I, V> sortedMap = sortByValue(originMap, isAccending);

            int count = 0;

            fileWriter.write(String.valueOf(user) + "\t1\t");

            for (Map.Entry<I, V> entry : sortedMap.entrySet()) {
                fileWriter.write(String.valueOf(entry.getKey()));
                count += 1;

                if (topK != null && count == topK) {
                    break;
                } else if (count < sortedMap.size()) {
                    fileWriter.write(",");
                }
            }

            fileWriter.write("\n");
        }

        fileWriter.close();
    }


    /**
     * Sort a map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap, final boolean ascending) {

        List<Map.Entry<K, V>> list =
                new LinkedList<>(unsortMap.entrySet());

        list.sort((o1, o2) -> ascending ? o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


    /**
     * Create user cliques map
     *
     * @param cliques: cliques
     * @return map
     */
    public static Map<Integer, Set<Integer>> createUserCliqueMap(List<Set<Integer>> cliques) {
        Map<Integer, Set<Integer>> userCliqueMap = new HashMap<>();

        for (int i = 0; i < cliques.size(); i++) {
            for (Integer user : cliques.get(i)) {
                if (!userCliqueMap.containsKey(user)) {
                    userCliqueMap.put(user, new HashSet<>());
                }

                userCliqueMap.get(user).add(i);
            }
        }

        return userCliqueMap;
    }

    public static Map<Integer, Map<Integer, Double>> normalize(Map<Integer, Map<Integer, Double>> map) {
        for (Integer key : map.keySet()) {
            double total = 0;
            for (double value : map.get(key).values()) {
                total += value;
            }

            for (int friend : map.get(key).keySet()) {
                double oldValue = map.get(key).get(friend);
                map.get(key).put(friend, oldValue * 1.0 / total);
            }
        }

        return map;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> getTopOfMap(Map<K, V> originMap, int top) {
        Map<K, V> sortedMap = sortByValue(originMap, false);

        int count = top;

        Map<K, V> result = new LinkedHashMap<>();
        for (K key : sortedMap.keySet()) {
            result.put(key, sortedMap.get(key));

            count = count - 1;
            if (count == 0) {
                break;
            }
        }

        return result;
    }


    public interface CombineFunc<V> {
        V combine(V v1, V v2);
    }
}
