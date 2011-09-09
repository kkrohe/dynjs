package org.dynjs.runtime;

import org.dynjs.exception.ReferenceError;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class DynObjectTest {

    private DynObject object;

    @Before
    public void setUp() throws Exception {
        object = new DynObject();
    }

    @Test
    public void hasDefaultAttributes() {
        assertThat(object.getProperty("prototype")).isNotNull();
    }

    @Test(expected = ReferenceError.class)
    public void throwsReferenceErrorOnMissingReference() {
        object.resolve("inexistentAttribute");
    }

    @Test
    public void aDefinedObjectExists() {
        object.define("meh", new DynObject());

        System.out.println(object.resolve("meh"));
        assertThat(object.resolve("meh")).isNotNull();

    }
}