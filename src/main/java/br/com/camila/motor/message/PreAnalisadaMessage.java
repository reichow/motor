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
public class PreAnalisadaMessage implements Serializable {

    private static final long serialVersionUID = 6221925921068344351L;

    private String estado;

    private Long numeroProposta;

    private Tipo proposta;
}
