package com.pan.pdftool;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PDFBoxTest {

    private static String path = "D:\\Mine\\tungtungPrimary\\nurserydoc\\";
    private static String path_compressed = "D:\\Mine\\tungtungPrimary\\nurserydoc\\compressed\\";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //merge();
        //String filename = "prize";
        //List<PDImageXObject> images = extractImages(filename);
        //writePdf(filename + "_compres", images);


        extractPages("D:\\Handbook.pdf", "D:\\Handbook_temp.pdf", 2, 288);
    }

    private static List<PDImageXObject> extractImages(String fileName) throws FileNotFoundException, IOException {
        List<PDImageXObject> images = new ArrayList<>();
        PDDocument document = PDDocument.load(new File( path + fileName +".pdf"));
        for (int i=0; i < document.getNumberOfPages(); i++){

            PDPage pdfpage = document.getPage(i);
            PDResources pdResources = pdfpage.getResources();
            for (COSName c : pdResources.getXObjectNames()) {
                PDXObject o = pdResources.getXObject(c);
                if (o instanceof PDImageXObject) {

                        File file = new File(path_compressed + fileName + "_" + i + ".png");
                        PDImageXObject image = (PDImageXObject) o;
                        ImageIO.write(image.getImage(), "png", file);

                        //if (i == 0 || i == 8 || i == 9 || i == 15 || i == 17) {

                        images.add(image);

                }
            }
        }
        return images;
    }

    private static void writePdf(String fileName, List<PDImageXObject> images) throws FileNotFoundException, IOException {
        PDDocument document = new PDDocument();

        List<PDImageXObject> compressedImages = compress(document, images, 0.9f);

        for (PDImageXObject img : compressedImages){
            float width = 595;
            float height = 842;
            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(img, 0, 0, width, height);

            contentStream.close();
        }

        document.save(path_compressed + fileName + ".pdf");
        document.close();
    }


    private static List<PDImageXObject> compress(PDDocument document, List<PDImageXObject> images, float compressRate) throws IOException {
        // Obtain writer for JPEG format

        List<PDImageXObject> compressedList = new ArrayList<>();
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        for (PDImageXObject img : images) {
            BufferedImage image = img.getImage();
            // The important part: Create in-memory stream
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);

            // NOTE: The rest of the code is just a cleaned up version of your code



            // Configure JPEG compression: 70% quality
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(compressRate); // 0.7f   70% quality

            // Set your in-memory stream as the output
            jpgWriter.setOutput(outputStream);

            // Write image as JPEG w/configured settings to the in-memory stream
            // (the IIOImage is just an aggregator object, allowing you to associate
            // thumbnails and metadata to the image, it "does" nothing)
            jpgWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);

            // Dispose the writer to free resources


            // Get data for further processing...
            byte[] jpegData = compressed.toByteArray();
            compressedList.add(PDImageXObject.createFromByteArray(document, jpegData, null));
        }
        jpgWriter.dispose();
        return compressedList;
    }


    private static void merge() throws FileNotFoundException, IOException {
        PDFMergerUtility ut = new PDFMergerUtility();

        ut.setDestinationFileName("D:\\Mine\\tungtungPrimary\\nurserydoc\\K2_compress.pdf");


        ut.addSource(new File("D:\\Mine\\tungtungPrimary\\nurserydoc\\K2_1_compres.pdf"));
        ut.addSource(new File("D:\\Mine\\tungtungPrimary\\nurserydoc\\K2_2_combined_compres.pdf"));
        //ut.addSource(new File("D:\\Mine\\tungtungPrimary\\nurserydoc\\K1_1_3.pdf"));

        ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }
    // startPage: starts with 0
    private static void extractPages(String file, String newFile, int startPage, int endPage) throws FileNotFoundException, IOException {
        PDDocument document = PDDocument.load(new File(file));
        PDDocument newDoc =  new PDDocument();
        Set<Integer> skipList = new HashSet<>();
        skipList.add(7);
        skipList.add(8);
        skipList.add(9);
        skipList.add(10);
        skipList.add(11);
        for (int i=0; i < document.getNumberOfPages(); i++) {
            if (skipList.contains(i)){
                continue;
            }
            if (i >= startPage && i <= endPage) {
                newDoc.addPage(document.getPage(i));
            }
        }
        newDoc.save(newFile);
        newDoc.close();
        document.close();
    }
}
