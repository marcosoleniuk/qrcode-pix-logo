package com.moleniuk.pixqrcode.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PixDTOResponse {
    private String pixCode;
    private String qrCodeBase64;
    private String chavePix;
    private String nomeBeneficiario;
    private String descricaoPagamento;
    private String cidadeBeneficiario;
    private Double valor;
}
