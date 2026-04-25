package br.com.getronics.interfaces;

import org.apache.poi.ss.usermodel.Row;

import java.io.File;

public interface DataMapper {
    String getOutputHeader(File file);

    String mapRow(Row row);
}
