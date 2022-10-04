package com.kartor.batchProcess.job;

import com.kartor.batchProcess.ftp.CoperturaFtpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@Slf4j
public class CoperturaItemProcessor implements ItemProcessor<List<CoperturaFtpDTO>, List<CoperturaFtpDTO>> {

    @Override
    public List<CoperturaFtpDTO> process(List<CoperturaFtpDTO> coperturaFtpDTOS) throws Exception {
        for (CoperturaFtpDTO c:
             coperturaFtpDTOS) {
            c.setTipoCopertura("Pippo");
            log.info(c.toString());
        }
        return coperturaFtpDTOS;
    }
}
