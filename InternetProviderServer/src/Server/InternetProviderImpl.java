package Server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

import Server.AccountsHierarchy.Accounts;

public class InternetProviderImpl implements InternetProvider {
    private String fromName;

    public InternetProviderImpl(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return "This is the Message from " + fromName;
    }

    public byte[] getXmlFileOfAccounts() {
        System.setProperty("javax.xml.accessExternalDTD", "all");  // свойство для разрешения доступа к сторонним DTD
        byte[] xmlFileByteArray = new byte[8]; // тут проинициализировал, чтобы компилятор не ругался. потом эта переменная примет массив с нужным кол-вом байт

        // В БУДУЩЕМ ЗДЕСЬ ДОЛЖНО БЫТЬ ЗАПОЛНЕНИЕ КЛАССОВ AccountsHierarchy ИЗ БАЗЫ ДАННЫХ
        //System.out.println("Введите имя входного XML файла:");
        String inputFileName = makeCorrectXmlName("InternetProviderServer\\src\\Server\\accounts-new.xml");

        //System.out.println("Введите имя выходного XML файла:");
        String outputFileName = makeCorrectXmlName("InternetProviderServer\\src\\Server\\xml-to-transfer.xml");

        try {
            // связывание класса с xml и чтение из него.
            JAXBContext jaxbContext = JAXBContext.newInstance(Accounts.class);
            InputStream inputStream = new FileInputStream(inputFileName);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Accounts accounts = (Accounts)unmarshaller.unmarshal(inputStream);
            inputStream.close();

            // изменение классов
//            accounts.getAccount().get(0).getTariff().get(0).getService().get(0).getInternet().setSpeed("200");
//            accounts.getAccount().get(1).getTariff().get(0).setCost("150");

            // запись в новый xml
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  // формат под xml стиль
            File outFile = new File(outputFileName);
            outFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(outFile);
            marshaller.marshal(accounts, outputStream);
            outputStream.close();

            // запись XML файла в массив байт для передачи
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(outFile))) {
                xmlFileByteArray = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(xmlFileByteArray);
            }
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return xmlFileByteArray;
    }

    private String makeCorrectXmlName(String inputString) {
        StringBuilder stringBuilder = new StringBuilder(inputString);
        if (!stringBuilder.toString().endsWith(".xml")) {
            stringBuilder.append(".xml");
        }
        return stringBuilder.toString();
    }
}
