package com.zalo.hackathon.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by chiennd on 19/06/2017.
 */
public abstract class ZaFileReader {
    private static final int BATCH_LOG = 100000;
    private Logger LOG = LogManager.getLogger(this.getClass().getName());
    private File file;

    public ZaFileReader(File file) {
        LogCenter.info(LOG, "Reading file " + file.getAbsolutePath());
        this.file = file;

        read();
    }

    public abstract void processLine(int indexLine, String line);

    protected void onProcessError(Exception e) {
        LogCenter.exception(LOG, e);
        System.exit(0);
    }

    public void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            int indexLine = 0;
            while ((line = br.readLine()) != null) {

                if (indexLine % BATCH_LOG == 0) {
                    LogCenter.info(LOG, "Process " + indexLine + " lines");
                }
                // process the line.
                processLine(indexLine, line);
                indexLine++;
            }
        } catch (IOException e) {
            onProcessError(e);
        }
    }
}
