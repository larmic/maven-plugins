package de.larmic.bitbucket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by larmic on 10.01.14.
 */
public class TodoMatcher {

    public static final Pattern TODO_PATTERN = Pattern.compile("\\s*(//|\\*) *TODO #?(\\d+)? *(.*)");
    private final boolean matches;
    private final Integer ticketNumber;
    private final String todoDescription;

    public TodoMatcher(final String value) {
        final Matcher m = TODO_PATTERN.matcher(value);

        this.matches = m.matches();

        if (this.matches) {
            m.groupCount();
            final String nullableTicketNumber = m.group(2);
            this.ticketNumber = nullableTicketNumber != null ? Integer.valueOf(nullableTicketNumber) : null;
            this.todoDescription = m.group(3);
        } else {
            this.ticketNumber = null;
            this.todoDescription = null;
        }
    }

    public boolean matches() {
        return this.matches;
    }

    public Integer getTicketNumber() {
        return this.ticketNumber;
    }

    public String getTodoDescription() {
        return this.todoDescription;
    }
}
