package com.backend.dawms.service;

import com.backend.dawms.model.Asset;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
@Slf4j
public class QRCodeService {
    private static final String QR_CODE_PATH = "src/main/resources/static/qrcodes/";

    public String generateQRCode(Asset asset) throws WriterException, IOException {
        String qrContent = String.format("DAWMS-ASSET-%d-%s", asset.getId(), asset.getSerialNumber());
        String fileName = "qr_" + asset.getId() + ".png";
        String filePath = QR_CODE_PATH + fileName;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
    }
} 