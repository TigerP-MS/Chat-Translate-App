package com.tigerpms.chatTranslateApp.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PythonServerManager {
    private Process pythonProcess;

    @PostConstruct
    public void startPythonServer() {
        try {
            String pythonServerCommand = "python3 /home/minsekan/Chat-Trasnslate-App/chatTranslateApp/src/main/python/pythonServer.py";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonServerCommand.split(" "));
            processBuilder.inheritIO();
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start Python server", e);
        }
    }

    @PreDestroy
    public void stopPythonServer() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
        }
    }
}
