package filetransfer.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileHandler {


    public static boolean readFile(String filePath, DataOutputStream output) {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(output);
            output.flush();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = bufferedInputStream.read(buffer)) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean writeFile(String filename, long fileSize, InputStream inputStream, Preferences preferences) {
        File parentFolder = new File("./Transferred Files/");
        if (!parentFolder.exists())
            parentFolder.mkdir();
        File file = new File("./Transferred Files/" + filename);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int count = 0;
            while (fileSize > 0 && (count = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
                fileSize -= count;
            }
            bufferedOutputStream.close();
            String sortedPath = findPath(file, preferences);
            System.out.println(sortedPath);
            parentFolder = new File(sortedPath);
            if (!parentFolder.exists()) {
                parentFolder.mkdirs();
                System.out.println("create folder");
            }
            Files.move(file.toPath(), new File(sortedPath + filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("done....");
        return true;
    }

    private static String findPath(File file, Preferences preferences) {
        String folder = preferences.getOtherPath();
        try {
            String type = Files.probeContentType(file.toPath());
            if (type != null) {
                if (type.startsWith("image")) {
                    folder = preferences.getImagesPath();
                } else if (type.startsWith("video")) {
                    folder = preferences.getVideosPath();
                } else if (type.startsWith("audio")) {
                    folder = preferences.getAudioPath();
                } else if (type.startsWith("text") || type.startsWith("application")) {
                    folder = preferences.getDocumentsPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!folder.endsWith("/"))
            folder += "/";
        return folder;
    }

    public static void writeString(String file, String data) {
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String file) {
        try {
            Path path = Paths.get(file);
            String data = Files.readString(path);
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
