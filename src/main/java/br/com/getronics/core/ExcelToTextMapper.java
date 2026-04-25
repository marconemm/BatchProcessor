package br.com.getronics.core;

import br.com.getronics.interfaces.DataMapper;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExcelToTextMapper implements DataMapper {
    private final List<WorkbookReader> mappedRowList;
    private WorkbookReader prevRow;
    private byte blankRowInSequence;

    public ExcelToTextMapper() {
        mappedRowList = new ArrayList<>();
        blankRowInSequence = 0;
    }

    public List<WorkbookReader> getMappedRowList() {
        return mappedRowList;
    }

    @Override
    public String getOutputHeader(final File file) {
        final String separator1 = "==========================";
        final String separator2 = "--------------------------";
        final int start = file.getName().length() - 11;
        final int end = file.getName().length() - 5;
        final String number = file.getName().substring(start, end);

        return String.format("%s\n%s\nEntregas em Lotes para a Ordem de Serviço\nNúmero: %s\n%s",
                separator1,
                separator1,
                number,
                separator2);
    }

    @Override
    public void mapRow(final Row row) {
        if (blankRowInSequence > 4)
            return;

        final WorkbookReader mappedRow = new WorkbookReader();

        mappedRow.setInstance(row);

        if (mappedRow.getId().isBlank())
            blankRowInSequence++;
        else
            blankRowInSequence = 0;

        mappedRowList.add(mappedRow);
    }

    @Override
    public void sortRowsList() {
        // 1. remove all the "Blank rows" and sort the mappedRowList:
        mappedRowList.removeIf(row -> row.getId().isBlank());
        mappedRowList.sort(Comparator.naturalOrder());
        prevRow = mappedRowList.getFirst();

        // 2. remove the sameId duplicates:
        mappedRowList.forEach(row -> {
            final boolean isFirst = row.compareTo(mappedRowList.getFirst()) == 0;
            final boolean isSameId = (byte) prevRow.compareTo(row) == 0;

            if (isFirst)
                row.setOrder((short) 1);
            else
                row.setOrder((short) (prevRow.getOrder() + 1));

            if (!isFirst && isSameId)
                row.setId("");

            prevRow = row;
        });
    }
}