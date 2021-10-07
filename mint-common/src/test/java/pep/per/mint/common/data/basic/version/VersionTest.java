package pep.per.mint.common.data.basic.version;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testSetGetSize() {
        VersionData versionData = new VersionData();
        versionData.setSize(100);
        System.out.println("versionData size:" + versionData.getSize());
    }

}