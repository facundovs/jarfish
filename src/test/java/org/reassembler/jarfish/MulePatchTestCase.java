package org.reassembler.jarfish;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

public class MulePatchTestCase extends TestCase
{

    public void testMulePatchCollision()
    {
        JarIndexer jarIndexer = new JarIndexer();
        jarIndexer.index(new File("src/test/resources/mule-jars"), false);
        List conflicts = jarIndexer.findDuplicateClasses(new Config());
        assertEquals(2, conflicts.size());
        List firstConflict = (List) conflicts.get(0);
        List secondConflict = (List) conflicts.get(1);
        assertEquals(2, firstConflict.size());
        assertEquals(2, secondConflict.size());

        assertCollision((EntryMeta) firstConflict.get(0), "com/ning/http/client/providers/grizzly/HttpTransactionContext.class", "src/test/resources/mule-jars/SE-5583-3.8.4.jar");
        assertCollision((EntryMeta) firstConflict.get(1), "com/ning/http/client/providers/grizzly/HttpTransactionContext.class", "src/test/resources/mule-jars/SE-5781-3.8.4.jar");
    }

    private void assertCollision (EntryMeta collision, String className, String jarName)
    {
        assertEquals(collision.getEntryName(), className);
        assertTrue(collision.getJarName().endsWith(jarName));
    }
}
