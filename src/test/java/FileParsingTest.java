import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
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

    public FileParsingTest() throws Exception {
    }

    @Test
    void pdfFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals("sample.pdf")) {
                InputStream inputStreamPdf = zf.getInputStream(zipEntry);
                PDF pdf = new PDF(inputStreamPdf);
                Assertions.assertEquals(2, pdf.numberOfPages);
                assertThat(pdf, new ContainsExactText("The end, and just as well"));
            }
        }
    }

    @Test
    void xlsFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals("file_example_XLS_10.xls")) {
                InputStream inputStreamXls = zf.getInputStream(zipEntry);
                XLS xls = new XLS(inputStreamXls);
                String cellValue = String.valueOf(xls.excel
                        .getSheetAt(0)
                        .getRow(7)
                        .getCell(5)
                        .getNumericCellValue());
                org.assertj.core.api.Assertions.assertThat(cellValue).contains("56");
            }
        }
    }

    @Test
    void csvFromZipParsingTest() throws Exception {
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().equals("addresses.csv")) {
                try (InputStream inputStreamCsv = zf.getInputStream(zipEntry)){
                    CSVReader csvReader = new CSVReader(new InputStreamReader(inputStreamCsv, StandardCharsets.UTF_8));
                    List<String[]> r = csvReader.readAll();
                    org.assertj.core.api.Assertions.assertThat(r).contains(
                            new String[] {
                                    "John", "Doe", "120 jefferson st.", "Riverside", " NJ", " 08075"
                            },
                            new String[] {
                                    "", "Blankman", "", "SomeTown", " SD", " 00298"
                            }
                    );
                    csvReader.close();
                }
            }
        }
    }
}

