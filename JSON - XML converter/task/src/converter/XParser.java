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
        Element el = new Element();
        structure.setRoot(true);
        structure.setElementName("root");
        String rinput = input.replace("\n", "").strip();
        Matcher headerMatcher = Pattern.compile("<[?]+.*[?]+>").matcher(input);
        if (headerMatcher.find()) {
            rinput = rinput.substring(headerMatcher.end());
        }
        Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(rinput);
        if (nameMatcher.find()) {
            String root = nameMatcher.group();
            el.setElementName(root);
            el.setRoot(true);
            el.setPath(root);
            structure.addChild(el);

            Matcher selectTagAttr = Pattern.compile("<" + root + ".*?>").matcher(rinput);
            if (selectTagAttr.find()) {
                String fullTag = selectTagAttr.group();
                Matcher attrNameMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_NAME).matcher(fullTag);
                Matcher attrValueMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_VALUE).matcher(fullTag);

                while (attrNameMatcher.find()) {
                    String attributeName = attrNameMatcher.group();
                    if (attrValueMatcher.find()) {
                        el.addAttr(attributeName, attrValueMatcher.group());
                    } else {
                        el.addAttr(attributeName, null);
                    }
                }
            }
            Matcher remHeader = Pattern.compile("(?=(<" + root + ")).*").matcher(rinput);
            if (remHeader.find()) {
                rinput = remHeader.group();
            }
        }

        recursionF(rinput, el);
        //structure.print();
        //structure.printRec();
        System.out.println(structure.printJson());
        //System.out.println(el.arrays.arrayRec());
        return structure;
    }

    public boolean checkForArrays(String in, String child) {
        ArrayList<String> arr = new ArrayList<>();
        for (String c : findChild(in)) {
            Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(c);
            String root = "";
            if (nameMatcher.find()) {
                root = nameMatcher.group();
                arr.add(root);
            }
        }
        Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(child);
        if (nameMatcher.find()) {
            int count = 0;
            String root = nameMatcher.group();
            for (String n : arr) {
                if (n.equals(root)) count++;
                if (count > 1) return true;
            }
        }
        return false;
    }

    public void parseArray(String in, Array arr) {
    //    System.out.println("in is" + in);
        if (findChild(in).size() == 0) {

            Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(in);
            if (nameMatcher.find()) {
                String value = "";
                String tagName = nameMatcher.group();
                Matcher selectTagAttr = Pattern.compile("<" + tagName + ".*?>").matcher(in);

                if (selectTagAttr.find()) {
                    String fullTag = selectTagAttr.group();
                    Matcher attrNameMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_NAME).matcher(fullTag);
                    Matcher attrValueMatcher = Pattern.compile(ExamplePattern.TAG_ATTR_VALUE).matcher(fullTag);

                    while (attrNameMatcher.find()) {
                        String attributeName = attrNameMatcher.group();
                        if (attrValueMatcher.find()) {
                            arr.addAttr(attributeName, attrValueMatcher.group());
                        } else {
                            arr.addAttr(attributeName, null);
                        }
                    }
                }
            }

            Matcher exMatcher = Pattern.compile(ExamplePattern.TAG_VALUE).matcher(in);
            Matcher nullMatcher = Pattern.compile("^\\s*<([\\w\\d\\s_!?=\"'])+\\/>").matcher(in);
            if (exMatcher.find()) {
                arr.setArrayValue(exMatcher.group());
            } else if (nullMatcher.find()) {
                arr.setArrayValue(" ");
            } else {
                arr.setArrayValue(null);
            }
            return;
        }

        for (String c : findChild(in)) {
            boolean isArray = checkForArrays(in, c);
         //   System.out.println("child is" + c);
            Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(c);
            if (nameMatcher.find()) {
                String value = "";
                String tagName = nameMatcher.group();
                Array child = new Array();
                child.setArrayName(tagName);

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
                arr.addChild(child);
                parseArray(c, child);
            }
        }

            /*
        Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(in);
        if (nameMatcher.find()) {
            String name = nameMatcher.group();
            Array child = new Array();
            child.setArrayName(name);
            arr.addChild(child);
        }*/

    }


    public void recursionF (String in, Element el) {
      //  System.out.println("in is" + in);

        if (findChild(in).size() == 0) {
            Matcher exMatcher = Pattern.compile(ExamplePattern.TAG_VALUE).matcher(in);
            Matcher nullMatcher = Pattern.compile("^\\s*<([\\w\\d\\s_!?=\"'])+\\/>").matcher(in);
            if (exMatcher.find()) {
                el.setElementValue(exMatcher.group());
            } else if (nullMatcher.find()) {
                el.setElementValue(" ");
            } else {
                el.setElementValue(null);
            }
            return;
        }

        for (String c : findChild(in)) {
            boolean isArray = checkForArrays(in, c);
          // System.out.println(isArray + " --child is" + c);
            Matcher nameMatcher = Pattern.compile(ExamplePattern.TAG_NAME).matcher(c);
            if (nameMatcher.find()) {
                String value = "";
                String tagName = nameMatcher.group();
                Element child = new Element();
                child.setElementName(tagName);
                if (isArray) {
                  //  System.out.println("tag is " + tagName);
                    if (el.arrays == null) {
                        Array arr = new Array();
                        el.setArrays(arr);
                    }
                    Array temp = new Array();
                    el.getArrays().addChild(temp);
               //     System.out.println("**" + c);
                    parseArray(c,temp);
                    continue;
                }
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
        //System.out.println(in);
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
        String next = val.trim();
        while (true) {

            int endPos = 0;
            next = next.trim();
           // System.out.println("in is " + next);
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
                    //value = value2Matcher.group() + " </" + tagName + ">";
                    value = value2Matcher.group();
                    closeTag = "(?<=\\/>).*";
                } else if (valueMatcher.find()) {
                    value = valueMatcher.group();
                    /*endPos = valueMatcher.end();
                    String temp = next;
                    while (true) {
                        int countO = 0;
                        int countC = 0;
                        String rep = "<" + tagName + "[ >]";
                        Matcher repeatMatcher = Pattern.compile(rep).matcher(value);
                        while (repeatMatcher.find()) {
                            countO++;
                        }
                        repeatMatcher = Pattern.compile("(?<=<\\/" + tagName + ">)").matcher(value);
                        while (repeatMatcher.find()) {
                            countC++;
                        }
                        if (countO == countC) {
                            break;
                        } else {
                            System.out.println(countO + "----" + countC);
                            temp.trim();
                            value.trim();
                            System.out.println("temp " + temp);
                            System.out.println("value " + value);
                            temp = temp.substring(endPos);
                            System.out.println("temp -" + temp);
                            temp = temp.trim();
                            if (temp.length() - value.length() >= 0) {
                                temp = temp.substring(value.length());
                                temp = temp.trim();
                                System.out.println("temp -" + temp);
                            } else break;
                            Matcher netxVal = Pattern.compile("<\\/" + tagName + ">").matcher(temp);
                            if (netxVal.find()) {
                                endPos = netxVal.end();
                                String nx = temp.substring(0, endPos);
                                nx.trim();
                                value += nx;
                                System.out.println("nx " + value);
                            }

                        }

                    }*/

                    /*int countRepetitions = 0;
                    String rep = "<" + tagName;
                    Matcher repeatMatcher = Pattern.compile(rep).matcher(value);
                    while (repeatMatcher.find()) {
                        countRepetitions++;
                    }
                    if (countRepetitions > 1) {
                        System.out.println(countRepetitions + " count " + value);
                        String repeat = "";
                        repeat += "(?<=\\/" + tagName + ">)[\\w\\d <>\\/\"\'._].+?<\\/" + tagName + ">";
                        //closeTag = "(?<=\\/" + tagName + ">)[\\w\\d <>\\/\"\'._].+?<\\/" + tagName + ">";
                        Matcher repeatMatcher2 = Pattern.compile(repeat).matcher(next);
                        if(repeatMatcher2.find()) {
                            String add = repeatMatcher2.group();
                            value += add;
                            //System.out.println("add is ***** "  + add);
                            //System.out.println("Repeat is ******** "  + value);
                        }
                    }*/


                    endPos = valueMatcher.end();
                    String temp = next;
                    while (true) {
                        int countO = 0;
                        int countC = 0;
                        String rep = "<" + tagName + "[ >]";
                        Matcher repeatMatcher = Pattern.compile(rep).matcher(value);
                        while (repeatMatcher.find()) {
                            countO++;
                        }
                        //repeatMatcher = Pattern.compile("(?<=<\\/" + tagName + ">)").matcher(value);
                        repeatMatcher = Pattern.compile("(?<=<\\/" + tagName + ">)").matcher(value);
                        while (repeatMatcher.find()) {
                            countC++;
                        }
                        if (countO == countC) {
                            break;
                        } else {
                            Matcher netxVal = Pattern.compile("<\\/" + tagName + ">").matcher(next);
                            int counter = 0;
                            int pos = 0;
                            while (netxVal.find()) {
                                pos = netxVal.end();
                                counter++;
                                if (counter == countO) break;

                            }
                            value = next.substring(0, pos);
                            /*
                            System.out.println(countO + "----" + countC);
                            temp.trim();
                            value.trim();
                            System.out.println("temp " + temp);
                            System.out.println("value " + value);
                            temp = temp.substring(endPos);
                            System.out.println("temp -" + temp);
                            temp = temp.trim();
                            if (temp.length() - value.length() >= 0) {
                                temp = temp.substring(value.length());
                                temp = temp.trim();
                                System.out.println("temp -" + temp);
                            } else break;
                            Matcher netxVal = Pattern.compile("<\\/" + tagName + ">").matcher(temp);
                            if (netxVal.find()) {
                                endPos = netxVal.end();
                                String nx = temp.substring(0, endPos);
                                nx.trim();
                                value += nx;
                                System.out.println("nx " + value);
                            }*/

                        }

                    }


                } else break;
                //System.out.println("value is " + value);
                childs.add(value);

                if (next.length() - value.length() >= 0) {
                    next = next.substring(value.length());
                 //   System.out.println("n is " + next + " --\n " + "v is " + value);
                   // System.out.println(next.length() + " ** " + value.length());
                } else {
                    break;
                }
                /*Matcher nextMatcher = Pattern.compile(closeTag).matcher(next);
                if (nextMatcher.find()) {
                    next = nextMatcher.group();
                }*/
            } else {
                break;
            }
        }
        //childs.forEach(x -> System.out.println(x));
        return childs;
    }
}
