package converter;

import java.util.Map;

public class ToJson {

    Element middleElement;

    public ToJson(Element element) {
        this.middleElement = element;
    }

    public String go() {
        String res = "";
        Map<String, String> attr = middleElement.getAttributes();
        if (attr.size() != 0) {
            res += "{\"" + middleElement.getElementName() + "\":{";
            for (Map.Entry<String, String> e : attr.entrySet()) {
                res += "\"@" + e.getKey() + "\":\"" + e.getValue() + "\",";
            }
            String n = middleElement.getElementValue() == null ? middleElement.getElementValue() :
                    "\"" + middleElement.getElementValue() + "\"";
            res += "\"#" + middleElement.getElementName() + "\":" + n + "}}";

        } else {
            res += "{\"" + middleElement.getElementName() + "\":\"" + middleElement.getElementValue() + "\"}";
        }

        return res;
    }

}
