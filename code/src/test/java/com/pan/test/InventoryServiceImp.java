package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.base.model.Page;
import com.poons.frame.base.po.SystemParam;
import com.poons.frame.base.service.SystemParamService;
import com.poons.frame.utils.FrameConstant;
import com.poons.frame.utils.StringUtil;
import com.poons.salesorder.model.InventorySumModel;
import com.poons.salesorder.po.BomInfo;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.InventoryInfoSum;
import com.poons.salesorder.po.InventoryInfoSumTrc;
import com.poons.salesorder.po.InventoryInfoTrc;
import com.poons.salesorder.po.InventoryInfoV;
import com.poons.salesorder.po.MaterialInRecord;
import com.poons.salesorder.po.MaterialInfo;
import com.poons.salesorder.po.MaterialOutRecord;
import com.poons.salesorder.po.ProductNoticeFinishQty;
import com.poons.salesorder.po.ProductNoticeFinishQtyTrc;
import com.poons.salesorder.po.ProductOutRecord;
import com.poons.salesorder.po.SalesOrderDetail;
import com.poons.salesorder.po.SalesOrderDetailTrc;
import com.poons.salesorder.service.BomService;
import com.poons.salesorder.service.InventoryInfoService;
import com.poons.salesorder.service.InventoryService;
import com.poons.salesorder.service.RawMaterialService;
import com.poons.salesorder.service.bo.BomItemBO;
import com.poons.salesorder.service.bo.MaterialQuantity;
import com.poons.salesorder.service.bo.ProductNoticeFinishQtyBO;
import com.poons.salesorder.service.bo.ProductOutPackageBO;
import com.poons.salesorder.utils.InventoryConstant;

@Service("inventoryService")
public class InventoryServiceImp implements InventoryService {

	@Autowired
	private UtilsDao utilsDao;
	@Autowired
	private InventoryInfoService inventoryInfoService;
	@Autowired
	private SystemParamService systemParamService;
	@Autowired
	private RawMaterialService rawMaterialService;
	@Autowired
	private BomService bomService;
	
