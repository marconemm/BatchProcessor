package br.com.getronics.utils.enums.styles;

import javafx.scene.paint.Color;

public enum E_Colors {
    ERROR_RED("ff4444"),
    //1 - Cinza equilibrado para bordas ou textos secundários
    NEUTRAL_MEDIUM("AFAEAF"),
    //2 - Verde escuro para fundos de containers ou headers
    PRIMARY_DEEP("01402E"),
    //3 - Verde médio para botões ou estados normais
    PRIMARY_MAIN("038C4C"),
    //4 - Verde Neon para o degradê, foco e progresso
    PRIMARY_ACCENT("0DF205"),
    // 5 - Fundo principal ou áreas de contraste limpo
    NEUTRAL_LIGHT("F2F2F2"),
    PRIMARY_BLACK("010001"),
    PRIMARY_VIBRANT("01A959");

    private final String hex;

    E_Colors(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return "#" + hex;
    }

    public Color getColor() {
        return Color.web(getHex());
    }
}
