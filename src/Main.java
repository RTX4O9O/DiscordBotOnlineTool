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
            System.out.print("請輸入機器人的Token：");
            token = scanner.nextLine();
            writeTokenToFile(token, tokenFilePath);
        }
        JDABuilder.createDefault(token).build();
        System.out.println("機器人已上線！");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("logout")) {
                clearTokenFile(tokenFilePath);
                System.out.println("Token已清除，程式重新開始執行。");
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
            System.out.println("無法讀取token檔案。");
            return null;
        }
    }

    private static void writeTokenToFile(String token, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(token);
            System.out.println("Token已成功存入檔案：token-temp");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("無法寫入檔案。");
        }
    }

    private static void clearTokenFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.delete()) {
                System.out.println("Token-temp檔案已清除。");
            } else {
                System.out.println("無法清除Token-temp檔案。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
