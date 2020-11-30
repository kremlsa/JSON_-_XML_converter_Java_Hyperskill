package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JParser {
    String input;

    public JParser(String input) {
        this.input = input;
    }

    public Element go() {
        Element middleElement = new Element();
        Matcher nameMatcher = Pattern.compile(ExamplePattern.JSON_KEY).matcher(input);
        Matcher valueMatcher = Pattern.compile(ExamplePattern.JSON_VALUE).matcher(input);
        Matcher attrNameMatcher = Pattern.compile(ExamplePattern.JSON_ATTR_NAME).matcher(input);
        Matcher attrValueMatcher = Pattern.compile(ExamplePattern.JSON_ATTR_VALUE).matcher(input);
        boolean isAttribute = false;

        while (attrNameMatcher.find()) {
            isAttribute = true;
            String attrName = attrNameMatcher.group();
            if (attrValueMatcher.find()) {
                String attrVal = attrValueMatcher.group();
                middleElement.addAttr(attrName, attrVal);
            }
        }
        if (isAttribute) {
            while (nameMatcher.find()) {
                String n = nameMatcher.group();
                middleElement.setElementName(n);
                valueMatcher = Pattern.compile("((?<=\"#" + n + "\"\\s?:\\s?)\".+?(?=\"))|(null)|((?<=#" + n + ").*[0-9]+?)").matcher(input);
                if (valueMatcher.find()) {
                    String val = valueMatcher.group();
                    if (val.contains(":")) {
                        val = val.replaceAll("\" : ", "");
                        middleElement.setElementValue(val);
                    } else if (!val.contains("\"")) {
                        middleElement.setElementValue(null);
                    } else {
                        val = val.replaceAll("\"", "");
                        middleElement.setElementValue(val);
                    }
                }
            }
        } else {
            nameMatcher = Pattern.compile("(?<=\")[0-9a-zA-Z.,!?]+?(?=\":|(\"\\s:))").matcher(input);
            while (nameMatcher.find()) {
                String n = nameMatcher.group();
                middleElement.setElementName(n);
                valueMatcher = Pattern.compile("(?<=:\"|(:\\s\"))[0-9a-zA-Z.,!?]+(?=\")|(null)").matcher(input);
                if (valueMatcher.find()) {
                    String val = valueMatcher.group();
                    middleElement.setElementValue(val);
                }
            }
        }
        return middleElement;
    }
}
