import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static JDA jda;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static OnlineStatus onlineStatus = OnlineStatus.ONLINE;
    private static Activity currentActivity;

    public static void main(String[] args) {
        suppressLogbackLogs();
        suppressJdaLog();
        startBot();
    }

    private static void startBot() {
        String userHome = System.getProperty("user.home");
        String botToolPath = Paths.get(userHome, "RTXBotTool").toString();
        String currentDirectory = System.getProperty("user.dir");
        String jarName = new File(currentDirectory).getName().replace(".jar", "");
        String tokenFolderPath = Paths.get(botToolPath, jarName).toString();
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
            token = userInput;
            writeTokenToFile(token, tokenFilePath);
        }

        try {
            jda = JDABuilder.createDefault(token).build();
            jda.getPresence().setStatus(getOnlineStatus());
            System.out.println("Bot is now online!");
            System.out.println("Successfully logged in using the token in the temp file");
            System.out.println("Hint: you can use \"/\" as a prefix to execute commands");
        } catch (Exception e) {
            System.out.println("Failed to start the bot: " + e.getMessage());
            clearTokenFile(tokenFilePath);
            startBot();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("/logout")) {
                System.out.println("Token cache has been cleaned. Program restarting......");
                clearTokenFile(tokenFilePath);
                jda.shutdownNow();
                startBot();
                return;
            } else if (input.equalsIgnoreCase("/stop")) {
                System.out.println("Bot is stopping...");
                jda.shutdownNow();
                break;

            } else if (input.equalsIgnoreCase("/onlinestatus") || input.equalsIgnoreCase("/os")) {
                System.out.println("Current online status is: " + getOnlineStatus().getKey());

            } else if (input.startsWith("/onlinestatus set ") || input.startsWith("/os set ")) {
                String[] osParts = input.split(" ");
                if (osParts.length == 3 && osParts[1].equalsIgnoreCase("set")) {
                    int statusCode;
                    try {
                        statusCode = Integer.parseInt(osParts[2]);
                        if (statusCode >= 0 && statusCode <= 3) {
                            setOnlineStatus(statusCode);
                            System.out.println("Online status has been set.");
                        } else {
                            System.out.println("Invalid status code. Use 0 for ONLINE, 1 for IDLE, 2 for DO NOT DISTURB, or 3 for INVISIBLE.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid status code. Use 0 for ONLINE, 1 for IDLE, 2 for DO NOT DISTURB, or 3 for INVISIBLE.");
                    }
                } else {
                    System.out.println("Invalid command. Use \"/onlinestatus set <0|1|2|3>\" to set online status.");
                }

            } else if (input.equalsIgnoreCase("/activity")) {
                if (currentActivity != null) {
                    System.out.println("Current Activity: " + currentActivity.getType() + " - " + currentActivity.getName());
                } else {
                    System.out.println("There's no activity right now.");
                }

            } else if (input.startsWith("/activity template set")) {

                String[] acParts = input.split(" ");
                String activityCode = acParts[3];
                String[] activityNameArray = Arrays.copyOfRange(acParts, 4, acParts.length);
                String activityName = String.join(" ", activityNameArray);

                switch (activityCode) {
                    case "p":
                        currentActivity = Activity.playing(activityName);
                        break;
                    case "l":
                        currentActivity = Activity.listening(activityName);
                        break;
                    case "w":
                        currentActivity = Activity.watching(activityName);
                        break;
                    case "c":
                        currentActivity = Activity.competing(activityName);
                        break;
                    case "s":
                        String[] activityNameArrayStream = Arrays.copyOfRange(acParts, 4, acParts.length - 2);
                        String activityNameStream = String.join(" ", activityNameArrayStream);
                        String streamUrl = acParts[acParts.length - 1];
                        currentActivity = Activity.streaming(activityNameStream, streamUrl);
                        break;
                    default:
                        System.out.println("Invalid activity code. Use p for Playing, s for Streaming, l for Listening, w for Watching, or c for Competing.");
                }
                jda.getPresence().setActivity(currentActivity);
                System.out.println("Activity has been set");


            } /*else if (input.startsWith("/activity custom set")) {

                String[] acParts = input.split(" ");
                String[] activityNameArray = Arrays.copyOfRange(acParts, 3, acParts.length);
                String activityName = String.join(" ", activityNameArray);

                currentActivity = Activity.of(Activity.ActivityType.CUSTOM_STATUS, activityName);

                jda.getPresence().setActivity(currentActivity);
                System.out.println("Activity has been set");
            }*/ else if (input.equalsIgnoreCase("/activity clear")) {
                currentActivity = null;
                System.out.println("Activity has been cleared");

            } else if (input.equalsIgnoreCase("/help")) {
                System.out.println("Helper: Type \"management\" for bot management related command list");
                System.out.println("Helper: Type \"onlinestatus\" for onlinestatus related command list");
                System.out.println("Helper: Type \"activity\" for activity related command list");
                System.out.println("Helper: Type \"/leave\" to leave Helper");
                Scanner helper = new Scanner(System.in);
                while (true) {
                    String helpTarget = helper.nextLine();
                    if (helpTarget.equals("management")) {
                        System.out.println("\"/stop\" - stop the ebot and turn off the console");
                        System.out.println("\"/logout\" - logout from current bot");
                    } else if (helpTarget.equals("onlinestatus")) {
                        System.out.println("\"/onlinestatus\" (or \"/os\") - show currently online status (ex. online, idle, dnd or invis)");
                        System.out.println("\"/onlinestatus set <0|1|2|3>\" (or \"/os set <0|1|2|3>\" ) - set online status of your bot");
                    } else if (helpTarget.equals("activity")) {
                        System.out.println("\"/activity\" - show currently activity");
                        System.out.println("\"/activity template set <p|l|w|c|s> <activity name> <url(only for streaming)>\" - set bot activity with discord provided template");
                        System.out.println("\"/activity clear\" - clear bot activity");
                    } else if (helpTarget.equals("/leave")) {
                        System.out.println("You've left Helper");
                        break;
                    } else {
                        System.out.println("Helper: Unrecognized command category.");
                        System.out.println("Helper: Type \"management\" for bot management related command list");
                        System.out.println("Helper: Type \"onlinestatus\" for onlinestatus related command list");
                        System.out.println("Helper: Type \"activity\" for activity related command list");
                        System.out.println("Helper: Type \"/leave\" to leave Helper");
                    }
                    System.out.println("Helper: For more command usage, please check https://github.com/RTX4O9O/DiscordBotOnlineTool/blob/main/README.md#Anything-else");
                    helper.nextLine();
                }
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
    private static void setOnlineStatus(int statusCode) {
        switch (statusCode) {
            case 0:
                onlineStatus = OnlineStatus.ONLINE;
                break;
            case 1:
                onlineStatus = OnlineStatus.IDLE;
                break;
            case 2:
                onlineStatus = OnlineStatus.DO_NOT_DISTURB;
                break;
            case 3:
                onlineStatus = OnlineStatus.INVISIBLE;
                break;
            default:
                System.out.println("Invalid status code. Use 0 for ONLINE, 1 for IDLE, 2 for DO NOT DISTURB, or 3 for INVISIBLE.");
                break;
        }
        if (jda != null) {
            jda.getPresence().setStatus(onlineStatus);
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


    private static OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }
}
