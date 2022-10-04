package com.kartor.batchProcess.job;

import com.kartor.batchProcess.ftp.CoperturaFtpDTO;
import com.kartor.batchProcess.ftp.FtpSailpostClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Locale;

@Slf4j
public class CoperturaItemReader<T> implements ItemReader<List<CoperturaFtpDTO>> {

    @Autowired
    FtpSailpostClient ftpSailpostClient;

    public final static List<String> FORMATO_COLONNE_COPERTURE = List.of("cap","località","provincia","codunivage","codpuntoposta","tipologia","area","customer_code");

    @Override
    public List<CoperturaFtpDTO> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!ftpSailpostClient.isLetturaFatta()) {
            ftpSailpostClient.setLetturaFatta(true);
            try {
                ftpSailpostClient.open();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (FTPFile ftpFile :
                        ftpSailpostClient.getFtpClient().listFiles()) {
                    log.info("++++++++++++++++" + ftpFile.getName());
                }
                String filePath = "/COPERTURA_STANDARD/XLS/COPERTURA.xls";
                boolean result = ftpSailpostClient.getFileFromPath(baos, filePath);
                if (!result) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("non e' stato possibile trovare il file specificato ").append(filePath);
                    throw new Exception(sb.toString());
                }
                // ho tutte le info nel baos
                return this.leggiBaos(baos);

            } catch (Exception e) {
                log.error("errore durante la chiamata al server FTP:" + e.getMessage());
                throw e;
            } finally {
                log.info("chiudo l'ftp");
                ftpSailpostClient.close();
            }
        }
        return null;
    }

    public List<CoperturaFtpDTO> leggiBaos(ByteArrayOutputStream baos) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray());
        Workbook workbook = WorkbookFactory.create(bais);
        Sheet sheet = workbook.getSheetAt(0);
        /*for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            log.info(cell.getStringCellValue());
        }*/
        ArrayList<CoperturaFtpDTO> coperturaFtpDTOS = new ArrayList<>();
        int i = 0;
        for(Row row: sheet)     //iteration over row using for each loop
        {
            log.info(String.valueOf(i));
            /*
             * colonna 0 CAP
             * colonna 1 Località
             * colonna 2 Provincia
             * colonna 3 CodUnivAge
             * colonna 4 CodPuntoPosta
             * colonna 5 tipologia
             * colonna 6 area
             * colonna 7 customer_code*/
            CoperturaFtpDTO coperturaFtpDTO = new CoperturaFtpDTO();
            int j = 0;
            for(Cell cell: row)    //iteration over cell using for each loop
            {
                // logger.info(cell.getStringCellValue());
                if(i == 0) { // prima riga con i nomi delle colonne
                    log.info("0: "+cell.getStringCellValue());
                    if(!FORMATO_COLONNE_COPERTURE.contains(cell.getStringCellValue().toLowerCase(Locale.ROOT))) {
                        throw new InvalidPropertiesFormatException("formato del file recuperato dal flusso ha delle anomalie: " +
                                cell.getStringCellValue() + " non è presente");
                    }
                } else { // altre righe cell.getSheet().getRow(0).getCell(j).getStringCellValue()+
                    // logger.info(cell.getSheet().getRow(0).getCell(j).getStringCellValue()+" "+i+": "+cell.getStringCellValue());
                    // logger.info(cell.getStringCellValue());
                    switch (cell.getSheet().getRow(0).getCell(j).getStringCellValue().toLowerCase(Locale.ROOT)) {
                        case "cap": { // CAP
                            coperturaFtpDTO.setCAP(cell.getStringCellValue());
                            break;
                        }
                        case "località": { // località
                            coperturaFtpDTO.setLocalita(cell.getStringCellValue());
                            break;
                        }
                        case "provincia": { // provincia
                            coperturaFtpDTO.setProvincia(cell.getStringCellValue());
                            break;
                        }
                        case "codunivage": { // codunivage
                            coperturaFtpDTO.setCodUnivAgenzia(Integer.parseInt(cell.getStringCellValue()));
                            break;
                        }
                        case "codpuntoposta": { // codpuntoposta
                            coperturaFtpDTO.setCodUnivPuntoPosta(Integer.parseInt(cell.getStringCellValue()));
                            break;
                        }
                        case "tipologia": { // tipologia
                            coperturaFtpDTO.setTipologia(cell.getStringCellValue());
                            break;
                        }
                        case "area": { // area
                            coperturaFtpDTO.setArea(cell.getStringCellValue());
                            break;
                        }
                        case "customer_code": { // customer_code
                            coperturaFtpDTO.setCustomerCode(cell.getStringCellValue());
                            break;
                        }
                    }

                }
                j+= 1;
            }
            // && coperturaFtpDTO.getProvincia().equals("MI")
            if(coperturaFtpDTO.getProvincia() != null ) {
                coperturaFtpDTOS.add(coperturaFtpDTO);
            }

            log.info("+++++++++++++++++++++++++++++++++++++++");
            i +=1;
            if(i == 3) {
                log.info("OUTPUT LETTURA:");
                coperturaFtpDTOS.forEach(c -> {log.info(c.toString());});
                return coperturaFtpDTOS;
            }
        }
        if(!coperturaFtpDTOS.isEmpty()) {
            coperturaFtpDTOS.forEach(c -> {log.info(c.toString());});
        }
        return coperturaFtpDTOS;
    }
}
