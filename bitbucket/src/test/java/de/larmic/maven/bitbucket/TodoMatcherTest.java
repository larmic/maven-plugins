package de.larmic.maven.bitbucket;

import de.larmic.maven.bitbucket.TodoMatcher;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by larmic on 10.01.14.
 */
public class TodoMatcherTest {

    public static final String TICKET_DESCRIPTION = "ticket description";

    @Test
    public void testMatchNoTodo() throws Exception {
        final TodoMatcher matcher = new TodoMatcher("// no TODO");
        Assert.assertFalse(matcher.matches());
        Assert.assertNull(matcher.getTicketNumber());
        Assert.assertNull(matcher.getTodoDescription());
    }

    @Test
    public void testMatchTodoWithoutTicket() throws Exception {
        final TodoMatcher matcher = new TodoMatcher("// TODO " + TICKET_DESCRIPTION);
        Assert.assertTrue(matcher.matches());
        Assert.assertNull(matcher.getTicketNumber());
        Assert.assertEquals(matcher.getTodoDescription(), TICKET_DESCRIPTION);
    }

    @Test
    public void testMatchTodoWithTicket() throws Exception {
        final Integer ticketNumber = 12334;
        final TodoMatcher matcher = new TodoMatcher("// TODO " + ticketNumber + TICKET_DESCRIPTION);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(matcher.getTicketNumber(), ticketNumber);
        Assert.assertEquals(matcher.getTodoDescription(), TICKET_DESCRIPTION);
    }
}
