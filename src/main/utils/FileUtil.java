package main.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> Read(String pathname) {
        List<String> result = new ArrayList<String>();

        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                 result.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
