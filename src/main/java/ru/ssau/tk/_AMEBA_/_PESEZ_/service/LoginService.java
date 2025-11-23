package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class LoginService {
    private final HashSet<String> gotLoginFor = new HashSet<>();
    private final Timer cleanupTimer = new Timer();
    private final long cleanupTimeout = 120;

    public void addGotLoginFor(String date) {
        gotLoginFor.add(date);
        // Планируем очистку
        cleanupTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        gotLoginFor.remove(date);
                    }
                },
                cleanupTimeout * 1000
        );
    }

    public boolean hasGotLoginFor(String date) {
        return gotLoginFor.contains(date);
    }

    public void removeGotLoginFor(String date) {
        gotLoginFor.remove(date);
    }

    public void cleanup() {
        gotLoginFor.clear();
        cleanupTimer.cancel();
    }
}