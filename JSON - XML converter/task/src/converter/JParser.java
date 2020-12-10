package converter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JParser {
    String input;
    public static boolean firstTime = true;

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

    public String goTestRec(String input) {
        Element root = new Element();
        String rinput = input.replace("\n", "").strip();
        root.setRoot(true);
        root.setElementValue("root");
        parseToken(rinput, root);
        //root.print();
      //  System.out.println(root.isLeaf());
        //root.printRec();
        System.out.println(root.printXml());
        return "";
    }

    public void setAttrName(String in, Element el) {
        Matcher attrNameMatcher = Pattern.compile(ExamplePattern.JSON_ATTR_NAME).matcher(in);
        Matcher attrValueMatcher = Pattern.compile("(?<=:\"|(:\\s\"))[0-9a-zA-Z.,!?]+(?=\")|(null)|(true)|(false)|(?<=[ :])[\\d.]+(?=)").matcher(in);
        if (attrNameMatcher.find()) {
            String attrName = attrNameMatcher.group();
            if (attrValueMatcher.find()) {
                String attrVal = attrValueMatcher.group();
                if (attrVal.equals("null")) el.addAttr(attrName, "");
                else el.addAttr(attrName, attrVal);
            } else el.addAttr(attrName, "");
        }else {
           // System.out.println("not found");
        }
    }

    public void setAttrValue(String in, Element el) {
        //System.out.println("I ++++ " + in);
        Matcher attrValueMatcher = Pattern.compile("((?<=\")[^@#:].+?(?=\"))|(null)|(true)|(false)").matcher(in);
        Matcher objectMatcher = Pattern.compile("(?=\\{\\s).*").matcher(in);
        if (objectMatcher.find()) {
            String object = objectMatcher.group();
            //System.out.println("O **** " + object);
            parseToken(object, el);
        } else if (attrValueMatcher.find()) {
            String value = attrValueMatcher.group();
            //System.out.println(in + " V --- " + value);
            if (value.equals("null")) value = " ";
            el.setElementValue(value);
        }
    }

    public void parseArray(String in, Array arr) {
        //System.out.println("Next Array" + in);

        for (String c : findChildsArray(in)) {
            //System.out.println("+++++" + c);
            Array child = new Array();
            child.setArrayName("element");
          //  System.out.println("ChildArr is " + c);
            if (c.trim().equals("null")) {
                child.setArrayValue(" ");
                arr.addChild(child);
           //     System.out.println("variant 1");
                continue;
            }
            if (c.trim().equals("true")) {
                child.setArrayValue("true");
                arr.addChild(child);
             //   System.out.println("variant 2");
                continue;
            }
            if (c.trim().equals("false")) {
                child.setArrayValue("false");
                arr.addChild(child);
            //    System.out.println("variant 3");
                continue;
            }
            if (c.trim().matches("[0-9.]+")) {
                child.setArrayValue(c.trim());
                arr.addChild(child);
            //    System.out.println("variant 4");
                continue;
            }
            if (c.trim().matches("\\{\\s*\\}") || c.trim().matches("\\[\\s*\\]") || c.trim().equals("\"\"")) {
                arr.addChild(child);
              //System.out.println("variant 5" + c.trim());
                continue;
            }
            if (c.trim().startsWith("[")) {
              //  System.out.println("variant 6");
                arr.addChild(child);
                parseArray(c.trim(), child);
                continue;
            }
            if (c.trim().startsWith("{")) {
            //if (c.trim().matches("^[\\w\\d\"_?!]+\\s*:\\s*\\{.*")) {
                //System.out.println("variant 7" + c.trim());
                Matcher nameMatcher = Pattern.compile("(?<=\")[\\w\\d.,!?]+?(?=\":|(\"\\s:))").matcher(c.trim());
                if (nameMatcher.find()) {
                    String keyName = nameMatcher.group();
                    Matcher tokenMatcher = Pattern.compile("(?<=:).*(?<=\\})").matcher(c);
                    //Correct value matcher
                    Matcher valueMatcher = Pattern.compile("(?<=[: ]\").*(?=\")|(?<=[: ])[\\d.]+(?=)|(null)|(true)|(false)").matcher(c);
                    if (tokenMatcher.find()) {
                        String token = tokenMatcher.group().trim();
                        // System.out.println("Recursive ************ " + token);
                        //parseArrayObject(token, child);
                    }
                }
                arr.addChild(child);
                parseArrayObject(c.trim(), child);
                continue;
            }
            //System.out.println("variant 8" + c.trim());
            child.setArrayValue(" ");
            child.setArrayValue(c.trim());
            arr.addChild(child);
        }

    }

    public String checkArrayAttr(String in,String key) {
      //  System.out.println(key + " +++ " + in);
        in = in.trim();
        Matcher objectMatcher = Pattern.compile("(?<=\\{).*(?=})").matcher(in);
        if (objectMatcher.find()) {
            String val = objectMatcher.group();
            if (!val.contains("{")) {
                    Matcher nameMatcher = Pattern.compile("(?<=\"#)[\\w\\d.,!?]+?(?=\":|(\"\\s:))").matcher(in);
                    if (nameMatcher.find()) {
                        String name = nameMatcher.group();
                  //      System.out.println("0000000 " + name);
                        if (!name.equals(key)) {
                            in = in.replace("@", "");
                            in = in.replace("#", "");
                       //     System.out.println("0000000 " + in);

                        }
                    }
            }
        }
        return in;
    }

    public void parseArrayObject(String in, Array arr) {

     //   System.out.println("in is " + in);
        in = checkArrayAttr(in, arr.getArrayName());
        for (String c : findChildsArray(in)) {
            c = c.trim();
         //   System.out.println("child is " + c);
            if (c.startsWith("\"@")) {
                Matcher attrNameMatcher = Pattern.compile(ExamplePattern.JSON_ATTR_NAME).matcher(c);
                Matcher attrValueMatcher = Pattern.compile("((?<=\")[^@#:].+?(?=\"))|(null)|(true)|(false)").matcher(c);
                if (attrValueMatcher.find() && attrNameMatcher.find()) {
                    String attrName = attrNameMatcher.group();
                    String attrVal = attrValueMatcher.group();
                //    System.out.println(attrName + "-----------" + attrVal);
                    arr.addAttr(attrName, attrVal);
                }
                continue;
            }
            if (c.startsWith("\"#" + arr.getArrayName())) {
                Matcher attrValueMatcher = Pattern.compile("(?=\\{).*|(null)|(true)|(false)|(?<=[: ])[\\d.]+|((?=\\[).*)|(?<=[: ]\").*(?=\")").matcher(c);
                if (attrValueMatcher.find()) {
                    String attrVal = attrValueMatcher.group();
               //     System.out.println(arr.getArrayName() + "9999**********" + attrVal);
                    if (attrVal.equals("null")) attrVal = " ";
                    if (attrVal.trim().startsWith("{")) {
                        parseArrayObject(attrVal, arr);
                        continue;
                    }
                    if (attrVal.trim().startsWith("[")) {
                        parseArray(attrVal, arr);
                        continue;
                    }
                    arr.setArrayValue(attrVal);
                }
                continue;
            }
            if (c.startsWith("\"") && c.matches("^[\\w\\d\"_?!]+\\s*:\\s*[\"\\d\\w,.]+")) {
                Matcher attrValueMatcher = Pattern.compile("(?<=[: ]\").*(?=\")|(null)|(true)|(false)|(?<=[: ])[\\d.]+").matcher(c);
                if (attrValueMatcher.find()) {
                    String attrVal = attrValueMatcher.group();
                    Matcher nameMatcher = Pattern.compile("(?<=\")[0-9a-zA-Z_.,!?]+?(?=\":|(\"\\s:))").matcher(c);
                    if (nameMatcher.find()) {
                        String n = nameMatcher.group();
                   //     System.out.println(n + "0-0-0-0" + attrVal);
                        Array child = new Array();
                        child.setArrayName(n);
                        child.setArrayValue(attrVal);
                        arr.addChild(child);
                    }
                }
                continue;
            }
            if (c.startsWith("\"") && c.matches("^[\\w\\d\"_?!]+\\s*:\\s*\\{.*")) {
             //   System.out.println("find object" + c);
                Matcher nameMatcher = Pattern.compile("(?<=\")[0-9a-zA-Z_.,!?]+?(?=\":|(\"\\s:))").matcher(c);
                if (nameMatcher.find()) {
                    String n = nameMatcher.group();
                    Matcher objectMatcher = Pattern.compile("(?=\\{).*").matcher(c);
                    if (objectMatcher.find()) {
                        String object = objectMatcher.group();
                        Array child = new Array();
                        child.setArrayName(n);
               //         System.out.println("Name is " + n);
                        arr.addChild(child);
                        parseArrayObject(object, child);
                    }
                }
                continue;
            }

            if (c.startsWith("\"") && c.matches("^[\\w\\d\"_?!]+\\s*:\\s*\\[.*")) {
                //System.out.println("find array");
                Matcher nameMatcher = Pattern.compile("(?<=\")[0-9a-zA-Z_.,!?]+?(?=\":|(\"\\s:))").matcher(c);
                if (nameMatcher.find()) {
                    String n = nameMatcher.group();
                    Matcher objectMatcher = Pattern.compile("(?=\\[).*").matcher(c);
                    if (objectMatcher.find()) {
                        String object = objectMatcher.group();
                        Array child = new Array();
                        child.setArrayName(n);
                        arr.addChild(child);
                        parseArray(object, child);
                    }
                }
                continue;
            }


        }

    }

    public ArrayList<String> findChildsArray(String in) {

        ArrayList<String> childs = new ArrayList<>();
        boolean isBalanced = false;
        boolean isFirst = true;
        boolean isInside = false;
        boolean isArray = false;
        int countOpen = 0;
        int countClose = 0;
        int countOpenArray = 0;
        int countCloseArray = 0;
        //System.out.println(in);
        if (in.length() > 2)
        in = in.substring(1, in.length() - 1).trim();
        //System.out.println("*** " + in);
        char[] chArr = in.toCharArray();
        String res = "";
        for (char c : chArr) {
            if (c == '{' && !isArray) {
                isInside = true;
                isFirst = false;
                countOpen++;
            }
            if (c == '[' && !isInside) {
                isArray = true;
                isFirst = false;
                countOpenArray++;

            }
            if (c == '}' && !isArray) {
                countClose++;
            }
            if (c == ']' && !isInside) {
                countCloseArray++;

            }
            if (!isInside && c == ',' && !isArray) {
                childs.add(res.trim());
                res = "";
            } else res += String.valueOf(c);
            if (countClose == countOpen && !isFirst && !isArray) {
                isInside = false;
                isBalanced = false;
                isFirst = true;
                countOpen = 0;
                countClose = 0;
            }
            if (countCloseArray == countOpenArray && !isFirst && !isInside) {
                isArray = false;
                isBalanced = false;
                isFirst = true;
                countOpenArray = 0;
                countCloseArray = 0;
            }
        }
        childs.add(res.trim());
       // childs.forEach(x -> System.out.println(x));
        ArrayList<String> resList = new ArrayList<>();
        for (String c : childs) {
            // System.out.println("----" + c);
            //System.out.println("****" + checkChild(c));/////////////
            c = checkChild(c);
            if (!c.equals("")) {
                resList.add(c);
            }
        }
        //return resList;
        return childs;
    }

    public String checkObjAttr(String in,String key) {
        //System.out.println(key + " +++ " + in);
        boolean findAt = false;
        boolean findVal = false;
        boolean isOk = true;
      //  System.out.println(in);
        in = stupidCheck2(in);
    //    System.out.println("Stupid " + in);
        for (String c : findChilds(in)) {
            c = c.trim();
            if (c.startsWith("\"@")) {
                findAt = true;
                if (c.contains("{") || c.contains("[")) {
                    if (!c.contains("{}") && !c.contains("[]")) isOk = false;
                }
            }
        }

        if (!isOk) {
            String res = "{ ";
            for (String c : findChilds(in)) {
                c = c.trim();
                if (c.startsWith("\"@")) {
                    c = c.replaceFirst("@", "");
                    res += c + ", ";
                } else if (c.startsWith("\"#")) {
                    c = c.replaceFirst("#", "");
                    res += c + ", ";
                } else res += c + ", ";
            }
            res = res.trim();
            res = res.substring(0, res.length() - 1) + " }";
     //       System.out.println("****" + res);
            return res;
            }


           /* Matcher wrongAtt = Pattern.compile("\"@[\\w\\d_!?]+\"\\s*:\\s*\\{\\s*[\\w\\d\"]+\\s*.+?\\}").matcher(in);
            if (wrongAtt.find()) {
                in = in.replace("@", "");
                in = in.replace("#", "");
            }*/
        return in;
    }

    public String stupidCheck2(String in) {
        String attr = null;
        String candidateAttr;
        String val = null;
        String name = null;
        String candidateVal;
        boolean findAtt = false;
        boolean findVal = false;
        boolean findCA = false;
        boolean findCV = false;
        for (String c : findChilds(in)) {
            c = c.trim();
            if (c.startsWith("\"@")) {
                findAtt = true;
                if (c.contains(":")) {
                    attr = c.split(":")[0].trim();
                    attr = attr.substring(2, attr.length() - 1);
                }
            } else if (c.startsWith("\"#")) {
                findVal = true;
                if (c.contains(":")) {
                    val = c.split(":")[0].trim();
                    val = val.substring(2, val.length() - 1);
                }
            } else if (c.startsWith("\"")) {
                if (c.contains(":")) {
                    name = c.split(":")[0].trim();
                    if (name != null)
                    name = name.substring(1, name.length() - 1);
                }
            }
        }
     //   System.out.println("Name is " + name + " attr " + attr);
        String res = "{ ";
        for (String c : findChilds(in)) {
            c = c.trim();
            if (c.startsWith("\"@") && attr.equals(name)) {

            } else if (c.startsWith("\"#") && name != null) {
                if (attr.equals(name))
                res += c.replace("#", "") + ",";
            } else res += c + ",";
        }
        res = res.substring(0, res.length() - 1) + " }";
        return res;
    }


    public void parseToken(String in, Element el) {
       // System.out.println("Input is " + in);

        in = checkObjAttr(in, el.getElementName());

      //  System.out.println("Check is " + in);

        for (String c : findChilds(in)) {
          //  System.out.println("Child is " + c);
            c = c.trim();
            if (c.startsWith("\"@")) {
                setAttrName(c, el);
                continue;
            }
            if (c.startsWith("\"#")) {
                setAttrValue(c, el);
                continue;
            }
            Element child = new Element();
            Matcher nameMatcher = Pattern.compile("(?<=\")[\\w\\d.,!?]+?(?=\":|(\"\\s:))").matcher(c);
            if (nameMatcher.find()) {
                String keyName = nameMatcher.group();
                //System.out.println("Keyname is " + keyName);
                child.setElementName(keyName);
                el.addChild(child);

                Matcher arrayMatcher = Pattern.compile("^\"" + keyName + "\"\\s*:\\s*\\[").matcher(c);
                if (arrayMatcher.find()) {
                    Matcher arrayValueMatcher = Pattern.compile("(?=\\[).*(?<=])").matcher(c);
                    if (arrayValueMatcher.find()) {
                        String arrValue = arrayValueMatcher.group();
                        //System.out.println(arrValue);
                        if (!arrValue.matches("\\[\\s*\\]")) {
                            Array arr = new Array();
                            arr.setRoot(true);
                            child.setArrays(arr);
                            parseArray(arrValue, arr);
                            continue;
                        }
                    }
                }

            }


            //Matcher tokenMatcher = Pattern.compile("(?<=:).*(?<=\\})").matcher(c);
            Matcher tokenMatcher = Pattern.compile("(?<=:)\\s*\\{.*\\}").matcher(c);
            //Correct value matcher
            Matcher valueMatcher = Pattern.compile("(?<=[: ]\").*(?=\")|(?<=[: ])[\\d.]+(?=)|(null)|(true)|(false)").matcher(c);
            if (tokenMatcher.find()) {
                String token = tokenMatcher.group().trim();
              //  System.out.println("Recursive ************ " + token);
                parseToken(token, child);
            } else if (valueMatcher.find()) {
                String value = valueMatcher.group();
                if (value.equals("null")) value = " ";
                child.setElementValue(value);
            }
        }
    }


    public String stupidCheck(String in) {
        String res = in;
        String attr = null;
        String candidateAttr;
        String val = null;
        String candidateVal;
        boolean findAtt = false;
        boolean findVal = false;
        boolean findCA = false;
        boolean findCV = false;
        for (String s : res.split(",")) {
            if (s.trim().startsWith("\"@")) {
                findAtt = true;
                if (s.contains(":")) {
                    attr = s.split(":")[0].trim();
                    attr = attr.substring(2, attr.length() - 1);
                }
            }
            if (s.trim().startsWith("\"#")) {
                findVal = true;
                if (s.contains(":")) {
                    val = s.split(":")[0].trim();
                    val = val.substring(2, val.length() - 1);
                }
            }
        }

        for (String s : res.split(",")) {
            if (s.trim().startsWith("\"" + attr) && !attr.equals("")) {
                findCA = true;
            }
            if (s.trim().startsWith("\"" + val) && !val.equals("")) {
                findCV = true;
            }
        }
        if (findCA && findCV) {
            String newRes = "";
            for (String s : res.split(",")) {
                s = s.trim();
                if (s.trim().startsWith("\"" + attr + "\"")) {
                    //String ra = " , " + s.substring(0, 1) + '@' + s.substring(1);
                    String ra = " , " + s;
                    newRes += ra;
                  //  System.out.println("newResA is" + ra);
                } else if (s.trim().startsWith("\"" + val + "\"")) {
                    //String rv = " , " + s.substring(0, 1) + '#' + s.substring(1);
                    String rv = " , " + s;
                    newRes += rv;
                   // System.out.println("newResV is" + rv);
                }
            }
           // System.out.println("***" + in);
       //System.out.println("***" + newRes);
            newRes = newRes.trim().substring(1);
            return newRes;
        }
        return in;
    }

    public String checkChild(String in) {
        //System.out.println("+++" + in);
        String res = in;
        String keyName = "";
        boolean isOk =true;
        Matcher attrFind = Pattern.compile("(?<=\\{).*.+?(?=\\})").matcher(in);
        Matcher nameMatcher = Pattern.compile("(?<=\")[#@\\w\\d.,!?]+?(?=\":|(\"\\s:))").matcher(in);
        if (nameMatcher.find()) {
            keyName = nameMatcher.group();
            //System.out.println("key ---- " + keyName);
            Matcher arrayMatcher = Pattern.compile("^\"" + keyName + "\"\\s*:\\s*\\[").matcher(in);
            if (arrayMatcher.find()) {
              //  System.out.println("Bracket");
                return in;
            }
        }

        if (in.trim().startsWith("\"\"")) return "";

        if (attrFind.find()) {
            String body = attrFind.group();
            body = stupidCheck(body);
            //System.out.println("res is " + res);
            //System.out.println("key is " + keyName);
            //System.out.println("body is " + body);
            String exl = "{";
            if (!body.contains(exl)) {
                boolean findAtt = false;
                boolean findVal = false;
                boolean findSome = false;
               // System.out.println("found" + body);
                for (String s : body.split(",")) {
                    if (!checkAttr(s.trim(), keyName)) isOk = false;
                    if (s.contains("@")) findAtt = true;
                    if (s.contains("#")) findVal = true;
                    if (!s.trim().startsWith("\"@") && !s.trim().startsWith("\"#")) findSome = true;
                }
                if (!findVal || findSome) isOk = false;
            }
            if (!isOk) {
                body = body.replace("@", "");
                body = body.replace("#", "");
            }
            res = "\"" + keyName + "\"" + " : { " + body + " }";
         //   System.out.println("res is " + res);
        }

        /*if (!isOk) {
            res = res.replace("@", "");
            res = res.replace("#", "");
        }*/
       // System.out.println("**********res is " + res);
        return res;
    }

    public boolean checkAttr(String in, String key) {
        if (in.contains(":")) {
           // System.out.println("attr is **" + in);
            String name = in.split(":")[0];
            String value = in.split(":")[1];
            if (name.equals("\"@\"")) return false;
            //if (name.startsWith("\"@\"") && value.trim().startsWith("{")) return false;//////
            if (name.equals("\"\"")) return false;
            if (!name.substring(2, name.length() - 1).equals(key) && name.startsWith("\"#")) return false;
        }

        return true;
    }


    public ArrayList<String> findChilds(String in) {

        ArrayList<String> childs = new ArrayList<>();
        boolean isBalanced = false;
        boolean isFirst = true;
        boolean isInside = false;
        boolean isArray = false;
        int countOpen = 0;
        int countClose = 0;
        int countOpenArray = 0;
        int countCloseArray = 0;
        //System.out.println(in);
        in = in.substring(1, in.length() - 1).trim();
        //System.out.println("*** " + in);
        char[] chArr = in.toCharArray();
        String res = "";
        for (char c : chArr) {
            if (c == '{' && !isArray) {
                isInside = true;
                isFirst = false;
                countOpen++;
            }
            if (c == '[' && !isInside) {
                isArray = true;
                isFirst = false;
                countOpenArray++;

            }
            if (c == '}' && !isArray) {
                countClose++;
            }
            if (c == ']' && !isInside) {
                countCloseArray++;

            }
            if (!isInside && c == ',' && !isArray) {
                childs.add(res.trim());
                res = "";
            } else res += String.valueOf(c);
            if (countClose == countOpen && !isFirst && !isArray) {
                isInside = false;
                isBalanced = false;
                isFirst = true;
                countOpen = 0;
                countClose = 0;
            }
            if (countCloseArray == countOpenArray && !isFirst && !isInside) {
                isArray = false;
                isBalanced = false;
                isFirst = true;
                countOpenArray = 0;
                countCloseArray = 0;
            }
        }
        childs.add(res.trim());
        ArrayList<String> resList = new ArrayList<>();
        for (String c : childs) {
           // System.out.println("----" + c);
            //System.out.println("****" + checkChild(c));/////////////
            c = checkChild(c);
            if (!c.equals("")) {
                resList.add(c);
            }
        }
        return resList;
    }


}

