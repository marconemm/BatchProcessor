package br.com.getronics.core;

import br.com.getronics.utils.BatchProcessorException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.jspecify.annotations.NonNull;

import static org.apache.logging.log4j.LogManager.getLogger;

public class WorkbookReader implements Comparable<WorkbookReader>, Cloneable {
    public static FormulaEvaluator evaluator;
    private final DataFormatter formatter;
    private String id, artifactName, task;
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

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(final String artifactName) {
        this.artifactName = artifactName;
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(final short order) {
        this.order = order;
    }

    public String getTask() {
        return task;
    }


    public void setInstance(final Row row) {
        id = getCellValueAsString(row.getCell(1));
        artifactName = getCellValueAsString(row.getCell(9));
        task = getCellValueAsString(row.getCell(12));
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
        return String.format("%s;Tarefa: %s \n", artifactName, task);
    }

    @Override
    public WorkbookReader clone() {
        try {
            return (WorkbookReader) super.clone();
        } catch (CloneNotSupportedException cnse) {
            getLogger().error("clone(): {}", cnse.getMessage());
            throw new BatchProcessorException(cnse.getMessage());
        }
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
