package org.example.controller;

import org.example.custom.exception.CustomException;
import org.example.model.service.BackupRestoreService;

import java.util.Map;

public class BackupRestoreController {
    private BackupRestoreService backupRestoreService;

    public BackupRestoreController(){
        this.backupRestoreService = new BackupRestoreService();
    }

    public int handleBackup() throws CustomException {
        return backupRestoreService.backupDatabase();
    }

    public int handleRestore(String backupFilePath) throws CustomException {
        return backupRestoreService.restoreDatabase(backupFilePath);
    }
    public void loadBackupFilesController(){
        backupRestoreService.loadBackupFiles();
    }

    public Map<Integer, String> getBackupFilesController() {
        return backupRestoreService.getBackupFiles();
    }
}
