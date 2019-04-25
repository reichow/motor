package br.com.camila.motor.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.camila.motor.domain.Tipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class AnalisarPreMessage implements Serializable {

    private static final long serialVersionUID = 5974814852932825514L;

    private String cpf;

    private Long numeroProposta;

    private Tipo proposta;
}
