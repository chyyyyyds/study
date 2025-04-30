package classLoader;

import java.net.URL;
import java.net.URLClassLoader;

public class MyURLClassLoader extends URLClassLoader {
    public MyURLClassLoader(URL[] urls) {
        super(urls);
    }
}
