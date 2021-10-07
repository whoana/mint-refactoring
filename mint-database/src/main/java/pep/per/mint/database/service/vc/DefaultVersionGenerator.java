package pep.per.mint.database.service.vc;

import java.util.Map;
import java.util.UUID;

public class DefaultVersionGenerator implements VersionGenerator {

    @Override
    public String generate() throws Exception {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generate(Map params) throws Exception {
        return UUID.randomUUID().toString();
    }
}
