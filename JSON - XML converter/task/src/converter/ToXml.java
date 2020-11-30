package converter;

import java.util.Map;

public class ToXml {

    Element middleElement;

    public ToXml(Element element) {
        this.middleElement = element;
    }

    public String go() {
        Map<String, String> attr = middleElement.getAttributes();
        String res = "<" + middleElement.getElementName();

        for (Map.Entry<String, String> e : attr.entrySet()) {
            res += " " + e.getKey() + "=\"" + e.getValue() + "\"";
        }

        if (middleElement.getElementValue() == null) {
            res += " />";
        } else {
            res += ">" + middleElement.getElementValue() + "</" + middleElement.getElementName() + ">";
        }
        return res;
    }
}
