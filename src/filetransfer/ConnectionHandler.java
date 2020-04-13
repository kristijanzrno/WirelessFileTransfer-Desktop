package filetransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private ServerSocket serverSocket;
    private Socket androidDevice;
    private int port = 9270;
    private boolean isRunning = true;

    DataInputStream input = null;
    DataOutputStream output = null;

    public ConnectionHandler(){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                androidDevice = serverSocket.accept();
                if(androidDevice != null)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        System.out.println("Connected");
        try {
            input = new DataInputStream(androidDevice.getInputStream());
            output = new DataOutputStream(androidDevice.getOutputStream());

            while (isRunning) {
                String androidMessage = receiveMessage();
                if(androidMessage !=null && !androidMessage.isEmpty())
                    System.out.println(androidMessage);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String receiveMessage(){
        if(input != null){
            try {
                return input.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return "Could not read data";
            }
        }
        return "";
    }
    public void sendMessage(String message){
        if(output != null){
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
