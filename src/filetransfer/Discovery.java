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
            while (isRunning) {
                byte[] receiveBuffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();
                System.out.println(message);
                if (message.startsWith("discovery")) {
                    String serverData = "disc_" + ip + "::" + port;
                    int mobileDevicePort = Integer.parseInt(message.split("@")[1]);
                    byte[] data = serverData.getBytes();
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length, packet.getAddress(), mobileDevicePort);
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
