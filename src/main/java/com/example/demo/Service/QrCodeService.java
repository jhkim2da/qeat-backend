package com.example.demo.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class QrCodeService {

    private static final int QR_SIZE = 300;

    private final Path rootDir;
    private final String urlPrefix;

    public QrCodeService(@Value("${file.dir}") String fileDir,
                         @Value("${file.url-prefix:/uploads}") String urlPrefix) {
        this.rootDir = Paths.get(fileDir).toAbsolutePath().normalize();
        this.urlPrefix = urlPrefix;
    }

    public String generateQrImage(String qrUrl, String tableToken) {
        if (qrUrl == null || qrUrl.isBlank()) {
            throw new IllegalArgumentException("QR URL이 비어 있습니다.");
        }
        if (tableToken == null || tableToken.isBlank()) {
            throw new IllegalArgumentException("테이블 토큰이 비어 있습니다.");
        }

        try {
            Path qrDir = rootDir.resolve("qr");
            Files.createDirectories(qrDir);

            String fileName = tableToken + ".png";
            Path target = qrDir.resolve(fileName);

            BitMatrix matrix = new QRCodeWriter().encode(
                    qrUrl,
                    BarcodeFormat.QR_CODE,
                    QR_SIZE,
                    QR_SIZE
            );

            MatrixToImageWriter.writeToPath(matrix, "PNG", target);

            return urlPrefix + "/qr/" + fileName;
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("QR 이미지 생성 실패", e);
        }
    }
}
