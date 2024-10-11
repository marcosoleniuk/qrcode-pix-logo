package com.moleniuk.pixqrcode.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PIX_GERADOS")
public class PixEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PIXCODE", length = 1024)
    private String pixCode;

    @Column(name = "QR_CODE_BASE64", length = 2048)
    private String qrCodeBase64;

    @Column(name = "CHAVE_PIX")
    private String chavePix;

    @Column(name = "DATA_GERACAO")
    private LocalDateTime dataGeracao;

    @Column(name = "NOME_BENEFICIARIO")
    private String nomeBeneficiario;

    @Column(name = "CIDADE_BENEFICIARIO")
    private String cidadeBeneficiario;

    @Column(name = "VALOR")
    private Double valor;

    @Column(name = "DESCRICAO_PAGAMENTO")
    private String descricaoPagamento;

}
