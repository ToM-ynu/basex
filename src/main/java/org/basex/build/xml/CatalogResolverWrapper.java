package org.basex.build.xml;

import java.lang.reflect.Method;

import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;

/**
 * Wraps the CatalogResolver Object.
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Michael Seiferle
 */
public final class CatalogResolverWrapper {
  /** Resolver if availiable. */
  private static final Object CM = init();


  /** private Constructor, no instantiation. */
  private CatalogResolverWrapper() {

  }

  /**
   * Initializes the CatalogManager.
   * @return CatalogManager instance iff found.
   */
  private static Object init() {
    Object cmm = null;
    try {
      cmm = Class.forName("org.apache.xml.resolver.CatalogManager").
      newInstance();
    } catch(Exception e) { }
    return cmm;
  }  
  
  /**
   * Returns a CatalogResolver instance or null if it could not be found.
   * @return CatalogResolver if availiable
   */
  public static Object getInstance() {
    return CM;
  }

  /**
   * Decorates the XMLReader with the catalog resolver 
   * if it has been found on the classpath. Does nothing otherwise.
   * @param r XMLReader
   * @param cat path.
   */
  public static void set(final XMLReader r, final String cat)  {
    if(null == CM) return;
      try {
        Class<?> clazz = Class.
          forName("org.apache.xml.resolver.CatalogManager");
        Method m = clazz.getMethod("setCatalogFiles", String.class);
        m.invoke(CM, cat);
        m = clazz.getMethod("setIgnoreMissingProperties", boolean.class);
        m.invoke(CM, true);
        m = clazz.getMethod("setPreferPublic", boolean.class);
        m.invoke(CM, true);
        m = clazz.getMethod("setUseStaticCatalog", boolean.class);
        m.invoke(CM, false);
        m = clazz.getMethod("setVerbosity", int.class);
        m.invoke(CM, 0);
        r.setEntityResolver((EntityResolver) Class.forName(
            "org.apache.xml.resolver.tools.CatalogResolver").getConstructor(
            new Class[] {
                Class.forName("org.apache.xml.resolver.CatalogManager")}).
                newInstance(CM));

      } catch(Exception e) { }
  }

}
