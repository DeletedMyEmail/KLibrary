package klibrary.utils;

import java.io.File;

/**
 * This class provides methods for OS dependent paths and directory creation
 * <br>
 * Part of the <a href="https://github.com/KaitoKunTatsu/KLibrary">KLibrary</a>
 *
 * @version 1.3.2 | last edit: 24.01.2023
 * @author Joshua Hartjes
 */
public class SystemUtils {

    /**
     * Gets the path to the directory for local applications
     * Supports Windows, Linux and macOS.
     *
     * @return Directory path for local application
     * */
    public static String getLocalApplicationPath()
    {
        String lPath = System.getProperty("user.home");
        if (isMacOS())
            lPath += "/Library/Application Support";
        else if (isWindows())
            lPath += "\\AppData\\Local";

        return lPath;
    }

    /**
     * Gets the path to the directory for roaming applications.
     * Supports Windows, Linux and macOS.
     *
     * @return Directory path for local application
     * */
    public static String getRoamingApplicationPath()
    {
        String lPath = System.getProperty("user.home");
        if (isMacOS())
            lPath += "/Library/Application Support";
        else if (isWindows())
            lPath += "\\AppData\\Roaming";

        return lPath;
    }

    /**
     * Creates a directory if it doesn't already exist. Parent directories are also created
     *
     * @return true if directory already exists or was successfully created; <br> false if directory creation failed
     * */
    public static boolean createDirIfAbsent(String lDirPath)
    {
        File lDir = new File(lDirPath);
        if (!lDir.exists()) {
            int lLastDirEndIndex = lDirPath.lastIndexOf('/');
            if (lLastDirEndIndex == -1) {
                lLastDirEndIndex = lDirPath.lastIndexOf('\\');
            }

            if (!createDirIfAbsent(lDirPath.substring(0, lLastDirEndIndex))) {
                return false;
            }
            return lDir.mkdir();
        }
        return true;
    }

    /**
     * Creates a file if it doesn't already exist.
     *
     * @return true if directory already exists or was successfully created; <br> false if file creation failed
     * */
    public static boolean createFileIfAbsent(String pFielPath) {
        try {
            new File(pFielPath).createNewFile();
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * @return Name of your operating system
     * */
    public static String getOSName() {
        return System.getProperty("os.name");
    }

    /**
     * @return true if {@link #getOSName()} returns a version of Linux
     * */
    public static boolean isLinux() {
        return getOSName().startsWith("Linux");
    }

    /**
     * @return true if {@link #getOSName()} returns a version of macOS
     * */
    public static boolean isMacOS() {
        return getOSName().startsWith("Mac OS");
    }


    /**
     * @return true if {@link #getOSName()} returns a version of Windows
     * */
    public static boolean isWindows() {
        return getOSName().startsWith("Windows");
    }
}
