package org.runesong.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @since 1.0
 */
public class IO
{
    private static final int BUFFER_SIZE = 8192;

    static {
        new IO(); // for code coverage
    }

    private IO() {}

    //-- Binary I/O --------------------------------------------------------------------------------------------------//

    /**
     * Reads the resource at the given source path. Source paths for classpath resources must be prefixed with
     * "classpath:".
     *
     * @param path the source path of the resource to be read
     *
     * @return an array of bytes
     *
     * @throws IOException if there is an I/O error while attempting to read the resource
     *
     * @since 1.0
     */
    public static byte[] read(String path) throws IOException
    {
        if (path.startsWith("classpath:")) {
            String resourceName = path.substring("classpath:".length());
            if (resourceName.startsWith(("/"))) {
                resourceName = resourceName.substring(1);
            }
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
                 ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE))
            {
                copy(in, out);
                return out.toByteArray();
            }
        } else {
            return read(Paths.get(path));
        }
    }

    /**
     * Reads the file at the given source {@link Path}.
     *
     * @param path the source path of the file to be read
     *
     * @return an array of bytes
     *
     * @throws IOException if there is an I/O error while attempting to read the file
     *
     * @since 1.0
     */
    public static byte[] read(Path path) throws IOException
    {
        try (InputStream in = Files.newInputStream(path);
             ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE))
        {
            copy(in, out);
            return out.toByteArray();
        }
    }

    /**
     * Copies data from the given {@link InputStream} to the supplied {@link OutputStream}.
     *
     * @param in  the source from which data is to be copied
     * @param out the target where data is to be copied
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];

        long total = 0;
        int length;

        do {
            length = in.read(buffer);
            if (length > 0) {
                total += length;
                out.write(buffer, 0, length);
            }
        } while (length != -1);

        return total;
    }

    /**
     * Copies the resource at the given source path to the supplied {@link OutputStream}. Source paths for classpath
     * resources must be prefixed with "classpath:".
     *
     * @param sourcePath the source path of the resource to be copied
     * @param out        the target where data is to be copied
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the resource
     *
     * @since 1.0
     */
    public static long copy(String sourcePath, OutputStream out) throws IOException
    {
        if (sourcePath.startsWith("classpath:")) {
            String resourceName = sourcePath.substring("classpath:".length());
            if (resourceName.startsWith(("/"))) {
                resourceName = resourceName.substring(1);
            }
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
                return copy(in, out);
            }
        } else {
            return copy(Paths.get(sourcePath), out);
        }
    }

    /**
     * Copies the file at the given source {@link Path} to the supplied {@link OutputStream}.
     *
     * @param sourcePath the source path of the file to be copied
     * @param out        the target where data is to be copied
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the file
     *
     * @since 1.0
     */
    public static long copy(Path sourcePath, OutputStream out) throws IOException
    {
        return Files.copy(sourcePath, out);
    }

    /**
     * Copies data from the given {@link InputStream} to the file at the supplied target path. Note that because
     * classpath resources are considered read-only, the target path does not support the "classpath:" suffix.
     *
     * @param in          the source from which data is to be copied
     * @param targetPath  the target path where the data is to be copied
     * @param copyOptions options for copying to the target file
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(InputStream in, String targetPath, CopyOption... copyOptions) throws IOException
    {
        return copy(in, Paths.get(targetPath), copyOptions);
    }

    /**
     * Copies data from the given {@link InputStream} to a file at the supplied target {@link Path}.
     *
     * @param in          the source from which data is to be copied
     * @param targetPath  the target path of the file where the source data is to be copied
     * @param copyOptions options for copying to the target file
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(InputStream in, Path targetPath, CopyOption... copyOptions) throws IOException
    {
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        return Files.copy(in, targetPath, copyOptions);
    }

    /**
     * Copies the resource at the given source path to the provided target path. Source paths for classpath resources
     * must be prefixed with "classpath:". Note that because classpath resources are considered read-only, the target
     * path does not support the "classpath:" suffix.
     *
     * @param sourcePath the source path of the resource to be copied
     * @param targetPath the target path of the file where the source data is to be copied
     * @param copyOptions options for copying to the target file
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the resource
     *
     * @since 1.0
     */
    public static long copy(String sourcePath, String targetPath, CopyOption... copyOptions) throws IOException
    {
        if (sourcePath.startsWith("classpath:")) {
            String resourceName = sourcePath.substring("classpath:".length());
            if (resourceName.startsWith(("/"))) {
                resourceName = resourceName.substring(1);
            }
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
                return copy(in, targetPath, copyOptions);
            }
        } else {
            return copy(Paths.get(sourcePath), Paths.get(targetPath), copyOptions);
        }
    }

    /**
     * Copies the file at the given source {@link Path} to the provided target {@link Path}.
     *
     * @param sourcePath  the source path of the file to be copied
     * @param targetPath  the target path of the file where the source data is to be copied
     * @param copyOptions options for copying to the target file
     *
     * @return the number of bytes copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the file
     *
     * @since 1.0
     */
    public static long copy(Path sourcePath, Path targetPath, CopyOption... copyOptions) throws IOException
    {
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        return Files.size(Files.copy(sourcePath, targetPath, copyOptions));
    }

    //-- Text I/O ----------------------------------------------------------------------------------------------------//

    /**
     * Copies data from the given {@link Reader} to the supplied {@link Writer}.
     *
     * @param in  the source from which data is to be copied
     * @param out the target where data is to be copied
     *
     * @return the number of characters copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(Reader in, Writer out) throws IOException
    {
        char[] buffer = new char[BUFFER_SIZE];

        long total = 0;
        int length;

        do {
            length = in.read(buffer);
            if (length > 0) {
                total += length;
                out.write(buffer, 0, length);
            }
        } while (length != -1);

        return total;
    }

    /**
     * Copies the resource at the given source path to the supplied {@link Writer} using the provided source character
     * encoding. Source paths for classpath resources must be prefixed with "classpath:".
     *
     * @param sourcePath     the source path of the resource to be copied
     * @param sourceEncoding the encoding to use when reading the source file
     * @param out            the target where data is to be copied
     *
     * @return the number of characters copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the resource
     *
     * @since 1.0
     */
    public static long copy(String sourcePath, Charset sourceEncoding, Writer out) throws IOException
    {
        if (sourcePath.startsWith("classpath:")) {
            String resourceName = sourcePath.substring("classpath:".length());
            if (resourceName.startsWith(("/"))) {
                resourceName = resourceName.substring(1);
            }
            try (InputStream byteStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
                 Reader in = new InputStreamReader(byteStream, sourceEncoding))
            {
                return copy(in, out);
            }
        } else {
            return copy(Paths.get(sourcePath), sourceEncoding, out);
        }
    }

    /**
     * Copies the resource from the given source path to the supplied {@link Writer} using the provided source character
     * encoding.
     *
     * @param sourcePath     the source path of the file to be copied
     * @param sourceEncoding the encoding to use when reading the source file
     * @param out            the target where data is to be copied
     *
     * @return the number of characters copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the file
     *
     * @since 1.0
     */
    public static long copy(Path sourcePath, Charset sourceEncoding, Writer out) throws IOException
    {
        try (InputStream byteStream = Files.newInputStream(sourcePath);
             Reader in = new InputStreamReader(byteStream, sourceEncoding))
        {
            return copy(in, out);
        }
    }

    /**
     * Copies data from the supplied {@link Reader} to the given target path using the provided target character
     * encoding. Note that because classpath resources are considered read-only, the target path does not support the
     * "classpath:" suffix.
     *
     * @param in             the source from which data is to be copied
     * @param targetPath     the target path of the file where the source data is to be copied
     * @param targetEncoding the encoding to use when writing the target file
     *
     * @return the number of characters copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(Reader in, String targetPath, Charset targetEncoding) throws IOException
    {
        return copy(in, Paths.get(targetPath), targetEncoding);
    }

    /**
     * Copies data from the supplied {@link Reader} to the given target path using the provided target character
     * encoding.
     *
     * @param in             the source from which data is to be copied
     * @param targetPath     the target path of the file where the source data is to be copied
     * @param targetEncoding the encoding to use when writing the target file
     *
     * @return the number of characters copied
     *
     * @throws IOException if there is an I/O error while attempting to copy the data
     *
     * @since 1.0
     */
    public static long copy(Reader in, Path targetPath, Charset targetEncoding) throws IOException
    {
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        try (OutputStream outputStream = Files.newOutputStream(targetPath);
             Writer out = new OutputStreamWriter(outputStream, targetEncoding))
        {
            return copy(in, out);
        }
    }
}
