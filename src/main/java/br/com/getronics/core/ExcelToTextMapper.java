package br.com.getronics.core;

import br.com.getronics.interfaces.DataMapper;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class ExcelToTextMapper implements DataMapper {
    private final List<WorkbookReader> mappedRowList;
    private final LinkedHashMap<String, List<String>> batchList;
    private WorkbookReader prevRow;
    private byte blankRowInSequence;

    public ExcelToTextMapper() {
        mappedRowList = new ArrayList<>();
        batchList = new LinkedHashMap<>();
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
        /* 1. remove all the "Blank rows";
         * 1.1 split the new batch row, if necessary; and
         * 1.2 sort the mappedRowList:
         * */
        mappedRowList.removeIf(row -> row.getId().isBlank());
        checkArtifactNames();
        mappedRowList.sort(Comparator.naturalOrder());

        // 2. set the orders:
        prevRow = mappedRowList.getFirst();
        mappedRowList.forEach(row -> {
            final boolean isFirst = row.compareTo(mappedRowList.getFirst()) == 0;

            if (isFirst)
                row.setOrder((short) 1);
            else
                row.setOrder((short) (prevRow.getOrder() + 1));

            prevRow = row;
        });

        // 3. remove the sameId duplicates:
//        final List<String> batchList = new ArrayList<>();

        prevRow = mappedRowList.getFirst();
        mappedRowList.forEach(row -> {
            final boolean isFirst = row.getOrder() == 1;
            final boolean hasSameId = (byte) prevRow.compareTo(row) == 0;

            if (!isFirst && hasSameId) {
                row.setId("");
            } else
                prevRow = row;
        });
    }

    private void checkArtifactNames() {
        final String REGEX = "\\s*[; /\\\\,.]\\s*";

        for (int rowIndex = 0; rowIndex < mappedRowList.size(); rowIndex++) {
            final WorkbookReader currRow = mappedRowList.get(rowIndex);
            final String[] artifactNamesList = currRow.getArtifactName().split(REGEX);

            // 1. If the artifact name contains any special REGEX character:
            if (artifactNamesList.length > 1) {
                // 2. Remove the currRow and reset the rowIndex:
                mappedRowList.remove(rowIndex);
                rowIndex = 0;

                // 3. Create the new clone rows:
                for (final String newArtifactName : artifactNamesList) {
                    final WorkbookReader newRow = currRow.clone();

                    newRow.setArtifactName(newArtifactName);
                    mappedRowList.add(newRow);
                }
            }
        }
    }
}