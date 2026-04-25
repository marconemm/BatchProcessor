package br.com.getronics.interfaces;

import org.apache.poi.ss.usermodel.Row;

import java.io.File;

public interface DataMapper {
    String getOutputHeader(final File file);

    void mapRow(final Row row);
    void sortRowsList();
}
