import java.util.regex.Pattern;

public class LexicalRule {
    private Pattern pattern;
    private String type;

    LexicalRule(Pattern pattern, String type) {
        this.pattern = pattern;
        this.type = type;
    }

    public boolean matches(String content) {
        return pattern.matcher(content).matches();
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "type: " + type + " pattern:" + pattern;
    }
}
