package com.moleniuk.pixqrcode.data.dto;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public record PixDTO(String nomeDestinatario, String chaveDestinatario, BigDecimal valor, String cidadeRemetente,
                     String descricao) {
    public PixDTO {
        if (requireNonNull(nomeDestinatario).isBlank())
            throw new IllegalArgumentException("O nome do destinatário é obrigatório.");
        nomeDestinatario = nomeDestinatario.trim();
        if (nomeDestinatario.length() > 25) {
            final var msg = "Nome do destinatário não pode ter mais que 25 caracteres. '%s' tem %d caracteres."
                    .formatted(nomeDestinatario, nomeDestinatario.length());
            throw new IllegalArgumentException(msg);
        }

        if (requireNonNull(chaveDestinatario).isBlank())
            throw new IllegalArgumentException("A chave PIX do destinatário é obrigatória.");
        chaveDestinatario = chaveDestinatario.trim();
        if (chaveDestinatario.length() > 77) {
            final var msg = "Chave PIX do destinatário não pode ter mais que 77 caracteres. '%s' tem %d caracteres."
                    .formatted(chaveDestinatario, chaveDestinatario.length());
            throw new IllegalArgumentException(msg);
        }

        if (requireNonNull(cidadeRemetente).isBlank())
            throw new IllegalArgumentException("A cidade do remetente é obrigatória.");
        cidadeRemetente = cidadeRemetente.trim();
        if (cidadeRemetente.length() > 15) {
            final var msg = "Cidade do remetente não pode ter mais que 15 caracteres. '%s' tem %d caracteres."
                    .formatted(cidadeRemetente, cidadeRemetente.length());
            throw new IllegalArgumentException(msg);
        }

        requireNonNull(descricao, "A descrição não pode ser nula. Informe um texto vazio no lugar.");
        descricao = descricao.trim();
        if (descricao.length() > 72) {
            final var msg = "Descrição não pode ter mais que 72 caracteres. '%s' tem %d caracteres."
                    .formatted(descricao, descricao.length());
            throw new IllegalArgumentException(msg);
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor do PIX deve ser maior que zero.");

        final var valorStr = formatNumber(valor);
        if (valorStr.length() > 10) {
            final var msg = "Valor não pode ter mais que 10 caracteres. '%s' tem %d caracteres."
                    .formatted(valorStr, valorStr.length());
            throw new IllegalArgumentException(msg);
        }
    }

    public PixDTO(String nomeBeneficiario, String chavePix, Double valor, String cidadeBeneficiario, String descricaoPagamento) {
        this(nomeBeneficiario, chavePix, BigDecimal.valueOf(valor), cidadeBeneficiario, descricaoPagamento);
    }

    public String valorStr() {
        return formatNumber(valor);
    }

    private static String formatNumber(final BigDecimal value) {
        return String.format("%.2f", value).formatted().replace(",", ".");
    }
}