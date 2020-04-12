package filetransfer;

import java.net.*;

public class Discovery implements Runnable {

    private boolean isRunning = true;
    private String ip;
    private String port;

    public Discovery(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8899, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            System.out.println("hey");
            while (isRunning) {
                byte[] receiveBuffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();
                if (message.equals("discovery")) {
                    System.out.println("discovered");
                    String serverData = "disc_" + ip + "::" + port;
                    byte[] data = serverData.getBytes();
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length, packet.getAddress(), 23423);

                    socket.send(dataPacket);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        this.isRunning = false;
    }
}
