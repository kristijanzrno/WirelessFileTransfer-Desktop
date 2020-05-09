package filetransfer.network;

import filetransfer.model.Device;

public interface NetworkHandlers {
    void onConnectionAttempted(Device device);

    void onDeviceConnected();

    void onConnectionRefused();

    void onConnectionTerminated();

    void onFileTransferStarted(int noOfFiles);

    void onFileTransferred();

    void onFileTransferFailed(String filename);

    void onReceivingFiles(int noOfFiles);

    void onFileReceived();

    void noDiscoveryPortsAvailable();
}
