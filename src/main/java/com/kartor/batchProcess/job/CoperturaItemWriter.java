package com.kartor.batchProcess.job;

import com.kartor.batchProcess.ftp.CoperturaFtpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class CoperturaItemWriter<T> implements ItemWriter<List<CoperturaFtpDTO>> {
    @Override
    public void write(List list) throws Exception {
        try {
            log.info("RISULTATO BATCH");
            list.forEach(e -> log.info(e.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
