package br.com.getronics.core;

import br.com.getronics.interfaces.DataMapper;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;

public class ExcelToTextMapper implements DataMapper {

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
    public String mapRow(final Row row) {
        final WorkbookReader reader = new WorkbookReader();

        reader.setInstance(row);

        return String.format("Item: %s\n%s%s%s",
                reader.getId(),
                reader.getArtifactName(),
                reader.getTask(),
                reader.getRemark());
    }
}