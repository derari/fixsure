package org.cthul.fixsure.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DataLoader {

    public static String[] readLines(Class clazz, String resource) {
        return readLines(clazz.getResourceAsStream(resource));
    }

    private static String[] readLines(InputStream resource) {
        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(resource, "utf-8"));
            String s;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("#")) {
                    if (s.startsWith("##")) {
                        s = s.substring(1);
                    } else {
                        continue;
                    }
                }
                result.add(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result.toArray(new String[result.size()]);
    }
    
}
