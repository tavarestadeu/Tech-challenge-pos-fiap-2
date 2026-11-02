    package com.br.fiap.desafio_tech_2.domain.entities;

    import jakarta.persistence.Column;
    import jakarta.persistence.Embeddable;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    @Embeddable
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Endereco {
        @Column(nullable = false)
        String logradouro;
        @Column(nullable = false)
        String numero;
        @Column(nullable = false)
        String cep;
        @Column(nullable = false)
        String bairro;
        @Column(nullable = false)
        String cidade;
        @Column(nullable = false)
        String uf;
        String complemento;


        public void atualizaDados(String logradouro, String numero, String cep, String bairro, String cidade, String uf, String complemento) {
            if (logradouro != null && !logradouro.isBlank()) this.logradouro = logradouro;
            if (numero != null && !numero.isBlank()) this.numero = numero;
            if (cep != null && !cep.isBlank()) this.cep = cep;
            if (bairro != null && !bairro.isBlank()) this.bairro = bairro;
            if (cidade != null && !cidade.isBlank()) this.cidade = cidade;
            if (uf != null && !uf.isBlank()) this.uf = uf;
            if (complemento != null) this.complemento = complemento;
            }

        }
