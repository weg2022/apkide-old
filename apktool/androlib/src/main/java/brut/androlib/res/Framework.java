package brut.androlib.res;

import static brut.androlib.ApkBuilder.LOGGER;
import static brut.androlib.Config.get;

import com.apkide.common.ApplicationProvider;
import com.apkide.common.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import brut.androlib.err.AndrolibException;
import brut.androlib.err.CantFindFrameworkResException;
import brut.androlib.res.decoder.ARSCDecoder;

public class Framework {
    private File mFrameworkDirectory = null;
    public void installFramework(File frameFile) throws AndrolibException {
        installFramework(frameFile, get().frameworkTag);
    }

    public void installFramework(File frameFile, String tag)
            throws AndrolibException {
        InputStream in = null;
        ZipOutputStream out = null;
        try {
            ZipFile zip = new ZipFile(frameFile);
            ZipEntry entry = zip.getEntry("resources.arsc");

            if (entry == null) {
                throw new AndrolibException("Can't find resources.arsc file");
            }

            in = zip.getInputStream(entry);
            byte[] data = IOUtils.readBytes(in);

            ARSCDecoder.ARSCData arsc = ARSCDecoder.decode(new ByteArrayInputStream(data), true, true);
            publicizeResources(data, arsc.getFlagsOffsets());

            File outFile = new File(getFrameworkDirectory(), arsc
                    .getOnePackage().getId()
                    + (tag == null ? "" : '-' + tag)
                    + ".apk");

            out = new ZipOutputStream(Files.newOutputStream(outFile.toPath()));
            out.setMethod(ZipOutputStream.STORED);
            CRC32 crc = new CRC32();
            crc.update(data);
            entry = new ZipEntry("resources.arsc");
            entry.setSize(data.length);
            entry.setMethod(ZipOutputStream.STORED);
            entry.setCrc(crc.getValue());
            out.putNextEntry(entry);
            out.write(data);
            out.closeEntry();

            //Write fake AndroidManifest.xml file to support original aapt
            entry = zip.getEntry("AndroidManifest.xml");
            if (entry != null) {
                in = zip.getInputStream(entry);
                byte[] manifest = IOUtils.readBytes(in);
                CRC32 manifestCrc = new CRC32();
                manifestCrc.update(manifest);
                entry.setSize(manifest.length);
                entry.setCompressedSize(-1);
                entry.setCrc(manifestCrc.getValue());
                out.putNextEntry(entry);
                out.write(manifest);
                out.closeEntry();
            }

            zip.close();
            LOGGER.info("Framework installed to: " + outFile);
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        } finally {
            IOUtils.safeClose(in,out);
        }
    }

    public void listFrameworkDirectory() throws AndrolibException {
        File dir = getFrameworkDirectory();
        if (dir == null) {
            LOGGER.error("No framework directory found. Nothing to list.");
            return;
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".apk")) {
                LOGGER.info(file.getName());
            }
        }
    }

    public void publicizeResources(File arscFile) throws AndrolibException {
        byte[] data = new byte[(int) arscFile.length()];

        try(InputStream in = Files.newInputStream(arscFile.toPath());
            OutputStream out = Files.newOutputStream(arscFile.toPath())) {
            //noinspection ResultOfMethodCallIgnored
            in.read(data);
            publicizeResources(data);
            out.write(data);
        } catch (IOException ex){
            throw new AndrolibException(ex);
        }
    }

    public void publicizeResources(byte[] arsc) throws AndrolibException {
        publicizeResources(arsc, ARSCDecoder.decode(new ByteArrayInputStream(arsc), true, true).getFlagsOffsets());
    }

    public void publicizeResources(byte[] arsc, ARSCDecoder.FlagsOffset[] flagsOffsets) {
        for (ARSCDecoder.FlagsOffset flags : flagsOffsets) {
            int offset = flags.offset + 3;
            int end = offset + 4 * flags.count;
            while (offset < end) {
                arsc[offset] |= (byte) 0x40;
                offset += 4;
            }
        }
    }

    public File getFrameworkDirectory() throws AndrolibException {
        if (mFrameworkDirectory != null) {
            return mFrameworkDirectory;
        }

        String path;

        // use default framework path or specified on the command line
        path = get().frameworkDirectory;

        File dir = new File(path);

        if (!dir.isDirectory() && dir.isFile()) {
            throw new AndrolibException("--frame-path is set to a file, not a directory.");
        }

        if (dir.getParentFile() != null && dir.getParentFile().isFile()) {
            throw new AndrolibException("Please remove file at " + dir.getParentFile());
        }

        if (! dir.exists()) {
            if (! dir.mkdirs()) {
                if (get().frameworkDirectory != null) {
                    LOGGER.error("Can't create Framework directory: " + dir);
                }
                throw new AndrolibException(String.format(
                        "Can't create directory: (%s). Pass a writable path with --frame-path {DIR}. ", dir
                ));
            }
        }

        if (get().frameworkDirectory == null) {
            if (! dir.canWrite()) {
                LOGGER.error(String.format("WARNING: Could not write to (%1$s), using %2$s instead...",
                        dir.getAbsolutePath(), System.getProperty("java.io.tmpdir")));
                LOGGER.error("Please be aware this is a volatile directory and frameworks could go missing, " +
                        "please utilize --frame-path if the default storage directory is unavailable");

                dir = ApplicationProvider.get().getTempDirectory();
            }
        }

        mFrameworkDirectory = dir;
        return dir;
    }

    public File getFrameworkApk(int id, String frameTag)
            throws AndrolibException {
        File dir = getFrameworkDirectory();
        File apk;

        if (frameTag != null) {
            apk = new File(dir, String.valueOf(id) + '-' + frameTag + ".apk");
            if (apk.exists()) {
                return apk;
            }
        }

        apk = new File(dir, id + ".apk");
        if (apk.exists()) {
            return apk;
        }

        if (id == 1) {
            try (InputStream in = getAndroidFrameworkResourcesAsStream();
                 OutputStream out = Files.newOutputStream(apk.toPath())) {
                IOUtils.copyBytes(in,out);
                return apk;
            } catch (IOException ex) {
                throw new AndrolibException(ex);
            }
        }

        throw new CantFindFrameworkResException(id);
    }

    public void emptyFrameworkDirectory() throws AndrolibException {
        File dir = getFrameworkDirectory();
        File apk;

        apk = new File(dir, "1.apk");

        if (! apk.exists()) {
            LOGGER.warning("Can't empty framework directory, no file found at: " + apk.getAbsolutePath());
        } else {
            try {
                if (apk.exists() && Objects.requireNonNull(dir.listFiles()).length > 1 && ! get().forceDeleteFramework) {
                    LOGGER.warning("More than default framework detected. Please run command with `--force` parameter to wipe framework directory.");
                } else {
                    for (File file : Objects.requireNonNull(dir.listFiles())) {
                        if (file.isFile() && file.getName().endsWith(".apk")) {
                            LOGGER.info("Removing " + file.getName() + " framework file...");
                            //noinspection ResultOfMethodCallIgnored
                            file.delete();
                        }
                    }
                }
            } catch (NullPointerException e) {
                throw new AndrolibException(e);
            }
        }
    }

    private InputStream getAndroidFrameworkResourcesAsStream() {
        try {
            return new FileInputStream(ApplicationProvider.get().foundAndroidFrameworkFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}