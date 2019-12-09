package com.poons.salesorder.service.imp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.base.model.Page;
import com.poons.frame.utils.StringUtil;
import com.poons.salesorder.dao.MaterialQuanliMapper;
import com.poons.salesorder.dao.SalesOrderDetailMapper;
import com.poons.salesorder.model.InventoryRecordModel;
import com.poons.salesorder.model.QulifiedRecordModel;
import com.poons.salesorder.po.InventoryRecord;
import com.poons.salesorder.po.MaterialQuanli;
import com.poons.salesorder.po.MaterialQuanliQuery;
import com.poons.salesorder.po.SalesOrderDetail;
import com.poons.salesorder.po.SalesOrderDetailQuery;
import com.poons.salesorder.po.report.EmpWithdrawDetail;
import com.poons.salesorder.service.ReportService;
import com.poons.salesorder.service.bo.ComboboxContentBO;
import com.poons.salesorder.utils.InventoryConstant;

@Service("reportService")
public class ReportServiceImp implements ReportService {
	@Autowired
	private UtilsDao utilsDao;
	@Autowired
	private MaterialQuanliMapper materialQuanliMapper;
	@Autowired
	private SalesOrderDetailMapper salesOrderDetailMapper;
	
	@Override
	@Transactional(readOnly = true)
	public List<ComboboxContentBO> getReportType() {
		List<ComboboxContentBO> resList = new ArrayList<ComboboxContentBO>();
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_PROD,"成品生产安装进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_PROD_NOCOLOR,"成品(不分色)生产安装进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_SEMIPROD,"半成品生产进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR,"半成品(不分色)生产进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_RAW,"原料仓库进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_RAWMIX,"原料配料车间进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_METAL,"五金件进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_PACKAGE,"包装物料进销存"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_EMPFETCHSEMI,"员工安装领用半成品"));
		resList.add(new ComboboxContentBO(InventoryConstant.Report_Type_EMPFETCHMETAL,"员工安装领用五金件"));
		return resList;
	}
	@Override
	@Transactional(readOnly = true)
	public Map<String,String> getColumnNames(String reptype){
		Map<String,String> resMap = new HashMap<String,String>();
		resMap.put("col_recordName", "月份");
		resMap.put("col_lastBalance", "上月结存");
		resMap.put("col_materialCode", "物料编码");
		resMap.put("col_materialName", "物料名称");
		resMap.put("col_colorCode", "颜色编码");
		resMap.put("col_colorName", "颜色");
		resMap.put("col_startDate", "开始结算时间");
		resMap.put("col_endDate", "截至结算时间");
		resMap.put("col_balance", "本月结存");
		resMap.put("col_remarks", "备注");
		if (reptype.equals(InventoryConstant.Report_Type_PROD)||reptype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)){
			resMap.put("col_periodIn", "本月生产及安装(只)");
			resMap.put("col_periodOut", "本月发出(只)");
		}else if (reptype.equals(InventoryConstant.Report_Type_SEMIPROD)||reptype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
			resMap.put("col_periodIn", "本月生产(只)");
			resMap.put("col_periodOut", "本月领出(只)");
		}else if (reptype.equals(InventoryConstant.Report_Type_RAW)){
			resMap.put("col_periodIn", "本月购进(公斤)");
			resMap.put("col_periodOut", "本月耗用(公斤)");
		}else if (reptype.equals(InventoryConstant.Report_Type_RAWMIX)){
			resMap.put("col_periodIn", "本月入仓(公斤)");
			resMap.put("col_periodOut", "本月耗用(公斤)");
		}else if (reptype.equals(InventoryConstant.Report_Type_METAL)){
			resMap.put("col_periodIn", "本月购进(只)");
			resMap.put("col_periodOut", "本月耗用(只)");
		}else if (reptype.equals(InventoryConstant.Report_Type_PACKAGE)){
			resMap.put("col_periodIn", "本月购进(只)");
			resMap.put("col_periodOut", "本月耗用(只)");
		}else if (reptype.equals(InventoryConstant.Report_Type_EMPFETCHSEMI)){
			resMap.put("col_lastBalance", "上月员工结存");
			resMap.put("col_periodIn", "本月领用(只)");
			resMap.put("col_periodOut", "本月交入(只)");
		}else if (reptype.equals(InventoryConstant.Report_Type_EMPFETCHMETAL)){
			resMap.put("col_lastBalance", "上月员工结存");
			resMap.put("col_periodIn", "本月领用(只)");
			resMap.put("col_periodOut", "本月交入(只)");
		}
		return resMap;
	}
	// 查询实时进销存记录，就是上面的那个表
	@Override
	@Transactional(readOnly = true)
	public List<InventoryRecordModel> queryInoutSum(String rectype, Date begindate, Date enddate,String materialcode,String materialname,Page page) {
		Map<String,Object> periodInParamMap = new HashMap<String,Object>();
		Map<String,Object> periodOutParamMap = new HashMap<String,Object>();
		
		String periodInSQL = "select t.materialCode,m.materialName,t.colorCode,c.colorName,t.quantity2 from material_in_record t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " left join colorinfo c on t.colorCode=c.colorCode "
				+" where t.TransType not in (:transTypeNotInList) and t.materialType in (:materialTypeList)";
		String periodOutSQL = "select t.materialCode,m.materialName,t.colorCode,c.colorName,t.quantity2 from ProductMaterialOut_V t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " left join colorinfo c on t.colorCode=c.colorCode "
				+ " where t.materialType in (:materialTypeList) ";//成品和五金件要直接出货，所以要加上成品出货
		
		String periodInSQLEmp = "select t.materialCode,m.materialName,t.colorCode,c.colorName,t.quantity2 from material_out_record t "
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " left join colorinfo c on t.colorCode=c.colorCode "
				+ " where t.materialType in (:materialTypeList)";
		String periodOutSQLEmp = "select t.materialCode,m.materialName,t.colorCode,c.colorName,t.quantity2 from material_in_record t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " left join colorinfo c on t.colorCode=c.colorCode "
				+ " where t.TransType in (:transTypeInList) and t.materialType in (:materialTypeList) ";

		String periodInSQLRaw = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity from RawMaterialIn_V t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " where 1=1 ";
				//+ " left join colorinfo c on t.colorCode=c.colorCode "; //RawMaterialIn_V 中已包含了'配料车间退回仓库'
		String periodOutSQLRaw = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity from raw_material_tran t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				//+ " left join colorinfo c on t.colorCode=c.colorCode "
				+" where t.TransType ='配料车间领用' and t.quantity>0  "; 
		
		String periodInSQLRawMix = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity from raw_material_tran t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				//+ " left join colorinfo c on t.colorCode=c.colorCode "
				+" where (t.TransType ='配料车间领用'  or t.TransType ='生产车间退回配料车间' or (t.TransType ='配料' and t.quantity>0 ))";
		String periodOutSQLRawMix = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity from RawMaterialMixOut_V t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " where 1=1 ";
				//+ " left join colorinfo c on t.colorCode=c.colorCode "
				//+" where ( t.TransType ='生产车间领用' or  t.TransType ='配料车间退回仓库' )";

		String periodInPackage = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity2,t.createDate from material_in_record t "
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " where (t.TransType ='采购入仓' or t.TransType ='外协包装物料回收' or t.TransType ='内部包装物料回收' or t.TransType ='盘点入仓-包装物料') and t.materialType='包装物料' ";
		String periodOutPackage = "select t.materialCode,m.materialName,'' as colorCode,'' as colorName,t.quantity2 from ProductMaterialOut_V t"
				+ " inner join materialinfo m on t.materialCode=m.materialCode "
				+ " where t.materialType='包装物料' ";
		List<String> materialTypeList = new ArrayList<String>();
		
		if (rectype.equals(InventoryConstant.Report_Type_PROD)||rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)){
			materialTypeList.add("成品");
		}else if (rectype.equals(InventoryConstant.Report_Type_SEMIPROD)||rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
			materialTypeList.add("半成品");
		}else if (rectype.equals(InventoryConstant.Report_Type_RAW)
				||rectype.equals(InventoryConstant.Report_Type_RAWMIX)){
			materialTypeList.add( "原料");
		}else if (rectype.equals(InventoryConstant.Report_Type_METAL)){
			materialTypeList.add( "五金件");
		}else if (rectype.equals(InventoryConstant.Report_Type_PACKAGE)){
			materialTypeList.add( "包装物料");
		}else if (rectype.equals(InventoryConstant.Report_Type_EMPFETCHSEMI)){
			materialTypeList.add("半成品");
			materialTypeList.add("成品");
		}else if (rectype.equals(InventoryConstant.Report_Type_EMPFETCHMETAL)){
			materialTypeList.add("五金件");
		}
		String splitString = "======";
		// last period inventory
		List<InventoryRecordModel> invRecList = this.getInventoryRecordList(rectype,null,null,materialcode,materialname,null);
		Map<String,InventoryRecordModel> resmap = new LinkedHashMap<String,InventoryRecordModel>();
		for (InventoryRecordModel inventoryRecordModel: invRecList){
			inventoryRecordModel.setLastBalance(inventoryRecordModel.getBalance());
			inventoryRecordModel.setPeriodIn(BigDecimal.ZERO);
			inventoryRecordModel.setPeriodOut(BigDecimal.ZERO);
			//inventoryRecordModel.setBalance(BigDecimal.ZERO);
			inventoryRecordModel.setRecordName("");
			inventoryRecordModel.setRemarks("");
			inventoryRecordModel.setStartDate(null);
			inventoryRecordModel.setEndDate(null);
			inventoryRecordModel.setBalanceDate(null);
			String key = null;
			if (rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
					|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
				key = inventoryRecordModel.getMaterialCode();
			}else{
				key = inventoryRecordModel.getMaterialCode() + splitString + inventoryRecordModel.getColorCode();
			}
			resmap.put(key,inventoryRecordModel);
		}
		StringBuilder sqlinrec = new StringBuilder("");
		StringBuilder sqloutrec = new StringBuilder("");
		List<String> transTypeNotInList = new ArrayList<String>();
		List<String> transTypeInList = new ArrayList<String>();

		if (rectype.equals(InventoryConstant.Report_Type_PROD)||rectype.equals(InventoryConstant.Report_Type_SEMIPROD)
				|| rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
				|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)
				|| rectype.equals(InventoryConstant.Report_Type_METAL)){
			sqlinrec.append(periodInSQL);
			sqloutrec.append(periodOutSQL);
			periodInParamMap.put("materialTypeList", materialTypeList);
			periodOutParamMap.put("materialTypeList", materialTypeList);
			
			transTypeNotInList.add(InventoryConstant.TransType_ProdIn_SubMat_SEMI);
			transTypeNotInList.add(InventoryConstant.TransType_ProdIn_Vendor_SubMat_SEMI);
			transTypeNotInList.add(InventoryConstant.TransType_ProdIn_SubMat_METAL);
			transTypeNotInList.add(InventoryConstant.TransType_ProdIn_Vendor_SubMat_METAL);
			periodInParamMap.put("transTypeNotInList", transTypeNotInList);
			
		}else if (rectype.equals(InventoryConstant.Report_Type_RAW)){
			sqlinrec.append(periodInSQLRaw);
			sqloutrec.append(periodOutSQLRaw);
		}else if (rectype.equals(InventoryConstant.Report_Type_PACKAGE)){
			sqlinrec.append(periodInPackage);
			sqloutrec.append(periodOutPackage);
		}else if (rectype.equals(InventoryConstant.Report_Type_RAWMIX)){
			sqlinrec.append(periodInSQLRawMix);
			sqloutrec.append(periodOutSQLRawMix);
		}else{
			sqlinrec.append(periodInSQLEmp);
			sqloutrec.append(periodOutSQLEmp);
			periodInParamMap.put("materialTypeList", materialTypeList);
			periodOutParamMap.put("materialTypeList", materialTypeList);
			
			if (rectype.equals(InventoryConstant.Report_Type_EMPFETCHSEMI)){
				transTypeInList.add(InventoryConstant.TransType_ProdIn_SubMat_SEMI);
				transTypeInList.add(InventoryConstant.TransType_ProdIn_Vendor_SubMat_SEMI);
				transTypeInList.add(InventoryConstant.TransType_MatIn_VendorSemiProd);
			}else{
				transTypeInList.add(InventoryConstant.TransType_ProdIn_SubMat_METAL);
				transTypeInList.add( InventoryConstant.TransType_ProdIn_Vendor_SubMat_METAL);
				//TransType_MatIn_VendorMetal
			}
			periodOutParamMap.put("transTypeInList", transTypeInList);
		}
		
		if (begindate!=null){
			sqlinrec.append( " and t.createDate >=:begindate ");
			sqloutrec.append( " and t.createDate >=:begindate ");
			periodInParamMap.put("begindate", begindate);
			periodOutParamMap.put("begindate", begindate);
		}
		if (enddate!=null){
			sqlinrec.append(" and t.createDate <=:enddate ");
			sqloutrec.append( " and t.createDate <=:enddate ");
			periodInParamMap.put("enddate", enddate);
			periodOutParamMap.put("enddate", enddate);
		}
		
		if ( !StringUtil.isNullOrBlank(materialname)){
			sqlinrec.append(" and m.materialName like :materialname ");
			sqloutrec.append(" and m.materialName like :materialname ");
			periodInParamMap.put("materialname", materialname+"%");
			periodOutParamMap.put("materialname", materialname+"%");
		}
		if ( !StringUtil.isNullOrBlank(materialcode)){
			sqlinrec.append(" and t.materialCode like :materialcode ");
			sqloutrec.append(" and t.materialCode like :materialcode ");
			periodInParamMap.put("materialcode", materialcode+"%");
			periodOutParamMap.put("materialcode", materialcode+"%");
		}
		
		sqlinrec.append(" order by t.materialCode,t.colorCode");
		sqloutrec.append(" order by t.materialCode,t.colorCode");
		
		List<Object[]> matInList = utilsDao.findBySql(sqlinrec.toString(),periodInParamMap);
		for (Object[] objects: matInList){
			String materialCode = objects[0].toString();
			String materialName = objects[1].toString();
			String colorCode = objects[2]==null?"":objects[2].toString();
			String colorName = objects[3]==null?"":objects[3].toString();
			String key = null;
			if (rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
				|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
				key = materialCode;
			}else{
				key = materialCode + splitString + colorCode;
			}
			BigDecimal perioldIn = (BigDecimal)objects[4];
			InventoryRecordModel inventoryRecordModel = resmap.get(key);
			if (inventoryRecordModel != null){
				inventoryRecordModel.setPeriodIn(inventoryRecordModel.getPeriodIn().add(perioldIn));
				inventoryRecordModel.setBalance(inventoryRecordModel.getBalance().add(perioldIn));
			}else{
				inventoryRecordModel =  new InventoryRecordModel();
				inventoryRecordModel.setMaterialCode(materialCode);
				if (rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
						|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
					inventoryRecordModel.setColorCode("");
					inventoryRecordModel.setColorName("");
				}else{
					inventoryRecordModel.setColorCode(colorCode);
					inventoryRecordModel.setColorName(colorName);
				}
				inventoryRecordModel.setMaterialName(materialName);
				inventoryRecordModel.setLastBalance(BigDecimal.ZERO);
				inventoryRecordModel.setPeriodIn(perioldIn);
				inventoryRecordModel.setPeriodOut(BigDecimal.ZERO);
				inventoryRecordModel.setBalance(perioldIn);
				resmap.put(key, inventoryRecordModel);
			}
		}
		List<Object[]> matOutList = utilsDao.findBySql(sqloutrec.toString(),periodOutParamMap);
		for (Object[] objects: matOutList){
			String materialCode = objects[0].toString();
			String materialName = objects[1].toString();
			String colorCode = objects[2]==null?"":objects[2].toString();
			String colorName = objects[3]==null?"":objects[3].toString();
			String key = null;
			if (rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
				|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
				key = materialCode;
			}else{
				key = materialCode + splitString + colorCode;
			}
			BigDecimal periodOut = (BigDecimal)objects[4];
			InventoryRecordModel inventoryRecordModel = resmap.get(key);
			if (inventoryRecordModel != null){
				inventoryRecordModel.setPeriodOut(inventoryRecordModel.getPeriodOut().add(periodOut));
				inventoryRecordModel.setBalance(inventoryRecordModel.getBalance().subtract(periodOut));
			}else{
				inventoryRecordModel =  new InventoryRecordModel();
				inventoryRecordModel.setMaterialCode(materialCode);
				if (rectype.equals(InventoryConstant.Report_Type_PROD_NOCOLOR)
						|| rectype.equals(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR)){
					inventoryRecordModel.setColorCode("");
					inventoryRecordModel.setColorName("");
				}else{
					inventoryRecordModel.setColorCode(colorCode);
					inventoryRecordModel.setColorName(colorName);
				}
				inventoryRecordModel.setMaterialName(materialName);
				inventoryRecordModel.setLastBalance(BigDecimal.ZERO);
				inventoryRecordModel.setPeriodIn(BigDecimal.ZERO);
				inventoryRecordModel.setPeriodOut(periodOut);
				inventoryRecordModel.setBalance(periodOut.negate());
				resmap.put(key, inventoryRecordModel);
			}
		}
		List<InventoryRecordModel> resList = null;
		if (page!=null){
			 resList = new ArrayList<InventoryRecordModel>();
			List<InventoryRecordModel> allList = new ArrayList<InventoryRecordModel>(resmap.values());
			int i=page.getPageIndex()*page.getPageSize();
			while (i<(page.getPageIndex()+1)*page.getPageSize() && i<allList.size()){
				resList.add(allList.get(i));
				i++;
			}
			page.setTotalRecordSize(Long.valueOf(allList.size()));
		}else{
			resList = new ArrayList<InventoryRecordModel>(resmap.values());
		}
		return resList;
	}
	private String checkRecordNameDuplicate(String rectype,String recordName){
		String res = recordName ;
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("recordType", rectype);
		
		List<String> matInList = utilsDao.findBySql("select distinct recordName from inventory_record where recordType=:recordType and isnewest=1",
				paramMap);
		if (matInList!=null && matInList.size()>0){
			String existRecName = matInList.get(0).toString();
			if (existRecName.startsWith(recordName)){
				String[] split = existRecName.split("_");
				if(split.length == 1){
					res = recordName + "_2";
				}else if(split.length > 1){
					res = recordName + "_" + String.valueOf(Integer.valueOf(split[1])+1);
				}
			}
		}
		return res;
	}
	/**
	 * matList 是界面上查询到的实时进销存记录
	 */
	@Override
	@Transactional
	public void saveInventoryRecord(String rectype, String begindate, String enddate)throws Exception{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    Date dtbegin = null;
	    Date dtend = null;
	    if (!StringUtil.isNullOrBlank(begindate))
	    	dtbegin =sdf.parse(begindate+" 00:00:00");
	    if (!StringUtil.isNullOrBlank(enddate))
	    	dtend = sdf.parse(enddate+" 23:59:59");
	    String recordName = enddate.substring(0, 7);
	    recordName = checkRecordNameDuplicate(rectype,recordName);
	    
	    Date balDate = new Date();
		//再查询一次，以防有进出仓记录在这段时间发生
		List<InventoryRecordModel> allList = queryInoutSum(rectype,dtbegin,dtend,null,null,null);
		List<InventoryRecord> invList = new ArrayList<InventoryRecord>();
		for (InventoryRecordModel reportModel: allList) {
			InventoryRecord inrec = new InventoryRecord();
			inrec.setMaterialCode(reportModel.getMaterialCode());
			inrec.setRecordName(recordName);
			inrec.setRecordType(rectype);
			inrec.setLastBalance(reportModel.getLastBalance());
			inrec.setPeriodIn(reportModel.getPeriodIn());
			inrec.setPeriodOut(reportModel.getPeriodOut());
			inrec.setBalance(reportModel.getBalance());
			inrec.setColorCode(reportModel.getColorCode());
			inrec.setStartDate(dtbegin);
			inrec.setEndDate(dtend);
			inrec.setBalanceDate(balDate);
			inrec.setRemarks("");
			inrec.setIsnewest(1);
			invList.add(inrec);
		}
	    //update all InventoryRecord TO old first
		utilsDao.execute("update InventoryRecord set isnewest=0 where recordType=? ", rectype);
		
		for (InventoryRecord inrec : invList){
			utilsDao.insert(inrec);
		}

	}
	//生成合格证记录
	@Override
	@Transactional
	public String genQulifiedRecord(String ordernumber){
		StringBuilder errorMsg = new StringBuilder("");
		//delete old record first
		MaterialQuanliQuery mqQuery = new MaterialQuanliQuery();
		mqQuery.createCriteria().andOrdernumberEqualTo(ordernumber);
		materialQuanliMapper.deleteByExample(mqQuery);
		
		SalesOrderDetailQuery querySaleDetail = new SalesOrderDetailQuery();
		querySaleDetail.createCriteria().andOrdernumberEqualTo(ordernumber);
		List<SalesOrderDetail> orderDetailList = 
			 	salesOrderDetailMapper.selectByExample(querySaleDetail);
		for (SalesOrderDetail salesOrderDetail: orderDetailList) {
			if (salesOrderDetail.getMaterialcode()==null || salesOrderDetail.getColorcode()==null){
				continue;
			}
			if (salesOrderDetail.getQuantityperpack()==null || salesOrderDetail.getQuantityperpack().compareTo(BigDecimal.ZERO) == 0){
				errorMsg.append("物料【"+salesOrderDetail.getMaterialcode()+"】的每包数量为0或未输入。");
				continue;
			}
		}
		if (errorMsg.toString().equals("")){
			for (SalesOrderDetail salesOrderDetail: orderDetailList) {
				//divideToIntegralValue 除后只取整数
				int count = salesOrderDetail.getQuantity().divideToIntegralValue(
						salesOrderDetail.getQuantityperpack()).intValue();
				Long remainder = salesOrderDetail.getQuantity().remainder(
						salesOrderDetail.getQuantityperpack()).longValue();
				for (int i = 0;i<=count;i++){
					//以防能整除时仍然插入0的记录
					if (i==count && remainder.intValue()==0 )
						break; 
					MaterialQuanli record = new MaterialQuanli();
					record.setOrdernumber(ordernumber);
					record.setColorcode(salesOrderDetail.getColorcode());
					record.setMaterialcode(salesOrderDetail.getMaterialcode());
					if (i < count){
						record.setQuantity(salesOrderDetail.getQuantityperpack().longValue());
					}else{
						record.setQuantity(remainder);
					}
					materialQuanliMapper.insertSelective(record);
				}
			}
		}
		return errorMsg.toString();
	}
	@Override
	@Transactional(readOnly = true)
	public Collection<EmpWithdrawDetail> genEmpWithdrawDetail(String matType,
			String userID, Date fromDate, Date toDate,String transtype) {
		LinkedHashMap<String,EmpWithdrawDetail> resMap = new LinkedHashMap<String,EmpWithdrawDetail>();
		
		Map<String,Object> params = new HashMap<String,Object>();
		StringBuilder sql = new StringBuilder("select t.USERID,t.USERNAME,t.materialCode,t.materialName,")
			.append("t.inqty,t.outqty,t.TransDate from empWithdrawDetail_V t where 1=1 ");
		
		if (StringUtils.isNotBlank(matType)){
			if (matType.equals("五金件")){
				sql.append(" and t.materialType = '五金件' ");
			}else{
				sql.append(" and t.materialType in ('成品','半成品') ");
			}
		}
		if (StringUtils.isNotBlank(transtype)){
			sql.append(" and t.TransType like :transtype ");
			params.put("transtype", transtype+"%");
		}else{
			sql.append(" and t.TransType like '外协%' ");
		}
		
		if (StringUtils.isNotBlank(userID)){
			sql.append(" and t.userid =:userid ");
			params.put("userid", userID);
		}
		if (fromDate != null){
			sql.append(" and t.TransDate >= :fromDate ");
			params.put("fromDate", fromDate);
		}
		if (toDate != null){
			sql.append(" and t.TransDate <= :toDate ");
			params.put("toDate", toDate);
		}
		sql.append(" order by t.USERID,t.TransDate,t.materialCode asc,t.inouttype desc ");
		List<Object[]> foundRes = utilsDao.findBySql(sql.toString(), params);
		
		for (Object[] objects : foundRes){
			
			EmpWithdrawDetail detail = new EmpWithdrawDetail();
			detail.setUserID(objects[0]==null?"":objects[0].toString());
			detail.setUserName(objects[1]==null?"":objects[1].toString());
			detail.setMaterialCode(objects[2]==null?"":objects[2].toString());
			detail.setMaterialName(objects[3]==null?"":objects[3].toString());
			detail.setTransDate(objects[6]==null?"":objects[6].toString());
			detail.setTotalInAmount(objects[4]==null?BigDecimal.ZERO:(BigDecimal)objects[4] );
			detail.setTotalOutAmount(objects[5]==null?BigDecimal.ZERO:(BigDecimal)objects[5] );
			detail.setTotalSumAmount(detail.getTotalOutAmount().subtract(detail.getTotalInAmount()));
			String key = detail.getUserID()+"-"+detail.getMaterialCode() +"-"+detail.getTransDate();
			if (resMap.get(key) == null){
				resMap.put(key, detail);
			}else{
				EmpWithdrawDetail oldDetail = resMap.get(key);
				oldDetail.setTotalInAmount( oldDetail.getTotalInAmount().add(detail.getTotalInAmount()));
				oldDetail.setTotalOutAmount( oldDetail.getTotalOutAmount().add(detail.getTotalOutAmount()));
				oldDetail.setTotalSumAmount( oldDetail.getTotalSumAmount().add(detail.getTotalSumAmount()));
			}
		}
		return resMap.values();
	}
	@Override
	@Transactional(readOnly = true)
	public List<InventoryRecordModel> getInventoryRecordList(String recordType,String recordName,String remarks,String materialcode,String materialname,Page page) {
		Map<String,Object> parmap = new HashMap<String,Object>();
		StringBuilder selecthql = new StringBuilder("select t.recordName,t.lastBalance,t.periodIn,t.periodOut,t.balance,t.startDate,t.endDate,")//0-6
		.append(" t.remarks,t.materialCode,m.materialname,t.colorCode,c.colorname");
		StringBuilder counthql = new StringBuilder("select count(t.id) ");
		StringBuilder hql = new StringBuilder(" from inventory_record t inner join MaterialInfo m  on t.materialCode=m.materialcode ")
				.append(" left join ColorInfo c on t.colorCode=c.colorcode ")
				.append(" where t.recordType=:rectype ");//
				
		if (!StringUtils.isEmpty(recordName)){
			hql.append(" and t.recordName=:recordName ");
			parmap.put("recordName", recordName);
		}else{
			if ( !StringUtil.isNullOrBlank(remarks)){
				hql.append(" and t.remarks = :remarks ");
				parmap.put("remarks", remarks);
			}else{
				hql.append(" and t.isnewest=1");
			}
		}

		if ( !StringUtil.isNullOrBlank(materialname)){
			hql.append(" and m.materialName like :materialname ");
			parmap.put("materialname", materialname+"%");
		}
		if ( !StringUtil.isNullOrBlank(materialcode)){
			hql.append(" and t.materialCode like :materialcode ");
			parmap.put("materialcode", materialcode+"%");
		}
		selecthql.append(hql).append(" order by t.materialCode asc,t.colorCode asc");
		counthql.append(hql);
		parmap.put("rectype", recordType);
		List<Object[]> recordlist = null;
		if (page==null){
			recordlist = utilsDao.findBySql(selecthql.toString(),parmap);
		}else{
			recordlist = utilsDao.findBySql(selecthql.toString(),page.getPageIndex()*page.getPageSize(),page.getPageSize(),
				parmap);
			page.setTotalRecordSize( utilsDao.countBySql(counthql.toString(), parmap));
		}
		List<InventoryRecordModel> resList = new ArrayList<InventoryRecordModel>();
		for (Object[] inventoryRecord:recordlist) {
			InventoryRecordModel model= new InventoryRecordModel();
			
			model.setRecordName(inventoryRecord[0]==null?"":inventoryRecord[0].toString());
			model.setLastBalance( inventoryRecord[1]==null?BigDecimal.ZERO:(BigDecimal)inventoryRecord[1]);
			model.setPeriodIn(inventoryRecord[2]==null?BigDecimal.ZERO:(BigDecimal)inventoryRecord[2]);
			model.setPeriodOut(inventoryRecord[3]==null?BigDecimal.ZERO:(BigDecimal)inventoryRecord[3]);
			model.setBalance(inventoryRecord[4]==null?BigDecimal.ZERO:(BigDecimal)inventoryRecord[4]);
			model.setStartDate(inventoryRecord[5]==null?null:(Date)inventoryRecord[5]);
			model.setEndDate(inventoryRecord[6]==null?null:(Date)inventoryRecord[6]);
			model.setRemarks(inventoryRecord[7]==null?"":inventoryRecord[7].toString());
			model.setMaterialCode(inventoryRecord[8]==null?"":inventoryRecord[8].toString());
			model.setMaterialName(inventoryRecord[9]==null?"":inventoryRecord[9].toString());
			model.setColorCode( inventoryRecord[10]==null?"":inventoryRecord[10].toString());
			model.setColorName(inventoryRecord[11]==null?"":inventoryRecord[11].toString());
			model.setRecordType(recordType);
			resList.add(model);
		}
		return resList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<QulifiedRecordModel> getQulifiedRecordModel(String ordernumber) {
		String sql = "select t.OrderNumber,t.materialCode,t.colorCode,t.quantity,m.materialName,o.CustOrderNumber,c.colorName"
				+" from material_quanlification t,materialinfo m,sales_order_master o,colorinfo c "
				+" where t.OrderNumber=o.OrderNumber and t.materialCode=m.materialCode and t.colorCode=c.colorCode"
				+" and t.OrderNumber=:ordernumber order by t.id asc";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("ordernumber", ordernumber);
		List<Object[]> objList = utilsDao.findBySql(sql, paramMap);
		
		List<QulifiedRecordModel> quanliList = new ArrayList<QulifiedRecordModel>();
		for (Object[] rusult :objList) {
			QulifiedRecordModel model = new QulifiedRecordModel();
			model.setOrdernumber(rusult[0]==null?"":rusult[0].toString());
			model.setQuantity( rusult[3]==null?"":rusult[3].toString());
			model.setMaterialcode(rusult[1]==null?"":rusult[1].toString());
			model.setMaterialname(rusult[4]==null?"":rusult[4].toString());
			model.setCustordernumber(rusult[5]==null?"":rusult[5].toString());
			model.setColorname(rusult[6]==null?"":rusult[6].toString());
			quanliList.add(model);
		}
		return quanliList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<String> getReportMonths(String reptype){
		String sql = "select distinct recordName from inventory_record where recordType=:recordType order by balanceDate desc";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("recordType", reptype);
		List<String> objList = utilsDao.findBySql(sql, paramMap);
		return objList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<InventoryRecordModel> queryPruductInRecords(String userID,
			Date begindate, Date enddate) {
		StringBuilder sql = new StringBuilder("select r.materialCode, sum(r.quantity) as quantity ")
			.append("from material_in_record r ")
			.append(" where r.transtype in ('生产入仓-成品','生产入仓-半成品') ");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		if (StringUtils.isNotBlank(userID)){
			sql.append(" and r.userid = :userID ");
			paramMap.put("userID", userID);
		}
		if (begindate != null){
			sql.append(" and  r.TransDate >= :begindate ");
			paramMap.put("begindate", begindate);
		}
		if (enddate != null){
			sql.append(" and r.TransDate <= :enddate ");
			paramMap.put("enddate", enddate);
		}
		sql.append(" group by r.materialCode");
		List<InventoryRecordModel> resList = new ArrayList<InventoryRecordModel>();
		List<Object[]> objList = utilsDao.findBySql(sql.toString(), paramMap);
		for(Object[] objs:objList){
			InventoryRecordModel invRecordModel = new InventoryRecordModel();
			invRecordModel.setMaterialCode(objs[1]==null?"":objs[0].toString());
			invRecordModel.setBalance(objs[1]==null?BigDecimal.ZERO:(BigDecimal)objs[1]);
			resList.add(invRecordModel);
		}
		return resList;
	}
	@Override
	@Transactional(readOnly = true)
	public List<InventoryRecordModel> compareWithInv(String rectype, Date begindate, Date enddate,String materialcode,String materialname) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		List<InventoryRecordModel> invRecList = this.queryInoutSum(rectype, begindate, enddate, null,null,null);
		StringBuilder sql = new StringBuilder("select ");
		boolean queryColor = false;
		
		if (InventoryConstant.Report_Type_RAWMIX.equals(rectype)){
    		sql.append(" mixQty ");
    	}else if (InventoryConstant.Report_Type_EMPFETCHSEMI.equals(rectype) 
    			||InventoryConstant.Report_Type_EMPFETCHMETAL.equals(rectype) ){
    		sql.append(" outsourceQty ");
    	}else if (InventoryConstant.Report_Type_PROD_NOCOLOR.equals(rectype) 
    			||InventoryConstant.Report_Type_SEMIPROD_NOCOLOR.equals(rectype) ){
    		sql.append(" sum(inventoryQty) ");
    	}else{
    		sql.append(" inventoryQty ");
    	}
		sql.append(" from inventoryinfosum where materialcode=:materialcode");
		
		
		if (InventoryConstant.Report_Type_PROD.equals(rectype )
				|| 	InventoryConstant.Report_Type_SEMIPROD.equals(rectype)
				|| InventoryConstant.Report_Type_EMPFETCHSEMI.equals(rectype)){
			queryColor  = true;
		}
		if (queryColor){
			sql.append(" and colorcode = :colorcode ");
		}
		List<InventoryRecordModel> resultList = new ArrayList<InventoryRecordModel>();
		for(InventoryRecordModel inventoryRecord:invRecList){
			paramMap.put("materialcode", inventoryRecord.getMaterialCode());
			if(queryColor){
				paramMap.put("colorcode", inventoryRecord.getColorCode());
			}
			List<BigDecimal> objList = utilsDao.findBySql(sql.toString(), paramMap);
			if (objList.size()<=0){
				continue;
			}else{
				BigDecimal qty =objList.get(0);
				if (qty.compareTo(inventoryRecord.getBalance())!=0){
					inventoryRecord.setCompareTargetQty(qty);
					resultList.add(inventoryRecord);
				}
			}
			paramMap.clear();
		}
		return resultList;
	}
}
