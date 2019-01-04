package com.eu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple TestJsch.
 */
public class TestJschTest
{
   @Test
    public void testMain(){
        TestJsch.main(null);
    }

    @Test
    public void testSSHConnection(){
        TestJsch.testSSHConnection();
    }
}
