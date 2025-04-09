package org.example.eventticketsystem.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class BarcodeGenerator {

    public static void main(String[] args) {
        try {
            String text = "https://example.com/event";
            String filePath = "d:/Repo/EventTicketsEASV/QRCode.png";
            int width = 300;
            int height = 300;

            generateQRCode(text, filePath, width, height);
            System.out.println("QR Code generated at: " + filePath);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateQRCode(String text, String filePath, int width,
                                      int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault()
                .getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}
