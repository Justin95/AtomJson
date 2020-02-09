
package atomjson.utils;

import java.io.File;

/**
 *
 * @author Justin Bonner
 */
public class TestingUtil {
    
    public static File getFile(String filename) {
        return new File(TestingUtil.class.getResource(filename).getPath());
    }
    
    public static File[] getFiles(String dirPath) {
        return getFile(dirPath).listFiles();
    }
    
}
