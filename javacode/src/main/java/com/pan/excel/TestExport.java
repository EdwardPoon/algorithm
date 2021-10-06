package com.pan.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestExport {


    private void exportExcel(){
        Map<String,String> colNameMap = reportService.getColumnNames(rectype);

        String fileName = "inventoryreport.xls";
        response.setContentType("application/msexcel");// 定义输出类型
        /*try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");// 解决中文
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }*/
        response.setHeader("content-disposition", "attachment; filename="
                + fileName);
        if ( StringUtil.isNullOrBlank(rectype)){
            return;
        }
        try {
            WritableWorkbook wwb;
            OutputStream os = response.getOutputStream();
            wwb = Workbook.createWorkbook(os);
            WritableSheet ws = wwb.createSheet("进销存报表", 0);
            ws.getSettings().setVerticalFreeze(1);// 第一行标题冻结。
            ws.getSettings().setVerticalFreeze(2);// 第一行标题冻结。
            // 表头样式
            WritableCellFormat headFormat = new WritableCellFormat(
                    new WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD,
                            false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN));
            WritableCellFormat normalFont = new WritableCellFormat(
                    new WritableFont(WritableFont.createFont("宋体"), 10));
            normalFont.setBorder(Border.ALL, BorderLineStyle.THIN);
            WritableCellFormat smallhead = new WritableCellFormat(
                    new WritableFont(WritableFont.createFont("宋体"), 14,
                            WritableFont.BOLD));
            smallhead.setAlignment(Alignment.CENTRE);

            String[] bt = new String[16];
            bt[0] = colNameMap.get("col_recordName");
            bt[1] = colNameMap.get("col_materialCode");
            bt[2] = colNameMap.get("col_materialName");
            bt[3] = colNameMap.get("col_colorCode");
            bt[4] = colNameMap.get("col_colorName");
            bt[5] = colNameMap.get("col_lastBalance");
            bt[6] = colNameMap.get("col_periodIn");
            bt[7] = colNameMap.get("col_periodInNet");
            bt[8] = colNameMap.get("col_periodOut");
            bt[9] = colNameMap.get("col_periodOutNet");
            bt[10] = colNameMap.get("col_balance");
            bt[11] = colNameMap.get("col_startDate");
            bt[12] = colNameMap.get("col_endDate");
            bt[13] = colNameMap.get("col_unitPrice");

            ws.setColumnView(0, 10);//set column width
            ws.setColumnView(1, 20);
            ws.setColumnView(2, 20);
            ws.setColumnView(3, 20);
            ws.setColumnView(4, 25);
            ws.setColumnView(5, 20);
            ws.setColumnView(6, 20);
            ws.setColumnView(7, 20);
            ws.setColumnView(8, 20);
            ws.setColumnView(9, 20);
            ws.setColumnView(10, 20);
            ws.setColumnView(11, 20);
            ws.setColumnView(12, 10);
            ws.setColumnView(13, 20);
            ws.setColumnView(14, 20);

            if ("compareToInvSum".equals(repType)){
                if (InventoryConstant.Report_Type_RAWMIX.equals(rectype)){
                    bt[14] = "配料车间库存";//column caption
                }else if (InventoryConstant.Report_Type_EMPFETCHSEMI.equals(rectype)
                        ||InventoryConstant.Report_Type_EMPFETCHMETAL.equals(rectype) ){
                    bt[14] = "员工库存";
                }else{
                    bt[14] = "仓库库存";
                }
                bt[15] = "差额";
            }

            ws.addCell(new Label(0, 0, "进销存报表", headFormat));// 表头
            // 表格标题
            for (int i = 0; i < bt.length; i++) {
                ws.addCell(new Label(i, 1, bt[i], smallhead));
            }
            int row = 2;
            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");


            String recordName = enddate.substring(0, 7);

            boolean useIntValut = rectype.equals(InventoryConstant.Report_Type_RAW)||rectype.equals(InventoryConstant.Report_Type_RAWMIX)?false:true;
            for (Iterator<InventoryRecordModel> iterator = resList.iterator(); iterator.hasNext();) {
                InventoryRecordModel inventoryRecordModel = iterator.next();
                ws.addCell(new Label(0, row, StringUtil.isNullOrEmpty(inventoryRecordModel.getRecordName()) ? recordName : inventoryRecordModel.getRecordName(), normalFont));
                ws.addCell(new Label(1, row, inventoryRecordModel.getMaterialCode(), normalFont));
                ws.addCell(new Label(2, row, inventoryRecordModel.getMaterialName(), normalFont));
                ws.addCell(new Label(3, row, inventoryRecordModel.getColorCode(), normalFont));
                ws.addCell(new Label(4, row, inventoryRecordModel.getColorName(), normalFont));

                if (useIntValut){
                    ws.addCell(new Label(5, row, inventoryRecordModel.getLastBalance().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(6, row, inventoryRecordModel.getPeriodIn().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(7, row, inventoryRecordModel.getPeriodInNet().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(8, row, inventoryRecordModel.getPeriodOut().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(9, row, inventoryRecordModel.getPeriodOutNet().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(10, row, inventoryRecordModel.getBalance().setScale(0).toString(), normalFont));
                }else{
                    ws.addCell(new Label(5, row, inventoryRecordModel.getLastBalance().toString(), normalFont));
                    ws.addCell(new Label(6, row, inventoryRecordModel.getPeriodIn().toString(), normalFont));
                    ws.addCell(new Label(7, row, inventoryRecordModel.getPeriodInNet().toString(), normalFont));
                    ws.addCell(new Label(8, row, inventoryRecordModel.getPeriodOut().toString(), normalFont));
                    ws.addCell(new Label(9, row, inventoryRecordModel.getPeriodOutNet().toString(), normalFont));
                    ws.addCell(new Label(10, row, inventoryRecordModel.getBalance().toString(), normalFont));
                }
                ws.addCell(new Label(11, row, inventoryRecordModel.getStartDate() == null ? begindate : dt1.format(inventoryRecordModel.getStartDate()), normalFont));
                ws.addCell(new Label(12, row, inventoryRecordModel.getEndDate() == null ? enddate : dt1.format(inventoryRecordModel.getEndDate()), normalFont));
                ws.addCell(new Label(13, row, inventoryRecordModel.getUnitPrice().toString(), normalFont));
                if ("compareToInvSum".equals(repType)){
                    ws.addCell(new Label(14, row, inventoryRecordModel.getCompareTargetQty()==null?"":inventoryRecordModel.getCompareTargetQty().setScale(0).toString(), normalFont));
                    ws.addCell(new Label(15, row, inventoryRecordModel.getCompareTargetQty()==null?"":inventoryRecordModel.getCompareTargetQty().subtract(inventoryRecordModel.getBalance()).setScale(0).toString(), normalFont));
                }
                row++;
            }

            response.flushBuffer();
            wwb.write();
            wwb.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
