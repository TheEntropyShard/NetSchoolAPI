package me.theentropyshard.netschoolapi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final Logger logger = new Logger();

    private Logger() {

    }

    public static Logger getLogger() {
        return Logger.logger;
    }

    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        this.log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    public void log(LogLevel level, String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        switch(level) {
            case INFO:
                System.out.printf("[%s INFO] %s%n", time, message);
                break;
            case WARNING:
                System.err.printf("[%s WARNING] %s%n", time, message);
                break;
            case ERROR:
                System.err.printf("[%s ERROR] %s%n", time, message);
                break;
            default:
                break;
        }
    }

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }
}
