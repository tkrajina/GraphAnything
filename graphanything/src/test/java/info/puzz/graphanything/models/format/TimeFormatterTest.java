package info.puzz.graphanything.models.format;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by puzz on 11/10/16.
 */

public class TimeFormatterTest {
    @Test
    public void test() throws Exception {
        TimeFormatter f = new TimeFormatter();
        Double parsed = f.parse("12:34");
        Assert.assertEquals(f.format(parsed, true), "12:34");
        Assert.assertEquals(f.format(parsed, false), "12:34:00");
    }
}