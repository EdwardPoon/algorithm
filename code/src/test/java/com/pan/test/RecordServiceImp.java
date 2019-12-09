package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
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
import com.poons.salesorder.model.MaterialBatchTranModel;
import com.poons.salesorder.model.MaterialOutRecordModel;
import com.poons.salesorder.po.MaterialInRecordV;
import com.poons.salesorder.service.RecordService;

@Service
public class RecordServiceImp implements RecordService {

	@Autowired
	private UtilsDao utilsDao;

	@Override
	@Transactional(readOnly = true)
	public List<MaterialBatchTranModel> getProdInBatchModel(String prodNoticeCode,String ordernumber,String materialType,String prodInType,String productionNoticeTypeCode,String optType ) {
	    Map<String,Object> parammap = new HashMap<String,Object>();
	    
	    parammap.put("prodNoticeCode", prodNoticeCode);
	    
	    StringBuilder sql = new StringBuilder("select t.materialcode,t.quantity,q.finishquantity,m.materialname,")
	    	.append(" m.unitweight,m.materialtype,t.colorcode,q.drawforsetupqty,inv.notoccupyqty,inv.inventoryqty,inv.setupingqty,inv.outsourceqty,t.productionNoticeCode " );
	    if (materialType.equals("wujin") || productionNoticeTypeCode.equals("directOut")){
	    	sql.append(",'' as colorname ");
	    }else{
	    	sql.append(",c.colorname ");
	    }
	    sql.append(" from production_notice_detail as t inner join MaterialInfo as m on t.materialcode=m.materialcode  ")
	    .append(" left join InventoryInfosum as inv on t.materialcode=inv.materialcode  ");
	    if  (!materialType.equals("wujin")&& !productionNoticeTypeCode.equals("directOut")){
	    	sql.append(" and t.colorcode=inv.colorcode ");
	    }
	    sql.append(" inner join productnotice_finishqty as q on t.ProductionDetailID=q.ProductionDetailID ");// t.productionnoticecode=q.productionnoticecode and t.materialcode=q.materialcode 
	    if  (!materialType.equals("wujin") && !productionNoticeTypeCode.equals("directOut")){
	    	//sql.append(" and t.colorcode=q.colorcode ");
	    	sql.append(" left join ColorInfo as c on t.colorcode=c.colorcode " );
	    }
	    sql.append(" where t.productionNoticeCode = :prodNoticeCode ");
	    
	    if  (materialType.equals("package")){
	    	if ( prodInType.equals("setup_co")){
	    		sql.append(" and t.materialCode in (select subMaterialCode from bominfo where materialType='包装物料' and subMaterialQuantity=1) ");
	    	}else if ( prodInType.equals("setup")){
	    		sql.append(" and t.materialCode in (select subMaterialCode from bominfo where materialType='包装物料' and subMaterialQuantity>1) ");
	    	}
	    }
	    
	    sql.append("order by t.materialcode,t.colorcode");
	    List<Object[]> objList = utilsDao.findBySql(sql.toString(),parammap);
		
	    List<MaterialBatchTranModel> resList = new ArrayList<MaterialBatchTranModel>();
		for (Object[] objs :objList) {
			MaterialBatchTranModel model = new MaterialBatchTranModel();
			model.setMaterialcode(objs[0].toString());
			model.setQuantity(objs[1]==null?BigDecimal.ZERO:(BigDecimal)objs[1]);
			model.setFinishquantity(objs[2]==null?BigDecimal.ZERO:(BigDecimal)objs[2]);
			model.setMaterialname(objs[3]==null?"":objs[3].toString());
			model.setUnitweight(objs[4]==null?BigDecimal.ZERO:(BigDecimal)objs[4]);
			model.setMaterialtype(objs[5]==null?"":objs[5].toString());
			model.setColorcode(objs[6]==null?"":objs[6].toString());
			model.setDrawforsetupqty(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
			model.setNotoccupyqty(objs[8]==null?BigDecimal.ZERO:(BigDecimal)objs[8]);
			model.setInventoryqty(objs[9]==null?BigDecimal.ZERO:(BigDecimal)objs[9]);
			model.setSetupingqty(objs[10]==null?BigDecimal.ZERO:(BigDecimal)objs[10]);
			model.setOutsourceqty(objs[11]==null?BigDecimal.ZERO:(BigDecimal)objs[11]);
			model.setColorname(objs[13]==null?"":objs[13].toString());
			model.setProdNoticeCode(objs[12]==null?"":objs[12].toString());
			resList.add(model);
		}
		//在查询可批量领用的半成品的时候，只查询半成品生产通知单的半成品列表
		//如果某些需要安装的成品作为半成品使用，则不会出现在生产通知单，只会出现在安装通知单
		if  ( (optType.equals("getOutForSetup")||optType.equals("recycleIn")) && materialType.equals("semiprod")){
			resList.addAll(getNeedSetupButNotProd(ordernumber));
		}
		return resList;
	}
	//需要安装但不是订单的出货产品
	private List<MaterialBatchTranModel> getNeedSetupButNotProd(String ordernumber ) {
	    List<MaterialBatchTranModel> resList = new ArrayList<MaterialBatchTranModel>();
		
	    Map<String,Object> parammap = new HashMap<String,Object>();
		String noticeCodeSetup = ordernumber+"_setup";
		
		String prodListSql = "select materialcode from production_notice_detail where productionnoticecode=:noticecode "
				+ " and materialcode not in (select materialcode from sales_order_detail where ordernumber=:ordernumber)";
		parammap.put("noticecode", noticeCodeSetup);
		parammap.put("ordernumber", ordernumber);
		List<String> materialCodeList = utilsDao.findBySql(prodListSql,parammap);
		
		if (materialCodeList!=null && materialCodeList.size()>0){

		    StringBuilder sql = new StringBuilder("select t.materialcode,t.quantity,q.finishquantity,m.materialname,")
		    	.append(" m.unitweight,m.materialtype,t.colorcode,q.drawforsetupqty,inv.notoccupyqty,inv.inventoryqty,inv.setupingqty,inv.outsourceqty,t.productionNoticeCode " );
		    sql.append(",c.colorname ");
		    sql.append(" from production_notice_detail as t inner join MaterialInfo as m on t.materialcode=m.materialcode  ")
		    .append(" left join InventoryInfosum as inv on t.materialcode=inv.materialcode  ");
		    sql.append(" and t.colorcode=inv.colorcode ");
		    sql.append(" inner join productnotice_finishqty as q on t.ProductionDetailID=q.ProductionDetailID ");// t.productionnoticecode=q.productionnoticecode and t.materialcode=q.materialcode 
		    sql.append(" left join ColorInfo as c on t.colorcode=c.colorcode " );
		    sql.append(" where t.materialcode in (:matcodelist) order by t.materialcode,t.colorcode");
		    
			parammap.clear();
		    parammap.put("matcodelist", materialCodeList);
		    List<Object[]> objList = utilsDao.findBySql(sql.toString(),parammap);
			for (Object[] objs :objList) {
				MaterialBatchTranModel model = new MaterialBatchTranModel();
				model.setMaterialcode(objs[0].toString());
				model.setQuantity(objs[1]==null?BigDecimal.ZERO:(BigDecimal)objs[1]);
				model.setFinishquantity(objs[2]==null?BigDecimal.ZERO:(BigDecimal)objs[2]);
				model.setMaterialname(objs[3]==null?"":objs[3].toString());
				model.setUnitweight(objs[4]==null?BigDecimal.ZERO:(BigDecimal)objs[4]);
				model.setMaterialtype(objs[5]==null?"":objs[5].toString());
				model.setColorcode(objs[6]==null?"":objs[6].toString());
				model.setDrawforsetupqty(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
				model.setNotoccupyqty(objs[8]==null?BigDecimal.ZERO:(BigDecimal)objs[8]);
				model.setInventoryqty(objs[9]==null?BigDecimal.ZERO:(BigDecimal)objs[9]);
				model.setSetupingqty(objs[10]==null?BigDecimal.ZERO:(BigDecimal)objs[10]);
				model.setOutsourceqty(objs[11]==null?BigDecimal.ZERO:(BigDecimal)objs[11]);
				model.setColorname(objs[13]==null?"":objs[13].toString());
				model.setProdNoticeCode(objs[12]==null?"":objs[12].toString());
				resList.add(model);
			}
		}
		return resList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MaterialInRecordV> getMaterialInRecordVs(List<String> transtypeList,String materialcode,String colorcode,Page page) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("transtypeList", transtypeList);
		StringBuilder selecthql = new StringBuilder("select t.recordid,t.materialcode,t.colorcode,t.transtype,t.transdate,")
			.append("t.ordernumber,t.unitcode,t.quantity,t.unitcode2,t.quantity2,")
			.append("t.userid,t.createdate,t.materialtype,t.materialname,t.username,")
			.append("t.colorname,t.unitprice,t.totalprice,t.warehouse ");
		StringBuilder counthql = new StringBuilder("select count(t.recordid)");
		StringBuilder hql = new StringBuilder(" from MaterialInRecordV t ")//,ColorInfo c
	    .append(" where t.transtype in (:transtypeList)");
		
		if (!StringUtil.isNullOrEmpty(materialcode)){
			hql.append(" and t.materialcode like :materialcode ");
			map.put("materialcode", materialcode+"%");
		}
		if (!StringUtil.isNullOrEmpty(colorcode)){
			hql.append(" and t.colorcode like :colorcode ");
			map.put("colorcode", colorcode+"%");
		}
		counthql.append(hql);
		selecthql.append(hql).append(" order by t.recordid desc");
	    List<Object[]> matList = utilsDao.find(selecthql.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(),map);
	    page.setTotalRecordSize(utilsDao.count(counthql.toString(),map));
	    List<MaterialInRecordV> matInRecList = new ArrayList<MaterialInRecordV>();
	    for (Object[] objs:matList) {
	    	MaterialInRecordV record = new MaterialInRecordV();
	    	record.setRecordid(objs[0]==null?0:(Integer)objs[0]);
	    	record.setMaterialcode(objs[1]==null?"":objs[1].toString());
	    	record.setColorcode(objs[2]==null?"":objs[2].toString());
	    	record.setTranstype(objs[3]==null?"":objs[3].toString());
	    	record.setTransdate(objs[4]==null?null:(Date)objs[4]);
	    	record.setOrdernumber(objs[5]==null?"":objs[5].toString());
	    	record.setUnitcode(objs[6]==null?"":objs[6].toString());
	    	record.setQuantity(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
	    	record.setUnitcode2(objs[8]==null?"":objs[8].toString());
	    	record.setQuantity2(objs[9]==null?BigDecimal.ZERO:(BigDecimal)objs[9]);
	    	record.setUserid(objs[10]==null?"":objs[10].toString());
	    	record.setCreatedate(objs[11]==null?null:(Date)objs[11]);
	    	record.setMaterialtype(objs[12]==null?"":objs[12].toString());
	    	record.setMaterialname(objs[13]==null?"":objs[13].toString());
	    	record.setUsername(objs[14]==null?"":objs[14].toString());
	    	record.setColorname(objs[15]==null?"":objs[15].toString());
	    	record.setUnitprice(objs[16]==null?BigDecimal.ZERO:(BigDecimal)objs[16]);
	    	record.setTotalprice(objs[17]==null?BigDecimal.ZERO:(BigDecimal)objs[17]);
	    	record.setWarehouse(objs[18]==null?"":objs[18].toString());
	    	
			matInRecList.add(record);
		}
	    return matInRecList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MaterialOutRecordModel> getMaterialOutRecordModels(List<String> transtype,String materialcode,String colorcode,Page page) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("transtypeList", transtype);		
		StringBuilder selecthql = new StringBuilder("select t.recordId,t.materialCode,t.colorCode,t.transType,t.transDate,")
		.append("t.orderNumber,t.unitCode,t.quantity,t.unitCode2,t.quantity2,t.userId,t.createDate,t.materialType,")
		.append("m.materialname,e.username,c.colorname ");
		StringBuilder counthql = new StringBuilder("select count(t.recordId)");
		StringBuilder hql = new StringBuilder(" from material_out_record t ")//MaterialOutRecord
		.append(" inner join MaterialInfo m on t.materialCode=m.materialcode ")
		.append(" left join om_employee e on t.userId=e.userid ")//Employee
		.append(" left join ColorInfo c on t.colorCode=c.colorcode ")
		.append(" where  ")//and t.colorCode*=c.colorcode
		.append(" t.transType in (:transtypeList) ");
		
		if (!StringUtil.isNullOrEmpty(materialcode)){
			hql.append(" and t.materialCode like :materialcode ");
			map.put("materialcode", materialcode+"%");
		}
		if (!StringUtil.isNullOrEmpty(colorcode)){
			hql.append(" and t.colorCode like :colorcode ");
			map.put("colorcode", colorcode+"%");
		}
		selecthql.append(hql).append(" order by t.recordId desc");
		counthql.append(hql);
		List<Object[]> matList = utilsDao.findBySql(selecthql.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(),map);
		page.setTotalRecordSize(utilsDao.countBySql(counthql.toString(), map));
		
		List<MaterialOutRecordModel> resList = new ArrayList<MaterialOutRecordModel>();
		for (Object[] objs : matList) {
			MaterialOutRecordModel model = new MaterialOutRecordModel();
			model.setRecordId(objs[0].toString());
			model.setMaterialCode( objs[1].toString());
			model.setColorCode(objs[2]==null?"":objs[2].toString());
			model.setTransType(objs[3]==null?"":objs[3].toString());
			model.setTransDate(objs[4]==null?null:(Date)objs[4]);
			model.setOrderNumber(objs[5]==null?"":objs[5].toString());
			model.setUnitCode(objs[6]==null?"":objs[6].toString());
			model.setQuantity(objs[7]==null?BigDecimal.ZERO:(BigDecimal)objs[7]);
			model.setUnitCode2(objs[8]==null?"":objs[8].toString());
			model.setQuantity2(objs[9]==null?BigDecimal.ZERO:(BigDecimal)objs[9]);
			model.setUserId(objs[10]==null?"":objs[10].toString());
			model.setCreateDate(objs[11]==null?null:(Date)objs[11]);
			model.setMaterialType(objs[12]==null?"":objs[12].toString());
			model.setMaterialname(objs[13]==null?"":objs[13].toString());
			model.setUsername(objs[14]==null?"":objs[14].toString());
			model.setColorname(objs[15]==null?"":objs[15].toString());
			resList.add(model);
		}
		return resList;
	}

}
