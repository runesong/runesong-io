package org.runesong.io.tests;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.runesong.io.IO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.logging.Level.WARNING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IOTest
{
    private static final Logger LOG = Logger.getLogger(IOTest.class.getName());

    private static final String testInputResource = "target/test-classes/UTF-8-test.txt";
    private static final String testClasspathResource = "classpath:UTF-8-test.txt";
    private static final String testOutputResource = "target/test-output/UTF-8-test.txt";

    private static byte[] BYTE_DATA;
    private static char[] CHAR_DATA;

    private static ByteArrayInputStream bytesIn;
    private static ByteArrayOutputStream bytesOut;

    private static CharArrayReader charsIn;
    private static CharArrayWriter charsOut;

    @BeforeClass
    public static void beforeClass() throws IOException
    {
        Files.deleteIfExists(Paths.get(testOutputResource));
        Files.deleteIfExists(Paths.get(testOutputResource).getParent());

        try (FileWriter out = new FileWriter(testInputResource)) {
            out.write("Characters U+0000 to U+007F\n");
            for (int i = 0x0000; i <= 0x007F; i++) {
                if (((i + 1) % 80) == 0) {
                    out.write("\n");
                }
                out.write(i);
            }
            out.write("Characters U+0080 to U+07FF\n");
            for (int i = 0x0080; i <= 0x07FF; i++) {
                if (((i + 1) % 80) == 0) {
                    out.write("\n");
                }
                out.write(i);
            }
            out.write("Characters U+0800 to U+FFFF\n");
            for (int i = 0x0800; i <= 0xFFFF; i++) {
                if (((i + 1) % 80) == 0) {
                    out.write("\n");
                }
                out.write(i);
            }
            out.write("Characters U+10000 to U+10FFFF\n");
            for (int i = 0x10000; i <= 0x10FFFF; i++) {
                if (((i + 1) % 80) == 0) {
                    out.write("\n");
                }
                out.write(i);
            }
        }

        BYTE_DATA = Files.readAllBytes(Paths.get(testInputResource));
        CHAR_DATA = new String(BYTE_DATA, UTF_8).toCharArray();

        bytesIn = new ByteArrayInputStream(BYTE_DATA);
        bytesOut = new ByteArrayOutputStream(BYTE_DATA.length);

        charsIn = new CharArrayReader(CHAR_DATA);
        charsOut = new CharArrayWriter(CHAR_DATA.length);

        IO.copy(bytesIn, testOutputResource);
        bytesIn.reset();
    }

    @Before
    public void before() throws IOException
    {
        Files.deleteIfExists(Paths.get(testOutputResource));
        Files.deleteIfExists(Paths.get(testOutputResource).getParent());
        bytesIn.reset();
        bytesOut.reset();
        charsIn.reset();
        charsOut.reset();
    }

    @AfterClass
    public static void afterClass()
    {
        try { bytesIn.close(); } catch (IOException ex) { LOG.log(WARNING, ex.getMessage(), ex); }
        try { bytesOut.close(); } catch (IOException ex) { LOG.log(WARNING, ex.getMessage(), ex); }

        charsIn.close();
        charsOut.close();
    }

    @Test
    public void read_Path() throws IOException
    {
        assertThat(IO.read(Paths.get(testInputResource)), is(equalTo(BYTE_DATA)));
        assertThat(IO.read(testInputResource), is(equalTo(BYTE_DATA)));
        assertThat(IO.read(testClasspathResource), is(equalTo(BYTE_DATA)));
        assertThat(IO.read(testClasspathResource.replace("classpath:", "classpath:/")), is(equalTo(BYTE_DATA)));
    }

    @Test
    public void copy_InputStream_to_OutputStream() throws IOException
    {
        assertThat(IO.copy(bytesIn, bytesOut), is(equalTo((long) BYTE_DATA.length)));
        bytesIn.reset();
    }

    @Test
    public void copy_Path_to_Path() throws IOException
    {
        assertThat(IO.copy(Paths.get(testInputResource), Paths.get(testOutputResource), REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(Paths.get(testOutputResource)), is(equalTo(BYTE_DATA)));

        assertThat(IO.copy(testInputResource, testOutputResource, REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(testOutputResource), is(equalTo(BYTE_DATA)));

        assertThat(IO.copy(testClasspathResource, testOutputResource, REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(testOutputResource), is(equalTo(BYTE_DATA)));

        assertThat(IO.copy(testClasspathResource.replace("classpath:", "classpath:/"), testOutputResource, REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(testOutputResource), is(equalTo(BYTE_DATA)));
    }

    @Test
    public void copy_Path_to_OutputStream() throws IOException
    {
        assertThat(IO.copy(Paths.get(testInputResource), bytesOut), is(equalTo((long) BYTE_DATA.length)));
        assertThat(bytesOut.toByteArray(), is(equalTo(BYTE_DATA)));
        bytesOut.reset();

        assertThat(IO.copy(testInputResource, bytesOut), is(equalTo((long) BYTE_DATA.length)));
        assertThat(bytesOut.toByteArray(), is(equalTo(BYTE_DATA)));
        bytesOut.reset();

        assertThat(IO.copy(testClasspathResource, bytesOut), is(equalTo((long) BYTE_DATA.length)));
        assertThat(bytesOut.toByteArray(), is(equalTo(BYTE_DATA)));
        bytesOut.reset();

        assertThat(IO.copy(testClasspathResource.replace("classpath:", "classpath:/"), bytesOut), is(equalTo((long) BYTE_DATA.length)));
        assertThat(bytesOut.toByteArray(), is(equalTo(BYTE_DATA)));
        bytesOut.reset();
    }

    @Test
    public void copy_InputStream_to_Path() throws IOException
    {
        assertThat(IO.copy(bytesIn, Paths.get(testOutputResource), REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(Paths.get(testOutputResource)), is(equalTo(BYTE_DATA)));
        bytesIn.reset();

        assertThat(IO.copy(bytesIn, testOutputResource, REPLACE_EXISTING), is(equalTo((long) BYTE_DATA.length)));
        assertThat(IO.read(testOutputResource), is(equalTo(BYTE_DATA)));
        bytesIn.reset();
    }

    @Test
    public void copy_Reader_to_Writer() throws IOException
    {
        assertThat(IO.copy(charsIn, charsOut), is(equalTo((long) CHAR_DATA.length)));
        assertThat(charsOut.toCharArray(),is(equalTo(CHAR_DATA)));
        charsIn.reset();
        charsOut.reset();
    }
    @Test
    public void copy_Path_to_Writer() throws IOException
    {
        assertThat(IO.copy(Paths.get(testInputResource), UTF_8, charsOut), is(equalTo((long) CHAR_DATA.length)));
        assertThat(charsOut.toCharArray(), is(equalTo(CHAR_DATA)));
        charsOut.reset();

        assertThat(IO.copy(testInputResource, UTF_8, charsOut), is(equalTo((long) CHAR_DATA.length)));
        assertThat(charsOut.toCharArray(), is(equalTo(CHAR_DATA)));
        charsOut.reset();

        assertThat(IO.copy(testClasspathResource, UTF_8, charsOut), is(equalTo((long) CHAR_DATA.length)));
        assertThat(charsOut.toCharArray(), is(equalTo(CHAR_DATA)));
        charsOut.reset();

        assertThat(IO.copy(testClasspathResource.replace("classpath:", "classpath:/"), UTF_8, charsOut), is(equalTo((long) CHAR_DATA.length)));
        assertThat(charsOut.toCharArray(), is(equalTo(CHAR_DATA)));
        charsOut.reset();
    }

    @Test
    public void copy_Reader_to_Path() throws IOException
    {
        assertThat(IO.copy(charsIn, Paths.get(testOutputResource), UTF_8), is(equalTo((long) CHAR_DATA.length)));
        assertThat(IO.read(Paths.get(testOutputResource)), is(equalTo(BYTE_DATA)));
        charsIn.reset();

        assertThat(IO.copy(charsIn, testOutputResource, UTF_8), is(equalTo((long) CHAR_DATA.length)));
        assertThat(IO.read(testOutputResource), is(equalTo(BYTE_DATA)));
        charsIn.reset();
    }
}
