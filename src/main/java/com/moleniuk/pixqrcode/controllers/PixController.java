package com.moleniuk.pixqrcode.controllers;

import com.google.zxing.WriterException;
import com.moleniuk.pixqrcode.data.dto.PixDTOResponse;
import com.moleniuk.pixqrcode.services.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/pix")
public class PixController {

    private final PixService pixService;

    @PostMapping("/gerar")
    public ResponseEntity<PixDTOResponse> generatePix(@RequestParam String chavePix,
                                                      @RequestParam String nomeBeneficiario,
                                                      @RequestParam String cidadeBeneficiario,
                                                      @RequestParam(required = false) String descricaoPagamento,
                                                      @RequestParam(required = false) Double valor,
                                                      @RequestParam(required = false) MultipartFile logo
    ) throws IOException, WriterException {
        BufferedImage logoImage = (logo != null && !logo.isEmpty()) ? ImageIO.read(logo.getInputStream()) : null;
        PixDTOResponse pixPayment = pixService.generatePixPayment(
                chavePix,
                nomeBeneficiario,
                cidadeBeneficiario,
                descricaoPagamento,
                valor,
                logoImage
        );
        return ResponseEntity.ok(pixPayment);
    }
}
