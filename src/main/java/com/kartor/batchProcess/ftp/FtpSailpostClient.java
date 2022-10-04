package com.kartor.batchProcess.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FtpSailpostClient {

    Logger logger = LoggerFactory.getLogger(FtpSailpostClient.class);
    private String fileSeparator;

    private String server;
    private int port;
    private String user;
    private String password;
    private String path;
    private FTPClient ftp;
    private boolean letturaFatta = false;

    // constructor
    public FtpSailpostClient(String server, int port, String user, String password, String path, String fileSeparator) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.path = path;
        this.fileSeparator = fileSeparator;
    }

    public void open() throws IOException {
        ftp = new FTPClient();
        ftp.enterLocalPassiveMode();
        ftp.setConnectTimeout(5000);
        ftp.setAutodetectUTF8(true);
        ftp.connect(server, port);
        ftp.enterLocalPassiveMode();
        ftp.login(user, password);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

    }

    public FTPClient getFtpClient() {
        return this.ftp;
    }

    public void close() throws IOException {
        ftp.disconnect();
    }

    public boolean isLetturaFatta() {
        return letturaFatta;
    }

    public void setLetturaFatta(boolean letturaFatta) {
        this.letturaFatta = letturaFatta;
    }

    /**
     * Restituisce il path completo del file a seconda dell'ambiente Prod o Stage
     * @param filename
     * @param relativePath
     * @return String: absolute path 
     */
    public String getFullPathFile(String filename, String relativePath) {
        StringBuilder sb = new StringBuilder(path)
                .append(fileSeparator).append(relativePath)
                .append(fileSeparator).append(filename) ;
        return sb.toString();
    }

    /**
     * 
     * @param stream
     * @param filename
     * @param codUnivocoAgenzia
     * @return
     * @throws IOException
     */
    public boolean putFileAgenziaToPath(ByteArrayInputStream stream, String filename, String codUnivocoAgenzia) throws IOException {
        return putFileToPath(stream, filename, codUnivocoAgenzia);
    }

    /**
     * Crea le directory del path richiesto
     * @param ftpClient
     * @param dirPath
     * @return
     * @throws IOException
     */
    public boolean makeDirectories(FTPClient ftpClient, String dirPath)
            throws IOException {
        String[] pathElements = dirPath.split(fileSeparator);
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Permette di scrivere nel filesystem in base all'Agenzia di appertenenza 
     * @param stream
     * @param filename
     * @param relativePath
     * @return
     * @throws IOException
     */
    public boolean putFileToPath(ByteArrayInputStream stream, String filename, String relativePath) throws IOException {
        StringBuilder sb = new StringBuilder(path).append(fileSeparator).append(relativePath);
        //se la directory non esiste devo crearla
        makeDirectories(this.getFtpClient(), sb.toString());
        sb.append(fileSeparator).append(filename);
        boolean result = ftp.storeFile(sb.toString(), stream);
        stream.close();
        return result;
    }
    
    

    public boolean getFileFromPath(OutputStream stream, String filePath) throws IOException {
        //se la directory non esiste devo crearla
        StringBuilder sb = new StringBuilder(filePath);
        logger.info("PATH FTP:  " + sb);
        /*if(ftp.changeWorkingDirectory(path)) {
            for (FTPFile ftpFile:
                    ftp.listFiles()) {
                logger.info("++++++++++++++++"+ftpFile.getName());
            }
        }*/
        boolean result = ftp.retrieveFile(sb.toString(), stream);
        logger.info("RISULTATO PER FILE: "+ result);
        if(result){
            stream.close();
        } else {
            logger.info("RISULTATO FALLITO");
        }
        return result;
    }



}
