package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.base.model.Page;
import com.poons.frame.utils.StringUtil;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.MaterialInfo;
import com.poons.salesorder.po.ProductNoticeFinishQty;
import com.poons.salesorder.po.SalesOrderMasterV;
import com.poons.salesorder.service.InventoryInfoService;
import com.poons.salesorder.service.SalesOrderService;
import com.poons.salesorder.service.bo.SalesOrderDetailBO;
import com.poons.salesorder.utils.InventoryConstant;

@Service
public class SalesOrderServiceImp implements SalesOrderService {
	@Autowired
	private UtilsDao utilsDao;
	@Autowired
	private InventoryInfoService inventoryInfoService;
	
	private final Map<String,String> prodTypeMap = new HashMap<String,String>();
	
	@Override
	@Transactional(readOnly = true)
	public List<SalesOrderMasterV> querySalesOrderList(String custcode,String orderstatus,Page page) {
		Map<String,Object> param = new HashMap<String,Object>();
		StringBuilder sb = new StringBuilder("from SalesOrderMasterV where 1=1 ");
		if (!StringUtil.isNullOrEmpty(custcode)){
			param.put("custcode", custcode);
			sb.append(" and custcode = :custcode");
		}
		if (!StringUtil.isNullOrEmpty(orderstatus)){
			if (orderstatus.equals("1")){
				sb.append(" and orderstatus = '1' ");
			}else{
				sb.append(" and (orderstatus <> '1' or orderstatus is null )");
			}
		}
		sb.append(" order by orderenterdate desc");
		page.setTotalRecordSize(utilsDao.count("select count(salesorderid) " + sb.toString(),param));
		return utilsDao.find(sb.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(), param);
	}

	@Override
	@Transactional
	public String deleteSalesOrder(String ordernumber) {
		
		releaseOccupyQty(ordernumber);
		
		utilsDao.execute("delete from SalesOrderMaster where ordernumber=?", ordernumber);
		utilsDao.execute("delete from SalesOrderDetail where ordernumber=?", ordernumber);
		// utilsDao.execute("delete from ProductOutRecord where ordernumber=?", ordernumber);
		utilsDao.execute("UPDATE ProductOutRecord SET remarks ='order deleted',ordernumber=concat(ordernumber,'_deleted')  where ordernumber=?", ordernumber);
		utilsDao.execute("delete from ProductionNoticeMaster where ordernumber=?", ordernumber);
		utilsDao.execute("delete from ProductNoticeFinishQty where productiondetailid in (select productiondetailid from ProductionNoticeDetail where ordernumber=?)",  ordernumber);
		utilsDao.execute("delete from ProductionNoticeDetail where ordernumber=?",  ordernumber);

		return "0";
	}
	
	private void releaseOccupyQty(String ordernumber){
		
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String date = DATE_FORMAT.format(new Date());

        String recordNumber = "DeleteOrder_"+ordernumber + date;
        List<String> prodNoticeCodeList = new ArrayList<String>();
        prodNoticeCodeList.add(ordernumber+"_prod");
        prodNoticeCodeList.add(ordernumber+"_semiprod");
        prodNoticeCodeList.add(ordernumber+"_setup");
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("prodNoticeCodeList", prodNoticeCodeList);
        List<ProductNoticeFinishQty> finishQtyList = utilsDao.find(" from ProductNoticeFinishQty where productionnoticecode in (:prodNoticeCodeList)",param);
        
        for(ProductNoticeFinishQty prodFinishQty:finishQtyList ){
            InventoryInfo inventoryInfo = new InventoryInfo();
            inventoryInfo.setMaterialcode(prodFinishQty.getMaterialcode());
            inventoryInfo.setColorcode(prodFinishQty.getColorcode());
            inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
            inventoryInfo.setUserid("");
            inventoryInfo.setUnitcode("只");
            if (prodFinishQty.getProductionnoticecode().endsWith("_prod") 
            		||prodFinishQty.getProductionnoticecode().endsWith("_setup") ){
            	inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
            	inventoryInfo.setMaterialtype("成品");
            }else{
            	if (prodTypeMap.get(prodFinishQty.getMaterialcode())==null){
	            	List<MaterialInfo> matList = utilsDao.find("from MaterialInfo where materialcode=?",
	            			prodFinishQty.getMaterialcode());
	            	if (matList.size()>0){
	            		prodTypeMap.put(prodFinishQty.getMaterialcode(), matList.get(0).getMaterialtype());
	            		inventoryInfo.setMaterialtype( matList.get(0).getMaterialtype());
	            	}else{
	            		inventoryInfo.setMaterialtype("半成品");
	            	}
            	}else{
            		inventoryInfo.setMaterialtype( prodTypeMap.get(prodFinishQty.getMaterialcode()));
            	}
            	inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
            }
            inventoryInfo.setQuantity(BigDecimal.valueOf(0));
            inventoryInfo.setNotoccupyqty(prodFinishQty.getFinishquantity());
            inventoryInfoService.updateInventory(inventoryInfo,recordNumber);
        }
		// will not resume drawForSetpQty, need to process manually(let's say add an operation turn back) 
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesOrderDetailBO> querySalesOrderDetail(String ordernumber) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("ordernumber", ordernumber);
		List<Object[]> salesdetails = utilsDao.findBySql(
						"select t.SalesOrderDetailID,t.materialCode,m.materialName,t.colorCode,c.colorName,t.quantity,t.TotalDeliverQuantity,t.quantityPerPack "
					+" from sales_order_detail t inner join materialinfo m on t.materialCode=m.materialCode "
					+" left join colorinfo c on t.colorCode=c.colorCode "
					+" where t.ordernumber=:ordernumber order by t.SalesOrderDetailID ",param);
		List<SalesOrderDetailBO> resList = new ArrayList<SalesOrderDetailBO>();
		for (Object[] objs:salesdetails){
			SalesOrderDetailBO salesOrderDetailBO = new SalesOrderDetailBO();
			salesOrderDetailBO.setSalesorderdetailid(objs[0].toString());
			salesOrderDetailBO.setMaterialcode(objs[1].toString());
			salesOrderDetailBO.setMaterialname(objs[2]==null?"":objs[2].toString());
			salesOrderDetailBO.setColorcode(objs[3]==null?"":objs[3].toString());
			salesOrderDetailBO.setColorname(objs[4]==null?"":objs[4].toString());
			salesOrderDetailBO.setQuantity(objs[5]==null?BigDecimal.ZERO:new BigDecimal(objs[5].toString()));
			salesOrderDetailBO.setTotaldeliverquantity(objs[6]==null?BigDecimal.ZERO:new BigDecimal(objs[6].toString()));
			salesOrderDetailBO.setQuantityPerPack(objs[7]==null?BigDecimal.ZERO:new BigDecimal(objs[7].toString()));
			resList.add(salesOrderDetailBO);
		}
		return resList;
	}

}
