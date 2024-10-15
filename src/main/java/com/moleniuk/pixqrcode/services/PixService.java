package com.moleniuk.pixqrcode.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.moleniuk.pixqrcode.data.dto.PixDTO;
import com.moleniuk.pixqrcode.data.dto.PixDTOResponse;
import com.moleniuk.pixqrcode.useCase.QRCodePixLogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class to generate a PIX payment
 */
@Service
@RequiredArgsConstructor
public class PixService {


    public PixDTOResponse generatePixPayment(String chavePix,
                                             String nomeBeneficiario,
                                             String cidadeBeneficiario,
                                             String descricaoPagamento,
                                             Double valor,
                                             BufferedImage logo
    ) throws WriterException, IOException {

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

        return new PixDTOResponse(
                codigoGerado,
                base64Image,
                dadosPix.chaveDestinatario(),
                dadosPix.nomeDestinatario(),
                dadosPix.descricao(),
                dadosPix.cidadeRemetente(),
                dadosPix.valor().doubleValue()
        );

    }

    /**
     * Generate a QR Code image from a given text
     *
     * @param pixCode the text to be encoded in the QR Code
     * @return the QR Code image
     * @throws WriterException if the QR Code cannot be generated
     */
    private BufferedImage generateQRCodeImage(String pixCode) throws WriterException {
        int qrCodeSize = 600;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = qrCodeWriter.encode(pixCode, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Add a logo to a QR Code image
     *
     * @param qrCodeImage the QR Code image
     * @param logo        the logo to be added
     * @return the QR Code image with the logo
     */
    private BufferedImage addLogoToQRCode(BufferedImage qrCodeImage, BufferedImage logo) {
        if (logo == null) {
            return qrCodeImage;
        }

        int qrCodeWidth = qrCodeImage.getWidth();
        int qrCodeHeight = qrCodeImage.getHeight();

        int maxLogoSize = qrCodeWidth / 3;
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (logoWidth > maxLogoSize || logoHeight > maxLogoSize) {
            double scalingFactor = Math.min((double) maxLogoSize / logoWidth, (double) maxLogoSize / logoHeight);
            logoWidth = (int) (logoWidth * scalingFactor);
            logoHeight = (int) (logoHeight * scalingFactor);
        }

        BufferedImage resizedLogo = resizeImage(logo, logoWidth, logoHeight);

        BufferedImage combinedImage = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = combinedImage.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

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

    /**
     * Resize an image to a given width and height
     *
     * @param originalImage the image to be resized
     * @param width         the new width
     * @param height        the new height
     * @return the resized image
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Convert an image to a Base64 string
     *
     * @param image the image to be converted
     * @return the Base64 string
     * @throws IOException if the image cannot be converted
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}
