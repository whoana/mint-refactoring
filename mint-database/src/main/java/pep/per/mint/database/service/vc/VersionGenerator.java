package pep.per.mint.database.service.vc;

import java.util.Map;

public interface VersionGenerator {
    public String generate() throws Exception;
    public String generate(Map params) throws Exception;
}
