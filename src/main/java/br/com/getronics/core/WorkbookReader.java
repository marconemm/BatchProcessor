package br.com.getronics.core;

import br.com.getronics.utils.BatchProcessorException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.util.Comparator;
import java.util.stream.Stream;

public class WorkbookReader implements Comparator<WorkbookReader> {
    public static FormulaEvaluator evaluator;
    private final DataFormatter formatter;
    private String id, artifactName, task, remark;
    private int qty;

    public WorkbookReader() {
        formatter = new DataFormatter();
    }

    public String getId() {
        return id;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getTask() {
        return task;
    }

    public String getRemark() {
        return remark;
    }

    public int getQty() {
        return qty;
    }

    public void setInstance(Row row) {
        id = getCellValueAsString(row.getCell(1));
        qty = Integer.parseInt(getCellValueAsString(row.getCell(8)));
        artifactName = getCellValueAsString(row.getCell(9));
        task = getCellValueAsString(row.getCell(12));
        remark = getCellValueAsString(row.getCell(13));
    }

    @Override
    public int compare(final WorkbookReader reader1, final WorkbookReader reader2) {
        final int[] parts1 = Stream.of(reader1.getId().split("\\."))
                .mapToInt(Integer::parseInt).toArray();
        final int[] parts2 = Stream.of(reader2.getId().split("\\."))
                .mapToInt(Integer::parseInt).toArray();
        final int length = Math.min(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            if (parts1[i] != parts2[i]) {
                return Integer.compare(parts1[i], parts2[i]);
            }
        }

        return Integer.compare(parts1.length, parts2.length);
    }

    private String getCellValueAsString(final Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> formatter.formatCellValue(cell, evaluator);
            case BLANK -> "";
            default -> throw new BatchProcessorException("Please, inform an valid CellType.");
        };
    }
}
