package filetransfer.network;

import filetransfer.Constants;
import filetransfer.model.Device;
import filetransfer.model.Message;

import java.net.*;

public class Discovery implements Runnable {

    private boolean isRunning = true;
    private Device device;
    private DatagramSocket socket;
    private NetworkHandlers networkHandlers;

    public Discovery(Device device, NetworkHandlers networkhandlers) {
        this.device = device;
        this.networkHandlers = networkhandlers;
    }

    @Override
    public void run() {
        try {
            createSocket(0);
            while (isRunning) {
                byte[] receiveBuffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();
                System.out.println(message);
                if (message.startsWith("discovery")) {
                    String serverData = new Message.Builder().add(Constants.DEVICE_DISCOVERED).add(device.getName()).add(device.getPort() + "").add(device.getAvailable()).add(device.getInfo()).build();
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

    public void createSocket(int counter) {
        try {
            socket = new DatagramSocket(Constants.DESKTOP_DISCOVERY_PORTS[counter], InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (Exception e) {
            if (counter > 3) {
                e.printStackTrace();
                isRunning = false;
                networkHandlers.noDiscoveryPortsAvailable();
            } else {
                createSocket(counter + 1);
            }
        }

    }

    public void stopRunning() {
        this.isRunning = false;
    }

}
