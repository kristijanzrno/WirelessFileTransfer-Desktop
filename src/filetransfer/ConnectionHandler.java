package filetransfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private ServerSocket serverSocket;
    private Socket androidDevice;
    private int port = 9270;
    private boolean isRunning = true;
    private boolean receiving;

    DataInputStream input = null;
    DataOutputStream output = null;

    public ConnectionHandler() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                androidDevice = serverSocket.accept();
                if (androidDevice != null)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        System.out.println("Connected...");
        try {
            input = new DataInputStream(androidDevice.getInputStream());
            output = new DataOutputStream(androidDevice.getOutputStream());

            while (isRunning) {
                String receivedData = receiveMessage();
                String[] message = receivedData.split(Constants.DATA_SEPARATOR);
                if (!receivedData.isEmpty())
                    System.out.println(message[0]);
                switch (message[0]) {
                    case Constants.FILE_SEND_MESSAGE:
                        System.out.println("Preparing to receive data. Receiving " + message[1] + " items.");
                        break;
                    case Constants.FILE_NAME_MESSAGE:
                        String filename = message[1];
                        long fileSize = Long.parseLong(message[2]);
                        System.out.println("Receiving " + filename + "...");
                        receiveFile(filename, fileSize, input);
                        break;
                    case Constants.FILE_SEND_COMPLETE_MESSAGE:
                        break;

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveMessage() {
        if (input != null) {
            try {
                return input.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return "Could not read data";
            }
        }
        return "";
    }

    private boolean receiveFile(String filename, long fileSize, InputStream inputStream) {
        receiving = true;
        File file = new File(filename);
        System.out.println("receiving....");
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int count = 0;
            while (fileSize > 0 && (count = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
                fileSize -= count;
            }
            bufferedOutputStream.close();
            //inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        receiving = false;
        System.out.println("done....");
        return true;
    }

    public void sendMessage(String message) {
        if (output != null) {
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() {
        isRunning = false;
    }


    public String getPort() {
        return String.valueOf(serverSocket.getLocalPort());
    }
}
