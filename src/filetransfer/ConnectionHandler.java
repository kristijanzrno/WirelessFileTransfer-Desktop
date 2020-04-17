package filetransfer;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionHandler extends Thread {
    private ServerSocket serverSocket;
    private Socket androidDevice;
    private int port = 9270;
    private boolean isRunning = true;
    private boolean receiving;

    DataInputStream input = null;
    DataOutputStream output = null;

    private Queue<Action> actions;

    public ConnectionHandler() {
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = sslServerSocketFactory.createServerSocket(port);
            //serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
        this.actions = new LinkedList<>();
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
                // SENDING
                String receivedData = receiveMessage();
                String[] message = receivedData.split(Constants.DATA_SEPARATOR);
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
                // RECEIVING
                Action action = actions.poll();
                if (action != null) {
                    switch (action.getAction()) {
                        case "send_message":
                            writeMessage(action.getMessage());
                            break;
                        case "send_file":
                            writeMessage(action.getMessage());
                            writeFile(action.getFilePath());
                            break;
                    }
                } else {
                    writeMessage("");
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Wrappers
    public void sendMessage(String message) {
        actions.add(new Action("send_message", message, null));
    }

    public void sendFile(String message, String filePath) {
        actions.add(new Action("send_file", message, filePath));
    }

    private void writeMessage(String message) throws IOException {
        output.writeUTF(message);
    }

    private void writeFile(String filePath) {
        System.out.println("sending...");
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(androidDevice.getOutputStream());
            output.flush();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = bufferedInputStream.read(buffer)) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done...");
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
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int count = 0;
            while (fileSize > 0 && (count = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
                fileSize -= count;
            }
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        receiving = false;
        System.out.println("done....");
        return true;
    }

    public void stopConnection() {
        isRunning = false;
    }


    public String getPort() {
        return String.valueOf(serverSocket.getLocalPort());
    }
}
