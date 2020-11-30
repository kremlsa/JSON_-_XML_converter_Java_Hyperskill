package converter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XParser {
    public static String input;
    public static List<String> proc = new ArrayList<>();
    public static int counter = 0;

    public XParser(String input) {
        this.input = input;
    }

    public Element goRec() {
        Map<String, String> processed = new LinkedHashMap<>();
        Element structure= new Element();
        structure.setRoot(true);
        String rinput = input.replace("\n", "").strip();
        Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(rinput);
        if (nameMatcher.find()) {
            String root = nameMatcher.group();
            structure.setElementName(root);
            structure.setPath(root);
        }
        recursionF(rinput, structure);
        structure.print();
        structure.printRec();
        return structure;
    }

    public void recursionF (String in, Element el) {

        if (findChild(in).size() == 0) {
            Matcher exMatcher = Pattern.compile(ExamplePattern.TAG_VALUE).matcher(in);
            if (exMatcher.find()) {
                el.setElementValue(exMatcher.group());
            }
            return;
        }

        for (String c : findChild(in)) {
            Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(c);
            if (nameMatcher.find()) {
                String value = "";
                String tagName = nameMatcher.group();
                Element child = new Element();
                child.setElementName(tagName);
                Matcher selectTagAttr = Pattern.compile("<" + tagName + ".*?>").matcher(c);

                if (selectTagAttr.find()) {
                    String fullTag = selectTagAttr.group();
                    Matcher attrNameMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_NAME).matcher(fullTag);
                    Matcher attrValueMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_VALUE).matcher(fullTag);

                    while (attrNameMatcher.find()) {
                        String attributeName = attrNameMatcher.group();
                        if (attrValueMatcher.find()) {
                            child.addAttr(attributeName, attrValueMatcher.group());
                        } else {
                            child.addAttr(attributeName, null);
                        }
                    }
                }
                String pat = "(?<=>).*(?=<\\/" + tagName + ">)";
                Matcher valueMatcher = Pattern.compile(pat).matcher(c);
                el.addChild(child);
                recursionF(c, child);
            }
        }



    }

    public ArrayList<String> findChild(String in) {
        Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(in);
        String root = "";
        String val = "";
        if (nameMatcher.find()) {
            root = nameMatcher.group();
        }
        Matcher valMatcher = Pattern.compile("(?<=>).*(?=<\\/" + root + ">)").matcher(in);
        if (valMatcher.find()) {
            val = valMatcher.group();
        }
        ArrayList<String> childs = new ArrayList<>();
        String next = val;
        while (true) {
            nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(next);
            if (nameMatcher.find()) {
                String value = "";
                String tagName = nameMatcher.group();
                String closeTag = "(?<=<\\/" + tagName + ">).*";
                String pat = "<" + tagName + ".+?\\/" + tagName + ">";
                String pat2 = "^\\s*<" + tagName + "([^>]+)\\/>";
                Matcher valueMatcher = Pattern.compile(pat).matcher(next);
                Matcher value2Matcher = Pattern.compile(pat2).matcher(next);
                if (value2Matcher.find()) {
                    value = value2Matcher.group() + " </" + tagName + ">";
                    closeTag = "(?<=\\/>).*";
                } else if (valueMatcher.find()) {
                    value = valueMatcher.group();
                } else break;
                childs.add(value);
                Matcher nextMatcher = Pattern.compile(closeTag).matcher(next);
                if (nextMatcher.find()) {
                    next = nextMatcher.group();
                }
            } else {
                break;
            }
        }
        return childs;
    }
}
