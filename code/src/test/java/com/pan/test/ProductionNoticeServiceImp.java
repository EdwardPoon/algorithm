package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.salesorder.model.InventoryRecordModel;
import com.poons.salesorder.po.BomInfo;
import com.poons.salesorder.po.BomInfoExt;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.MaterialInfo;
import com.poons.salesorder.po.Prodmodelinfo;
import com.poons.salesorder.po.ProductNoticeFinishQty;
import com.poons.salesorder.po.ProductionNoticeDetail;
import com.poons.salesorder.po.ProductionNoticeDetailRaw;
import com.poons.salesorder.po.ProductionNoticeMaster;
import com.poons.salesorder.po.SalesOrderDetail;
import com.poons.salesorder.service.InventoryService;
import com.poons.salesorder.service.ProductionNoticeService;
import com.poons.salesorder.service.bo.CalRawMaterialInfo;
import com.poons.salesorder.service.bo.ProductOutPackageBO;
import com.poons.salesorder.utils.InventoryConstant;

@Service("productionNoticeService")
public class ProductionNoticeServiceImp implements ProductionNoticeService {

    @Autowired
    private UtilsDao utilsDao;
    @Autowired
    private InventoryService inventoryService;
    
    private Map<String,String> bomInfoExt;
    
    @Override
    @Transactional
    public String generateProductionNotice(String ordernumber) {
    	bomInfoExt = loadBomInfoExt();
    	
        //utilsDao.execSql("update sales_order_master set OrderStatus='0' where ordernumber=? and OrderStatus='' ",ordernumber);
        utilsDao.execSql("UPDATE sales_order_detail set colorcode='' where colorcode is null and ordernumber=? ",ordernumber);
        
        StringBuilder strRes = new StringBuilder("");
        //查询出订单明细的产品、数量
        List<SalesOrderDetail> orderDetailList = 
                utilsDao.find("from SalesOrderDetail where ordernumber=?",ordernumber);

        //utilsDao.execSql("update sales_order_master set OrderStatus='0' where ordernumber=? and OrderStatus=''", ordernumber);
        //生产通知单master信息记录
        //先删除原来的master
        utilsDao.execute("delete from ProductionNoticeMaster where ordernumber=?", ordernumber);
        //成品生产通知单master
        ProductionNoticeMaster noticeMasterProd = new ProductionNoticeMaster();
        noticeMasterProd.setNoticetype(0);
        String noticeTypeCodeProd = "prod";
        String noticeCodeProd = ordernumber+"_"+noticeTypeCodeProd;
        noticeMasterProd.setProductionnoticecode(noticeCodeProd);
        noticeMasterProd.setProductionnoticename(noticeCodeProd);
        noticeMasterProd.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterProd);
        //半成品生产通知单master
        ProductionNoticeMaster noticeMasterSemiProd = new ProductionNoticeMaster();
        noticeMasterSemiProd.setNoticetype(1);
        String noticeTypeCodeProdSemi = "semiprod";
        String noticeCodeProdSemi = ordernumber+"_"+noticeTypeCodeProdSemi;
        noticeMasterSemiProd.setProductionnoticecode(noticeCodeProdSemi);
        noticeMasterSemiProd.setProductionnoticename(noticeCodeProdSemi);
        noticeMasterSemiProd.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterSemiProd);
        //安装通知单master
        ProductionNoticeMaster noticeMasterSetup = new ProductionNoticeMaster();
        noticeMasterSetup.setNoticetype(2);
        String noticeTypeCodeSetup = "setup";
        String noticeCodeSetup = ordernumber+"_"+noticeTypeCodeSetup;
        noticeMasterSetup.setProductionnoticecode(noticeCodeSetup);
        noticeMasterSetup.setProductionnoticename(noticeCodeSetup);
        noticeMasterSetup.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterSetup);
        //五金件领料通知单master
        ProductionNoticeMaster noticeMasterHardwareCollect = new ProductionNoticeMaster();
        noticeMasterHardwareCollect.setNoticetype(3);
        String noticeTypeCodeHardwareCollect = "hardwareCollect";
        String noticeCodeHardwareCollect = ordernumber+"_"+noticeTypeCodeHardwareCollect;
        noticeMasterHardwareCollect.setProductionnoticecode(noticeCodeHardwareCollect);
        noticeMasterHardwareCollect.setProductionnoticename(noticeCodeHardwareCollect);
        noticeMasterHardwareCollect.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterHardwareCollect);
        //包装领料通知单master
        ProductionNoticeMaster noticeMasterPackageCollect = new ProductionNoticeMaster();
        noticeMasterPackageCollect.setNoticetype(7);
        String noticeTypeCodePackageCollect = "packageCollect";
        String noticeCodePackageCollect = ordernumber+"_"+noticeTypeCodePackageCollect;
        noticeMasterPackageCollect.setProductionnoticecode(noticeCodePackageCollect);
        noticeMasterPackageCollect.setProductionnoticename(noticeCodePackageCollect);
        noticeMasterPackageCollect.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterPackageCollect);
        //原料领料通知单master
        ProductionNoticeMaster noticeMasterResourceCollect = new ProductionNoticeMaster();
        noticeMasterResourceCollect.setNoticetype(4);
        String noticeTypeCodeResourceCollect = "resourceCollect";
        String noticeCodeResourceCollect = ordernumber+"_"+noticeTypeCodeResourceCollect;
        noticeMasterResourceCollect.setProductionnoticecode(noticeCodeResourceCollect);
        noticeMasterResourceCollect.setProductionnoticename(noticeCodeResourceCollect);
        noticeMasterResourceCollect.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterResourceCollect);
        //水口领料通知单master
        ProductionNoticeMaster noticeMasterNozzleCollect = new ProductionNoticeMaster();
        noticeMasterNozzleCollect.setNoticetype(5);
        String noticeTypeCodeNozzleCollect = "nozzleCollect";
        String noticeCodeNozzleCollect = ordernumber+"_"+noticeTypeCodeNozzleCollect;
        noticeMasterNozzleCollect.setProductionnoticecode(noticeCodeNozzleCollect);
        noticeMasterNozzleCollect.setProductionnoticename(noticeCodeNozzleCollect);
        noticeMasterNozzleCollect.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterNozzleCollect);
        //成品直接出货
        ProductionNoticeMaster noticeMasterDirectOut = new ProductionNoticeMaster();
        noticeMasterDirectOut.setNoticetype(6);
        String noticeTypeCodeDirectOut = "directOut";
        String noticeCodeDirectOut = ordernumber+"_"+noticeTypeCodeDirectOut;
        noticeMasterDirectOut.setProductionnoticecode(noticeCodeDirectOut);
        noticeMasterDirectOut.setProductionnoticename(noticeCodeDirectOut);
        noticeMasterDirectOut.setOrdernumber(ordernumber);
        utilsDao.insert(noticeMasterDirectOut);
        //先删除原来的detail
        utilsDao.execute("delete from ProductionNoticeDetail where ordernumber=?", ordernumber);

        //对每只产品，查询bom表，计算出需要生产的半成品、需要安装的、需要领用的五金件
        Map<String,BigDecimal> wujinMap = new HashMap<String,BigDecimal>();//五金料数量map
        Map<String,BigDecimal> packageMap = new HashMap<String,BigDecimal>();//包装物料数量map
        Map<String,ProductionNoticeDetail> semiProdMap = new HashMap<String,ProductionNoticeDetail>();//半成品生产通知map
        Map<String,BigDecimal> resorceMap = new HashMap<String,BigDecimal>();//原料数量map,不含颜色
        Map<String,BigDecimal> resorceColorMap = new HashMap<String,BigDecimal>();//原料数量map,含颜色
        Map<String,BigDecimal> setupMap = new HashMap<String,BigDecimal>();//安装map
        Map<String,Integer> prodAndSetupMap = new HashMap<String,Integer>();//生产并安装map
        //Map<String,BigDecimal> nozzleMap = new HashMap<String,BigDecimal>();//水口料数量map
        Date deliverDate = null;
        for (Iterator<SalesOrderDetail> iterator = orderDetailList.iterator(); iterator.hasNext();) {
            SalesOrderDetail salesOrderDetail =  iterator.next();
            if (salesOrderDetail.getMaterialcode() == null)
                continue;
            deliverDate = salesOrderDetail.getDeliverdate();
            
            List<MaterialInfo> materialList1 =utilsDao.find("from MaterialInfo where materialcode=?",salesOrderDetail.getMaterialcode());
            if (materialList1.size() <= 0) continue;
            MaterialInfo materialInfo1= materialList1.get(0);
            //如果是成品并且不需要生产，直接保存外购直接出货单
            if(materialInfo1.getIsneedprodction()!=null && materialInfo1.getIsneedprodction().intValue()==2){
                ProductionNoticeDetail noticeDetailSetup = new ProductionNoticeDetail();
                noticeDetailSetup.setColorcode(salesOrderDetail.getColorcode());
                noticeDetailSetup.setMaterialcode(salesOrderDetail.getMaterialcode());
                noticeDetailSetup.setProductionnoticecode(noticeCodeDirectOut);
                noticeDetailSetup.setOrdernumber(ordernumber);
                noticeDetailSetup.setProductionNoticeTypeCode(noticeTypeCodeDirectOut);
                noticeDetailSetup.setQuantity(salesOrderDetail.getQuantity());
                noticeDetailSetup.setUnitcode(salesOrderDetail.getUnitcode());
                noticeDetailSetup.setDeliverdate(salesOrderDetail.getDeliverdate());
                //noticeDetailSetup.setSalesorderdetailid(salesOrderDetail.getSalesorderdetailid());
                utilsDao.insert(noticeDetailSetup);
                updateProductNoticeQty(noticeDetailSetup);
                continue;
            }
            //如果是成品并需要安装，保存安装通知单
            if(materialInfo1.getIsneedsetup().equals("是")){
                String key = salesOrderDetail.getMaterialcode() +"---===" + salesOrderDetail.getColorcode();
                if (setupMap.get(key.toString())!=null){
                    setupMap.put(key.toString(), setupMap.get(key.toString()).add(salesOrderDetail.getQuantity()));
                }else{
                    setupMap.put(key.toString(),salesOrderDetail.getQuantity());
                }
            }

            List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",salesOrderDetail.getMaterialcode());

            if (bomlist==null || bomlist.size()<=0){
                //getIsneedprodction!=2 的产品，需要生产，但没bom表记录的产品
                if(materialInfo1.getIsneedprodction()==null || materialInfo1.getIsneedprodction().intValue()!=2){
                    strRes.append(materialInfo1.getMaterialcode()).append(";");
                    //System.out.println("1=="+ materialInfo1.getMaterialcode());
                }
                continue;
            }
            CalRawMaterialInfo rawMatQty = null;
            String submaterialCode = null;
            for (Iterator<BomInfo> iterator2 = bomlist.iterator(); iterator2.hasNext();) {
                BomInfo bomInfo = iterator2.next();
                submaterialCode = bomInfo.getSubmaterialcode();
                String retStr = genSubMaterial(ordernumber,bomInfo,salesOrderDetail.getMaterialcode(),salesOrderDetail.getColorcode(),salesOrderDetail.getDeliverdate(),
                		salesOrderDetail.getQuantity(),wujinMap,packageMap, semiProdMap,resorceMap,resorceColorMap,setupMap);
                strRes.append(retStr);
            }
            //成品生产通知单
            if(materialInfo1.getIsneedsetup().equals("否")||materialInfo1.getIsneedsetup().equals("生产并安装")){
                //不需要安装，即只由原料组成的，需计算原料，用于成品生产通知单
                if (materialInfo1.getIsneedsetup().equals("否")){ //生产并安装 的不需要
                    rawMatQty =calculateRawQuantity(
                            salesOrderDetail.getQuantity(),
                            salesOrderDetail.getMaterialcode());//计算原料数量的结果
                }
                
                ProductionNoticeDetail noticeDetailProd1 = new ProductionNoticeDetail();
                noticeDetailProd1.setColorcode(salesOrderDetail.getColorcode());
                noticeDetailProd1.setMaterialcode(salesOrderDetail.getMaterialcode());
                noticeDetailProd1.setProductionnoticecode(noticeCodeProd);
                noticeDetailProd1.setOrdernumber(ordernumber);
                noticeDetailProd1.setProductionNoticeTypeCode(noticeTypeCodeProd);
                noticeDetailProd1.setQuantity(salesOrderDetail.getQuantity());
                noticeDetailProd1.setUnitcode(materialInfo1.getStandardunitcode());
                noticeDetailProd1.setDeliverdate(salesOrderDetail.getDeliverdate());
                //noticeDetailProd1.setSalesorderdetailid(salesOrderDetail.getSalesorderdetailid());
                utilsDao.insert(noticeDetailProd1);
                updateProductNoticeQty(noticeDetailProd1);
                if (materialInfo1.getIsneedsetup().equals("否")){//成品直接由原料组成
                    ProductionNoticeDetailRaw noticeDetailProdRaw = new ProductionNoticeDetailRaw();
                    noticeDetailProdRaw.setNozzleweight(rawMatQty.getNozzleWeight());
                    noticeDetailProdRaw.setProdgroupcount(rawMatQty.getProdgroupcount());
                    noticeDetailProdRaw.setTotalweight(rawMatQty.getMaterialWeight());
                    noticeDetailProdRaw.setProdweight(materialInfo1.getUnitweight()==null?BigDecimal.ZERO
                            :materialInfo1.getUnitweight().multiply(noticeDetailProd1.getQuantity()).divide(BigDecimal.valueOf(1000)));
                    noticeDetailProdRaw.setRawmatcode(submaterialCode);
                    noticeDetailProdRaw.setMaterialcode(salesOrderDetail.getMaterialcode());
                    noticeDetailProdRaw.setColorcode(salesOrderDetail.getColorcode());
                    noticeDetailProdRaw.setProductiondetailid(noticeDetailProd1.getProductiondetailid());
                    utilsDao.insert(noticeDetailProdRaw);
                }else{//生产并安装的（成品直接由半成品组成，但直接生产）
                    for (Iterator<BomInfo> iterator2 = bomlist.iterator(); iterator2.hasNext();) {
                        BomInfo bomInfo = iterator2.next();
                        String key = bomInfo.getSubmaterialcode()+"---"+salesOrderDetail.getColorcode();
                        prodAndSetupMap.put(key, noticeDetailProd1.getProductiondetailid());
                    }
                }
            }
            calculateProductOutPackageAndUpdateSalesDetail(salesOrderDetail);
        }
        //安装通知单
        for (Iterator<Entry<String,BigDecimal>> iterator = setupMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();
            String materialCode = entry.getKey().split("---===")[0];
            String colorCode = "";
            if (entry.getKey().split("---===").length>1){
                colorCode = entry.getKey().split("---===")[1];
            }
            ProductionNoticeDetail noticeDetailSetup = new ProductionNoticeDetail();
            noticeDetailSetup.setColorcode(colorCode);
            noticeDetailSetup.setMaterialcode(materialCode);
            noticeDetailSetup.setProductionnoticecode(noticeCodeSetup);
            noticeDetailSetup.setOrdernumber(ordernumber);
            noticeDetailSetup.setProductionNoticeTypeCode(noticeTypeCodeSetup);
            noticeDetailSetup.setQuantity(entry.getValue());
            noticeDetailSetup.setUnitcode("只");
            noticeDetailSetup.setDeliverdate(deliverDate);
            //noticeDetailSetup.setSalesorderdetailid(salesOrderDetail.getSalesorderdetailid());
            utilsDao.insert(noticeDetailSetup);
            updateProductNoticeQty(noticeDetailSetup);
        }
        //半成品生产通知单
        for (Iterator<Entry<String,ProductionNoticeDetail>> iterator = semiProdMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,ProductionNoticeDetail> entry = iterator.next();
            ProductionNoticeDetail productionNoticeDetail = entry.getValue();
            if (productionNoticeDetail.getIsNeedGenerate().intValue()==1){
                utilsDao.insert(productionNoticeDetail);
                updateProductNoticeQty(productionNoticeDetail);
            }
            for (ProductionNoticeDetailRaw productionNoticeDetailRaw :productionNoticeDetail.getProductionNoticeDetailRaws()){
                if (productionNoticeDetail.getIsNeedGenerate().intValue()==1){
                    productionNoticeDetailRaw.setProductiondetailid(productionNoticeDetail.getProductiondetailid());
                }else{
                    String key = productionNoticeDetailRaw.getMaterialcode()+"---"+productionNoticeDetailRaw.getColorcode();
                    productionNoticeDetailRaw.setProductiondetailid(prodAndSetupMap.get(key));
                }
                utilsDao.insert(productionNoticeDetailRaw);
            }
        }
        //保存五金领用通知单
        for (Iterator<Entry<String,BigDecimal>> iterator = wujinMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();
            ProductionNoticeDetail noticeDetailMetals = new ProductionNoticeDetail();
            noticeDetailMetals.setMaterialcode(entry.getKey());
            noticeDetailMetals.setProductionnoticecode(noticeCodeHardwareCollect);
            noticeDetailMetals.setOrdernumber(ordernumber);
            noticeDetailMetals.setProductionNoticeTypeCode(noticeTypeCodeHardwareCollect);
            noticeDetailMetals.setUnitcode("件");
            noticeDetailMetals.setQuantity(entry.getValue());
            noticeDetailMetals.setDeliverdate(deliverDate);
            noticeDetailMetals.setColorcode("");
            //noticeDetailMetals.setUnitprice(materialInfo.getUnitprice());
            //productionNoticeDetailMapper.insertSelective(noticeDetailMetals);
            utilsDao.insert(noticeDetailMetals);
            updateProductNoticeQty(noticeDetailMetals);
        }
        //保存包装物料领用通知单
        for (Iterator<Entry<String,BigDecimal>> iterator = packageMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();
            ProductionNoticeDetail noticeDetailPackage = new ProductionNoticeDetail();
            noticeDetailPackage.setMaterialcode(entry.getKey());
            noticeDetailPackage.setProductionnoticecode(noticeCodePackageCollect);
            noticeDetailPackage.setOrdernumber(ordernumber);
            noticeDetailPackage.setProductionNoticeTypeCode(noticeTypeCodePackageCollect);
            noticeDetailPackage.setUnitcode("件");
            noticeDetailPackage.setQuantity(entry.getValue());
            noticeDetailPackage.setDeliverdate(deliverDate);
            noticeDetailPackage.setColorcode("");
            //noticeDetailMetals.setUnitprice(materialInfo.getUnitprice());
            //productionNoticeDetailMapper.insertSelective(noticeDetailMetals);
            utilsDao.insert(noticeDetailPackage);
            updateProductNoticeQty(noticeDetailPackage);
        }
        //水口料领用
        for (Iterator<Entry<String,BigDecimal>> iterator = resorceColorMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();
            /*if(resorceColorMap.get(key).intValue() == 0)
                continue;*/
            String materialCode = entry.getKey().split("---===")[0];
            String colorCode = "";
            if (entry.getKey().split("---===").length>1)
                colorCode = entry.getKey().split("---===")[1];
            InventoryInfo inventoryInfo = new InventoryInfo();
            inventoryInfo.setMaterialcode(materialCode);
            inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
            inventoryInfo.setColorcode(colorCode);
            inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_RAW);
            List<InventoryInfo> inventoryList = utilsDao.findByExample(inventoryInfo);
            
            if (inventoryList.size()>0){
                //BigDecimal resObj = (BigDecimal)invenList.get(0);
                BigDecimal nozzleInvenQuantity =inventoryList.get(0).getQuantity();
                BigDecimal nozzleQuantity =new BigDecimal(0);
                //若领料量大于水口存量，则领用水口量为
                if (entry.getValue().compareTo(nozzleInvenQuantity) >= 0){
                    nozzleQuantity = nozzleInvenQuantity;
                }else{
                    nozzleQuantity = entry.getValue();
                }
                //保存水口领料, 不用扣减库存水口料，因为真正领料时才扣减，也不用扣减resorceMap，因为两个map独立，领料数只为提示
                ProductionNoticeDetail noticeDetailNozzle = new ProductionNoticeDetail();
                noticeDetailNozzle.setColorcode(colorCode);
                noticeDetailNozzle.setMaterialcode(materialCode);
                noticeDetailNozzle.setProductionnoticecode(noticeCodeNozzleCollect);
                noticeDetailNozzle.setOrdernumber(ordernumber);
                noticeDetailNozzle.setProductionNoticeTypeCode(noticeTypeCodeNozzleCollect);
                noticeDetailNozzle.setQuantity(nozzleQuantity);
                noticeDetailNozzle.setUnitcode("公斤");
                noticeDetailNozzle.setDeliverdate(deliverDate);
                //noticeDetailNozzle.setUnitprice(materialInfo.getUnitprice());
                //productionNoticeDetailMapper.insertSelective(noticeDetailNozzle);
                utilsDao.insert(noticeDetailNozzle);
                updateProductNoticeQty(noticeDetailNozzle);
                    
            }
        }
        //save resource map, always including color, produtionNotice form will sum the data group by materialCode 
        Map<String,BigDecimal> subResorceColorMap = new HashMap<String,BigDecimal>();
        for (Iterator<Entry<String,BigDecimal>> iterator = resorceColorMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();

            String[] keyArr = entry.getKey().split("---===");
            String colorcode = "";
            String resMatieralCode = keyArr[0];
            if (keyArr.length>1){
                colorcode = keyArr[1];
            }
            List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",resMatieralCode);
            if (bomlist!=null && bomlist.size()>0){
                for (BomInfo bomInfo : bomlist){
                    String key = bomInfo.getSubmaterialcode() + "---==="  +colorcode;
                    BigDecimal value = entry.getValue().multiply(bomInfo.getSubmaterialquantity());
                    BigDecimal oldValue = subResorceColorMap.get(key);
                    if (oldValue==null){
                        subResorceColorMap.put(key, value);
                    }else{
                        subResorceColorMap.put(key, oldValue.add(value));
                    }
                }
            }
        }
        //merge into the resorceColorMap
        for (Iterator<Entry<String,BigDecimal>> iterator = subResorceColorMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();
            BigDecimal oldValue = resorceColorMap.get(entry.getKey());
            if (oldValue==null){
                resorceColorMap.put(entry.getKey(), entry.getValue());
            }else{
                resorceColorMap.put(entry.getKey(), oldValue.add(entry.getValue()));
            }
        }
        
        for (Iterator<Entry<String,BigDecimal>> iterator = resorceColorMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String,BigDecimal> entry = iterator.next();

            String[] keyArr = entry.getKey().split("---===");
            String colorcode = "";
            String resMatieralCode = keyArr[0];
            if (keyArr.length>1){
                colorcode = keyArr[1];
            }
            
            ProductionNoticeDetail noticeDetailRes = new ProductionNoticeDetail();
            noticeDetailRes.setMaterialcode(resMatieralCode);
            noticeDetailRes.setColorcode(colorcode);
            noticeDetailRes.setProductionnoticecode(noticeCodeResourceCollect);
            noticeDetailRes.setOrdernumber(ordernumber);
            noticeDetailRes.setProductionNoticeTypeCode(noticeTypeCodeResourceCollect);
            noticeDetailRes.setQuantity(entry.getValue());
            noticeDetailRes.setUnitcode("公斤");
            noticeDetailRes.setDeliverdate(deliverDate);
            noticeDetailRes.setResoucelevel(1);
            //noticeDetailMetals.setUnitprice(materialInfo.getUnitprice());
            utilsDao.insert(noticeDetailRes);
            updateProductNoticeQty(noticeDetailRes);
        }
        return strRes.toString();
    }
    private Map<String,String> loadBomInfoExt(){
    	List<BomInfoExt> list = utilsDao.find("from BomInfoExt");
    	Map<String,String> map = new HashMap<String,String>();
    	for (BomInfoExt bomInfoExt: list){
    		map.put(bomInfoExt.getParentmaterialcode()+"-"+bomInfoExt.getParentcolorcode()
    				+"-"+bomInfoExt.getSubmaterialcode(), bomInfoExt.getSubcolorcode());
    	}
    	return map;
    }
    private void calculateProductOutPackageAndUpdateSalesDetail(SalesOrderDetail salesOrderDetail){
        ProductOutPackageBO productOutPackageBO = new ProductOutPackageBO();
        productOutPackageBO.setTotalcount(salesOrderDetail.getQuantity());
        productOutPackageBO.setCppack(salesOrderDetail.getQuantityperpack());
        inventoryService.calculateProductOutPackages(productOutPackageBO);
        salesOrderDetail.setPackages(productOutPackageBO.getPackages());
        salesOrderDetail.setCpPack(productOutPackageBO.getCppack());
        salesOrderDetail.setRoundCount(productOutPackageBO.getRoundCount());
        salesOrderDetail.setResidueCount(productOutPackageBO.getResidueCount());
        salesOrderDetail.setResiduePackage(productOutPackageBO.getResiduePackage());
        utilsDao.update(salesOrderDetail);
    }
    //生成和计算bomInfo中子物料的记录
    //inputCount 因为循环计算，所以子层就不能直接用salesOrderDetail.getQuantity()，用这个变量
    //wujinMap 五金料数量map
    //semiProdMap 半成品map
    // resorceMap 原料数量map,不含颜色
    // resorceColorMap原料数量map,含颜色
    private String genSubMaterial(String ordernumber,BomInfo bomInfo,String materialCode,String colorCode,Date deliverdate,BigDecimal inputCount,
            Map<String,BigDecimal> wujinMap,Map<String,BigDecimal> packageMap,Map<String,ProductionNoticeDetail> semiProdMap,
            Map<String,BigDecimal> resorceMap,Map<String,BigDecimal> resorceColorMap,Map<String,BigDecimal> setupMap){
        StringBuilder strRes = new StringBuilder("");
        CalRawMaterialInfo rawMatQty = null;
        String noticeTypeCodeProdSemi = "semiprod";
        String noticeCodeProdSemi = ordernumber+"_"+noticeTypeCodeProdSemi;
        String submaterialCode = bomInfo.getSubmaterialcode();
        String bomExtMapKey = materialCode
        		+ "-" + colorCode
        		+ "-" + submaterialCode;
        System.out.println("bomExtMapKey:" + bomExtMapKey);
        
        List<MaterialInfo> materialList =utilsDao.find("from MaterialInfo where materialcode=?",submaterialCode);
        //System.out.println("子物料:"+materialCode);
        if (materialList.size() <= 0) 
            strRes.toString();
        MaterialInfo subMat = materialList.get(0);
        //如果是半成品，保存生产通知单
        if(subMat.getMaterialtype().equals("半成品") 
                || subMat.getMaterialtype().equals("成品")){
            BigDecimal quantity = inputCount.multiply(
                    bomInfo.getSubmaterialquantity());

            if(subMat.getMaterialtype().equals("成品") && subMat.getIsneedsetup().equals("是")){
            	String subMatColorCode = null;
                if (bomInfoExt.get(bomExtMapKey)!=null){
                	subMatColorCode = bomInfoExt.get(bomExtMapKey);
                }else{
                	subMatColorCode = colorCode;
                }
                System.out.println("成品，setup，materialCode:" + subMat.getMaterialcode() + ",subMatColorCode:" + subMatColorCode);
                //如果需要安装，保存安装通知单(因为某些成品又由一些需安装的成品组成)
                String key = subMat.getMaterialcode() +"---===" + subMatColorCode;
                if (setupMap.get(key.toString())!=null){
                    setupMap.put(key.toString(), setupMap.get(key.toString()).add(quantity));
                }else{
                    setupMap.put(key.toString(), quantity);
                }

                //如果需安装，并且是成品，需要再次递归
                List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",subMat.getMaterialcode());
                for (Iterator<BomInfo> iterator = bomlist.iterator(); iterator.hasNext();) {
                    BomInfo bomInfo2 = iterator.next();
                    
                    strRes.append(genSubMaterial(ordernumber, bomInfo2, bomInfo2.getParentmaterialcode(),subMatColorCode,deliverdate,inputCount.multiply(bomInfo.getSubmaterialquantity()),
                            wujinMap,packageMap, semiProdMap, resorceMap, resorceColorMap,setupMap));
                }
                return strRes.toString();
            }else{
            	System.out.println("半成品或成品，nosetup，materialCode:" + subMat.getMaterialcode() + ",submaterialCode:" + submaterialCode);
	            //半成品，查询对应的原料
	            List<BomInfo> bomResList =utilsDao.find("from BomInfo where parentmaterialcode=?",submaterialCode);
	            
	            if (bomResList.size()<=0){
	                //System.out.println("2=="+ submaterialCode);
	                strRes.append(submaterialCode).append(";");
	            }else{
	                //因为每只产品，或半成品只有一种原料
	                BomInfo bomResInfo = bomResList.get(0);
	                String materialResCode = bomResInfo.getSubmaterialcode();
	                //System.out.println("原料:"+materialResCode);
	                //如果是原料，保存原料领料通知单
	                if(bomResInfo.getMaterialtype().equals("原料")){
	                    //通过模具计算需要多少原料
	                    //需生产件数 = 订单件数 * bom的半成品数量
	                    rawMatQty = calculateRawQuantity(
	                            inputCount.multiply(bomInfo.getSubmaterialquantity()),
	                            submaterialCode);
	                    if(rawMatQty!=null){
	                        String key =  materialResCode;
	                        if (resorceMap.get(key)!=null){
	                            resorceMap.put(key, resorceMap.get(key).add(rawMatQty.getMaterialWeight()));
	                        }else{
	                            resorceMap.put(key, rawMatQty.getMaterialWeight());
	                        }
	                        if (subMat.getMaterialdesc().equals(InventoryConstant.MaterialDesc_NoColor)){
	                            key =materialResCode + "---===";
	                        }else{
	                        	String semicolorcode = colorCode;
	                        	if (bomInfoExt.get(bomExtMapKey)!=null){
	                            	semicolorcode = bomInfoExt.get(bomExtMapKey);
	                            }
	                            key =materialResCode + "---===" + semicolorcode;
	                        }
	                        if (resorceColorMap.get(key)!=null){
	                            resorceColorMap.put(key, resorceColorMap.get(key).add(rawMatQty.getMaterialWeight()));
	                        }else{
	                            resorceColorMap.put(key, rawMatQty.getMaterialWeight());
	                        }
	                    }
	                }
	                //半成品生产通知单
	                String key2 = "";
	                String semicolorcode = "";
	                if (subMat.getMaterialdesc().equals(InventoryConstant.MaterialDesc_NoColor)){
	                    key2 = submaterialCode+"___";
	                }else{
	                    key2 = submaterialCode+"___"+colorCode;
	                    semicolorcode = colorCode;
	                    
	                    if (bomInfoExt.get(bomExtMapKey)!=null){
	                    	semicolorcode = bomInfoExt.get(bomExtMapKey);
	                    }
	                }
	                if (semiProdMap.get(key2)==null){
	                    ProductionNoticeDetail noticeDetailProd = new ProductionNoticeDetail();
	                    noticeDetailProd.setColorcode(semicolorcode);
	                    noticeDetailProd.setMaterialcode(submaterialCode);
	                    noticeDetailProd.setProductionnoticecode(noticeCodeProdSemi);
	                    noticeDetailProd.setOrdernumber(ordernumber);
	                    noticeDetailProd.setProductionNoticeTypeCode(noticeTypeCodeProdSemi);
	                    noticeDetailProd.setQuantity(quantity);
	                    noticeDetailProd.setUnitcode(bomInfo.getSubmaterialunit());
	                    noticeDetailProd.setUnitprice(subMat.getUnitprice());
	                    if (rawMatQty==null){
	                        System.out.println("rawMatQty is null,materialCode: "+submaterialCode);
	                    }
	                    noticeDetailProd.setDeliverdate(deliverdate);
	                    if (subMat.getIsneedsetup().equals("不需生产")){
	                        noticeDetailProd.setIsNeedGenerate(0);
	                    }else{
	                        noticeDetailProd.setIsNeedGenerate(1);
	                    }
	                    
	                    /*noticeDetailProd.setProdgroupcount(rawMatQty.getProdgroupcount());
	                    noticeDetailProd.setNozzleweight(rawMatQty.getNozzleWeight());
	                    noticeDetailProd.setTotalweight(rawMatQty.getMaterialWeight());
	                    noticeDetailProd.setProdweight(subMat.getUnitweight()==null?BigDecimal.ZERO
	                            :subMat.getUnitweight().multiply(noticeDetailProd.getQuantity()).divide(BigDecimal.valueOf(1000)));
	                    noticeDetailProd.setRawmatcode(bomResInfo.getSubmaterialcode());*/
	                    ProductionNoticeDetailRaw noticeDetailProdRaw = new ProductionNoticeDetailRaw();
	                    noticeDetailProdRaw.setNozzleweight(rawMatQty.getNozzleWeight());
	                    noticeDetailProdRaw.setProdgroupcount(rawMatQty.getProdgroupcount());
	                    noticeDetailProdRaw.setTotalweight(rawMatQty.getMaterialWeight());
	                    BigDecimal prodWeight = subMat.getUnitweight()==null?BigDecimal.ZERO
	                            :subMat.getUnitweight().multiply(noticeDetailProd.getQuantity()).divide(BigDecimal.valueOf(1000));
	                    noticeDetailProdRaw.setProdweight(prodWeight);
	                    noticeDetailProdRaw.setRawmatcode(bomResInfo.getSubmaterialcode());
	                    noticeDetailProdRaw.setMaterialcode(submaterialCode);
	                    noticeDetailProdRaw.setColorcode(semicolorcode);
	                    //noticeDetailProdRaw.setProductiondetailid(productiondetailid);
	                    noticeDetailProd.addProductionNoticeDetailRaw(noticeDetailProdRaw);
	                    //productionNoticeDetailMapper.insertSelective(noticeDetailProd);
	                    //utilsDao.insert(noticeDetailProd);
	                    //updateProductNoticeQty(noticeDetailProd);
	                    
	                    semiProdMap.put(key2, noticeDetailProd);
	                }else{
	                    ProductionNoticeDetail noticeDetailProd = semiProdMap.get(key2);
	                    noticeDetailProd.setQuantity(noticeDetailProd.getQuantity().add(quantity));
	                    ProductionNoticeDetailRaw noticeDetailProdRaw = noticeDetailProd.getProductionNoticeDetailRaws().get(0);
	                    noticeDetailProdRaw.setNozzleweight(noticeDetailProdRaw.getNozzleweight().add(rawMatQty.getNozzleWeight()));
	                    noticeDetailProdRaw.setTotalweight(noticeDetailProdRaw.getTotalweight().add(rawMatQty.getMaterialWeight()));
	                    BigDecimal prodWeight = subMat.getUnitweight()==null?BigDecimal.ZERO
	                            :subMat.getUnitweight().multiply(noticeDetailProd.getQuantity()).divide(BigDecimal.valueOf(1000));
	                    noticeDetailProdRaw.setProdweight(prodWeight.add(noticeDetailProdRaw.getProdweight()));
	                    noticeDetailProdRaw.setProdgroupcount(noticeDetailProdRaw.getProdgroupcount().add(rawMatQty.getProdgroupcount()));
	                    /*
	                    Map<String,Object> params = new HashMap<String,Object>();
	                    params.put("quantity", noticeDetailProd.getQuantity());
	                    noticeDetailProd.setNozzleweight( noticeDetailProd.getNozzleweight().add(rawMatQty.getNozzleWeight()));
	                    params.put("nozzleweight", noticeDetailProd.getNozzleweight());
	                    noticeDetailProd.setTotalweight( noticeDetailProd.getTotalweight().add(rawMatQty.getMaterialWeight()));
	                    params.put("totalweight", noticeDetailProd.getTotalweight());
	                    params.put("colorcode", semicolorcode);
	                    params.put("materialcode", submaterialCode);
	                    params.put("productionnoticecode", noticeCodeProdSemi);
	                    utilsDao.execute("update ProductionNoticeDetail set quantity=:quantity,nozzleweight=:nozzleweight,totalweight=:totalweight "
	                            + " where colorcode=:colorcode and materialcode=:materialcode and productionnoticecode=:productionnoticecode", 
	                            params);*/
	                    semiProdMap.put(key2, noticeDetailProd);
	                }
	            }
            }
        }
        //如果是五金件，保存领用通知单
        else if(subMat.getMaterialtype().equals("五金件")){
            
            BigDecimal quantity = inputCount.multiply(
                    bomInfo.getSubmaterialquantity());
            String key = submaterialCode;
            if (wujinMap.get(key)==null){
                wujinMap.put(key,quantity);
            }else{
                wujinMap.put(key,wujinMap.get(key).add( quantity));
            }
        }
        //如果是包装物料，保存领用通知单
        else if(subMat.getMaterialtype().equals("包装物料")){
            
            BigDecimal quantity = inputCount.divideToIntegralValue(
                    bomInfo.getSubmaterialquantity());
            String key = submaterialCode;
            if (packageMap.get(key)==null){
                packageMap.put(key,quantity);
            }else{
                packageMap.put(key,packageMap.get(key).add( quantity));
            }
        }
        //如果是原料，保存原料领用通知单
        else if(subMat.getMaterialtype().equals("原料")){
            //通过模具计算需要多少原料
            //需生产件数 = 订单件数 
            rawMatQty = calculateRawQuantity(inputCount, materialCode);
            
            if(rawMatQty!=null){
                String key = submaterialCode;
                if (resorceMap.get(key.toString())!=null){
                    resorceMap.put(key.toString(), resorceMap.get(key.toString()).add(rawMatQty.getMaterialWeight()));
                }else{
                    resorceMap.put(key.toString(), rawMatQty.getMaterialWeight());
                }
                key = submaterialCode + "---==="+ colorCode;
                if (resorceColorMap.get(key.toString())!=null){
                    resorceColorMap.put(key.toString(), resorceColorMap.get(key.toString()).add(rawMatQty.getMaterialWeight()));
                }else{
                    resorceColorMap.put(key.toString(), rawMatQty.getMaterialWeight());
                }
            }
        }
        return strRes.toString();
    }
    
