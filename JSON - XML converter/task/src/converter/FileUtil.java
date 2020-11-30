package converter;

import java.io.*;

public class FileUtil {

    public static void saveToFile(String output) {
        String filepath = "test2.txt";
        File file = new File(filepath);
        BufferedWriter bf = null;;
        try{
            bf = new BufferedWriter( new FileWriter(file));
            bf.write(output);
            bf.flush();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch(Exception e){}
        }
    }

    public static String loadFromFile() {
        String result = "";
        BufferedReader reader = null;
        File file = new File("test.txt");
        if (file.exists()) {
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = null;
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                result += line;
            }
            try {
                    reader.close();
                } catch(Exception e){}
        }
        return result;
    }
}
