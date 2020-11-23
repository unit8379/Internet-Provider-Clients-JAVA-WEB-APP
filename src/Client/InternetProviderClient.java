package Client;

import Server.InternetProvider;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class InternetProviderClient {
    public static void main(String[] args) {
        AppManager();
    }

    private static void AppManager() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Система для работы с базой данных клиентов интернет-провайдера.\n\nВведите номер требуемого действия над базой данных." +
                "\n1. Создать в папке клиентской программы XML файл с информацией обо всех клиентах интернет-провайдера");

        String userInput = scanner.next();
        while (!tryParseInt(userInput)) {
            System.out.println("Введите корректное значение.");
            userInput = scanner.next();
        }

        switch (Integer.parseInt(userInput)) {
            case 1:
                getXmlFileOfAccounts();
                break;

            default:
                System.out.println("Выполнение программы окончено.");
        }
    }

    static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void getXmlFileOfAccounts() {
        System.setProperty("java.security.policy", "src\\Client\\client.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        String url = "127.0.0.1"; // localhost

        try {
            Registry registry = LocateRegistry.getRegistry(url);
            InternetProvider internetProvider = (InternetProvider) registry.lookup("actions");

            // Открытие потока на вывод и заполнение XML файла на стороне клиента пришедшими с сервера данными
            try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("src\\Client\\accounts.xml"))) {
                bufferedOutputStream.write(internetProvider.getXmlFileOfAccounts());
            }
        }
        catch (Exception e) {
            System.out.println("Client exception: ");
            e.printStackTrace();
        }
    }
}
