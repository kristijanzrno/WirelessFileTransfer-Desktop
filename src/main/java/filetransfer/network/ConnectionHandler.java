package filetransfer.network;

import filetransfer.*;
import filetransfer.model.Action;
import filetransfer.model.Device;
import filetransfer.model.Message;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionHandler extends Thread {
    private ServerSocket serverSocket;
    private Socket androidDevice;
    private boolean isRunning = true;
    private NetworkHandlers networkHandlers;

    DataInputStream input = null;
    DataOutputStream output = null;

    private Queue<Action> actions;

    private boolean test = true;

    public ConnectionHandler(NetworkHandlers networkHandlers) {
        this.networkHandlers = networkHandlers;
        this.actions = new LinkedList<>();
        createSocket(49152);
    }

    private void createSocket(int port) {
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = sslServerSocketFactory.createServerSocket(port);
        } catch (IOException e) {
            if (port <= 65535) {
                // Try the next port in range(449152 - 65535) until one is available
                createSocket(port + 1);
            } else {
                e.printStackTrace();
                isRunning = false;
            }

        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("waiting for connection......");
                androidDevice = serverSocket.accept();
                isRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
            System.out.println("Connected...");

            try {
                input = new DataInputStream(androidDevice.getInputStream());
                output = new DataOutputStream(androidDevice.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }

            while (isRunning) {
                // RECEIVING
                Message receivedMessage = null;
                try {
                    receivedMessage = receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    isRunning = false;
                }

                if (receivedMessage != null) {
                    switch (receivedMessage.getAction()) {
                        case Constants.FILE_SEND_MESSAGE:
                            System.out.println("Preparing to receive data. Receiving " + receivedMessage.paramAt(0) + " items.");
                            networkHandlers.onReceivingFiles(Integer.parseInt(receivedMessage.paramAt(0)));
                            break;
                        case Constants.FILE_NAME_MESSAGE:
                            String filename = receivedMessage.paramAt(0);
                            long fileSize = Long.parseLong(receivedMessage.paramAt(1));
                            System.out.println("Receiving " + filename + "...");
                            if (!receiveFile(filename, fileSize, input)) {
                                sendMessage(new Message.Builder().add(Constants.FILE_TRANSFER_ERROR).add(filename).build());
                                networkHandlers.onFileTransferFailed(filename);
                            } else {
                                sendMessage(Constants.FILE_RECEIVED);
                                networkHandlers.onFileReceived();
                            }
                            break;
                        case Constants.FILE_RECEIVED:
                            networkHandlers.onFileTransferred();
                            break;
                        case Constants.FILE_TRANSFER_ERROR:
                            networkHandlers.onFileTransferFailed(receivedMessage.paramAt(0));
                            break;
                        case Constants.CONNECTION_TERMINATOR:
                            isRunning = false;
                            //terminateConnection();
                            //stopConnection();
                            break;
                        case Constants.CONNECTION_REQUEST:
                            //todo accept/refuse
                            Device device = new Device();
                            device.setName(receivedMessage.paramAt(0));
                            networkHandlers.onConnectionAttempted(device);
                            break;
                    }
                }
                // SENDING
                Action action = actions.poll();
                try {
                    if (action != null) {
                        switch (action.getAction()) {
                            case "send_message":
                                writeMessage(action.getMessage());
                                if (action.getMessage().equals(Constants.CONNECTION_TERMINATOR)) {
                                    System.out.println("WROTE TERMINATION");
                                    isRunning = false;
                                    //serverSocket.close();
                                    //createSocket(49152);
                                }
                                break;
                            case "send_file":
                                writeMessage(action.getMessage());
                                writeFile(action.getFilePath());
                                break;
                        }
                    } else {
                        writeMessage("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            networkHandlers.onConnectionTerminated();
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

    }


    private Message receiveMessage() throws IOException {
        if (input != null) {
            return new Message(input.readUTF());
        }
        return null;
    }

    private boolean receiveFile(String filename, long fileSize, InputStream inputStream) {
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
        System.out.println("done....");
        return true;
    }

    public void resetSocket() {
        //sendMessage(Constants.CONNECTION_TERMINATOR);
        isRunning = false;
        createSocket(49152);
    }

    public void terminateConnection() {
        sendMessage(Constants.CONNECTION_TERMINATOR);
        networkHandlers.onConnectionTerminated();
    }

    public void acceptConnection() {
        sendMessage(Constants.CONNECTION_ACCEPTED);
    }

    public void refuseConnection() {
        sendMessage(Constants.CONNECTION_REFUSED);
    }


    public String getIPAddress() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("ntu.ac.uk", 80));
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getPort() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    public String getDeviceName() {
        String deviceName = "Unknown";
        try {
            InetAddress address = InetAddress.getLocalHost();
            deviceName = address.getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceName = deviceName.replace(".local", "");
        return deviceName;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                terminateConnection();
            }
        });
    }

}
