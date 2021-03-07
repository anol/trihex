import org.ghost4j.converter.PDFConverter;
import org.ghost4j.document.PSDocument;
import org.ghost4j.document.PaperSize;
import pdf.MyPDFConverter;
import trihex.TrihexBuilder;

import java.io.File;
import java.io.FileOutputStream;

import static org.ghost4j.converter.PDFConverter.OPTION_AUTOROTATEPAGES_OFF;

public class Main {

    public static void main(String[] args) throws Throwable {
        File outputFile = new File("trihex.pdf");
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        String temp = "target/temp.ps";
        File tempFile = new File(temp);
        TrihexBuilder builder = new TrihexBuilder();
        String postscript = builder.convertToPs("test");
        new FileOutputStream(tempFile).write(postscript.getBytes());
        builder.convertToPdf(temp, outputFile);
    }

}
