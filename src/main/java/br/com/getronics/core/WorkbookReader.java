package br.com.getronics.core;

import br.com.getronics.utils.BatchProcessorException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.jspecify.annotations.NonNull;

public class WorkbookReader implements Comparable<WorkbookReader> {
    public static FormulaEvaluator evaluator;
    private final DataFormatter formatter;
    private String id, artifactName, task, remark, qty;
    private short order;

    public WorkbookReader() {
        formatter = new DataFormatter();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(final short order) {
        this.order = order;
    }

    public void setInstance(final Row row) {
        id = getCellValueAsString(row.getCell(1));
        qty = getCellValueAsString(row.getCell(8));
        artifactName = getCellValueAsString(row.getCell(9));
        task = getCellValueAsString(row.getCell(12));
        remark = getCellValueAsString(row.getCell(13));
    }

    @Override
    public int compareTo(final @NonNull WorkbookReader compareTo) {
        // 1. If any doesn't have id or them have same id, they're equal.
        if (id == null
                || compareTo.getId() == null
                || id.equals(compareTo.getId())) {
            return 0;
        }

        // 2. Get the parts lists:
        final String[] parts1 = id.split("\\.");
        final String[] parts2 = compareTo.getId().split("\\.");
        final int lessLength = Math.min(parts1.length, parts2.length);

        for (int i = 0; i < lessLength; i++) {
            final int v1 = parts1[i].isBlank() ? 0 : Integer.parseInt(parts1[i]);
            final int v2 = parts2[i].isBlank() ? 0 : Integer.parseInt(parts2[i]);

            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }

        // 3. The shortest string is fewer: Ex: "1.2" is fewer than "1.2.1":
        return Integer.compare(parts1.length, parts2.length);
    }

    @Override
    public String toString() {
        String result;

        if (!id.isBlank()) {
            result = String.format("-----\nSequencial - %s:\n%02d)- %s;Tarefa: %s Observação: %s",
                    id, order, artifactName, task, remark);
        } else {
            result = String.format("%02d)- %s;Tarefa: %s Observação: %s", order, artifactName, task, remark);
        }

        return result;
    }

    private String getCellValueAsString(final Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case NUMERIC -> {
                final double value = cell.getNumericCellValue();
                if (value % 1 == 0) {
                    yield String.valueOf((int) cell.getNumericCellValue());
                }
                yield String.valueOf((int) cell.getNumericCellValue());
            }
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> formatter.formatCellValue(cell, evaluator);
            case BLANK -> "";
            default -> throw new BatchProcessorException("Please, inform a valid CellType.");
        };
    }
}
