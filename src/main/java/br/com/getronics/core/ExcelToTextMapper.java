package br.com.getronics.core;

import br.com.getronics.interfaces.DataMapper;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.*;

public class ExcelToTextMapper implements DataMapper {
    private final List<WorkbookReader> mappedRowList;
    private final LinkedHashMap<String, List<WorkbookReader>> batchList;
    private WorkbookReader prevRow;
    private short blankRowInSequence, separatorSize;

    public ExcelToTextMapper() {
        mappedRowList = new ArrayList<>();
        batchList = new LinkedHashMap<>();
        blankRowInSequence = separatorSize = 0;
    }

    @Override
    public String getOutputHeader(final File file) {
        final String header = "Entregas em Lotes para a Ordem de Serviço da Planilha:\n" + file.getName();
        separatorSize = (short) ((header.length() / 2) + 10);

        final String separator1 = getSeparator("=");
        final String separator2 = getSeparator("-");

        separatorSize = (short) ((separatorSize / 2) - 6);

        return String.format("%s\n%s  INICIO   %s\n%s\n%s\n%s",
                separator1,
                getSeparator("="),
                getSeparator("="),
                separator1,
                header,
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

        /*3. remove the sameId duplicates; and
         * 3.1 fill the batchList:
         * */
        prevRow = mappedRowList.getFirst();
        mappedRowList.forEach(row -> {
            final boolean isFirst = row.getOrder() == 1;
            final boolean hasSameId = (byte) prevRow.compareTo(row) == 0;

            if (isFirst) {
                final List<WorkbookReader> firstBatchList = new ArrayList<>();

                firstBatchList.add(row);
                batchList.putFirst(row.getId(), firstBatchList);

                return;
            }

            if (hasSameId) {
                batchList.get(row.getId()).add(row);
                row.setId("");
            } else {
                final List<WorkbookReader> nextBatchList = new ArrayList<>();

                nextBatchList.add(row);
                batchList.putFirst(row.getId(), nextBatchList);
                prevRow = row;
            }
        });

        // 4. reset the reference for the prevRow:
        prevRow = mappedRowList.getFirst();
    }

    @Override
    public String getBatchRow() {
        final StringBuilder result = new StringBuilder();
        final short backUp = separatorSize;

        separatorSize = (short) ((separatorSize * 2) + 12);
        batchList.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by keys (Sequencial).
                .forEach(batch -> result.append("Sequencial - ")
                        .append(batch.getValue().getFirst().getId())
                        .append("\nLote:\n")
                        .append(getBatch(batch.getValue()))
                        .append(getSeparator("_"))
                        .append("\n\n")
                );

        separatorSize = backUp;
        result.deleteCharAt(result.length() - 1); // remove the last "\n"
        result.append(getSeparator("="))
                .append("    FIM    ")
                .append(getSeparator("="))
                .append("\n\n\n");

        return result.toString();
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

    private String getBatch(final List<WorkbookReader> batchList) {
        final StringBuilder result = new StringBuilder();

        batchList.forEach(artifact -> {
            final String batch = String.format("%s;Tarefa: %s \n",
                    artifact.getArtifactName(),
                    artifact.getTask()
            );

            result.append(batch);
        });

        return result.toString();
    }

    private String getSeparator(final String c) {
        final StringBuilder result = new StringBuilder(c);

        result.repeat(c, Math.max(0, separatorSize));

        return result.toString();
    }
}