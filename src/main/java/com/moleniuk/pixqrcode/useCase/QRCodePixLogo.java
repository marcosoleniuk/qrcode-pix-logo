package com.moleniuk.pixqrcode.useCase;

import com.moleniuk.pixqrcode.data.dto.PixDTO;
import org.json.JSONObject;


public final class QRCodePixLogo {

    private static final String PFI = "01";
    public static final String COD_CRC = "6304";
    private static final String COD_PAIS = "BR";
    private static final String COD_MOEDA = "986";
    private static final String ARRANJO_PAGAMENTO = "BR.GOV.BCB.PIX";
    private static final String MCC = "0000";
    private static final String COD_CAMPO_VALOR = "54";
    private static final String ID_TRANSACAO_VAZIO = "***";
    private final String idTransacao;

    private final PixDTO dadosPix;
    private String code = "";

    public QRCodePixLogo(final PixDTO dadosPix) {
        this(dadosPix, ID_TRANSACAO_VAZIO);
    }

    public QRCodePixLogo(final PixDTO dadosPix, final String idTransacao) {
        if (idTransacao.length() > 25) {
            final var msg = "idTransacao deve ter no máximo 25 caracteres. Valor %s tem %d caracteres.".formatted(idTransacao, idTransacao.length());
            throw new IllegalArgumentException(msg);
        }

        this.idTransacao = idTransacao;
        this.dadosPix = dadosPix;
    }

    private JSONObject newJSONObject() {
        final var jsonTemplate =
                """
                        {
                            '00': '%s',
                            '26': {
                                '00': '%s',
                                '01': '%s',
                                '02': '%s'
                            },
                            '52': '%s',
                            '53': '%s',
                            '%s': '%s',
                            '58': '%s',
                            '59': '%s',
                            '60': '%s',
                            '62': {
                                '05': '%s'
                            }
                        }
                        """;

        final var json =
                jsonTemplate
                        .formatted(
                                PFI, ARRANJO_PAGAMENTO, dadosPix.chaveDestinatario(), dadosPix.descricao(),
                                MCC, COD_MOEDA, COD_CAMPO_VALOR, dadosPix.valorStr(), COD_PAIS,
                                dadosPix.nomeDestinatario(), dadosPix.cidadeRemetente(), idTransacao);
        return new JSONObject(json);
    }

    public String generate() {
        final String partialCode = generateInternal(newJSONObject()) + COD_CRC;
        final String checksum = crcChecksum(partialCode);
        return setCode(partialCode + checksum);
    }

    private String setCode(final String code) {
        this.code = code;
        return code;
    }

    private String generateInternal(final JSONObject jsonObj) {
        final var sb = new StringBuilder();
        jsonObj.keySet().stream().sorted().forEach(key -> {
            final Object val = jsonObj.get(key);
            final String str = encodeValue(key, val);
            sb.append(leftPad(key)).append(strLenLeftPadded(str)).append(str);
        });

        return sb.toString();
    }

    private String encodeValue(final String key, final Object val) {
        if (val instanceof JSONObject jsonObjValue)
            return generateInternal(jsonObjValue);
        return key.equals(COD_CAMPO_VALOR) ? val.toString() : removeSpecialChars(val);
    }

    private String crcChecksum(final String partialCode) {
        int crc = 0xFFFF;
        final var byteArray = partialCode.getBytes();
        for (final byte b : byteArray) {
            crc ^= b << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) == 0)
                    crc = crc << 1;
                else crc = (crc << 1) ^ 0x1021;
            }
        }

        final int decimal = crc & 0xFFFF;
        return leftPad(Integer.toHexString(decimal), 4).toUpperCase();
    }

    private String removeSpecialChars(final Object value) {
        return value.toString().replaceAll("[^a-zA-Z0-9\\-@\\.\\*\\s]", "");
    }

    static String strLenLeftPadded(final String value) {
        if (value.length() > 99) {
            final var msg = "Tamanho máximo dos valores dos campos deve ser 99. '%s' tem %d caracteres.".formatted(value, value.length());
            throw new IllegalArgumentException(msg);
        }

        final String len = String.valueOf(value.length());
        return leftPad(len);
    }

    private static String leftPad(final String code) {
        return leftPad(code, 2);
    }


    private static String leftPad(final String code, final int len) {
        final var format = "%1$" + len + "s";
        return format.formatted(code).replace(' ', '0');
    }

    @Override
    public String toString() {
        return code;
    }
}
