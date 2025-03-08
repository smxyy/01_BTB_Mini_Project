package org.example.model.service;

import org.example.custom.exception.CustomException;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class BackupRestoreService {
    private static Map<Integer, String> backupFiles = new HashMap<>();
    private static final String BACKUP_FILE_PATH = "backup_files.properties";

    // Load backup files from properties file
    public void loadBackupFiles() {
        try (InputStream input = new FileInputStream(BACKUP_FILE_PATH)) {
            Properties properties = new Properties();
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                int index = Integer.parseInt(key);
                String fileName = properties.getProperty(key);
                backupFiles.put(index, fileName);
            }
        } catch (IOException e) {
            // If the file doesn't exist or can't be loaded, we'll simply start with an empty map
            System.err.println("Failed to load backup files: " + e.getMessage());
        }
    }

    // Save the backup files to the properties file
    public static void saveBackupFiles() {
        try (OutputStream output = new FileOutputStream(BACKUP_FILE_PATH)) {
            Properties properties = new Properties();

            // Ensure that we don't overwrite the existing properties
            for (Map.Entry<Integer, String> entry : backupFiles.entrySet()) {
                properties.setProperty(String.valueOf(entry.getKey()), entry.getValue());
            }
            properties.store(output, "Backup Files");  // Store the properties with a comment header
        } catch (IOException e) {
            System.err.println("Failed to save backup files: " + e.getMessage());
        }
    }

    // Method to load properties from the config.properties file
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Method to get the pgDumpPath from properties
    public static String getPgDumpPath(Properties properties) {
        return properties.getProperty("pgDumpPath");
    }

    // Method to get the database user from properties
    public static String getDbUser(Properties properties) {
        return properties.getProperty("db.user");
    }

    // Method to get the database host from properties
    public static String getDbHost(Properties properties) {
        return properties.getProperty("db.host");
    }

    // Method to get the database port from properties
    public static String getDbPort(Properties properties) {
        return properties.getProperty("db.port");
    }

    // Method to get the database name from properties
    public static String getDbName(Properties properties) {
        return properties.getProperty("db.name");
    }

    // Backup method using the loaded properties
    public int backupDatabase() throws CustomException {
        Properties properties = loadProperties();  // Load all properties

        String pgDumpPath = getPgDumpPath(properties);  // Load the pgDumpPath
        String dbUser = getDbUser(properties);  // Load db user
        String dbHost = getDbHost(properties);  // Load db host
        String dbPort = getDbPort(properties);  // Load db port
        String dbName = getDbName(properties);  // Load db name

        if (pgDumpPath == null || dbUser == null || dbHost == null || dbPort == null || dbName == null) {
            System.err.println("Some properties are not properly configured.");
            return -1;
        }

        // Make sure backupFiles is loaded before starting the backup
        loadBackupFiles();

        String dateFormat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String backupFileName = String.format("Version%s-product-backup-%s.sql", getNextIndex(), dateFormat);

        // Store the backup file name in the collection with an index
        backupFiles.put(getNextIndex(), backupFileName);

        // Save backup files to the properties file
        saveBackupFiles();

        // Generate the pg_dump command
        String command = String.format("%s -U %s -h %s -p %s -F p -b -v -f %s %s",
                pgDumpPath, dbUser, dbHost, dbPort, backupFileName, dbName);

        // Execute the backup command
        try {
            Process process = Runtime.getRuntime().exec(command);
            int result = process.waitFor();  // Wait for the process to complete
            return result;
        } catch (IOException | InterruptedException e) {
            throw new CustomException(e.getMessage());
        }
    }

    // Helper method to get the next available index for backup
    private int getNextIndex() {
        // If no backups exist, start at 1
        if (backupFiles.isEmpty()) {
            return 1;
        }

        // Find the highest index and increment by 1
        int maxIndex = Collections.max(backupFiles.keySet());
        return maxIndex + 1;
    }

    public Map<Integer, String> getBackupFiles() {
        return backupFiles;
    }

    // Restore the database from a backup file
    public int restoreDatabase(String backupFilePath) throws CustomException{
        Properties properties = loadProperties();  // Load all properties

        String pgRestorePath = getPgDumpPath(properties);
        String dbUser = getDbUser(properties);  // Load db user
        String dbHost = getDbHost(properties);  // Load db host
        String dbPort = getDbPort(properties);  // Load db port
        String dbName = getDbName(properties);  // Load db name

        // Validate that all necessary properties are loaded
        if (pgRestorePath == null || dbUser == null || dbHost == null || dbPort == null || dbName == null || backupFilePath == null) {
            throw new CustomException("Missing required properties for restore operation.");
        }

        // Construct the restore command
        String command = String.format("%s -U %s -h %s -p %s -d %s -v %s",
                pgRestorePath, dbUser, dbHost, dbPort, dbName, backupFilePath);

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            return exitCode;
        } catch (IOException | InterruptedException e){
            throw new CustomException(e.getMessage());
        }
    }
}
