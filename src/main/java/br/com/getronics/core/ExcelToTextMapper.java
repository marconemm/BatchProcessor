package br.com.getronics.core;

import br.com.getronics.interfaces.DataMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelToTextMapper implements DataMapper {

    @Override
    public String getOutputHeader() {
        return "DATA | DESCRIÇÃO | VALOR | ORIGEM";
    }

    @Override
    public String mapRow(final Row row) {
        // Extração segura de dados (tratando células nulas)
        final String date = getCellValueAsString(row.getCell(0));
        final String description = getCellValueAsString(row.getCell(1));
        final String value = getCellValueAsString(row.getCell(2));

        return String.format("%s | %s | %s", date, description, value);
    }

    private String getCellValueAsString(final Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}