	private void insertSalesOrderDetailTrace(SalesOrderDetail oldSalesOrderDetail,BigDecimal addValue,String recordNumber,int type){
		
		SalesOrderDetailTrc salesOrderDetailTrc = new SalesOrderDetailTrc();
		try{
			BeanUtils.copyProperties(oldSalesOrderDetail,salesOrderDetailTrc);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		if (type==1){
			salesOrderDetailTrc.setInventoryquantityBefore(oldSalesOrderDetail.getInventoryquantity());
			salesOrderDetailTrc.setInventoryquantityAfter(oldSalesOrderDetail.getInventoryquantity().add(addValue));
			salesOrderDetailTrc.setInventoryquantityUpdate(addValue);
			salesOrderDetailTrc.setTotaldeliverquantityBefore(BigDecimal.ZERO);
			salesOrderDetailTrc.setTotaldeliverquantityAfter(BigDecimal.ZERO);
			salesOrderDetailTrc.setTotaldeliverquantityUpdate(BigDecimal.ZERO);
		}else{
			salesOrderDetailTrc.setInventoryquantityBefore(BigDecimal.ZERO);
			salesOrderDetailTrc.setInventoryquantityAfter(BigDecimal.ZERO);
			salesOrderDetailTrc.setInventoryquantityUpdate(BigDecimal.ZERO);
			salesOrderDetailTrc.setTotaldeliverquantityBefore(oldSalesOrderDetail.getTotaldeliverquantity());
			salesOrderDetailTrc.setTotaldeliverquantityAfter(oldSalesOrderDetail.getTotaldeliverquantity().add(addValue));
			salesOrderDetailTrc.setTotaldeliverquantityUpdate(addValue);
		}
		salesOrderDetailTrc.setRecordnumber(recordNumber);
		salesOrderDetailTrc.setTraceid(null);
		utilsDao.insert(salesOrderDetailTrc);
	}
	private void updateProductNoticeFinishQty(Integer id,Integer productionDetailId,BigDecimal oldValue, BigDecimal addValue,int type,String recordNumber){
		ProductNoticeFinishQty productNoticeFinishQty = null;
		if (id!=null){
			productNoticeFinishQty = utilsDao.get(ProductNoticeFinishQty.class, id);
		}else{
			List<ProductNoticeFinishQty> list = utilsDao.find("from ProductNoticeFinishQty where productiondetailid=?", productionDetailId);
			productNoticeFinishQty = list.get(0);
		}
		Map<String,Object> param = new HashMap<String,Object>();

		ProductNoticeFinishQtyTrc productNoticeFinishQtyTrc = new ProductNoticeFinishQtyTrc();
		try{
			BeanUtils.copyProperties(productNoticeFinishQty,productNoticeFinishQtyTrc);
			if (type==1){
				productNoticeFinishQtyTrc.setFinishquantityBefore(oldValue);
				productNoticeFinishQtyTrc.setFinishquantityAfter(oldValue.add(addValue));
				productNoticeFinishQtyTrc.setFinishquantityUpdate(addValue);
				productNoticeFinishQtyTrc.setDrawforsetupqtyBefore(BigDecimal.valueOf(0));
				productNoticeFinishQtyTrc.setDrawforsetupqtyAfter(BigDecimal.valueOf(0));
				productNoticeFinishQtyTrc.setDrawforsetupqtyUpdate(BigDecimal.valueOf(0));
				
				String hql = "update ProductNoticeFinishQty t set t.finishquantity=t.finishquantity+:finishquantity where t.id=:id ";
				param.put("id", productNoticeFinishQty.getId());
				param.put("finishquantity", addValue);
				utilsDao.execute(hql,param);

			}else{
				productNoticeFinishQtyTrc.setFinishquantityBefore(BigDecimal.valueOf(0));
				productNoticeFinishQtyTrc.setFinishquantityAfter(BigDecimal.valueOf(0));
				productNoticeFinishQtyTrc.setFinishquantityUpdate(BigDecimal.valueOf(0));
				productNoticeFinishQtyTrc.setDrawforsetupqtyBefore(oldValue);
				productNoticeFinishQtyTrc.setDrawforsetupqtyAfter(oldValue.add(addValue));
				productNoticeFinishQtyTrc.setDrawforsetupqtyUpdate(addValue);
				
				String hql ="update ProductNoticeFinishQty t set t.drawforsetupqty=t.drawforsetupqty+:drawforsetupqty where t.id=:id";
				param.put("id", productNoticeFinishQty.getId());
				param.put("drawforsetupqty", addValue);
				utilsDao.execute(hql, param);
			}
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		productNoticeFinishQtyTrc.setRecordnumber(recordNumber);
		productNoticeFinishQtyTrc.setTraceid(null);
		utilsDao.insert(productNoticeFinishQtyTrc);
	}
	@Override
	@Transactional
	public int chargeOff(String recordType,String recordId){
		int ret = 0;
		String recordNumber = "";
		String recordNumber_chargeoff = "";
		if (recordType.equals("materialIn")){
			MaterialInRecord materialInRecord = utilsDao.get(MaterialInRecord.class,Integer.valueOf(recordId));
			
			if (materialInRecord.getRecordstatus()!=null && materialInRecord.getRecordstatus()==1){
				ret = 1;
				return ret;
			}
			
			MaterialInRecord recordChargeOff = new MaterialInRecord();
			recordNumber = materialInRecord.getRecordnumber();
			recordNumber_chargeoff = recordNumber+"_chargeoff";
			try{
				BeanUtils.copyProperties(materialInRecord, recordChargeOff);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
			recordChargeOff.setRecordid(null);
			recordChargeOff.setQuantity(materialInRecord.getQuantity().negate());
			recordChargeOff.setQuantity2(materialInRecord.getQuantity2().negate());
			recordChargeOff.setRecordnumber(recordNumber_chargeoff);
			recordChargeOff.setCreatedate(new Date());
			if (materialInRecord.getTotalprice()!=null){
				recordChargeOff.setTotalprice(materialInRecord.getTotalprice().negate());
			}
			utilsDao.insert(recordChargeOff);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("recordId", Integer.valueOf(recordId));
			utilsDao.execute("update MaterialInRecord set recordstatus='1' where recordid=:recordId",map);
			
			// charge the semiprod record and metal record which is use for 进销存报表和员工领用统计
			if (materialInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)
					|| materialInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_Setup)){
				List<MaterialInRecord> matRecList = utilsDao.find("from MaterialInRecord where recordid<>? and recordnumber=?",
						Integer.valueOf(recordId), materialInRecord.getRecordnumber());
				for (MaterialInRecord matInRecord:matRecList){
					if (matInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_SubMat_SEMI)
							|| matInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_SubMat_METAL)
							|| matInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor_SubMat_SEMI)
							|| matInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor_SubMat_METAL)){
						MaterialInRecord matInRecordChargeOff = new MaterialInRecord();
						try{
							BeanUtils.copyProperties(matInRecord, matInRecordChargeOff);
						}catch(Exception e){
							throw new RuntimeException(e);
						}
						matInRecordChargeOff.setRecordid(null);
						matInRecordChargeOff.setRecordnumber(recordChargeOff.getRecordnumber());
						matInRecordChargeOff.setQuantity(matInRecord.getQuantity().negate());
						matInRecordChargeOff.setQuantity2(matInRecord.getQuantity2().negate());
						if (matInRecord.getTotalprice()!=null){
							matInRecordChargeOff.setTotalprice(matInRecord.getTotalprice().negate());
						}
						utilsDao.insert(matInRecordChargeOff);
					}
				}
			}else if (materialInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_PROD) 
					|| materialInRecord.getTranstype().equals(InventoryConstant.TransType_ProdIn_SEMIPROD)){
				
			}
		}else if (recordType.equals("materialOut")){
			MaterialOutRecord materialOutRecord = utilsDao.get(MaterialOutRecord.class,Integer.valueOf(recordId));
			if (materialOutRecord.getRecordstatus()!=null && materialOutRecord.getRecordstatus()==1){
				ret = 1;
				return ret;
			}
			recordNumber = materialOutRecord.getRecordnumber();
			recordNumber_chargeoff = recordNumber+"_chargeoff";
			MaterialOutRecord recordChargeOff = new MaterialOutRecord();
			try{
				BeanUtils.copyProperties(materialOutRecord,recordChargeOff );
			}catch(Exception e){
				throw new RuntimeException(e);
			}
			recordChargeOff.setRecordId(null);
			recordChargeOff.setQuantity(materialOutRecord.getQuantity().negate());
			recordChargeOff.setQuantity2(materialOutRecord.getQuantity2().negate());
			recordChargeOff.setRecordnumber(recordNumber_chargeoff);
			recordChargeOff.setCreateDate(new Date());
			utilsDao.insert(recordChargeOff);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("recordId", Integer.valueOf(recordId));
			utilsDao.execute("update MaterialOutRecord set recordstatus='1' where recordId=:recordId",map);
		}else if (recordType.equals("productOut")){
			
		}
		 
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String updateInventory = "update InventoryInfo set quantity=quantity+:quantity,notoccupyqty=notoccupyqty+:notoccupyqty where inventoryid=:inventoryid";
		String updateSalesOrderDetail = "update SalesOrderDetail set inventoryquantity=inventoryquantity+:inventoryquantity where salesorderdetailid=:salesorderdetailid";
		String updateProdNoticeFinish = "update ProductNoticeFinishQty set finishquantity=finishquantity+:finishquantity,drawforsetupqty=drawforsetupqty+:drawforsetupqty where id=:id";
		String updateInventorySum = "update InventoryInfoSum set inventoryqty=inventoryqty+:inventoryqty,notoccupyqty=notoccupyqty+:notoccupyqty,"
					+" producingqty=producingqty+:producingqty,setupingqty=setupingqty+:setupingqty, "
					+"outsourceqty=outsourceqty+:outsourceqty,mixqty=mixqty+:mixqty where inventoryid=:inventoryid";

		List<InventoryInfoTrc> invListTRC = utilsDao.find("from InventoryInfoTrc where recordnumber=?",recordNumber);
		for (InventoryInfoTrc inventoryInfoTrc : invListTRC) {
			
			InventoryInfo oldInventoryInfo = null;
			List<InventoryInfo> invList = utilsDao.find("from InventoryInfo where inventoryid=?",inventoryInfoTrc.getInventoryid());
			if (invList.size()>0){
				oldInventoryInfo = invList.get(0);
			}
			
			paramMap.clear();
			paramMap.put("inventoryid", inventoryInfoTrc.getInventoryid());
			paramMap.put("quantity", inventoryInfoTrc.getQuantityUpdate().negate());
			paramMap.put("notoccupyqty", inventoryInfoTrc.getQuantityUpdate().negate());
			utilsDao.execute(updateInventory, paramMap);
			
			if (oldInventoryInfo!=null){
				InventoryInfo newInventoryInfo = new InventoryInfo();
				newInventoryInfo.setInventoryid(inventoryInfoTrc.getInventoryid());
				newInventoryInfo.setMaterialcode(inventoryInfoTrc.getMaterialcode());
				newInventoryInfo.setColorcode(inventoryInfoTrc.getColorcode());
				newInventoryInfo.setUnitcode(inventoryInfoTrc.getUnitcode());
				newInventoryInfo.setQuantitytype(inventoryInfoTrc.getQuantitytype());
				newInventoryInfo.setWarehouse(inventoryInfoTrc.getWarehouse());
				newInventoryInfo.setMaterialtype(inventoryInfoTrc.getMaterialtype());
				newInventoryInfo.setUserid(inventoryInfoTrc.getUserid());
				newInventoryInfo.setQuantity(oldInventoryInfo.getQuantity().add(inventoryInfoTrc.getQuantityUpdate().negate()));
				newInventoryInfo.setNotoccupyqty(oldInventoryInfo.getNotoccupyqty().add(inventoryInfoTrc.getQuantityUpdate().negate()));
				
				inventoryInfoService.insertInventoryInfoTrace(newInventoryInfo,oldInventoryInfo,recordNumber_chargeoff);
			}
		}
		List<SalesOrderDetailTrc> orderDetailList = utilsDao.find("from SalesOrderDetailTrc where recordnumber=?",recordNumber);
		for (SalesOrderDetailTrc salesOrderDetailTrc : orderDetailList) {
			paramMap.clear();
			paramMap.put("salesorderdetailid", salesOrderDetailTrc.getSalesorderdetailid());
			paramMap.put("inventoryquantity", salesOrderDetailTrc.getInventoryquantityUpdate().negate());
			utilsDao.execute(updateSalesOrderDetail, paramMap);
		}
		
		List<ProductNoticeFinishQtyTrc> finishQtyList = utilsDao.find("from ProductNoticeFinishQtyTrc where recordnumber=?",recordNumber);
		for (ProductNoticeFinishQtyTrc productNoticeFinishQtyTrc : finishQtyList) {
			paramMap.clear();
			paramMap.put("id", productNoticeFinishQtyTrc.getId());
			paramMap.put("finishquantity", productNoticeFinishQtyTrc.getFinishquantityUpdate().negate());
			paramMap.put("drawforsetupqty", productNoticeFinishQtyTrc.getDrawforsetupqtyUpdate().negate());
			utilsDao.execute(updateProdNoticeFinish, paramMap);
		}

		List<InventoryInfoSumTrc> invSumListTRC = utilsDao.find("from InventoryInfoSumTrc where recordnumber=?",recordNumber);
		for (InventoryInfoSumTrc inventoryInfoSumTrc : invSumListTRC) {
			InventoryInfoSum oldInventoryInfoSum = null;
			List<InventoryInfoSum> invSumList = utilsDao.find("from InventoryInfoSum where inventoryid=?",inventoryInfoSumTrc.getInventoryid());
			if (invSumList.size()>0){
				oldInventoryInfoSum = invSumList.get(0);
			}
			paramMap.clear();
			paramMap.put("inventoryid", inventoryInfoSumTrc.getInventoryid());
			paramMap.put("inventoryqty", inventoryInfoSumTrc.getInventoryqtyUpdate().negate());
			paramMap.put("notoccupyqty", inventoryInfoSumTrc.getNotoccupyqtyUpdate().negate());
			paramMap.put("producingqty", inventoryInfoSumTrc.getProducingqtyUpdate().negate());
			paramMap.put("setupingqty", inventoryInfoSumTrc.getSetupingqtyUpdate().negate());
			paramMap.put("outsourceqty", inventoryInfoSumTrc.getOutsourceqtyUpdate().negate());
			paramMap.put("mixqty", inventoryInfoSumTrc.getMixqtyUpdate().negate());
			utilsDao.execute(updateInventorySum, paramMap);
			
			if (oldInventoryInfoSum!=null){
				InventoryInfoSum newInventoryInfoSum = new InventoryInfoSum();
				newInventoryInfoSum.setInventoryid(inventoryInfoSumTrc.getInventoryid());
				newInventoryInfoSum.setMaterialcode(oldInventoryInfoSum.getMaterialcode());
				newInventoryInfoSum.setColorcode(oldInventoryInfoSum.getColorcode());
				newInventoryInfoSum.setMaterialtype(oldInventoryInfoSum.getMaterialtype());
				newInventoryInfoSum.setUnitcode(oldInventoryInfoSum.getUnitcode());
				newInventoryInfoSum.setInventoryqty(oldInventoryInfoSum.getInventoryqty().add(inventoryInfoSumTrc.getInventoryqtyUpdate().negate()));
				newInventoryInfoSum.setNotoccupyqty(oldInventoryInfoSum.getNotoccupyqty().add(inventoryInfoSumTrc.getNotoccupyqtyUpdate().negate()));
				newInventoryInfoSum.setProducingqty(oldInventoryInfoSum.getProducingqty().add(inventoryInfoSumTrc.getProducingqtyUpdate().negate()));
				newInventoryInfoSum.setSetupingqty(oldInventoryInfoSum.getSetupingqty().add(inventoryInfoSumTrc.getSetupingqtyUpdate().negate()));
				newInventoryInfoSum.setOutsourceqty(oldInventoryInfoSum.getOutsourceqty().add(inventoryInfoSumTrc.getOutsourceqtyUpdate().negate()));
				newInventoryInfoSum.setMixqty(oldInventoryInfoSum.getMixqty().add(inventoryInfoSumTrc.getMixqtyUpdate().negate()));
				inventoryInfoService.insertInventoryInfoSumTrace(newInventoryInfoSum,oldInventoryInfoSum,recordNumber_chargeoff);
			}
		}
		return ret;
	}

	class UpdateProdQtyResult{
	      boolean ifFinishProdQty = false; //是否够填满生产通知单,true 就够
	      BigDecimal leftQty = BigDecimal.ZERO;
	      StringBuilder remarks = new StringBuilder("");
	}
	//生产及安装入仓
	@Override
	@Transactional
	public int submitProdIn(MaterialInRecord materialIn,String prodNoticeCode){
		
		List<MaterialInfo> materialList = utilsDao.find("from MaterialInfo t where t.materialcode=?",materialIn.getMaterialcode());
		MaterialInfo materialInfo = null;
		if (materialList.size() > 0) {
			materialInfo = materialList.get(0);
		}
		if (materialInfo == null)
			return 1;
		materialIn.setUnitprice(materialInfo.getUnitprice());
		materialIn.setTotalprice(materialInfo.getUnitprice().multiply(materialIn.getQuantity()));
		BigDecimal allQty = materialIn.getQuantity2();
		//update the finished quantity in salesorder detail and productnotice detail
		boolean assignOrderNum = StringUtil.isNullOrBlank(materialIn.getOrdernumber())?false:true;
		BigDecimal inputQty = materialIn.getQuantity2();
		UpdateProdQtyResult prodQtyRes 
			= updateAllProdNoticeAndSaleOrderFinishQty(materialIn,inputQty,prodNoticeCode,assignOrderNum);
		/*if (materialIn.getTranstype().indexOf("半成品")>0)
			prodQtyRes = updateFinishedForSemiProd(materialIn,fltInputQty,prodNoticeCode,null,assignOrderNum);
		else
			prodQtyRes = updateFinishedForProd(materialIn,fltInputQty,prodNoticeCode,null,assignOrderNum);*/
		//插入MaterialInRecord
		materialIn.setWarehouse(prodQtyRes.remarks.toString());
		materialIn.setRecordstatus(0);
		utilsDao.insert(materialIn);

		//add the product or semi-product inventory
		InventoryInfo inventoryInfo = new InventoryInfo();
		inventoryInfo.setMaterialcode(materialIn.getMaterialcode());
		inventoryInfo.setColorcode(materialIn.getColorcode());
		inventoryInfo.setQuantity(allQty);
		inventoryInfo.setNotoccupyqty(prodQtyRes.leftQty);
		/*BigDecimal notFinishedProdNoticeQty = getNotFinishedQtyFromProdNotice(materialInfo.getMaterialcode(),materialIn.getColorcode());
		if (allQty.compareTo(notFinishedProdNoticeQty)>0 ){
			inventoryInfo.setNotoccupyqty(allQty.subtract(notFinishedProdNoticeQty));
		}else{
			inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
		}*/
		inventoryInfo.setMaterialtype(materialIn.getMaterialtype());
		inventoryInfo.setUnitcode("只");
		//inventoryInfo.setUserid(materialIn.getUserid());
		inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
		if ( materialInfo.getMaterialtype().equals("半成品"))
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
		else
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
		inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
		//如果是成品，并需要安装，扣减半成品和五金件的安装库存
		if (materialInfo.getMaterialtype().equals("成品") &&
				materialInfo.getIsneedsetup().equals("是")){
			List<BomInfo> bomList =utilsDao.find("from BomInfo where parentmaterialcode=?",materialInfo.getMaterialcode());
			for (Iterator<BomInfo> iterator = bomList.iterator(); iterator.hasNext();) {
				BomInfo bomInfo = iterator.next();

				List<MaterialInfo> materialList2 = utilsDao.find("from MaterialInfo t where t.materialcode=?",bomInfo.getSubmaterialcode());
				if (materialList2.size() > 0) {
					MaterialInfo submaterialInfo = materialList2.get(0);
					if (submaterialInfo.getMaterialtype().equals("半成品")||submaterialInfo.getMaterialtype().equals("成品")){
						BigDecimal quantity = allQty.multiply(bomInfo.getSubmaterialquantity());
						
						InventoryInfo infoSemi = new InventoryInfo();
						infoSemi.setMaterialcode(submaterialInfo.getMaterialcode());
						if (submaterialInfo.getMaterialdesc().contains(InventoryConstant.MaterialDesc_NoColor)){
							infoSemi.setColorcode("");
						}else{
							infoSemi.setColorcode(materialIn.getColorcode());
						}
						infoSemi.setQuantity(new BigDecimal(0).subtract(quantity));
						infoSemi.setMaterialtype(submaterialInfo.getMaterialtype());
						infoSemi.setUnitcode("只");
						
						infoSemi.setQuantitytype(InventoryConstant.QuantityType_Setup);
						infoSemi.setUserid(materialIn.getUserid());
						if (materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Setup)){
							infoSemi.setWarehouse(InventoryConstant.WareHouseType_Setup);
						}else if (materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)){
							infoSemi.setWarehouse(InventoryConstant.WareHouseType_Vendor);
						}
						inventoryInfoService.updateInventory(infoSemi,materialIn.getRecordnumber());
						//insert in record for inventory report
						insertSubMaterialInRec(infoSemi,materialIn);
					}else if (submaterialInfo.getMaterialtype().equals("五金件")){
						//包装物料在外协入仓，并且是1:1时才需扣减外协存量，产品生产和内部安装入仓时不需扣减，
						BigDecimal quantity =allQty.multiply(bomInfo.getSubmaterialquantity());
						
						InventoryInfo intrMetal = new InventoryInfo();
						intrMetal.setMaterialcode(submaterialInfo.getMaterialcode());
						intrMetal.setQuantity(quantity.negate());
						intrMetal.setMaterialtype(submaterialInfo.getMaterialtype());
						intrMetal.setUnitcode("只");
						intrMetal.setQuantitytype(InventoryConstant.QuantityType_Setup);
						intrMetal.setUserid(materialIn.getUserid());
						if (materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Setup))
							intrMetal.setWarehouse(InventoryConstant.WareHouseType_Setup);
						else if (materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor))
							intrMetal.setWarehouse(InventoryConstant.WareHouseType_Vendor);
						
						inventoryInfoService.updateInventory(intrMetal,materialIn.getRecordnumber());
						//insert in record for inventory report
						insertSubMaterialInRec(intrMetal,materialIn);
					}else if (submaterialInfo.getMaterialtype().equals("包装物料")
							&& materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)
							&& (bomInfo.getSubmaterialquantity().intValue()==1) ){
						//包装物料在外协入仓，并且是1:1时才需扣减外协存量，产品生产和内部安装入仓时不需扣减，
						//扣减外协存量
						InventoryInfo invVendor = new InventoryInfo();
						invVendor.setMaterialcode(submaterialInfo.getMaterialcode());
						invVendor.setQuantity(allQty.negate());
						invVendor.setMaterialtype(submaterialInfo.getMaterialtype());
						invVendor.setUnitcode("只");
						invVendor.setQuantitytype(InventoryConstant.QuantityType_Setup);
						invVendor.setUserid(materialIn.getUserid());
						invVendor.setWarehouse(InventoryConstant.WareHouseType_Vendor);
						
						inventoryInfoService.updateInventory(invVendor,materialIn.getRecordnumber());
						//insert in record for inventory report
						insertSubMaterialInRec(invVendor,materialIn);
						//增加安装车间存量
						InventoryInfo invSetup = new InventoryInfo();
						invSetup.setMaterialcode(submaterialInfo.getMaterialcode());
						invSetup.setQuantity(allQty);
						invSetup.setMaterialtype(submaterialInfo.getMaterialtype());
						invSetup.setUnitcode("只");
						invSetup.setQuantitytype(InventoryConstant.QuantityType_Setup);
						invSetup.setUserid(materialIn.getUserid());
						invSetup.setWarehouse(InventoryConstant.WareHouseType_Setup);
						inventoryInfoService.updateInventory(invSetup,materialIn.getRecordnumber());
					}
				}
			}
		}
		if (materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_PROD)||
				materialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_SEMIPROD)){
			
			MaterialQuantity materialQuantity = new MaterialQuantity();
			materialQuantity.setMaterialCode(materialIn.getMaterialcode());
			materialQuantity.setColorCode(materialIn.getColorcode()==null?"":materialIn.getColorcode());
			materialQuantity.setQuantityWeight(materialIn.getQuantity());
			materialQuantity.setQuantityPiece(materialIn.getQuantity2());
			List<MaterialQuantity> rawList = rawMaterialService.calSubMaterialByProductWeight(materialQuantity);
			String quantityType = InventoryConstant.QuantityType_Manufacture;
			String wareHourse = InventoryConstant.WareHouseType_Manufacture;
			for (MaterialQuantity rawMat : rawList){
				
				if (rawMat.getMaterialType().equals("原料")){
					rawMaterialService.updateInventory(new InventoryInfo(rawMat.getMaterialCode(), rawMat.getColorCode(),  
						 quantityType, wareHourse,rawMat.getQuantityWeight().negate()),materialIn.getRecordnumber());
				}else if (rawMat.getMaterialType().equals("五金件")){
					//只有生产及安装的成品这里才有五金件
					//包装物料在产品生产和安装入仓时不需扣减，出货才扣减
					InventoryInfo intrMetal = new InventoryInfo();
					intrMetal.setMaterialcode(rawMat.getMaterialCode());
					intrMetal.setQuantity(rawMat.getQuantityPiece().negate());
					intrMetal.setMaterialtype(rawMat.getMaterialType());
					intrMetal.setColorcode("");
					intrMetal.setUnitcode("只");
					intrMetal.setQuantitytype(InventoryConstant.QuantityType_Manufacture);
					intrMetal.setUserid(materialIn.getUserid());
					intrMetal.setWarehouse(InventoryConstant.WareHouseType_Manufacture);
					inventoryInfoService.updateInventory(intrMetal,materialIn.getRecordnumber());
					//insert in record for inventory report
					insertSubMaterialInRec(intrMetal,materialIn);
				}
			}
		}
		return 0;
	}
	//用于安装入仓成品时，insert对应的五金件和半成品MaterialInRecord，用于员工安装领用进销存报表统计
	private void insertSubMaterialInRec(InventoryInfo infoSemi,MaterialInRecord parentMaterialIn){
		boolean needInsert = true;
		MaterialInRecord materialIn = new MaterialInRecord();
		materialIn.setMaterialcode(infoSemi.getMaterialcode());
		materialIn.setColorcode(infoSemi.getColorcode());
		if (infoSemi.getMaterialtype().equals("五金件")){
			if (parentMaterialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)){
				materialIn.setTranstype(InventoryConstant.TransType_ProdIn_Vendor_SubMat_METAL);
			}else{
				materialIn.setTranstype(InventoryConstant.TransType_ProdIn_SubMat_METAL);
			}
		}else if (infoSemi.getMaterialtype().equals("包装物料")){
			if (parentMaterialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)){
				materialIn.setTranstype(InventoryConstant.TransType_ProdIn_Vendor_SubMat_PACKAGE);
			}else{
				needInsert = false; //内部安装不需要更新包装物料
			}
		}else{
			if (parentMaterialIn.getTranstype().equals(InventoryConstant.TransType_ProdIn_Vendor)){
				materialIn.setTranstype(InventoryConstant.TransType_ProdIn_Vendor_SubMat_SEMI);
			}else{
				materialIn.setTranstype(InventoryConstant.TransType_ProdIn_SubMat_SEMI);
			}
		}
		materialIn.setTransdate(parentMaterialIn.getTransdate());
		materialIn.setOrdernumber(parentMaterialIn.getOrdernumber());
		materialIn.setQuantity(new BigDecimal(0));
		materialIn.setUnitcode("公斤");
		materialIn.setUnitcode2("只");
		materialIn.setQuantity2(infoSemi.getQuantity().negate());
		materialIn.setWarehouse("");
		materialIn.setUserid(parentMaterialIn.getUserid());
		materialIn.setCreatedate(parentMaterialIn.getCreatedate());
		materialIn.setMaterialtype(infoSemi.getMaterialtype());
		materialIn.setRecordnumber(parentMaterialIn.getRecordnumber());
		if (needInsert){
			utilsDao.insert(materialIn);
		}
	}
	//原料、五金件、包装物料,水口入仓,成品直接采购入仓(purchase and 回收) 
	@Override
	@Transactional
	public int submitRawMaterialIn(MaterialInRecord materialIn){
		materialIn.setUnitcode("公斤");
		materialIn.setUnitcode2("只");
		if (materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_BUY)
				|| materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_NOZZLE)){
			materialIn.setUserid("");
		}
		materialIn.setRecordstatus(0);
		utilsDao.insert(materialIn);
		
		InventoryInfo inventoryInfo = new InventoryInfo();
		inventoryInfo.setMaterialcode(materialIn.getMaterialcode());
		inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
		inventoryInfo.setMaterialtype(materialIn.getMaterialtype());
		
		if (materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_BUY)) {
			inventoryInfo.setColorcode(materialIn.getColorcode());
			inventoryInfo.setUnitcode(materialIn.getUnitcode2());
			inventoryInfo.setQuantity(materialIn.getQuantity2());
			if (materialIn.getMaterialtype().equals("原料")){
				inventoryInfo.setQuantity(materialIn.getQuantity());
				inventoryInfo.setUnitcode(materialIn.getUnitcode());
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_RAW);
				inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
			}else if (materialIn.getMaterialtype().equals("五金件")){
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_METAL);
				inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
			}else if (materialIn.getMaterialtype().equals("包装物料")){
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PACKAGE);
				inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
			}else{
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
				inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
			}
			inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
		}else{
		
			if (materialIn.getMaterialtype().equals("原料")){
				inventoryInfo.setColorcode("");
				inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
				inventoryInfo.setUnitcode(materialIn.getUnitcode());
				inventoryInfo.setQuantity(materialIn.getQuantity());
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_RAW);
				inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
			} else if (materialIn.getMaterialtype().equals("水口料")) {
				inventoryInfo.setColorcode(materialIn.getColorcode());
				inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
				inventoryInfo.setUnitcode(materialIn.getUnitcode());
				inventoryInfo.setQuantity(materialIn.getQuantity());
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Mixture);
				inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
				//dec
				inventoryInfo.setQuantity(materialIn.getQuantity().negate());
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Manufacture);
				inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_Manufacture);
				inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
			}else {//五金件和半成品
				inventoryInfo.setUnitcode(materialIn.getUnitcode2());
				inventoryInfo.setQuantity(materialIn.getQuantity2());
				inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
				if (materialIn.getMaterialtype().equals("五金件")){
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_METAL);
					inventoryInfo.setColorcode("");
				}else if (materialIn.getMaterialtype().equals("包装物料")){
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PACKAGE);
					inventoryInfo.setColorcode("");
				}else{
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
					inventoryInfo.setColorcode(materialIn.getColorcode());
				}
				inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
				
				//dec 
				if (materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_InnerMetal)
						||materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_InnerPackage)
						||materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_InnerSemiProd)){
					inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_Setup);
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Setup);
					inventoryInfo.setQuantity(materialIn.getQuantity2().negate());
					inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
					inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
				}else if (materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_VendorMetal)
						||materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_VendorPackage)
						||materialIn.getTranstype().equals(InventoryConstant.TransType_MatIn_VendorSemiProd)){
					inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_Setup);
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Vendor);
					inventoryInfo.setQuantity(materialIn.getQuantity2().negate());
					inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
					inventoryInfoService.updateInventory(inventoryInfo,materialIn.getRecordnumber());
				}
			}
		}
		return 0;
	}
	//更新已领用数量
	private void updateDrawforSetupQty(MaterialOutRecord materialOut,String prodNoticeCode){
		BigDecimal leftqty = materialOut.getQuantity2();
		boolean assignOrderNum = false;
		String prodType = null;
		if (prodNoticeCode!=null){
			prodType = prodNoticeCode.split("_")[1];
		}else{
			if (materialOut.getMaterialType().equals("成品") ||materialOut.getMaterialType().equals("半成品")){
				prodType =  "_semiprod"; 
			}else if ( materialOut.getMaterialType().equals("五金件")){
				prodType =  "_hardwareCollect"; 
			}else if ( materialOut.getMaterialType().equals("包装物料")){
				prodType =  "_packageCollect"; 
			}
		}

		if (!StringUtil.isNullOrBlank(materialOut.getOrderNumber())){
			assignOrderNum = true;
		}
		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find("from MaterialInfo t where t.materialcode=?",materialOut.getMaterialCode());
		if (matList.size() <= 0)
			return ;
		else
			materialInfo = matList.get(0);
		
		List<ProductNoticeFinishQtyBO> finishQtyList = getProdNoticeNotFinishQtyBOList(
				materialOut.getMaterialCode(), materialOut.getColorCode(),
				materialInfo.getMaterialdesc(), 1);
		if (assignOrderNum && prodNoticeCode!=null) {
			finishQtyList = sortProductNoticeFinishQtyList(finishQtyList,
					prodNoticeCode);
		}
		//get all the record
		//if ordernumber is not null,update the ordernumber first
		for (Iterator<ProductNoticeFinishQtyBO> iterator = finishQtyList.iterator(); iterator.hasNext();) {
			ProductNoticeFinishQtyBO productionNoticeDetail = iterator.next();
			if (!productionNoticeDetail.getProductionnoticecode().endsWith(prodType)){
				continue;
			}
			BigDecimal gap = productionNoticeDetail.getQuantity().subtract(productionNoticeDetail.getDrawforsetupqty());
			BigDecimal updateqty;
			if (leftqty.compareTo(gap)>0){
				updateqty = gap;
			}else{
				updateqty = leftqty;
			}
			leftqty = leftqty.subtract(updateqty);
			updateProductNoticeFinishQty(null,productionNoticeDetail.getProductiondetailid(),productionNoticeDetail.getDrawforsetupqty(),
					updateqty,2,materialOut.getRecordnumber());

			if (leftqty.compareTo(BigDecimal.ZERO) <=0)
				return;
		}
	}
	//领取物料(生产领用 and 安装领用 and 外协加工)
	@Override
	@Transactional
	public int submitDrawMaterialOut(MaterialOutRecord materialOut,String prodNoticeCode){
		
		materialOut.setRecordstatus(0);
		utilsDao.insert(materialOut);
		//update draw for setup quantity
		if (materialOut.getMaterialType().equals("成品") 
				|| materialOut.getMaterialType().equals("半成品")
				|| materialOut.getMaterialType().equals("五金件")
				|| materialOut.getMaterialType().equals("包装物料")){
			//更新已领用数量
			updateDrawforSetupQty(materialOut,prodNoticeCode);
		}
		//subtract house inventory
		InventoryInfo inventoryInfo = new InventoryInfo();
		//inventoryInfo.setUserid(materialOut.getUserId());
		inventoryInfo.setMaterialcode(materialOut.getMaterialCode());
		inventoryInfo.setMaterialtype(materialOut.getMaterialType());
		inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
		//领料出仓时，不会扣减未占用数(只有五金件除外，因为五金件没有“用未占用库存更新已完成数”)
		//如果入仓时有订单A，会先用入仓数填满了订单A再添加未占用库存
		//领料出仓时是不会检查已完成数，如果先“用未占用库存更新已完成数”，然后领用时又扣减未占用库存，就变成重复扣减
		inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
		if (materialOut.getMaterialType().equals("原料") ){
			inventoryInfo.setColorcode("");
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_RAW);
			inventoryInfo.setUnitcode(materialOut.getUnitCode());
			inventoryInfo.setQuantity(materialOut.getQuantity().negate());
		}else if ( materialOut.getMaterialType().equals("水口料")){
			inventoryInfo.setColorcode(materialOut.getColorCode());
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Mixture);
			inventoryInfo.setUnitcode(materialOut.getUnitCode());
			inventoryInfo.setQuantity(materialOut.getQuantity().negate());
		}else if (materialOut.getMaterialType().equals("五金件")
					||materialOut.getMaterialType().equals("包装物料")){
			inventoryInfo.setColorcode("");
			if (materialOut.getMaterialType().equals("五金件")){
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_METAL);
			}else{
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PACKAGE);
			}
			inventoryInfo.setUnitcode(materialOut.getUnitCode2());
			inventoryInfo.setQuantity(materialOut.getQuantity2().negate());
			
			//只有五金件除外，因为五金件没有“用未占用库存更新已完成数”
			InventoryInfoSum existInventoryInfo = inventoryInfoService.getInventoryInfoSum(inventoryInfo.getMaterialcode(),inventoryInfo.getColorcode());
			if (existInventoryInfo == null){
				inventoryInfo.setNotoccupyqty(materialOut.getQuantity2().negate());
			}else if (materialOut.getQuantity2().compareTo(
					existInventoryInfo.getInventoryqty().subtract(existInventoryInfo.getNotoccupyqty()))>0)
			{
				BigDecimal notOccupyQty = materialOut.getQuantity2().subtract(existInventoryInfo.getInventoryqty().subtract(existInventoryInfo.getNotoccupyqty()));
				inventoryInfo.setNotoccupyqty(notOccupyQty.negate());
			}
		} else if (materialOut.getMaterialType().equals("成品") ||
				materialOut.getMaterialType().equals("半成品")){
			//无论是成品或半成品领料出仓，都是从半成品仓库中扣减，因为有些成品也作为半成品使用的
			inventoryInfo.setColorcode(materialOut.getColorCode());
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
			inventoryInfo.setUnitcode(materialOut.getUnitCode2());
			inventoryInfo.setQuantity(materialOut.getQuantity2().negate());
			
			inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
			/**
			InventoryInfoSum existInventoryInfo = inventoryInfoService.getInventoryInfoSum(inventoryInfo.getMaterialcode(),inventoryInfo.getColorcode());
			if (existInventoryInfo == null){
				inventoryInfo.setNotoccupyqty(materialOut.getQuantity2().negate());
			}else {
				BigDecimal inventAndNotOccupyGap = existInventoryInfo.getInventoryqty().subtract(existInventoryInfo.getNotoccupyqty());
				BigDecimal notOccupyQty = BigDecimal.ZERO;
				//inv:2100,notOccupy:100 , 出倉 2000
				if (materialOut.getQuantity2().compareTo(inventAndNotOccupyGap)>0){
					notOccupyQty = materialOut.getQuantity2().subtract(inventAndNotOccupyGap);//只需減去 出倉數減現有數的差額
					inventoryInfo.setNotoccupyqty(notOccupyQty.negate());
				}
			}*/
		}
		inventoryInfoService.updateInventory(inventoryInfo,materialOut.getRecordnumber());
		
		//add producing inventory
		InventoryInfo inventoryInfoP = new InventoryInfo();
		inventoryInfoP.setUserid(materialOut.getUserId());
		inventoryInfoP.setMaterialcode(materialOut.getMaterialCode());
		inventoryInfoP.setMaterialtype(materialOut.getMaterialType());
		if (materialOut.getMaterialType().equals("原料")){
			inventoryInfo.setColorcode("");
			inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Manufacture);
			inventoryInfoP.setQuantitytype(InventoryConstant.QuantityType_Manufacture);
			inventoryInfoP.setUnitcode(materialOut.getUnitCode());
			inventoryInfoP.setQuantity(materialOut.getQuantity());
		}else if ( materialOut.getMaterialType().equals("水口料")){
			inventoryInfoP.setColorcode(materialOut.getColorCode());
			inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Manufacture);
			inventoryInfoP.setQuantitytype(InventoryConstant.QuantityType_Manufacture);
			inventoryInfoP.setUnitcode(materialOut.getUnitCode());
			inventoryInfoP.setQuantity(materialOut.getQuantity());
		}else if (materialOut.getMaterialType().equals("五金件")
				|| materialOut.getMaterialType().equals("包装物料")){
			inventoryInfoP.setQuantitytype(InventoryConstant.QuantityType_Setup);
			if (materialOut.getTransType().equals(InventoryConstant.TransType_MatOut_Vendor)){
				inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Vendor);
			}else if (materialOut.getTransType().equals(InventoryConstant.TransType_MatOut_Produce)){
				inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Manufacture);
			}else{
				inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Setup);
			}
			inventoryInfoP.setUnitcode(materialOut.getUnitCode2());
			inventoryInfoP.setQuantity(materialOut.getQuantity2());
		} else if (materialOut.getMaterialType().equals("半成品")||materialOut.getMaterialType().equals("成品")){
			inventoryInfoP.setQuantitytype(InventoryConstant.QuantityType_Setup);
			if (materialOut.getTransType().equals(InventoryConstant.TransType_MatOut_Vendor)){
				inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Vendor);
			}else{
				inventoryInfoP.setWarehouse(InventoryConstant.WareHouseType_Setup);
			}
			inventoryInfoP.setColorcode(materialOut.getColorCode());
			inventoryInfoP.setUnitcode(materialOut.getUnitCode2());
			inventoryInfoP.setQuantity(materialOut.getQuantity2());
		}
		inventoryInfoService.updateInventory(inventoryInfoP,materialOut.getRecordnumber());
		return 0;
	}
	@Override
	@Transactional
	public int submitRecycleProduct(ProductOutRecord productOutRecord) {
		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find("from MaterialInfo t where t.materialcode=?",productOutRecord.getMaterialcode());
		if (matList!=null && matList.size() > 0){
			materialInfo = matList.get(0);
			int outorder = 1;
			productOutRecord.setOutorder(outorder);
			productOutRecord.setOutnumber("退货");
			
			calculateProductOutPackages(productOutRecord);
			productOutRecord.setTotalcount(productOutRecord.getTotalcount().negate());
			utilsDao.insert(productOutRecord);
			
			//update orderDetail
			this.updateOrderDetailQty(productOutRecord.getOrdernumber(), productOutRecord.getMaterialcode(), productOutRecord.getColorcode(),
					productOutRecord.getTotalcount(), false,2, productOutRecord.getRecordnumber());
			//update inventory
			InventoryInfo inventoryInfo = new InventoryInfo();
			inventoryInfo.setMaterialcode(productOutRecord.getMaterialcode());
			inventoryInfo.setColorcode(productOutRecord.getColorcode());
			inventoryInfo.setMaterialtype(materialInfo.getMaterialtype());
			inventoryInfo.setUnitcode("只");
			if (materialInfo.getMaterialtype().equals("五金件")){//会直接出货五金件，但不会直接出货包装物料
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_METAL);
				inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
			}else{
				if (productOutRecord.getTranstype().equals(InventoryConstant.TransType_ProdOut_RecycleProd) 
						|| productOutRecord.getTranstype().equals(InventoryConstant.TransType_ProdOut_Chargeoff)){
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
					inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
				}else if (productOutRecord.getTranstype().equals(InventoryConstant.TransType_ProdOut_RecycleRec)){
					inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Recycle);
					inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_Recycle);					
				}
			}
			inventoryInfo.setQuantity( productOutRecord.getTotalcount().negate());
			inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
			inventoryInfoService.updateInventory(inventoryInfo,productOutRecord.getRecordnumber());
		}
		return 0;
	}
	@Override
	@Transactional
	public int submitProductOut(ProductOutRecord productOutRecord){
		
		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find("from MaterialInfo t where t.materialcode=?",productOutRecord.getMaterialcode());
		if (matList!=null && matList.size() > 0){
			materialInfo = matList.get(0);
		
			int outorder = 1;
			List<Integer> maxOutList = utilsDao.find("select t.outorder from ProductOutRecord t where t.ordernumber=? and t.transdate=? and t.transtype='成品出货'",
					productOutRecord.getOrdernumber(),productOutRecord.getTransdate());
			if (maxOutList.size()>0){
				outorder = maxOutList.get(0).intValue();
			}else{
				maxOutList = utilsDao.find("select max(t.outorder) from ProductOutRecord t where t.ordernumber=? ",
						productOutRecord.getOrdernumber());
				if (maxOutList.size()>0 && !StringUtil.isNullOrEmpty(maxOutList.get(0))){
					outorder = maxOutList.get(0).intValue() +1;
				}
			}
			productOutRecord.setOutorder(outorder);
			productOutRecord.setOutnumber("第 " + outorder +" 次出货");
			//insert 
			//同一日有相同的，即update
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("salesorderdetailid",productOutRecord.getSalesorderdetailid());
			param.put("outorder",productOutRecord.getOutorder());
			List<ProductOutRecord> existRec = utilsDao.find("from ProductOutRecord t where t.salesorderdetailid=:salesorderdetailid and t.outorder=:outorder ",
					param);
			if (existRec.size()>0){
				ProductOutRecord old = existRec.get(0);
				old.setTotalcount(productOutRecord.getTotalcount().add(old.getTotalcount()));
				calculateProductOutPackages(old);
				utilsDao.update(old);
			}else{
				calculateProductOutPackages(productOutRecord);
				productOutRecord.setTranstype(InventoryConstant.TransType_ProdOut);
				utilsDao.insert(productOutRecord);
			}
			//update orderDetail
			this.updateOrderDetailQty(productOutRecord.getOrdernumber(), productOutRecord.getMaterialcode(), productOutRecord.getColorcode(),
					productOutRecord.getTotalcount(), false,2, productOutRecord.getRecordnumber());
			//update inventory
			InventoryInfo inventoryInfo = new InventoryInfo();
			inventoryInfo.setMaterialcode(productOutRecord.getMaterialcode());
			inventoryInfo.setColorcode(productOutRecord.getColorcode());
			inventoryInfo.setMaterialtype(materialInfo.getMaterialtype());
			inventoryInfo.setUnitcode("只");
			inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
			if (materialInfo.getMaterialtype().equals("五金件")){//成品出货可以直接出五金件
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_METAL);
			}else{
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
			}
			inventoryInfo.setQuantity( productOutRecord.getTotalcount().negate());
			inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
			inventoryInfoService.updateInventory(inventoryInfo,productOutRecord.getRecordnumber());
			// TODO  calcuate package
			updatePackageInventory(productOutRecord.getMaterialcode(),productOutRecord.getTotalcount(),productOutRecord.getRecordnumber());
			
			utilsDao.execute("update SalesOrderMaster set orderstatus='1' where ordernumber=?",productOutRecord.getOrdernumber());
		}
		return 0;
	}
	private void updatePackageInventory(String materialcode,BigDecimal quantity,String recordNumber){
		List<BomItemBO> packageItemList = bomService.getBomSumBO(materialcode, quantity).getPackageItemList();
		for (BomItemBO bomItemBO: packageItemList){
			InventoryInfo inventoryInfo = new InventoryInfo();
			inventoryInfo.setMaterialcode(bomItemBO.getMaterialcode());
			inventoryInfo.setColorcode("");
			inventoryInfo.setMaterialtype(bomItemBO.getMaterialtype());
			inventoryInfo.setUnitcode("只");
			inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_Setup);
			inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_Setup);
			inventoryInfo.setQuantity( bomItemBO.getQuantity().negate());
			inventoryInfo.setNotoccupyqty(BigDecimal.ZERO);
			inventoryInfoService.updateInventory(inventoryInfo,recordNumber);
		}

	}
	private void calculateProductOutPackages(ProductOutRecord productOutRecord){
		ProductOutPackageBO productOutPackageBO = new ProductOutPackageBO();
		productOutPackageBO.setTotalcount(productOutRecord.getTotalcount());
		productOutPackageBO.setCppack(productOutRecord.getCppack());
		calculateProductOutPackages(productOutPackageBO);
		productOutRecord.setPackages(productOutPackageBO.getPackages());
		productOutRecord.setCppack(productOutPackageBO.getCppack());
		productOutRecord.setRoundcount(productOutPackageBO.getRoundCount());
		productOutRecord.setResiduecount(productOutPackageBO.getResidueCount());
		productOutRecord.setResiduepackage(productOutPackageBO.getResiduePackage().intValue());
	}
	@Override
	@Transactional(readOnly = true)
	public void calculateProductOutPackages(ProductOutPackageBO productOutPackageBO){
		
		BigDecimal totalCount = productOutPackageBO.getTotalcount();
		BigDecimal qtyPerPack = productOutPackageBO.getCppack();
		if (totalCount.compareTo(qtyPerPack)<0){
			productOutPackageBO.setPackages(1);
			productOutPackageBO.setCppack(totalCount);
			productOutPackageBO.setRoundCount(totalCount);
			productOutPackageBO.setResidueCount(BigDecimal.ZERO);
			productOutPackageBO.setResiduePackage(BigDecimal.ZERO);
		}else{
			productOutPackageBO.setPackages(totalCount.divide(qtyPerPack,0,BigDecimal.ROUND_DOWN).intValue());
			productOutPackageBO.setRoundCount(qtyPerPack.multiply(BigDecimal.valueOf(productOutPackageBO.getPackages())));
			productOutPackageBO.setResidueCount(totalCount.divideAndRemainder(qtyPerPack)[1]);
			if (productOutPackageBO.getResidueCount().compareTo(BigDecimal.ZERO) >0)
				productOutPackageBO.setResiduePackage(BigDecimal.ONE);
			else
				productOutPackageBO.setResiduePackage(BigDecimal.ZERO);
			// transform : qtyPerPack:30,Packages:2,Roundcount:60,Residuepackage:1,Residuecount:3 
			//       ====> qtyPerPack:30,Packages:1,Roundcount:30,Residuepackage:1,Residuecount:33 
			SystemParam systemParam = systemParamService.getSystemParamByParamName(FrameConstant.PRODUCT_OUT_PACKAGE_PERCENT);
			if (systemParam!=null){
				if (productOutPackageBO.getResidueCount().compareTo(BigDecimal.ZERO) >0){
					
					BigDecimal resiCountPercent = productOutPackageBO.getResidueCount().divide(qtyPerPack,4,BigDecimal.ROUND_DOWN);
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					String pattern = "0.0"; //"#,##0.0#"
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					decimalFormat.setParseBigDecimal(true);
					BigDecimal systemParamPercent = BigDecimal.ONE;
					try {
						systemParamPercent = ((BigDecimal) decimalFormat.parse(systemParam.getParamValue())).movePointLeft(2);
					}catch (Exception e){
						e.printStackTrace();
					}
					if (resiCountPercent.compareTo(systemParamPercent)<=0){
						productOutPackageBO.setPackages(productOutPackageBO.getPackages()-1);
						productOutPackageBO.setRoundCount(qtyPerPack.multiply(BigDecimal.valueOf(productOutPackageBO.getPackages())));
						productOutPackageBO.setResiduePackage(BigDecimal.ONE);
						productOutPackageBO.setResidueCount(productOutPackageBO.getResidueCount().add(qtyPerPack));
					}
				}
			}
		}
	}
	//未占用库存更新已完成数
	@Override
	@Transactional
	public void updateFinishQtyByInventory(String ordernumber,String prodNoticeCode){
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String date = DATE_FORMAT.format(new Date());
		String recordNumberDate = "updateFinishByNotOccupy"+date;
		//select records 
	    Map<String,Object> parammap = new HashMap<String,Object>();
	    parammap.put("noticecode", prodNoticeCode);
	    StringBuilder sql = new StringBuilder("select t.ProductionDetailID,t.materialcode,t.colorcode,inv.materialType,")
	    	.append(" t.quantity,q.finishquantity,inv.notoccupyqty " )
	    .append(" from Production_Notice_Detail t  ")
	    .append(" inner join productnotice_finishqty q on t.ProductionDetailID=q.ProductionDetailID ")
	    .append(" inner join inventoryinfosum inv on t.materialcode=inv.materialcode and t.colorcode=inv.colorcode ")
	    .append(" where t.productionnoticecode=:noticecode and t.quantity>q.finishquantity and inv.notoccupyqty>0 ");
	    List<Object[]> objList = utilsDao.findBySql(sql.toString(),parammap);
	    int i=0;
		//update each item
		for (Iterator<Object[]> iterator = objList.iterator(); iterator.hasNext();) {
			Object[] objs =  iterator.next();

			String recordNumber = String.format("%1s%2$05d",recordNumberDate,i++);
			
			Integer productionDetailID = Integer.valueOf(objs[0].toString());
			String materialcode = objs[1].toString();
			String colorcode = objs[2].toString();
			String materialType = objs[3].toString();
			BigDecimal orderqty =  (BigDecimal)objs[4];
			BigDecimal finishquantity = (BigDecimal)objs[5];
			BigDecimal notoccupyqty = (BigDecimal)objs[6];
			
			BigDecimal updateQty = BigDecimal.ZERO;
			if (orderqty.subtract(finishquantity).compareTo(notoccupyqty)>0){
				updateQty = notoccupyqty;
			}else{
				updateQty = orderqty.subtract(finishquantity);
			}

			this.updateProductNoticeFinishQty(null, productionDetailID, finishquantity, updateQty, 1, recordNumber);
			//update not Occupy inventory 
			InventoryInfo inventoryInfo = new InventoryInfo();
			inventoryInfo.setMaterialcode(materialcode);
			inventoryInfo.setColorcode(colorcode);
			inventoryInfo.setMaterialtype(materialType);
			inventoryInfo.setQuantity(BigDecimal.ZERO);
			inventoryInfo.setNotoccupyqty(updateQty.negate());
			inventoryInfo.setUnitcode("只");
			inventoryInfo.setUserid("");
			inventoryInfo.setQuantitytype(InventoryConstant.QuantityType_WareHouse);
			if (prodNoticeCode.endsWith("_semiprod")){
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
			}else{
				inventoryInfo.setWarehouse(InventoryConstant.WareHouseType_PRODUCT);
			}
			inventoryInfoService.updateInventory(inventoryInfo, recordNumber);

			if (prodNoticeCode.endsWith("_prod")||prodNoticeCode.endsWith("_setup")
					||prodNoticeCode.endsWith("_directOut")){
				this.updateOrderDetailQty(ordernumber, materialcode, colorcode, updateQty, false, 1,recordNumber);
			}
		}
	}
	
	private UpdateProdQtyResult updateAllProdNoticeAndSaleOrderFinishQty(MaterialInRecord materialIn, BigDecimal inputQty,
			String prodNoticeCode, boolean assignOrderNum) {
		UpdateProdQtyResult prodRes = new UpdateProdQtyResult();
		prodRes.leftQty= inputQty;
		
		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find("from MaterialInfo t where t.materialcode=?",materialIn.getMaterialcode());
		if (matList.size() <= 0)
			return prodRes;
		else
			materialInfo = matList.get(0);

		List<ProductNoticeFinishQtyBO> finishQtyList = getProdNoticeNotFinishQtyBOList(materialIn.getMaterialcode(),materialIn.getColorcode(),
					materialInfo.getMaterialdesc(),0);
		if (assignOrderNum){
			finishQtyList = sortProductNoticeFinishQtyList(finishQtyList,prodNoticeCode);
		}
		for (Iterator<ProductNoticeFinishQtyBO> iterator = finishQtyList.iterator(); iterator.hasNext();) {
			ProductNoticeFinishQtyBO productNoticeFinishQtyBO = iterator.next();
			if (prodRes.leftQty.compareTo(BigDecimal.ZERO)>0){
				prodRes = updateOneProdNoticeAndSaleOrderFinishQty(productNoticeFinishQtyBO,prodRes,materialIn.getRecordnumber());
			}
		}
		return prodRes;
	}
	// queryType 0:finishquantity, 1:drawforsetupqty
	private List<ProductNoticeFinishQtyBO> getProdNoticeNotFinishQtyBOList(String materialcode,String colorCode,String materialdesc,int queryType){
		Map<String, Object> map = new HashMap<String, Object>();
		
		StringBuilder prodNoticSQL = new StringBuilder("select q.id,q.productionnoticecode,q.materialcode,q.colorcode,")
				.append("q.finishquantity,q.productiondetailid,d.quantity,q.drawforsetupqty ")
				.append(" from production_notice_detail d,PRODUCTNOTICE_FINISHQTY q")
				.append(" where d.productiondetailid=q.productiondetailid ")
				.append(" and d.materialCode=:materialCode ");
		map.put("materialCode",materialcode);
		
		if (materialdesc.equals(InventoryConstant.MaterialDesc_NoColor)|| StringUtil.isNullOrBlank(colorCode)) {
			// 半成品生产，并且是不分色时，不需要设置颜色(只有半成品才不分色)
		} else {
			prodNoticSQL.append(" and d.colorCode=:colorCode");
			map.put("colorCode",colorCode);
		}
		if (queryType==0){
			prodNoticSQL.append(" and q.finishquantity<d.quantity  ");
		}else{
			prodNoticSQL.append(" and q.drawforsetupqty<d.quantity  ");
		}
		prodNoticSQL.append(" order by d.DeliverDate asc");
		List<Object[]> resultList = utilsDao.findBySql(prodNoticSQL.toString(), map);
		List<ProductNoticeFinishQtyBO> finishQtyList = new ArrayList<ProductNoticeFinishQtyBO>();
		for (Iterator<Object[]> iterator = resultList.iterator(); iterator.hasNext();) {
			Object[] objects = iterator.next();
			ProductNoticeFinishQtyBO productNoticeFinishQtyBO = new ProductNoticeFinishQtyBO();
			productNoticeFinishQtyBO.setId(Integer.valueOf(objects[0].toString()));
			productNoticeFinishQtyBO.setProductionnoticecode(objects[1].toString());
			productNoticeFinishQtyBO.setMaterialcode(objects[2].toString());
			productNoticeFinishQtyBO.setColorcode(objects[3].toString());
			productNoticeFinishQtyBO.setFinishquantity( (BigDecimal)objects[4]);
			productNoticeFinishQtyBO.setProductiondetailid(Integer.valueOf(objects[5].toString()));
			productNoticeFinishQtyBO.setQuantity( (BigDecimal)objects[6]);
			productNoticeFinishQtyBO.setDrawforsetupqty((BigDecimal)objects[7]);
			finishQtyList.add(productNoticeFinishQtyBO);
		}
		return finishQtyList;
	}
	private UpdateProdQtyResult updateOneProdNoticeAndSaleOrderFinishQty(ProductNoticeFinishQtyBO productNoticeFinishQtyBO,
			UpdateProdQtyResult updateProdQtyResult,String recordNumber ) {
		String orderNumber = productNoticeFinishQtyBO.getProductionnoticecode().split("_")[0];
		String prodType = productNoticeFinishQtyBO.getProductionnoticecode().split("_")[1];
		boolean updateFinishState = false;
		if (updateProdQtyResult.leftQty.compareTo(BigDecimal.ZERO)>0){
			
			BigDecimal pn_qtyNotFinish = productNoticeFinishQtyBO.getQuantity().subtract(
					productNoticeFinishQtyBO.getFinishquantity());

			BigDecimal pn_addValue = BigDecimal.ZERO;
			if (pn_qtyNotFinish.compareTo(updateProdQtyResult.leftQty)>0) {// if not finishqty bigger than input qty
				pn_addValue = updateProdQtyResult.leftQty;
				updateProdQtyResult.leftQty =  BigDecimal.ZERO;
				updateProdQtyResult.ifFinishProdQty = false;
			} else {
				pn_addValue = pn_qtyNotFinish;
				updateProdQtyResult.leftQty = updateProdQtyResult.leftQty.subtract(pn_qtyNotFinish);
			}
			updateProductNoticeFinishQty(productNoticeFinishQtyBO.getId(),null,productNoticeFinishQtyBO.getFinishquantity(),
					pn_addValue,1,recordNumber);
			String type = "成品";
			if (prodType.equals("semiprod"))
				type = "半成品";
			updateProdQtyResult.remarks.append(orderNumber).append(":").append(type).append(":")
					.append(pn_addValue).append("——");

			//if some prod is used for the semiprod,the current algorithm maybe not correct,
			//but when we generate the productNotice Detail, we will merge it. so that can't be perfect
			if (prodType.equals("setup") || prodType.equals("prod")){
				if (updateProdQtyResult.leftQty.compareTo(BigDecimal.ZERO)>0){
					updateFinishState = true;
				}
				updateOrderDetailQty(orderNumber,productNoticeFinishQtyBO.getMaterialcode(),productNoticeFinishQtyBO.getColorcode(),
						pn_addValue,updateFinishState,1,recordNumber);
			}
		}
		return updateProdQtyResult;
	}
	private void updateOrderDetailQty(String orderNumber,String materialCode,String colorCode,BigDecimal quantity,boolean updateFinishState,int type,String recordNumber){
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuilder salesOrderSQL = new StringBuilder(
				" from SalesOrderDetail ")
				.append(" where  materialcode=:materialCode and ordernumber=:OrderNumber ");
		paramMap.put("OrderNumber", orderNumber);
		paramMap.put("materialCode", materialCode);
		if (!StringUtil.isNullOrBlank(colorCode)){
			salesOrderSQL.append(" and colorcode=:colorCode  ");
			paramMap.put("colorCode", colorCode);
		}
		List<SalesOrderDetail> salesOrderDetailList = utilsDao.find(salesOrderSQL.toString(),paramMap);
		if (salesOrderDetailList.size()<=0){
			return;
		}
		SalesOrderDetail oldSalesOrderDetail = new SalesOrderDetail();
		BeanUtils.copyProperties(salesOrderDetailList.get(0),oldSalesOrderDetail);
		
		StringBuilder hql = new StringBuilder( "update SalesOrderDetail ");
		
		if (type==1){
			hql.append(" set inventoryquantity=inventoryquantity+:quantity");
		}else{
			hql.append(" set totaldeliverquantity=totaldeliverquantity+:quantity");
		}
		paramMap.clear();
		if (updateFinishState){
			hql.append(",orderdetailstate=:orderdetailstate");
			paramMap.put("orderdetailstate", 1);
		}
		hql.append(" where salesorderdetailid=:salesorderdetailid ");
		paramMap.put("quantity", quantity);
		paramMap.put("salesorderdetailid", salesOrderDetailList.get(0).getSalesorderdetailid());
		utilsDao.execute(hql.toString(), paramMap);

		insertSalesOrderDetailTrace(oldSalesOrderDetail,quantity,recordNumber,type);
	}
	private List<ProductNoticeFinishQtyBO> sortProductNoticeFinishQtyList(List<ProductNoticeFinishQtyBO> finishQtyList,String prodNoticeCode){
		boolean foundMatch = false;
		String orderNumber = prodNoticeCode.split("_")[0];
		for (int i=0;i<finishQtyList.size();i++){
			ProductNoticeFinishQtyBO productNoticeFinishQty = finishQtyList.get(i);
			if (productNoticeFinishQty.getProductionnoticecode().equals(prodNoticeCode)){
				foundMatch = true;
				finishQtyList.remove(productNoticeFinishQty);
				finishQtyList.add(0, productNoticeFinishQty);
			}else{
				String curOrderNumber = productNoticeFinishQty.getProductionnoticecode().split("_")[0];
				if (orderNumber.equals(curOrderNumber)){
					if (foundMatch){
						finishQtyList.remove(productNoticeFinishQty);
						finishQtyList.add(1, productNoticeFinishQty);
					}else{
						finishQtyList.remove(productNoticeFinishQty);
						finishQtyList.add(0, productNoticeFinishQty);
					}
				}
			}
		}
		return finishQtyList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<InventoryInfoV> getInventoryInfoV(String materialtype,String userID) {
		StringBuilder hql = new StringBuilder(" from InventoryInfoV t where t.materialtype= :materialtype");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("materialtype", materialtype);
		
		if ( !StringUtil.isNullOrBlank(userID)){
			hql.append(" and t.userid= :userid ");
			paramMap.put("userid", userID);
		}
		hql.append(" order by t.materialcode,t.colorcode,t.warehouse");
		List<InventoryInfoV> marList = utilsDao.find( hql.toString(),paramMap);  

		return marList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<InventorySumModel> getInventorySum(String materialtype,String materialCode,String materialName,String colorCode,Page page) {

		StringBuilder selectsql = new StringBuilder("select t.materialcode,m.materialname,t.colorcode,c.colorname,t.materialtype, t.unitcode, " )
		.append("t.inventoryqty,t.notoccupyqty,t.setupingqty,t.outsourceqty,t.producingqty,t.mixqty,t.recycleqty ");
		StringBuilder countsql = new StringBuilder("select count(t.inventoryID)");
		StringBuilder sql = new StringBuilder(" from InventoryInfoSum t")
		.append(" inner join materialinfo m on t.materialcode=m.materialcode")
		.append(" left join colorinfo c on t.colorcode=c.colorcode")
		.append(" where t.materialtype= :materialtype ");
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("materialtype", materialtype);
		if ( !StringUtil.isNullOrBlank(materialName)){
			sql.append(" and m.materialName like :materialname ");
			paramMap.put("materialname", materialName+"%");
		}
		if ( !StringUtil.isNullOrBlank(materialCode)){
			sql.append(" and t.materialcode like :materialcode ");
			paramMap.put("materialcode", materialCode+"%");
		}
		if ( !StringUtil.isNullOrBlank(colorCode)){
			sql.append(" and t.colorcode like :colorcode ");
			paramMap.put("colorcode", colorCode+"%");
		}
		selectsql.append(sql).append(" order by t.materialcode,t.colorcode");
		countsql.append(sql);
		List<Object[]> marList = utilsDao.findBySql( selectsql.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(),paramMap);  
		page.setTotalRecordSize(utilsDao.countBySql(countsql.toString(),paramMap));
		
		List<InventorySumModel> resList = new ArrayList<InventorySumModel>();
		for (Object[] objs:marList) {
			InventorySumModel model = new InventorySumModel();
			model.setMaterialcode(objs[0].toString());
			model.setMaterialname(objs[1]==null?"":objs[1].toString());
			model.setColorcode(objs[2]==null?"":objs[2].toString());
			model.setColorname(objs[3]==null?"":objs[3].toString());
			model.setMaterialtype(objs[4]==null?"":objs[4].toString());
			model.setUnitcode(objs[5]==null?"":objs[5].toString());
			model.setInventoryqty(objs[6]==null?BigDecimal.ZERO:(BigDecimal)objs[6]);
			model.setNotoccupyqty(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
			model.setSetupingqty(objs[8]==null?BigDecimal.ZERO:(BigDecimal)objs[8]);
			model.setOutsourceqty(objs[9]==null?BigDecimal.ZERO:(BigDecimal)objs[9]);
			model.setProducingqty(objs[10]==null?BigDecimal.ZERO:(BigDecimal)objs[10]);
			model.setMixqty(objs[11]==null?BigDecimal.ZERO:(BigDecimal)objs[11]);
			model.setRecycleqty(objs[12]==null?BigDecimal.ZERO:(BigDecimal)objs[12]);
			resList.add(model);
		}
		return resList;
	}
	/*// 更新后，返回还剩的fltInputQty
	private UpdateProdQtyResult updateProductNoticeFinishQty(float fltInputQty,String prodNoticeCode,
			  String orderNumber,MaterialInRecord materialIn,StringBuilder remarks) {
		UpdateProdQtyResult result = new UpdateProdQtyResult();
		if (fltInputQty<=0){
			return result;
		}
		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find("from MaterialInfo t where t.materialcode=?", materialIn.getMaterialcode());
		if (matList.size()<=0)
			return result;
		else
			materialInfo = matList.get(0);

		Map<String,Object> map = new HashMap<String,Object>();
		
		StringBuilder prodNoticSQL = new StringBuilder(
				"select q.id,d.quantity,q.finishquantity,m.DeliverDate,m.OrderNumber,d.productionNoticeCode")
				.append(" from production_notice_detail d,production_notice_master pm,sales_order_master m,PRODUCTNOTICE_FINISHQTY q")
				.append(" where d.productionNoticeCode=pm.ProductionNoticeCode and pm.OrderNumber=m.OrderNumber ")
				.append(" and d.productionNoticeCode=q.productionNoticeCode and d.materialCode=q.materialCode and d.colorCode=q.colorCode ")
				.append(" and q.finishquantity<d.quantity and d.materialCode=:materialCode ");
		map.put("materialCode", materialIn.getMaterialcode());
		if (!StringUtil.isNullOrBlank(prodNoticeCode)){
			prodNoticSQL.append(" and d.productionNoticeCode =:productionNoticeCode ");
			map.put("productionNoticeCode", prodNoticeCode);
		}
		
		if (materialInfo.getMaterialdesc().equals(InventoryConstant.MaterialDesc_NoColor)){
			//半成品生产，并且是不分色时，不需要设置颜色(只有半成品才不分色)
		}else{
			prodNoticSQL.append(" and d.colorCode=:colorCode");
			map.put("colorCode",materialIn.getColorcode());
		}
		//更新成品入仓是需要订单号的，因为成品入仓按逐笔订单记录更新，需更新salesorder的状态
		if (!StringUtil.isNullOrBlank(orderNumber)){
			prodNoticSQL.append(" and m.OrderNumber=:OrderNumber ");
			map.put("OrderNumber", orderNumber);
		}
		prodNoticSQL.append(" order by m.DeliverDate asc,q.id asc ");
		List<Object[]> prodDetailList = utilsDao.findBySql(prodNoticSQL.toString(),map);
		if (prodDetailList.size()<=0){
			result.ifFinishProdQty = true;
			result.leftQty = fltInputQty;
			return result;
		}else{
			//boolean[] arrIsFinish = new boolean[prodDetailList.size()];
			int i = 0;
			for(Iterator<Object[]> iterator = prodDetailList.iterator(); iterator
					.hasNext();) {
				if (fltInputQty <=0)
					break;
				Object[] prodNotObjs = iterator.next();
				Integer pn_id = Integer.valueOf(prodNotObjs[0].toString());
				float pn_quantity = new BigDecimal(prodNotObjs[1].toString())
						.floatValue();
				float pn_finishQty = new BigDecimal(prodNotObjs[2].toString())
						.floatValue();
				float pn_qtyNotFinish = pn_quantity - pn_finishQty;
				prodNoticeCode = prodNotObjs[5].toString();
				orderNumber = prodNotObjs[4].toString();
				
				float pn_addValue = 0;
				if (pn_qtyNotFinish > fltInputQty) {// 未完成的数量大于输入数
					pn_addValue = fltInputQty;
					fltInputQty = 0;
					result.ifFinishProdQty = false;
				} else {
					pn_addValue = pn_qtyNotFinish;
					fltInputQty = fltInputQty - pn_qtyNotFinish;
					if (prodDetailList.size()-1==i)
						result.ifFinishProdQty = true;
				}
				String hql = "update ProductNoticeFinishQty t set t.finishquantity=t.finishquantity+? where t.id=? ";
				utilsDao.execute(hql, BigDecimal.valueOf(pn_addValue), pn_id);//
				insertProductNoticeFinishQtyTrace(pn_id,null,BigDecimal.valueOf(pn_addValue),1,materialIn.getRecordnumber());
				String type = "成品";
				if (prodNoticeCode.indexOf("_semiprod") > 0)
					type = "半成品";
				remarks.append(orderNumber).append(":").append(type).append(":")
						.append(pn_addValue).append("——");
				i++;
			}
			
			result.leftQty = fltInputQty;
			return result;
		}
	}
	*/
	/*
	private UpdateProdQtyResult updateFinishedForSemiProd(MaterialInRecord materialIn, float fltInputQty,
			String prodNoticeCode, StringBuilder remarks,boolean assignOrderNum) {
		
		if (remarks == null)
			remarks = new StringBuilder("");
		//如果有prodNoticeCode，先更新该订单号的
		UpdateProdQtyResult prodRes = null;
		if (!StringUtil.isNullOrBlank(prodNoticeCode)){
			String ordernumber = prodNoticeCode.split("_")[0];
			prodRes = updateProductNoticeFinishQty(fltInputQty,prodNoticeCode,
					ordernumber,materialIn, remarks);
		}
		//再更新其他没有订单号的
		if (prodRes != null)
			fltInputQty = prodRes.leftQty;
		if (fltInputQty > 0)
			prodRes = updateProductNoticeFinishQty(fltInputQty,"","",
					materialIn, remarks);
		prodRes.remarks = remarks.toString();
		return prodRes;
	}

	// update finishedQuantity in salesOrder and productionOrder detail
	// if the input quantity is bigger than order number,split the quantity into
	// several order)
	//成品生产和安装入仓时更新已完成数（既可以是成品，也可以是半成品的，也调用这个方法）
	private UpdateProdQtyResult updateFinishedForProd(MaterialInRecord materialIn, float fltInputQty,
			String prodNoticeCode, StringBuilder remarks,boolean assignOrderNum) {
		UpdateProdQtyResult prodRes = new UpdateProdQtyResult();
		if (remarks == null)
			remarks = new StringBuilder("");
		Map<String,Object> map = new HashMap<String,Object>();
		String ordernumber = "";
		// 查询出所有未完成的订单明细列表
		StringBuilder salesOrderSQL = new StringBuilder(
				"select d.SalesOrderDetailID,d.quantity,d.inventoryQuantity,m.DeliverDate,d.OrderNumber")
				.append("  from sales_order_detail d,sales_order_master m ")
				.append(" where d.OrderNumber=m.OrderNumber and d.orderDetailState=0 and d.materialCode=:materialCode and d.colorCode=:colorCode ");
		if (assignOrderNum){
			salesOrderSQL.append(" and m.OrderNumber=:OrderNumber ");
			map.put("OrderNumber", materialIn.getOrdernumber());
			ordernumber = materialIn.getOrdernumber();
		}
		map.put("colorCode", materialIn.getColorcode());
		map.put("materialCode", materialIn.getMaterialcode());

		salesOrderSQL.append(" order by m.DeliverDate asc,d.SalesOrderDetailID asc ");
		
		List<Object[]> salesOrderDetailList = utilsDao.findBySql(salesOrderSQL.toString(),map);
		if (salesOrderDetailList.size()<=0){
			//有两种可能，如果指定了订单，证明已完成订单和生产通知数，那再更新没有指定订单的
			if (assignOrderNum){
				assignOrderNum = false;
				prodRes = updateFinishedForProd(materialIn, fltInputQty, "", remarks,false);
			}else//否则如果本来就没有订单号，表明已完成所有
				prodRes.ifFinishProdQty = true;
				prodRes.leftQty = fltInputQty;
				prodRes.remarks = remarks.toString();
				return prodRes;
		}else{
			for (Iterator<Object[]> iterator = salesOrderDetailList.iterator(); iterator
					.hasNext();) {
				if (fltInputQty <= 0)
					break;
				Object[] objs = iterator.next();
				Integer orderDetailID = Integer.valueOf(objs[0].toString());
				ordernumber = objs[4].toString();
				prodRes = updateProductNoticeFinishQty(fltInputQty,prodNoticeCode,
						ordernumber,materialIn,remarks);
				// 再看有没有半成品生产通知单。因为有些成品可以作为别的产品的半成品
				if ( assignOrderNum && (prodRes.leftQty>0)){
					String semiProdcode = "";
					if (!StringUtil.isNullOrBlank(prodNoticeCode))
						semiProdcode = materialIn.getOrdernumber() + "_semiprod";
					prodRes = updateProductNoticeFinishQty(prodRes.leftQty,semiProdcode,
						ordernumber,materialIn,remarks);
				}
	
				float so_quantity = new BigDecimal(objs[1].toString()).floatValue();
				float so_finishQty = new BigDecimal(objs[2].toString())
						.floatValue();
				float so_qtyNotFinish = so_quantity - so_finishQty;
				float addValue = 0;
				if (so_qtyNotFinish > 0) {
					if (so_qtyNotFinish > fltInputQty) {// 未完成的数量大于输入数
						addValue = fltInputQty;
					} else {
						addValue = so_qtyNotFinish;
					}
				}
				Map<String,Object> paramMap = new HashMap<String,Object>();
				StringBuilder hql = new StringBuilder( "update SalesOrderDetail t set t.inventoryquantity=t.inventoryquantity+:inventoryquantity");
				if (prodRes.ifFinishProdQty){
					hql.append(",orderdetailstate=:orderdetailstate");
					paramMap.put("orderdetailstate", 1);
				}
				hql.append(" where t.salesorderdetailid=:salesorderdetailid ");
				paramMap.put("inventoryquantity", BigDecimal.valueOf(addValue));
				paramMap.put("salesorderdetailid", orderDetailID);
				utilsDao.execute(hql.toString(), paramMap);
				
				insertSalesOrderDetailTrace(orderDetailID,BigDecimal.valueOf(addValue),materialIn.getRecordnumber());
					
				fltInputQty = prodRes.leftQty;
			}
			if (assignOrderNum && fltInputQty > 0){
				prodRes = updateFinishedForProd(materialIn, fltInputQty, "", remarks,false);
			}
		}
		prodRes.remarks = remarks.toString();
		prodRes.leftQty = fltInputQty;
		return prodRes;
	}
	*/
	/*
	private BigDecimal getNotFinishedQtyFromProdNotice(String materialcode,String colorcode){
		BigDecimal returnVal = BigDecimal.valueOf(0);

		MaterialInfo materialInfo = null;
		List<MaterialInfo> matList = utilsDao.find(
				"from MaterialInfo t where t.materialcode=?",
				materialcode);
		if (matList.size() <= 0)
			return returnVal;
		else
			materialInfo = matList.get(0);

		Map<String, Object> map = new HashMap<String, Object>();

		StringBuilder prodNoticSQL = new StringBuilder(
				"select sum(d.quantity-q.finishquantity)")
				.append(" from production_notice_detail d,PRODUCTNOTICE_FINISHQTY q")
				.append(" where ")
				.append(" d.productiondetailid=q.productiondetailid ")
				.append(" and q.finishquantity<d.quantity and d.materialCode=:materialCode ");
		map.put("materialCode",materialcode);
		
		if (materialInfo.getMaterialdesc().equals(
				InventoryConstant.MaterialDesc_NoColor)) {
			// 半成品生产，并且是不分色时，不需要设置颜色(只有半成品才不分色)
		} else {
			prodNoticSQL.append(" and d.colorCode=:colorCode");
			map.put("colorCode", colorcode);
		}
		List<Object> resultList = utilsDao.findBySql(prodNoticSQL.toString(), map);
		if (resultList.size()>0 && resultList.get(0)!=null){
			returnVal = (BigDecimal)resultList.get(0);
		}
		return returnVal;
	}*/
	/*
	//盘点入仓
	@Transactional
	public int inventoryMaterialIn(MaterialInRecord materialIn){
		//插入MaterialInRecord
		materialInRecordMapper.insertSelective(materialIn);
		//更新inventory
		String quantityType = Constant.QuantityType_WareHouse;
		String unitCode = "只";
		InventoryInfoQuery inventoryQuery = new InventoryInfoQuery();
		inventoryQuery.createCriteria().andMaterialcodeEqualTo(materialIn.getMaterialcode()).
			andColorcodeEqualTo(materialIn.getColorcode()).andQuantitytypeEqualTo(quantityType)
			.andMaterialtypeEqualTo(materialIn.getMaterialtype());
		List<InventoryInfo> inventoryList = inventoryInfoMapper.selectByExample(inventoryQuery);
		InventoryInfo InventoryInfo;
		if (inventoryList.size()>0){
			InventoryInfo = inventoryList.get(0);
			InventoryInfo.setQuantity(InventoryInfo.getQuantity().add(materialIn.getQuantity2()));
			inventoryInfoMapper.updateByPrimaryKeySelective(InventoryInfo);
		}else{
			InventoryInfo = new InventoryInfo();
			InventoryInfo.setColorcode(materialIn.getColorcode());
			InventoryInfo.setQuantitytype(quantityType);
			InventoryInfo.setMaterialcode(materialIn.getMaterialcode());
			InventoryInfo.setUnitcode(unitCode);
			InventoryInfo.setQuantity(materialIn.getQuantity2());
			InventoryInfo.setMaterialtype(materialIn.getMaterialtype());
			if(materialIn.getMaterialtype().indexOf("成品")>0){
				InventoryInfo.setWarehouse(Constant.WareHouseType_PRODUCT);
			}else
				InventoryInfo.setWarehouse(Constant.WareHouseType_RAW);
			inventoryInfoMapper.insertSelective(InventoryInfo);
		}
		return 0;
	}*/
}
