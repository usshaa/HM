package com.hotelmanagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Component
public class ExportUtil {

    public String generateCsv(List<Map<String, Object>> data, String[] headers) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
        CSVPrinter csvPrinter = new CSVPrinter(stringWriter, csvFormat);

        for (Map<String, Object> row : data) {
            Object[] values = new Object[headers.length];
            for (int i = 0; i < headers.length; i++) {
                values[i] = row.getOrDefault(headers[i], "");
            }
            csvPrinter.printRecord(values);
        }

        csvPrinter.flush();
        return stringWriter.toString();
    }

    public String objectToJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
