package com.kartor.batchProcess;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by s.g. on 05/12/16.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String OK = "OK";
    public static final String KO = "KO";
    private String stato;

    private String descrizione;

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    private String idCreato;

}
