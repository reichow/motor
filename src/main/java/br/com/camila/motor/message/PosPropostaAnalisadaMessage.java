package br.com.camila.motor.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class PosPropostaAnalisadaMessage implements Serializable {

    private static final long serialVersionUID = -2854687297939154544L;

    private String estado;

    private Long numeroProposta;
}
