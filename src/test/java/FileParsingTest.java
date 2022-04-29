import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;


public class FileParsingTest {

    ClassLoader cl = FileParsingTest.class.getClassLoader();
    ZipFile zf = new ZipFile(new File("C:/Users/WS06/IdeaProjects/FileParcingExample/" +
            "src/test/resources/example-zip.zip"));
    ZipInputStream zipInputStream = new ZipInputStream(cl.getResourceAsStream("example-zip.zip"));
    ZipEntry zipEntry;
    String pdfName = "sample.pdf";
    String pdfCheck = "The end, and just as well";
    String xlsName = "file_example_XLS_10.xls";
    String xlsCellValueCheck = "56";
    String csvName = "addresses.csv";


    public FileParsingTest() throws Exception {
    }

    @DisplayName("Парсинг PDF файла, находящегося в ZIP архиве")
    @Test
    void pdfFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals(pdfName)) {
                InputStream inputStreamPdf = zf.getInputStream(zipEntry);
                PDF pdf = new PDF(inputStreamPdf);
                Assertions.assertEquals(2, pdf.numberOfPages);
                assertThat(pdf, new ContainsExactText(pdfCheck));
            }
        }
    }

    @DisplayName("Парсинг XLS файла, находящегося в ZIP архиве")
    @Test
    void xlsFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals(xlsName)) {
                InputStream inputStreamXls = zf.getInputStream(zipEntry);
                XLS xls = new XLS(inputStreamXls);
                String cellValue = String.valueOf(xls.excel
                        .getSheetAt(0)
                        .getRow(7)
                        .getCell(5)
                        .getNumericCellValue());
                org.assertj.core.api.Assertions.assertThat(cellValue).contains(xlsCellValueCheck);
            }
        }
    }

    @DisplayName("Парсинг CSV файла, находящегося в ZIP архиве")
    @Test
    void csvFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals(csvName)) {
                try (InputStream inputStreamCsv = zf.getInputStream(zipEntry)) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(inputStreamCsv, StandardCharsets.UTF_8));
                    List<String[]> r = csvReader.readAll();
                    org.assertj.core.api.Assertions.assertThat(r).contains(
                            new String[]{
                                    "John", "Doe", "120 jefferson st.", "Riverside", " NJ", " 08075"
                            },
                            new String[]{
                                    "", "Blankman", "", "SomeTown", " SD", " 00298"
                            }
                    );
                    csvReader.close();
                }
            }
        }
    }

    @DisplayName("Парсинг json файла")
    @Test
    void jsonParsingTest() throws Exception {
        Gson gson = new Gson();
        try (InputStream is = cl.getResourceAsStream("json/sample2.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            org.assertj.core.api.Assertions.assertThat(jsonObject.get("lastName").getAsString()).isEqualTo("Jackson");
            org.assertj.core.api.Assertions.assertThat(jsonObject.get("address").getAsJsonObject().get("city")
                    .getAsString()).isEqualTo("San Diego");

        }

    }
}

