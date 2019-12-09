package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.base.model.Page;
import com.poons.salesorder.model.MaterialBatchTranModel;
import com.poons.salesorder.model.RawMaterialTranModel;
import com.poons.salesorder.po.BomInfo;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.MaterialInfo;
import com.poons.salesorder.po.RawMaterialTran;
import com.poons.salesorder.service.InventoryInfoService;
import com.poons.salesorder.service.InventoryService;
import com.poons.salesorder.service.RawMaterialService;
import com.poons.salesorder.service.bo.MaterialQuantity;
import com.poons.salesorder.utils.InventoryConstant;

@Service("rawMaterialService")
public class RawMaterialServiceImp implements RawMaterialService {

	@Autowired
	private UtilsDao utilsDao;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private InventoryInfoService inventoryInfoService;
	
	@Override
	@Transactional(readOnly=true)
	public List<MaterialBatchTranModel> queryRawMaterial(String ordernumber,
			String transType) {
		List<MaterialBatchTranModel> retList = new ArrayList<MaterialBatchTranModel>();
		StringBuilder sql = new StringBuilder("");
		boolean showColor = false;
		if (transType.endsWith("toMix")){
			showColor = false;
		}else if (transType.endsWith("mixToInv")){
			showColor = false;
		}else if (transType.endsWith("toProd")){
			showColor = true;
		}else if (transType.endsWith("prodToMix")){
			showColor = true;
		}else if (transType.endsWith("mixToMix")){
			showColor = true;
		}
		sql.append("select p.materialCode,m.materialName,m.materialDesc,");//
		if (showColor){
			sql.append("p.quantity,io.inventoryqty,io.mixqty,i.producingqty,p.colorCode,c.colorName ");
			sql.append(" from PRODUCTION_NOTICE_DETAIL p ");
		}else{
			sql.append("p.quantity,io.inventoryqty,io.mixqty,io.producingqty,'' as colorCode,'' as colorName ");
			sql.append(" from PRODUCTION_RESOURCE_NOCOLOR p ");
		}
		
		sql.append(" inner join materialinfo m on p.materialCode=m.materialCode ");
		sql.append(" left join INVENTORYINFOSUM_WITHOUTCOLOR io on p.materialCode=io.materialCode ");
		sql.append(" left join inventoryinfosum i on p.materialCode=i.materialCode ");
		if (showColor){
			sql.append(" and p.colorCode=i.colorCode ");
			sql.append(" left join colorinfo c on p.colorCode=c.colorCode ");
			sql.append(" where p.productionNoticeTypeCode='resourceCollect' ");
		}else{
			sql.append(" and i.colorcode='' ")
			.append(" where 1=1 ");
		}
		// can't use inner join, since there would be multiple records
		if (transType.endsWith("mixToMix")){
			sql.append(" and p.materialCode in (select distinct b.parentMaterialCode from bominfo b, materialinfo m1 where b.parentMaterialCode=m1.materialCode and m1.materialType='原料') ");
		}
		sql.append(" and p.ordernumber = :ordernumber order by p.materialCode");
	    Map<String,Object> parammap = new HashMap<String,Object>();
	    parammap.put("ordernumber", ordernumber);
	    List<Object[]> objList = utilsDao.findBySql(sql.toString(), parammap);
		for (Iterator<Object[]> iterator = objList.iterator(); iterator.hasNext();) {
			Object[] objs =  iterator.next();
			MaterialBatchTranModel materialBatchTranModel = new MaterialBatchTranModel();
			materialBatchTranModel.setMaterialcode(objs[0].toString());
			materialBatchTranModel.setMaterialname(objs[1].toString());
			materialBatchTranModel.setMaterialdesc(objs[2].toString());
			materialBatchTranModel.setQuantity(objs[3]==null?BigDecimal.ZERO:(BigDecimal)objs[3]);
			materialBatchTranModel.setInventoryqty(objs[4]==null?BigDecimal.ZERO:(BigDecimal)objs[4]);
			materialBatchTranModel.setMixqty(objs[5]==null?BigDecimal.ZERO:(BigDecimal)objs[5]);
			materialBatchTranModel.setProducingqty(objs[6]==null?BigDecimal.ZERO:(BigDecimal)objs[6]);
			materialBatchTranModel.setColorcode(objs[7]==null?"":objs[7].toString());
			materialBatchTranModel.setColorname(objs[8]==null?"":objs[8].toString());
			
			retList.add(materialBatchTranModel);
		}
		return retList;
	}

