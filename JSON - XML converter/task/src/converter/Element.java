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
        child.setPath(this.path + ", " + child.getElementName());
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
            } else System.out.println();
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
}
