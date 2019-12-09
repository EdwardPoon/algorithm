package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.salesorder.po.BomInfo;
import com.poons.salesorder.service.BomService;
import com.poons.salesorder.service.bo.BomItemBO;
import com.poons.salesorder.service.bo.BomSumBO;

@Service
public class BomServiceImpl implements BomService {

	@Autowired
	private UtilsDao utilsDao;
	
	@Override
	public BomSumBO getBomSumBO(String parentmaterialcode,BigDecimal parentMaterialQuantity) {
		BomSumBO bomSumBO = new BomSumBO();
		bomSumBO.setParentmaterialcode(parentmaterialcode);
		List<BomInfo> bomList =utilsDao.find("from BomInfo where parentmaterialcode=?",parentmaterialcode);
		List<BomItemBO> packageItemList = new ArrayList<BomItemBO>();
		for (Iterator<BomInfo> iterator = bomList.iterator(); iterator.hasNext();) {
			BomInfo bomInfo = iterator.next();
			if (bomInfo.getMaterialtype().equals("包装物料")){
				BomItemBO bomItemBO = new BomItemBO();
				bomItemBO.setUnitcode(bomInfo.getSubmaterialunit());
				bomItemBO.setMaterialcode(bomInfo.getSubmaterialcode());
				bomItemBO.setMaterialtype(bomInfo.getMaterialtype());
				
				BigDecimal quantity = parentMaterialQuantity.divideToIntegralValue(
						bomInfo.getSubmaterialquantity());
				bomItemBO.setQuantity(quantity);
				packageItemList.add(bomItemBO);
			}
		}
		bomSumBO.setPackageItemList(packageItemList);
		return bomSumBO;
	}

}