	@Override
	@Transactional
	public void createRawMaterialTran(RawMaterialTran rawMaterialTran) {
		String transType = rawMaterialTran.getTranstype();
		String transTypeDesc = "";
		String quantityType = "";
		String wareHourse = "";
		String colorcode = "";
		List<InventoryInfo> inventoryInfoList = new ArrayList<InventoryInfo>();
		if (transType.endsWith("toMix")){
			transTypeDesc = InventoryConstant.TransType_RawMat_toMix;
			quantityType = InventoryConstant.QuantityType_WareHouse;
			wareHourse = InventoryConstant.WareHouseType_RAW;
			colorcode = "";
			
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity().negate()));
			
			quantityType = InventoryConstant.QuantityType_Mix;
			wareHourse = InventoryConstant.WareHouseType_Mixture;
			colorcode = "";
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity()));
		}else if (transType.endsWith("mixToInv")){
			transTypeDesc = InventoryConstant.TransType_RawMat_mixToInv;

			quantityType = InventoryConstant.QuantityType_WareHouse;
			wareHourse = InventoryConstant.WareHouseType_RAW;
			colorcode = "";
			
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity()));
			
			quantityType = InventoryConstant.QuantityType_Mix;
			wareHourse = InventoryConstant.WareHouseType_Mixture;
			colorcode = "";
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity().negate()));
			
		}else if (transType.endsWith("toProd")){
			transTypeDesc = InventoryConstant.TransType_RawMat_toProd;

			quantityType = InventoryConstant.QuantityType_Manufacture;
			wareHourse = InventoryConstant.WareHouseType_Manufacture;
			colorcode = rawMaterialTran.getColorcode();
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity()));
			
			quantityType = InventoryConstant.QuantityType_Mix;
			wareHourse = InventoryConstant.WareHouseType_Mixture;
			colorcode = "";
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity().negate()));
		} else if (transType.endsWith("prodToMix")){
			transTypeDesc = InventoryConstant.TransType_RawMat_prodToMix;
			quantityType = InventoryConstant.QuantityType_Mix;
			wareHourse = InventoryConstant.WareHouseType_Mixture;
			colorcode = "";
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity()));

			quantityType = InventoryConstant.QuantityType_Manufacture;
			wareHourse = InventoryConstant.WareHouseType_Manufacture;
			colorcode = rawMaterialTran.getColorcode();
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity().negate()));
		} else if (transType.endsWith("mixToMix")){
			
			transTypeDesc = InventoryConstant.TransType_RawMat_mixdToMix;
			quantityType = InventoryConstant.QuantityType_Mix;
			wareHourse = InventoryConstant.WareHouseType_Mixture;
			colorcode = "";
			inventoryInfoList.add(new InventoryInfo(rawMaterialTran.getMaterialcode(),
					colorcode,quantityType,wareHourse,rawMaterialTran.getQuantity()));
			// TODO 
			List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",rawMaterialTran.getMaterialcode());
			if (bomlist!=null && bomlist.size()>0){
				for (BomInfo bomInfo : bomlist){
					String submaterialcode = bomInfo.getSubmaterialcode() ;
					BigDecimal quantity = rawMaterialTran.getQuantity().multiply(bomInfo.getSubmaterialquantity());
					
					quantityType = InventoryConstant.QuantityType_Mix;
					wareHourse = InventoryConstant.WareHouseType_Mixture;
					colorcode = "";
					inventoryInfoList.add(new InventoryInfo(submaterialcode,
							colorcode,quantityType,wareHourse,quantity.negate()));
					// insert negate toMix record for inventory record
					RawMaterialTran subRawMaterialTran = new RawMaterialTran();
					subRawMaterialTran.setMaterialcode(submaterialcode);
					subRawMaterialTran.setColorcode("");
					subRawMaterialTran.setOrdernumber(rawMaterialTran.getOrdernumber());
					subRawMaterialTran.setTransdate(rawMaterialTran.getTransdate());
					subRawMaterialTran.setUnitcode("公斤");
					subRawMaterialTran.setQuantity(quantity.negate());
					subRawMaterialTran.setTranstype(InventoryConstant.TransType_RawMat_mixdToMix);
					
					utilsDao.insert(subRawMaterialTran);
				}
			}
		}
		rawMaterialTran.setRecordstatus(0);
		
		rawMaterialTran.setTranstype(transTypeDesc);
		utilsDao.insert(rawMaterialTran);
		
		for (InventoryInfo inventoryInfo:inventoryInfoList){
			updateInventory(inventoryInfo,rawMaterialTran.getRecordnumber());
		}
	}
	@Override
	@Transactional
	public void updateInventory(InventoryInfo inventoryInfo,String recordNumber){
		inventoryInfo.setMaterialtype("原料");
		inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
		inventoryInfo.setUnitcode("公斤");
		inventoryInfo.setUserid("");
		inventoryInfoService.updateInventorySum(inventoryInfo,recordNumber);
	}

	@Override
	@Transactional(readOnly=true)
	public List<MaterialQuantity> calSubMaterialByProductWeight(
			MaterialQuantity materialQuantity) {
		List<MaterialQuantity> resList = new ArrayList<MaterialQuantity>();
		List<MaterialInfo> materialList =utilsDao.find("from MaterialInfo where materialcode=?",materialQuantity.getMaterialCode());
		MaterialInfo materialInfo = materialList.get(0);
		BigDecimal quantityPiece = BigDecimal.ZERO;
		if (materialQuantity.getQuantityPiece()==null && materialQuantity.getQuantityWeight()!=null){
			quantityPiece = materialQuantity.getQuantityWeight().multiply(BigDecimal.valueOf(1000))
						.divide(materialInfo.getUnitweight(), BigDecimal.ROUND_HALF_UP);
			materialQuantity.setQuantityPiece(quantityPiece);
		}else{
			quantityPiece = materialQuantity.getQuantityPiece();
		}
		
		if (materialInfo.getMaterialtype().equals("半成品") || 
				(materialInfo.getMaterialtype().equals("成品") &&
				materialInfo.getIsneedsetup().equals("否"))){
			List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",materialQuantity.getMaterialCode());
			for (BomInfo bomInfo :bomlist){
				MaterialQuantity materialQuantityRes = new MaterialQuantity();
				materialQuantityRes.setMaterialCode(bomInfo.getSubmaterialcode());
				materialQuantityRes.setColorCode(materialQuantity.getColorCode());
				materialQuantityRes.setMaterialType(bomInfo.getMaterialtype());
				materialQuantityRes.setQuantityWeight(materialQuantity.getQuantityWeight());
				resList.add(materialQuantityRes);
			}
		}else if (materialInfo.getMaterialtype().equals("成品") &&
				materialInfo.getIsneedsetup().equals("生产并安装")){
			List<BomInfo> bomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",materialQuantity.getMaterialCode());
			for (BomInfo bomInfo :bomlist){
				
				if (bomInfo.getMaterialtype().equals("五金件")){
				
					MaterialQuantity materialQuantityRes = new MaterialQuantity();
					materialQuantityRes.setMaterialCode(bomInfo.getSubmaterialcode());
					materialQuantityRes.setColorCode("");
					materialQuantityRes.setMaterialType(bomInfo.getMaterialtype());
					materialQuantityRes.setQuantityPiece(quantityPiece);
					resList.add(materialQuantityRes);

				}else if (bomInfo.getMaterialtype().equals("半成品")||bomInfo.getMaterialtype().equals("成品")){

					List<MaterialInfo> semiMaterialList =utilsDao.find("from MaterialInfo where materialcode=?",bomInfo.getSubmaterialcode());
					MaterialInfo semiMaterialInfo = semiMaterialList.get(0);

					BigDecimal unitWeight = semiMaterialInfo.getUnitweight();
					List<BomInfo> subbomlist =utilsDao.find("from BomInfo where parentmaterialcode=?",bomInfo.getSubmaterialcode());
					
					for (BomInfo subbomInfo :subbomlist){
						if (subbomInfo.getMaterialtype().equals("原料")){
							
							MaterialQuantity materialQuantityRes = new MaterialQuantity();
							materialQuantityRes.setMaterialCode(subbomInfo.getSubmaterialcode());
							materialQuantityRes.setColorCode(materialQuantity.getColorCode());
							materialQuantityRes.setMaterialType(subbomInfo.getMaterialtype());
							materialQuantityRes.setQuantityWeight(quantityPiece.multiply(unitWeight).divide(BigDecimal.valueOf(1000)));
							resList.add(materialQuantityRes);
						}
					}
				}
			}
		}
		return resList;
	}

	@Override
	@Transactional(readOnly=true)
	public List<RawMaterialTranModel> queryRawMaterialList(String materialcode,String colorcode,Page page) {
		StringBuilder selecthql = new StringBuilder("select t.recordid,t.materialcode,t.colorcode,t.transtype,t.transdate,t.createdate,")
		.append("t.unitcode,t.quantity,t.recordnumber,m.materialname,c.colorname")
		.append(" from raw_material_tran t inner join MaterialInfo m on t.materialcode=m.materialcode " )
		.append(" left join colorinfo c on t.colorCode=c.colorCode where 1=1 " );
		StringBuilder counthql = new StringBuilder("select count(t.recordid) from raw_material_tran t where 1=1");
	    Map<String,Object> parammap = new HashMap<String,Object>();
	    if (StringUtils.isNotBlank(materialcode)){
	    	selecthql.append(" and t.materialcode like :materialcode ");
	    	counthql.append(" and t.materialcode like :materialcode ");
	    	parammap.put("materialcode", materialcode+"%");
	    }
	    if (StringUtils.isNotBlank(colorcode)){
	    	selecthql.append(" and t.colorcode like :colorcode ");
	    	counthql.append(" and t.colorcode like :colorcode ");
	    	parammap.put("colorcode", colorcode+"%");
	    }
	    selecthql.append(" order by t.createdate desc");
	    List<Object[]> objList =  utilsDao.findBySql(selecthql.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(),parammap);
	    page.setTotalRecordSize(utilsDao.countBySql(counthql.toString(),parammap));
	    List<RawMaterialTranModel> resList = new ArrayList<RawMaterialTranModel>();
	    for(Object[] objs : objList){
	    	RawMaterialTranModel rawMaterialTranModel = new RawMaterialTranModel();
	    	rawMaterialTranModel.setRecordid((Integer)objs[0]);
	    	rawMaterialTranModel.setMaterialcode(objs[1].toString());
	    	rawMaterialTranModel.setColorcode(objs[2]==null?"":objs[2].toString());
	    	rawMaterialTranModel.setTranstype(objs[3].toString());
	    	rawMaterialTranModel.setTransdate(objs[4]==null?null:(Date)objs[4]);
	    	rawMaterialTranModel.setCreateDate(objs[5]==null?null:(Date)objs[5]);
	    	rawMaterialTranModel.setUnitcode(objs[6]==null?"":objs[6].toString());
	    	rawMaterialTranModel.setQuantity(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
	    	rawMaterialTranModel.setRecordnumber(objs[8]==null?"":objs[8].toString());
	    	rawMaterialTranModel.setMaterialname(objs[9]==null?"":objs[9].toString());
	    	rawMaterialTranModel.setColorname(objs[10]==null?"":objs[10].toString());
	    	resList.add(rawMaterialTranModel);
	    }
	    return resList;
	}
}
