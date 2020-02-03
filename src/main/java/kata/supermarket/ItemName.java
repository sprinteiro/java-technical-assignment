package kata.supermarket;

import lombok.EqualsAndHashCode;

import static org.apache.commons.lang3.Validate.notBlank;

@EqualsAndHashCode
public class ItemName {
    private final String value;

    public ItemName(final String value) {
        this.value = notBlank(value);
    }

    public String value() {
        return value;
    }
}
