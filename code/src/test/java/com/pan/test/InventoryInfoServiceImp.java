package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.utils.StringUtil;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.InventoryInfoSum;
import com.poons.salesorder.po.InventoryInfoSumTrc;
import com.poons.salesorder.po.InventoryInfoTrc;
import com.poons.salesorder.service.InventoryInfoService;
import com.poons.salesorder.utils.InventoryConstant;

@Service
public class InventoryInfoServiceImp implements InventoryInfoService {

	@Autowired
	private UtilsDao utilsDao;

	//更新库存，inventoryInfo的quantity为要增加或减少的库存，若想减库存，将quantity设为负即可
	@Override
	public void updateInventory(InventoryInfo inventoryInfo,
			String recordNumber) {

		// 既可以是成品，又可以是半成品的，仓库库存进入半成品仓
		Map<String, String> semiMap = new HashMap<String, String>();
		List<String> prodlist = utilsDao.findBySql(
						"select m.materialCode from bominfo t,materialinfo m where t.subMaterialCode=m.materialCode and m.materialType='成品'",
						new HashMap());
		for (Iterator<String> iterator = prodlist.iterator(); iterator
				.hasNext();) {
			String objects = iterator.next();
			semiMap.put(objects, "1");
		}
		if (inventoryInfo.getQuantitytype().equals(
				InventoryConstant.QuantityType_WareHouse)
				&& inventoryInfo.getMaterialtype().equals("成品")) {
			if (semiMap.get(inventoryInfo.getMaterialcode()) != null)
				inventoryInfo
						.setWarehouse(InventoryConstant.WareHouseType_SEMIPROD);
		}
		if (inventoryInfo.getUserid() == null){
			inventoryInfo.setUserid("");
		}
		InventoryInfo updateInventoryInfo = getInventoryInfoWithParam(inventoryInfo);
		if (updateInventoryInfo != null) {
			InventoryInfo oldInventoryInfo = new InventoryInfo();
			BeanUtils.copyProperties(updateInventoryInfo,oldInventoryInfo);
			updateInventoryInfo.setQuantity(updateInventoryInfo.getQuantity().add(
					inventoryInfo.getQuantity()));
			if (updateInventoryInfo.getNotoccupyqty()==null){
				updateInventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
			}
			updateInventoryInfo.setNotoccupyqty(updateInventoryInfo.getNotoccupyqty().add(
						inventoryInfo.getNotoccupyqty()));
			//由于领用安装出仓时可能领出比目前已知订单数量多，这样会导致未占用库存跟库存数一样，因此成品出仓时要也要减未占用库存，否则会出现未占有库存比库存多的情況
			//recordNumber.startsWith(InventoryConstant.RECORDNUMBER_PREFIX_PRODOUT)
			/**
			if(recordNumber.startsWith(InventoryConstant.RECORDNUMBER_PREFIX_MATERIALOUT)){
				if (updateInventoryInfo.getNotoccupyqty().compareTo(updateInventoryInfo.getQuantity())>0){
					updateInventoryInfo.setNotoccupyqty(updateInventoryInfo.getQuantity());
				}
			}*/
			utilsDao.update(updateInventoryInfo);
			insertInventoryInfoTrace(updateInventoryInfo,oldInventoryInfo,recordNumber);
		} else {
			if (inventoryInfo.getNotoccupyqty()==null)
				inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
			//inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
			if (inventoryInfo.getColorcode()==null)
				inventoryInfo.setColorcode("");
			utilsDao.insert(inventoryInfo);
			InventoryInfo oldInventoryInfo = new InventoryInfo();
			oldInventoryInfo.setQuantity(BigDecimal.valueOf(0));
			oldInventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
			insertInventoryInfoTrace(inventoryInfo,oldInventoryInfo,recordNumber);
		}
		updateInventorySum(inventoryInfo,recordNumber);
	}
	
