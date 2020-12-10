package converter;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class JTests {

    public static String test1 = "{ \"key\": { \"child_key1\": { \"attribute1\": \"value1\", \"attribute2\": \"value2\", \"child_key1\": \"value3\" } } }";
    public static String test2 = "\"attribute1\": null , \"attribute2\": \"value2\", \"child_key1\": \"value3\"";
    public static String test3 = "{ \"child_key1\": { \"attribute1\": \"value1\", \"attribute2\": \"value2\", \"child_key3\": \"value3\" }, \"child_key2\": { \"attribute3\": \"value3\", \"attribute4\": \"value4\", \"child_key5\": \"value5\" } } ";
    public static String test4 = "{ \"key\": { \"child_key1\": { \"attribute1\": \"value1\", \"attribute2\": \"value2\", \"child_key3\": \"value3\"  }, \"child_key2\": { \"attribute3\": {  \"attribute4\": \"value4\", \"attribute5\": \"value5\" }, \"attribute6\": \"value6\", \"child_key7\": \"value7\"  } } }\n";
    public static String test5 = "{\n" +
            "    \"key\": {\n" +
            "        \"child_key1\": \"child_key_value\",\n" +
            "        \"child_key2\": \"child_key_value\",\n" +
            "        \"child_key3\": {\n" +
            "            \"child_child_key\": \"value\"\n" +
            "        },\n" +
            "        \"child_key4\": {\n" +
            "            \"child_child_key1\": \"value1\",\n" +
            "            \"child_child_key2\": \"value2\"\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    public static String test6 = "{ \"child_key1\": {\n" +
            "            \"@attribute1\": \"value1\",\n" +
            "            \"@attribute2\": \"value2\",\n" +
            "            \"#child_key1\": \"value3\"\n" +
            "        }}";
    public static String test7 = "\"inner4\": {            \"@\": 123,            \"#inner4\": \"value3\"        }";
    public static String test8 = "<transaction>        <id>6753322</id>        <number region='Russia'>8-900-000-000</number>        <special1>false</special1>        <special2>true</special2>        <empty1 />        <empty2></empty2>        <array1>            <element />            <element />        </array1>        <array2>            <element></element>            <element />            <element>123</element>            <element>123.456</element>            <element>                <key1>value1</key1>                <key2 attr=\"value2\">value3</key2>            </element>            <element attr2='value4'>value5</element>            <element>                <attr3>value4</attr3>                <elem>value5</elem>            </element>            <element>                <deep deepattr=\"deepvalue\">                    <element>1</element>                    <element>2</element>                    <element>3</element>                </deep>            </element>        </array2>        <inner1>            <inner2>                <inner3>                    <key1>value1</key1>                    <key2>value2</key2>                </inner3>            </inner2>        </inner1>        <inner4>            <inner4>value3</inner4>        </inner4>        <inner5>            <attr1>123.456</attr1>            <inner4>value4</inner4>        </inner5>        <inner6 attr2=\"789.321\">value5</inner6>        <inner7>value6</inner7>        <inner8>            <attr3>value7</attr3>        </inner8>        <inner9>            <attr4>value8</attr4>            <inner9>value9</inner9>            <something>value10</something>        </inner9>        <inner10 attr5='' />        <inner11 attr11=\"value11\">            <inner12 attr12=\"value12\">                <inner13 attr13=\"value13\">                    <inner14>v14</inner14>                </inner13>            </inner12>        </inner11>        <inner15></inner15>        <inner16>            <somekey>keyvalue</somekey>            <inner16>notnull</inner16>        </inner16>        <crazyattr1 attr1='123'>v15</crazyattr1>        <crazyattr2 attr1=\"123.456\">v16</crazyattr2>        <crazyattr3 attr1=''>v17</crazyattr3>        <crazyattr9>            <attr1>                <key>4</key>            </attr1>            <crazyattr9>v23</crazyattr9>        </crazyattr9>    </transaction>";
    @Test
    public void testO() {
        //String res = new JParser("").findJsonObject(test3);

        ArrayList<String> t = new ArrayList<>();
        ArrayList<String> res = new ArrayList<>();
        res = new XParser("").findChild(test8);
        //res.forEach(x -> System.out.println(x));
        assertEquals(t,res);

        //Boolean resB = new JParser("").isLeafAttr(test1);
        //assertEquals(true,resB);
    }

}
