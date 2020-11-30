package converter;

import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args)
    {
        String text = FileUtil.loadFromFile();
        FileUtil.saveToFile(text);
        if (text.startsWith("{")) {
            JParser jp = new JParser(text);
            Element element = jp.go();
            System.out.println(new ToXml(element).go());
        } else {
            XParser xp = new XParser(text);
            xp.goRec();
        }
    }
}

