package filetransfer.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class QRUtils {
    public static final BufferedImage generateQRCode(String data, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BufferedImage bufferedImage = null;
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();
            Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, width, height);
            graphics2D.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (bitMatrix.get(i, j)) {
                        graphics2D.fillRect(i, j, 1, 1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
