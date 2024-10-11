package com.moleniuk.pixqrcode.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moleniuk.pixqrcode.data.dto.PixDTO;
import com.moleniuk.pixqrcode.data.entities.PixEntities;
import com.moleniuk.pixqrcode.data.repositories.PixRepository;
import com.moleniuk.pixqrcode.useCase.QRCodePixLogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PixService {

    private final PixRepository pixRepository;

    public PixEntities generatePixPayment(String chavePix,
                                          String nomeBeneficiario,
                                          String cidadeBeneficiario,
                                          String descricaoPagamento,
                                          Double valor,
                                          BufferedImage logo
    ) throws IOException, WriterException {

        final var dadosPix = new PixDTO(
                nomeBeneficiario,
                chavePix,
                valor,
                cidadeBeneficiario,
                descricaoPagamento
        );

        final var pixCode = new QRCodePixLogo(dadosPix);

        String codigoGerado = pixCode.generate();


        BufferedImage qrCodeImage = generateQRCodeImage(codigoGerado);

        BufferedImage combinedImage = addLogoToQRCode(qrCodeImage, logo);

        String base64Image = convertImageToBase64(combinedImage);

        PixEntities pixPayment = PixEntities.builder()
                .pixCode(codigoGerado)
                .qrCodeBase64(base64Image)
                .chavePix(chavePix)
                .nomeBeneficiario(nomeBeneficiario)
                .descricaoPagamento(descricaoPagamento)
                .cidadeBeneficiario(cidadeBeneficiario)
                .valor(valor)
                .dataGeracao(LocalDateTime.now())
                .build();

        return pixRepository.save(pixPayment);
    }

    private BufferedImage generateQRCodeImage(String pixCode) throws WriterException {
        int qrCodeSize = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(pixCode, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private BufferedImage addLogoToQRCode(BufferedImage qrCodeImage, BufferedImage logo) {
        if (logo == null) {
            return qrCodeImage;
        }

        int qrCodeWidth = qrCodeImage.getWidth();
        int qrCodeHeight = qrCodeImage.getHeight();

        int maxLogoSize = qrCodeWidth / 5;
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (logoWidth > maxLogoSize || logoHeight > maxLogoSize) {
            double scalingFactor = (double) maxLogoSize / Math.max(logoWidth, logoHeight);
            logoWidth = (int) (logoWidth * scalingFactor);
            logoHeight = (int) (logoHeight * scalingFactor);
        }

        BufferedImage resizedLogo = resizeImage(logo, logoWidth, logoHeight);

        BufferedImage combinedImage = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = combinedImage.createGraphics();
        g2.drawImage(qrCodeImage, 0, 0, null);

        int logoX = (qrCodeWidth - logoWidth) / 2;
        int logoY = (qrCodeHeight - logoHeight) / 2;
        int padding = 1;
        g2.setColor(Color.WHITE);
        g2.fillRect(logoX - padding, logoY - padding, logoWidth + 2 * padding, logoHeight + 2 * padding);

        g2.drawImage(resizedLogo, logoX, logoY, null);
        g2.dispose();

        return combinedImage;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}