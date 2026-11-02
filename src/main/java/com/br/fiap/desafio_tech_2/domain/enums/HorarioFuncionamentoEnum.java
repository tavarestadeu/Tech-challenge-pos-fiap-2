package com.br.fiap.desafio_tech_2.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum HorarioFuncionamentoEnum {
    DIURNO("Diurno"),
    NOTURNO("Noturno"),
    AMBOS("Almoço e jantar"),
    VINTE_E_QUATRO_HORAS("24 horas");


    private final String descricao;

    @Override
    public String toString() {
        return getDescricao();
    }
}