////////////////////////////////////////////////////////////////
    public void updateProductNoticeQty(ProductionNoticeDetail noticeDetailProd){
        ProductNoticeFinishQty finishqty = new ProductNoticeFinishQty();
        finishqty.setProductionnoticecode(noticeDetailProd.getProductionnoticecode());
        finishqty.setMaterialcode(noticeDetailProd.getMaterialcode());
        finishqty.setColorcode(noticeDetailProd.getColorcode());
        List<ProductNoticeFinishQty> list = utilsDao.findByExample(finishqty);
        if (list.size()>0){
            ProductNoticeFinishQty old = list.get(0);
            old.setProductiondetailid(noticeDetailProd.getProductiondetailid());
            utilsDao.update(old);
        }else{
            finishqty.setFinishquantity(BigDecimal.valueOf(0));
            finishqty.setDrawforsetupqty(BigDecimal.valueOf(0));
            finishqty.setProductiondetailid(noticeDetailProd.getProductiondetailid());
            utilsDao.insert(finishqty);
        }
    }
    //计算生产这么多件产品，需要多少原料（按模具信息计算）
    private CalRawMaterialInfo calculateRawQuantity(BigDecimal prodCount,String materialCode){
        Prodmodelinfo modelInfo = utilsDao.get(Prodmodelinfo.class, materialCode);
        if (modelInfo==null)
            return null;
        CalRawMaterialInfo calRalMatInfo = new CalRawMaterialInfo();
        BigDecimal groupCount = prodCount.divide(BigDecimal.valueOf(modelInfo.getItemCount()),
                0,BigDecimal.ROUND_UP);
        
        calRalMatInfo.setProdgroupcount(groupCount);
        calRalMatInfo.setMaterialWeight(groupCount.multiply(modelInfo.getTotalWeight())
                .divide(BigDecimal.valueOf(1000),3,BigDecimal.ROUND_UP));
        calRalMatInfo.setNozzleWeight(groupCount.multiply(modelInfo.getNozzleWeight())
                .divide(BigDecimal.valueOf(1000),3,BigDecimal.ROUND_UP));
        return calRalMatInfo;
    }
    @Override
    @Transactional(readOnly = true)
    public List<InventoryRecordModel> convertToRawMaterialList(
            List<InventoryRecordModel> prodAmountList) {
        List<InventoryRecordModel> retList = new ArrayList<InventoryRecordModel>();
        
        Map<String,InventoryRecordModel> resMap = new HashMap<String,InventoryRecordModel>();
        Map<String,Object> paramMap = new HashMap<String,Object>();
        for (InventoryRecordModel prodModel :prodAmountList){
            paramMap.clear();
            paramMap.put("materialcode", prodModel.getMaterialCode());
            
            List<Object[]> bomlist =utilsDao.findBySql("select b.subMaterialCode,m.materialName from BomInfo b, materialinfo m "
                    +" where b.subMaterialCode=m.materialCode and parentmaterialcode=:materialcode",paramMap);

            if (bomlist!=null && bomlist.size()>0){
                String materialCode = bomlist.get(0)[0]==null?"":bomlist.get(0)[0].toString();              
                String materialName = bomlist.get(0)[1]==null?"":bomlist.get(0)[1].toString();              
                if (resMap.get(materialCode)==null){
                    InventoryRecordModel res = new InventoryRecordModel();
                    res.setMaterialCode(materialCode);
                    res.setMaterialName(materialName);
                    res.setBalance(prodModel.getBalance());
                    resMap.put(materialCode, res);
                }else{
                    InventoryRecordModel res = resMap.get(materialCode);
                    res.setBalance(res.getBalance().add(prodModel.getBalance()));
                }
            }
        }
        List<InventoryRecordModel> resList = new ArrayList<InventoryRecordModel>();
        resList.addAll(resMap.values());
        return resList;
    }

}
