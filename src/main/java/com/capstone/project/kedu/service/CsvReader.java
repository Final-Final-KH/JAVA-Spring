package com.capstone.project.kedu.service;

import com.capstone.project.kedu.entity.KeduEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<KeduEntity> readCsv(String filePath) throws Exception {
        List<KeduEntity> course = new ArrayList<>();

        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);

        for()
    }
}
