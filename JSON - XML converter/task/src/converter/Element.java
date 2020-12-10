package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Element {
    String elementName = null;
    String elementValue = null;
    boolean isRoot = false;
    Map<String, String> attributes;
    String ident = " ".repeat(8);
    String identPrev = " ".repeat(4);
    String path = "";
    List<Element> childs;
    Array arrays = null;

    public void setArrays(Array arrays) {
        this.arrays = arrays;
    }

    public Array getArrays() {
        return arrays;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean getRoot() {
        return isRoot;
    }

    public Element() {
        childs = new ArrayList<>();
        attributes = new LinkedHashMap<>();
    }

    public boolean isLeaf() {
        return !(childs.size() > 0);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }

    public String getElementName() {
        return elementName;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void addChild(Element child) {
        if (isRoot) {
            child.setPath(child.getElementName());
        } else {
            child.setPath(this.path + ", " + child.getElementName());
        }
        childs.add(child);
    }

    public List<Element> getChilds() {
        return childs;
    }

    public void addAttr(String name, String value){
        attributes.putIfAbsent(name, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void print() {
        System.out.println("Element:");
        System.out.println("path = " + path);
        System.out.println("Childs = " + childs.size());
        if (!isRoot) {
            if (isLeaf()) {
                String v = elementValue;
                if (elementValue == null) {
                    v = "\"" + "\"";
                } else if (elementValue != null && elementValue.equals(" ")) {
                    v = "null";
                } else {
                    v = "\"" + v + "\"";
                }
                System.out.println("value = " + v);
            }
            if (attributes.size() > 0) {
                System.out.println("attributes:");
                String res = "";
                for (Map.Entry m : attributes.entrySet()) {
                    res += m.getKey() + " = " + "\"" + m.getValue() + "\"" + "\n";
                }
                System.out.println(res);
            } else System.out.println("No atributes\n");

            if (arrays != null) {
                System.out.println(arrays.printJson());
                System.out.println();
                System.out.println(arrays.arrayRec());
            }

        } else {
            System.out.println();
        }
    }

    public void printRec() {
        for (Element e : childs) {
            e.print();
            e.printRec();
        }
    }

    public String xmlAttr() {
        String res = "";
        if (attributes.size() > 0) {
            for (Map.Entry m : attributes.entrySet()) {
                res += " " + m.getKey() + " = " + "\"" + m.getValue() + "\"";
            }
        }
        return res;
    }

    public String printXml() {
        String res = "";
        if (childs.size() > 1) res += "<root>";
        res += printXmlRec();
        if (childs.size() > 1) res += "</root>";
        return res;
    }

    public String printArr() {
        String res = "";
        if (arrays != null) {
            res += arrays.arrayRec();
        }
        return res;
    }

    public String printXmlRec() {
        String res = "";
        for (Element e : childs) {
            if (e.elementValue != null && e.elementValue.equals(" ")) {
                res += "<" + e.elementName + e.xmlAttr() + e.printXmlRec() + " />";
            } else {
                if (e.elementValue == null) e.elementValue = "";
                res += "<" + e.elementName + e.xmlAttr() + ">" + e.elementValue + e.printArr() + e.printXmlRec() + "</" + e.elementName + ">";
            }
        }
        return res;
    }

    public String getJsonValue() {

        if (isRoot) return getElementName();

        String res = "";
        String val = "";
        String arrVal = "";
        if (arrays != null) {
            //arrVal = "[" +  arrays.printJson() + "]";
            arrVal = arrays.printJson();

        }
        if (getElementValue() == null) {
            if (childs.size() > 0) val = "";
            else val = "\"\"";
        } else if (getElementValue().equals(" ")) {
            val = "null";
        } else if (getElementValue().equals("")) {
            val = "\"\"";
        } else if (arrays != null) {
            val = "";
        }
        else {
            val = "\"" +  getElementValue() + "\"";
        }
        if (attributes.size() > 0) {

            if (childs.size() > 0) {
                for (Map.Entry m : attributes.entrySet()) {
                    String valA = "";
                    if (m.getKey() == null) {
                        valA = "\"\"";
                    } else if (m.getKey().equals(" ")) {
                        valA = "null";
                    } else if (m.getKey().equals("")) {
                        valA = "\"\"";
                    } else {
                        valA = "\"@" + m.getKey() + "\"";
                    }

                    res += valA + ": " + "\"" + m.getValue() + "\"";
                    res += ", ";
                }

            } else {
                res += "{ ";
                for (Map.Entry m : attributes.entrySet()) {
                    String valA = "";
                    if (m.getKey() == null) {
                        valA = "\"\"";
                    } else if (m.getKey().equals(" ")) {
                        valA = "null";
                    } else if (m.getKey().equals("")) {
                        valA = "\"\"";
                    } else {
                        valA = "\"@" + m.getKey() + "\"";
                    }
                    res += valA + ": " + "\"" + m.getValue() + "\"";
                    res += ", ";
                }

            }
            //res = res.substring(0, res.length()-1);
            if (childs.size() > 0) {
                res += "\"#" + getElementName() + "\"" + ": " + "{";
            } else {
                if (arrays == null) {
                    res += "\"#" + getElementName() + "\"" + ": " + val + arrVal;
                    res += " }";
                } else {
                    res += "\"#" + getElementName() + "\"" + ": " + arrVal;
                    res += " }";
                }
            }

        }
        else {
            if (arrays == null) {
                res += val;
            } else {
                res += arrVal;
            }
        }
        return res;
    }

    public String printJsonRec() {
        if (childs.size() > 0) {
            String res = "";
            for (int i = 0; i < childs.size(); i++) {
                Element e = childs.get(i);
                String rbA = (e.childs.size() > 0 && e.attributes.size() > 0) ? " }" : "";
                String lbracket = e.childs.size() > 0 ? "{ " : "";
                String rbracket = e.childs.size() > 0 ? " }" : "";
                String zpt = i < childs.size() - 1 ? ", " : "";
                res += "\"" + e.getElementName() + "\": " + lbracket + e.getJsonValue()  + e.printJsonRec() + rbA + rbracket + zpt;
                //res += "\"" + e.getElementName() + "\": " + lbracket + e.getJsonValue()  + e.printJsonRec() + rbA + rbracket + zpt;
                //res += "\"" + e.getElementName() + "\": " + lbracket + e.printJsonRec() + rbracket + zpt;
            }
            return res;
        } else {
           //return getJsonValue();
            return "";
        }
    }

    public String getAttrJ() {
        String res = "";
        if (attributes.size() > 0) {
            for (Map.Entry m : attributes.entrySet()) {
                res += "\"@" + m.getKey() + "\"" + ": " + "\"" + m.getValue() + "\"";
                res += ", ";
            }
            //res = res.substring(0, res.length()-1);
            res += "\"#" + getElementName() + "\"" + ": " ;
        }
            return res;
    }

    public String printJson() {
        if (childs.size() > 0) {
            String res = "{ ";
            for (Element e : childs) {
                //res += "\"" + e.getElementName() + "\": " + "{ ";
                String lbracket = e.childs.size() > 0 ? "{ " : "";
                String rbracket = e.childs.size() > 0 ? " }" : "";
                String lbracketA = e.attributes.size() > 0 ? "{ " : "";
                String rbracketA = e.attributes.size() > 0 ? " }" : "";
                //res += "\"" + e.getElementName() + "\": " + lbracket + e.getAttrJ() + lbracketA;
                res += "\"" + e.getJsonValue() + "\": " + lbracket + e.getAttrJ() + lbracketA + (e.arrays != null ? e.arrays.printJson() : "");
                String temp = e.printJsonRec();
                temp = temp.trim();
                res += temp;
                res += rbracketA;
                res += rbracket;
                //System.out.println(temp);
                // res += temp.substring(0, temp.length() -1);
                //res += " }";
            }
            res += " }";
            return res;
        } else {
            //res += "\"" + e.getElementName() + "\": " + lbracket + e.getAttrJ() + lbracketA;
            String res = "\"" + getJsonValue() + "\": " + arrays.printJson();
            return res;
        }
    }

}
