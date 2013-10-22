/**
 * Development Environment for Web
 * Copyright (C) 2012-2013 Jaroslav Tulach <jaroslav.tulach@apidesign.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://opensource.org/licenses/GPL-2.0.
 */
package org.apidesign.bck2brwsr.dew.javac;

import java.io.IOException;
import java.util.Arrays;
import org.apidesign.bck2brwsr.vmtest.Compare;
import org.apidesign.bck2brwsr.vmtest.VMTest;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class CompileTest  {
    @Compare public String testCompile() throws IOException {
        String html = "";
        String java = "package x.y.z;"
            + "class X { "
            + "   static void main(String... args) { throw new RuntimeException(\"Hello brwsr!\"); }"
            + "}";
        Compile result = Compile.create(html, java);
        
        final byte[] bytes = result.get("x/y/z/X.class");
        assertNotNull(bytes, "Class X is compiled: " + result);
        return Arrays.toString(bytes);
    }
    
    @Test public void canCompilePublicClass() throws IOException {
        String html = "";
        String java = "package x.y.z;"
            + "public class X {\n"
            + "   static void main(String... args) { throw new RuntimeException(\"Hello brwsr!\"); }\n"
            + "}\n";
        Compile result = Compile.create(html, java);
        
        final byte[] bytes = result.get("x/y/z/X.class");
        assertNotNull(bytes, "Class X is compiled: " + result);
    }
    
    @Test public void mainClassIsFirst() throws IOException {
        String html = "";
        String java = "package x.y.z;"
            + "public class X {\n"
            + "   class I1 {}\n"
            + "   class I2 {}\n"
            + "   class I3 {}\n"
            + "   class I4 {}\n"
            + "   class I5 {}\n"
            + "}\n";
        org.apidesign.bck2brwsr.dew.javac.JavacResult result = org.apidesign.bck2brwsr.dew.javac.Main.doCompile(html, java);
        assertEquals(result.getClasses().size(), 6, "Six classes generated");
        assertEquals(result.getClasses().get(0).getClassName(), "x/y/z/X.class", "Main class is the first one");
    }
    
    @Compare public String testAnnotationProcessorCompile() throws IOException {
        String html = "";
        String java = "package x.y.z;"
            + "@net.java.html.json.Model(className=\"Y\", properties={})\n"
            + "class X {\n"
            + "   static void main(String... args) { Y y = new Y(); }\n"
            + "}\n";
        Compile result = Compile.create(html, java);
        
        final byte[] bytes = result.get("x/y/z/Y.class");
        assertNotNull(bytes, "Class Y is compiled: " + result);
        
        byte[] out = new byte[256];
        System.arraycopy(bytes, 0, out, 0, Math.min(out.length, bytes.length));
        return Arrays.toString(out);
    }
    
    @Factory public static Object[] create() {
        return VMTest.create(CompileTest.class);
    }
    
    static void assertNotNull(Object obj, String msg) {
        assert obj != null : msg;
    }
    
}
