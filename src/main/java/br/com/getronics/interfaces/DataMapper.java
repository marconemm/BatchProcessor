package br.com.getronics.interfaces;

import org.apache.poi.ss.usermodel.Row;

public interface DataMapper {
    String getOutputHeader();

    String mapRow(Row row);
}
