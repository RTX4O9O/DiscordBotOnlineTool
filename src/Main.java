import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.JDABuilder;
import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

public class Main {
    private static JDA jda;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        suppressLogbackLogs();
        suppressJdaLog();
        startBot();
    }

    private static void startBot() {
        String userName = System.getProperty("user.name");
        String currentDirectory = System.getProperty("user.dir");
        String jarName = new File(currentDirectory).getName().replace(".jar", "");
        String botToolPath = Paths.get("C:", "Users", userName, "RTXBotTool").toString();
        String tokenFolderPath = Paths.get(botToolPath, jarName, "token-temp").toString();

        File tokenFolder = new File(tokenFolderPath);
        if (!tokenFolder.exists()) {
            tokenFolder.mkdirs();
        }

        String tokenFilePath = Paths.get(tokenFolderPath, "token-temp").toString();
        String token = readTokenFromFile(tokenFilePath);
        if (token == null || token.isEmpty() || !validateToken(token)) {
            System.out.println("The provided token is invalid!");
            clearTokenFile(tokenFilePath);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Bot Token: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("stop")) {
                System.out.println("Bot is stopping...");
                executorService.shutdownNow();
                return;
            }

            token = userInput;
            writeTokenToFile(token, tokenFilePath);
        }

        try {
            jda = JDABuilder.createDefault(token).build();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            System.out.println("Bot is now online!");
            System.out.println("Successfully logged in using the token in the temp file");
        } catch (Exception e) {
            System.out.println("Failed to start the bot: " + e.getMessage());
            clearTokenFile(tokenFilePath);
            startBot();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("logout")) {
                System.out.println("Token cache has been cleaned. Program restarting......");
                clearTokenFile(tokenFilePath);
                jda.shutdownNow();
                startBot();
                return;
            } else if (input.equalsIgnoreCase("stop")) {
                System.out.println("Bot is stopping...");
                jda.shutdownNow();
                break;
            }
        }
    }

    private static void suppressLogbackLogs() {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.ERROR);
    }

    private static void suppressJdaLog() {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
    }

    private static boolean validateToken(String token) {
        return !token.isEmpty();
    }

    private static String readTokenFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load token");
            return null;
        }
    }

    private static void writeTokenToFile(String token, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(token);
            System.out.println("Token has been saved to: token-temp");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write token into cache");
        }
    }

    private static void clearTokenFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.delete()) {
                System.out.println("Token-temp has been cleaned");
            } else {
                System.out.println("Failed to clean Token-temp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
