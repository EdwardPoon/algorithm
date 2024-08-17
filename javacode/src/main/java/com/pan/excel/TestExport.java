package com.pan.excel;

import jxl.Workbook;
import jxl.write.Number;
import jxl.write.*;

import java.io.File;
import java.io.IOException;

public class TestExport {

    public static void main(String[] args) {
        exportExcel();
    }


    private static void exportExcel(){


        String EXCEL_FILE_LOCATION = "d://inventoryreport.xls";
        //1. Create an Excel file
        WritableWorkbook myFirstWbook = null;
        try {

            myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));

            // create an Excel sheet
            WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);

            // add something into the Excel sheet
            Label label = new Label(0, 0, "Test Count");
            excelSheet.addCell(label);

            Number number = new Number(0, 1, 20);
            excelSheet.addCell(number);

            label = new Label(1, 0, "Result");
            excelSheet.addCell(label);

            label = new Label(1, 1, "Passed");
            excelSheet.addCell(label);

            number = new Number(0, 2, 30);
            excelSheet.addCell(number);

            // update cell
            WritableCell writableCell = excelSheet.getWritableCell(0, 2);
            ((Number) writableCell).setValue(50);

            label = new Label(1, 2, "Passed 2");
            excelSheet.addCell(label);

            Formula formula = new Formula(0, 3, "SUM(A2:A3)");
            excelSheet.addCell(formula);
            myFirstWbook.write();

        } catch (IOException | WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
