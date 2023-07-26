import net.dv8tion.jda.api.JDABuilder;

import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        startBot();
    }

    private static void startBot() {
        String currentDirectory = System.getProperty("user.dir");
        String tokenFilePath = Paths.get(currentDirectory, "src", "token-temp").toString();
        String token = readTokenFromFile(tokenFilePath);
        if (token == null || token.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Bot Token: ");
            token = scanner.nextLine();
            writeTokenToFile(token, tokenFilePath);
        }
        JDABuilder.createDefault(token).build();
        System.out.println("Bot is now online!");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("logout")) {
                clearTokenFile(tokenFilePath);
                System.out.println("Token cache has been cleaned. Program restarting......");
                startBot();
                return;
            }
        }
    }

    private static String readTokenFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load token");
            return null;
        }
    }

    private static void writeTokenToFile(String token, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(token);
            System.out.println("Token has been save to：token-temp");
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
