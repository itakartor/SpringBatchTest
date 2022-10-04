package com.kartor.batchProcess.ftp;

import lombok.Data;

@Data
public class CoperturaFtpDTO {
    private String CAP;

    private String Localita;

    private String Provincia;
    private Integer CodUnivAgenzia;
    private Integer CodUnivPuntoPosta;
    private String tipologia;
    private String Area;
    private String customerCode;

    private String tipoCopertura;
}
