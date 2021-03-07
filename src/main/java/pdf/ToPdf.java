package pdf;

import org.ghost4j.converter.PDFConverter;
import org.ghost4j.document.PSDocument;
import org.ghost4j.document.PaperSize;

import java.io.File;
import java.io.FileOutputStream;

import static org.ghost4j.converter.PDFConverter.OPTION_AUTOROTATEPAGES_ALL;

public class ToPdf {

    public void convert(String inputFileName,
                        File outputFile, String pageSize) throws Throwable {
        System.out.println("ps2pdf input=\"" + inputFileName + " output=\"" + outputFile.getCanonicalPath() + "\"");
        //load PostScript document
        PSDocument document = new PSDocument();
        document.load(new File(inputFileName));
        //create OutputStream
        FileOutputStream fos = new FileOutputStream(outputFile);
        //create converter
        MyPDFConverter converter = new MyPDFConverter();
        //set options
        converter.setPDFSettings(PDFConverter.OPTION_PDFSETTINGS_DEFAULT);
        switch (pageSize) {
            case "a4":
                converter.setPaperSize(PaperSize.A4);
                break;
            case "a3":
                converter.setPaperSize(PaperSize.A3);
                break;
            case "a2":
                converter.setPaperSize(PaperSize.A2);
                break;
            case "a1":
                converter.setPaperSize(PaperSize.A1);
                break;
            case "a0":
                converter.setPaperSize(PaperSize.A0);
                break;
            default:
                break;
        }
        converter.setAutoRotatePages(OPTION_AUTOROTATEPAGES_ALL);
        //convertArea
        converter.convert(document, fos);
    }

}