	@Override
	public void updateInventorySum(InventoryInfo inventoryInfo,String recordNumber){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("from InventoryInfoSum where materialcode=:materialcode  ");
		paramMap.put("materialcode", inventoryInfo.getMaterialcode());
		hql.append(" and colorcode=:colorcode");
		if (StringUtil.isNullOrBlank(inventoryInfo.getColorcode())){
			paramMap.put("colorcode", "");
		}else{
			paramMap.put("colorcode", inventoryInfo.getColorcode());
		}
		List<InventoryInfoSum> list = utilsDao.find(hql.toString(),paramMap);
		if (list.size()>0){
			InventoryInfoSum inventoryInfoSum = list.get(0);
			InventoryInfoSum oldinventoryInfoSum = new InventoryInfoSum();
			try{
				BeanUtils.copyProperties(inventoryInfoSum,oldinventoryInfoSum);
			}catch (Exception e){
				throw new RuntimeException(e);
			}
			
			inventoryInfoSum.setNotoccupyqty(inventoryInfoSum.getNotoccupyqty().add(inventoryInfo.getNotoccupyqty()));
			if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Setup)){
				inventoryInfoSum.setSetupingqty(inventoryInfoSum.getSetupingqty().add(inventoryInfo.getQuantity()));
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Vendor)){
				inventoryInfoSum.setOutsourceqty(inventoryInfoSum.getOutsourceqty().add(inventoryInfo.getQuantity()));
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Mixture)){
				inventoryInfoSum.setMixqty(inventoryInfoSum.getMixqty().add(inventoryInfo.getQuantity()));
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Manufacture)){
				inventoryInfoSum.setProducingqty(inventoryInfoSum.getProducingqty().add( inventoryInfo.getQuantity()));
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Recycle)){
				inventoryInfoSum.setRecycleqty(inventoryInfoSum.getRecycleqty().add( inventoryInfo.getQuantity()));
			}else{
				inventoryInfoSum.setInventoryqty(inventoryInfoSum.getInventoryqty().add(inventoryInfo.getQuantity()));
			}
			/**
			if(recordNumber.startsWith(InventoryConstant.RECORDNUMBER_PREFIX_PRODOUT)
					|| recordNumber.startsWith(InventoryConstant.RECORDNUMBER_PREFIX_MATERIALOUT)){
				if (inventoryInfoSum.getNotoccupyqty().compareTo(inventoryInfoSum.getInventoryqty())>0){
					inventoryInfoSum.setNotoccupyqty(inventoryInfoSum.getInventoryqty());
				}
			}**/
			utilsDao.update(inventoryInfoSum);
			insertInventoryInfoSumTrace(inventoryInfoSum,oldinventoryInfoSum,recordNumber);
		}else{
			InventoryInfoSum inventoryInfoSum = new InventoryInfoSum();
			try{
				BeanUtils.copyProperties(inventoryInfo,inventoryInfoSum);
			}catch (Exception e){
				throw new RuntimeException(e);
			}
			
			if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Setup)){
				inventoryInfoSum.setSetupingqty(inventoryInfo.getQuantity());
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Vendor)){
				inventoryInfoSum.setOutsourceqty(inventoryInfo.getQuantity());
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Mixture)){
				inventoryInfoSum.setMixqty(inventoryInfo.getQuantity());
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Manufacture)){
				inventoryInfoSum.setProducingqty(inventoryInfo.getQuantity());
			}else if (inventoryInfo.getWarehouse().equals(InventoryConstant.WareHouseType_Recycle)){
				inventoryInfoSum.setRecycleqty(inventoryInfo.getQuantity());
			}else{
				inventoryInfoSum.setInventoryqty(inventoryInfo.getQuantity());
			}
			if (StringUtil.isNullOrBlank(inventoryInfo.getColorcode())){
				inventoryInfoSum.setColorcode("");
			}
			utilsDao.insert(inventoryInfoSum);
			insertInventoryInfoSumTrace(inventoryInfoSum,new InventoryInfoSum(),recordNumber);
		}
	}
	@Override
	public void insertInventoryInfoSumTrace(InventoryInfoSum inventoryInfoSum,InventoryInfoSum oldInventoryInfoSum,String recordNumber){
		InventoryInfoSumTrc inventoryInfoSumTrc = new InventoryInfoSumTrc();
		try{
			BeanUtils.copyProperties(inventoryInfoSum,inventoryInfoSumTrc);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		inventoryInfoSumTrc.setRecordnumber(recordNumber);
		inventoryInfoSumTrc.setInventoryqtyBefore(oldInventoryInfoSum.getInventoryqty());
		inventoryInfoSumTrc.setInventoryqtyAfter(inventoryInfoSum.getInventoryqty());
		inventoryInfoSumTrc.setInventoryqtyUpdate(inventoryInfoSum.getInventoryqty().subtract(oldInventoryInfoSum.getInventoryqty()));
		
		inventoryInfoSumTrc.setNotoccupyqtyBefore(oldInventoryInfoSum.getNotoccupyqty());
		inventoryInfoSumTrc.setNotoccupyqtyAfter(inventoryInfoSum.getNotoccupyqty());
		inventoryInfoSumTrc.setNotoccupyqtyUpdate(inventoryInfoSum.getNotoccupyqty().subtract(oldInventoryInfoSum.getNotoccupyqty()));
		
		inventoryInfoSumTrc.setSetupingqtyBefore(oldInventoryInfoSum.getSetupingqty());
		inventoryInfoSumTrc.setSetupingqtyAfter(inventoryInfoSum.getSetupingqty());
		inventoryInfoSumTrc.setSetupingqtyUpdate(inventoryInfoSum.getSetupingqty().subtract(oldInventoryInfoSum.getSetupingqty()));
		
		inventoryInfoSumTrc.setProducingqtyBefore(oldInventoryInfoSum.getProducingqty());
		inventoryInfoSumTrc.setProducingqtyAfter(inventoryInfoSum.getProducingqty());
		inventoryInfoSumTrc.setProducingqtyUpdate(inventoryInfoSum.getProducingqty().subtract(oldInventoryInfoSum.getProducingqty()));

		inventoryInfoSumTrc.setOutsourceqtyBefore(oldInventoryInfoSum.getOutsourceqty());
		inventoryInfoSumTrc.setOutsourceqtyAfter(inventoryInfoSum.getOutsourceqty());
		inventoryInfoSumTrc.setOutsourceqtyUpdate(inventoryInfoSum.getOutsourceqty().subtract(oldInventoryInfoSum.getOutsourceqty()));

		inventoryInfoSumTrc.setMixqtyBefore(oldInventoryInfoSum.getMixqty());
		inventoryInfoSumTrc.setMixqtyAfter(inventoryInfoSum.getMixqty());
		inventoryInfoSumTrc.setMixqtyUpdate(inventoryInfoSum.getMixqty().subtract(oldInventoryInfoSum.getMixqty()));
		
		inventoryInfoSumTrc.setRecycleqtyBefore(oldInventoryInfoSum.getRecycleqty());
		inventoryInfoSumTrc.setRecycleqtyAfter(inventoryInfoSum.getRecycleqty());
		inventoryInfoSumTrc.setRecycleqtyUpdate(inventoryInfoSum.getRecycleqty().subtract(oldInventoryInfoSum.getRecycleqty()));
		
		inventoryInfoSumTrc.setTraceid(null);
		utilsDao.insert(inventoryInfoSumTrc);
	}
	
	@Override
	public void insertInventoryInfoTrace(InventoryInfo inventoryInfo,InventoryInfo oldInventoryInfo,
			String recordNumber){
		InventoryInfoTrc inventoryInfoTrc = new InventoryInfoTrc();
		try{
			BeanUtils.copyProperties(inventoryInfo,inventoryInfoTrc);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		inventoryInfoTrc.setRecordnumber(recordNumber);
		inventoryInfoTrc.setQuantityBefore(oldInventoryInfo.getQuantity());
		inventoryInfoTrc.setQuantityAfter(inventoryInfo.getQuantity());
		inventoryInfoTrc.setQuantityUpdate(inventoryInfo.getQuantity().subtract(oldInventoryInfo.getQuantity()));
		inventoryInfoTrc.setNotoccupyqtyBefore(oldInventoryInfo.getNotoccupyqty());
		inventoryInfoTrc.setNotoccupyqtyAfter(inventoryInfo.getNotoccupyqty());
		inventoryInfoTrc.setNotoccupyqtyUpdate(inventoryInfo.getNotoccupyqty().subtract(oldInventoryInfo.getNotoccupyqty()));
		inventoryInfoTrc.setTraceid(null);
		utilsDao.insert(inventoryInfoTrc);
	}
	@Override
	public InventoryInfo getInventoryInfoWithParam(InventoryInfo inventoryInfo){
		InventoryInfo inventoryInfoRet = null;
		if (inventoryInfo.getNotoccupyqty() ==null)
			inventoryInfo.setNotoccupyqty(BigDecimal.valueOf(0));
		InventoryInfo query = new InventoryInfo();
		query.setMaterialcode(inventoryInfo.getMaterialcode());
		query.setQuantitytype(inventoryInfo.getQuantitytype());
		query.setWarehouse(inventoryInfo.getWarehouse());
		query.setUnitcode(inventoryInfo.getUnitcode());
		//query.setMaterialtype(inventoryInfo.getMaterialtype());

		if (!StringUtil.isNullOrBlank(inventoryInfo.getUserid())) {
			query.setUserid(inventoryInfo.getUserid());
		} else{
			query.setUserid("");
		}

		if (!StringUtil.isNullOrBlank(inventoryInfo.getColorcode())) {
			query.setColorcode(inventoryInfo.getColorcode());
		}
		List<InventoryInfo> inventoryList = utilsDao.findByExample(query);
		if (inventoryList.size()>0)
			inventoryInfoRet = inventoryList.get(0);
		return inventoryInfoRet;
	}

	@Override
	public InventoryInfoSum getInventoryInfoSum(String materialCode,
			String colorCode) {
		InventoryInfoSum inventoryInfoSum = null;
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if (colorCode==null){
			colorCode = "";
		}
		paramMap.put("materialcode", materialCode);
		paramMap.put("colorcode", colorCode);
		List<InventoryInfoSum> list = utilsDao.find("from InventoryInfoSum where materialcode=:materialcode and colorcode=:colorcode",paramMap);
		if (!list.isEmpty()){
			inventoryInfoSum = list.get(0);
		}
		return inventoryInfoSum;
	}

}
