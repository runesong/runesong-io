package org.runesong.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IO
{
    private static final int BUFFER_SIZE = 8192;

    static {
        new IO(); // for code coverage
    }

    private IO() {}

    //-- Binary I/O --------------------------------------------------------------------------------------------------//

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

    public static byte[] read(Path path) throws IOException
    {
        try (InputStream in = Files.newInputStream(path);
             ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE))
        {
            copy(in, out);
            return out.toByteArray();
        }
    }

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

    public static long copy(Path sourcePath, OutputStream out) throws IOException
    {
        return Files.copy(sourcePath, out);
    }

    public static long copy(InputStream in, String targetPath, CopyOption... copyOptions) throws IOException
    {
        return copy(in, Paths.get(targetPath), copyOptions);
    }

    public static long copy(InputStream in, Path targetPath, CopyOption... copyOptions) throws IOException
    {
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        return Files.copy(in, targetPath, copyOptions);
    }

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

    public static long copy(Path sourcePath, Path targetPath, CopyOption... copyOptions) throws IOException
    {
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        return Files.size(Files.copy(sourcePath, targetPath, copyOptions));
    }

    //-- Text I/O ----------------------------------------------------------------------------------------------------//

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

    public static long copy(Path sourcePath, Charset sourceEncoding, Writer out) throws IOException
    {
        try (InputStream byteStream = Files.newInputStream(sourcePath);
             Reader in = new InputStreamReader(byteStream, sourceEncoding))
        {
            return copy(in, out);
        }
    }

    public static long copy(Reader in, String targetPath, Charset targetEncoding) throws IOException
    {
        return copy(in, Paths.get(targetPath), targetEncoding);
    }

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
