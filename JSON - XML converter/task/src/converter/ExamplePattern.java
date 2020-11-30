package converter;

public class ExamplePattern {
    public static final String TAG_NAME = "(?<=<)\\w+(?=\\s|>)";
    public static final String TAG_VALUE = "(?<=>).+(?=<)";
    public static final String TAG_ATTR_NAME = "(?<=\\s)\\w+(?=(\\s=)|=)";
    public static final String TAG_ATTR_VALUE = "(?<=\")\\w+(?=\")";
    public static final String JSON_KEY = "(?<=\"#).+?(?=\")";
    public static final String JSON_VALUE = "((?<=\")[^@#].+?(?=\"[^,]))|(null)";
    public static final String JSON_ATTR_NAME = "(?<=\"@).+?(?=\")";
    public static final String JSON_ATTR_VALUE = "((?<=\")[0-9a-zA-Z.,!?]+(?=\",))|(?<=\\s)[0-9]+?(?=,)";
}
