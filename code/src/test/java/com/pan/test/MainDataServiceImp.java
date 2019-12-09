package com.poons.salesorder.service.imp;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poons.frame.base.dao.UtilsDao;
import com.poons.frame.utils.StringUtil;
import com.poons.salesorder.po.BomInfo;
import com.poons.salesorder.po.ColorInfo;
import com.poons.salesorder.po.CustomInfo;
import com.poons.salesorder.po.Employee;
import com.poons.salesorder.po.InventoryInfo;
import com.poons.salesorder.po.InventoryRecord;
import com.poons.salesorder.po.MaterialInRecord;
import com.poons.salesorder.po.MaterialInfo;
import com.poons.salesorder.po.Prodmodelinfo;
import com.poons.salesorder.service.InventoryInfoService;
import com.poons.salesorder.service.MainDataService;
import com.poons.salesorder.utils.InventoryConstant;
import com.poons.salesorder.service.ReportService;
import com.poons.salesorder.model.InventoryRecordModel;

@Service("mainDataService")
public class MainDataServiceImp implements MainDataService {

	@Autowired
	private UtilsDao utilsDao;
	@Autowired
	private InventoryInfoService inventoryInfoService;
	
	@Autowired
	private ReportService reportService;
	@Override
    @Transactional
	public String importInventoryData(File file,boolean isAdjust){
		Workbook workbook = null;
		String res = null;
		try{
			workbook = Workbook.getWorkbook(file);
			
			Sheet shmat = null;
			if (isAdjust){// isAdjust 非初始庫存 
				shmat = workbook.getSheet(6);
			}else{
				shmat = workbook.getSheet(5);
			}
			List<InventoryInfo> inventoryInfoList = new ArrayList<InventoryInfo>();
			StringBuilder sb = new StringBuilder("");
			res = getInventoryList(sb,shmat,inventoryInfoList);
			if (!res.equals(""))
                return res;			
			if (!res.toCharArray().equals("")){
				
				if (!isAdjust){
					utilsDao.execSql("truncate inventoryinfo");
					utilsDao.execSql("truncate inventoryinfosum");
					utilsDao.execSql("truncate inventoryInfo_TRC");
					utilsDao.execSql("truncate inventoryinfosum_TRC");
				}
				String recordNumber = "";
				if (isAdjust){
					recordNumber = "adjustInventory";
				}else{
					recordNumber = "initialInventory";
				}
				for (InventoryInfo inventoryInfo:inventoryInfoList){
					inventoryInfoService.updateInventory(inventoryInfo, recordNumber);
					if(isAdjust){
						//为了进销存报表有记录，必须插入进出仓记录
						insertMaterialInRecord(inventoryInfo, recordNumber);
					}
				}
			}
			if (!isAdjust){
				insertInventoryRecord(inventoryInfoList);
				generateInventoryDataForNoColor();
			}
			return res;
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			workbook.close();
		}
	}
	@Override
	@Transactional
	public void generateInventoryDataForNoColor() {
		utilsDao.execute("delete from InventoryRecord where (recordType='prod_nocolor' or recordType='semiprod_nocolor') and remarks ='initialInventory'");
		List<List<InventoryRecordModel>> recListOfList = new ArrayList<List<InventoryRecordModel>>();
		
		recListOfList.add(reportService.getInventoryRecordList(InventoryConstant.Report_Type_PROD,null,"initialInventory",null,null,null));
		recListOfList.add(reportService.getInventoryRecordList(InventoryConstant.Report_Type_SEMIPROD,null,"initialInventory",null,null,null));
		Date now = new Date();
		Map<String,InventoryRecord> resmap = new LinkedHashMap<String,InventoryRecord>();
		for (List<InventoryRecordModel> invRecList: recListOfList){
			
			for (InventoryRecordModel inventoryRecordModel: invRecList){
				String key = inventoryRecordModel.getMaterialCode();
				InventoryRecord inrec = resmap.get(key);
				if (inrec==null){
					inrec = new InventoryRecord();
					inrec.setMaterialCode(inventoryRecordModel.getMaterialCode());

					inrec.setRecordName(inventoryRecordModel.getRecordName());
					inrec.setLastBalance(BigDecimal.ZERO);
					inrec.setPeriodIn(BigDecimal.ZERO);
					inrec.setPeriodOut(BigDecimal.ZERO);
					inrec.setBalance(inventoryRecordModel.getBalance());
					inrec.setStartDate(now);
					inrec.setEndDate(now);
					inrec.setBalanceDate(now);
					inrec.setRemarks("initialInventory");
					inrec.setIsnewest(1);
					if (inventoryRecordModel.getRecordType().equals(InventoryConstant.Report_Type_PROD)){
						inrec.setRecordType(InventoryConstant.Report_Type_PROD_NOCOLOR);
					}else{
						inrec.setRecordType(InventoryConstant.Report_Type_SEMIPROD_NOCOLOR);
					}
					inrec.setColorCode("");
					resmap.put(key, inrec);
				}else{
					inrec.setBalance(inrec.getBalance().add(inventoryRecordModel.getBalance()));
					//resmap.put(key, inrec);
				}
			}
		}
		for (InventoryRecord inrec : resmap.values()){
			utilsDao.insert(inrec);
		}
	}
	private void insertMaterialInRecord(InventoryInfo inventoryInfo,String recordNumber){
		MaterialInRecord materialInRecord = new MaterialInRecord();
		materialInRecord.setMaterialcode(inventoryInfo.getMaterialcode());
		materialInRecord.setColorcode(inventoryInfo.getColorcode());
		materialInRecord.setMaterialtype(inventoryInfo.getMaterialtype());
		//materialInRecord.setRecordnumber(recordNumber);
		materialInRecord.setUnitcode("公斤");
		materialInRecord.setUnitcode2("只");
		if (inventoryInfo.getMaterialtype().equals("原料")){
			materialInRecord.setQuantity(inventoryInfo.getQuantity());
			materialInRecord.setQuantity2(BigDecimal.ZERO);
		}else{
			materialInRecord.setQuantity(BigDecimal.ZERO);
			materialInRecord.setQuantity2(inventoryInfo.getQuantity());
		}
		
		materialInRecord.setTransdate(new Date());
		materialInRecord.setTranstype("盘点入仓-"+inventoryInfo.getMaterialtype());
		materialInRecord.setOrdernumber("");
		materialInRecord.setWarehouse("adjust inventory");
		materialInRecord.setUserid("");
		materialInRecord.setUnitprice(BigDecimal.ZERO);
		materialInRecord.setTotalprice(BigDecimal.ZERO);
		materialInRecord.setRecordstatus(0);
		
		utilsDao.insert(materialInRecord);
	}
	
	private void insertInventoryRecord(List<InventoryInfo> inventoryInfoList){
		
		utilsDao.execute("update InventoryRecord set isnewest=0 where recordType='prod' ");
		utilsDao.execute("update InventoryRecord set isnewest=0 where recordType='semiprod' ");
		utilsDao.execute("update InventoryRecord set isnewest=0 where recordType='metal' ");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String recordName = sdf.format(now).substring(0, 7);
	    
		for (InventoryInfo inventoryInfo:inventoryInfoList){
			InventoryRecord inrec = new InventoryRecord();
			inrec.setMaterialCode(inventoryInfo.getMaterialcode());
			String rectype = "";
			if (inventoryInfo.getMaterialtype().equals("成品")){
				rectype = InventoryConstant.Report_Type_PROD;
			}else if (inventoryInfo.getMaterialtype().equals("半成品")){
				rectype = InventoryConstant.Report_Type_SEMIPROD;
			}else if (inventoryInfo.getMaterialtype().equals("五金件")){
				rectype = InventoryConstant.Report_Type_METAL;
			}else if (inventoryInfo.getMaterialtype().equals("包装物料")){
				rectype = InventoryConstant.Report_Type_PACKAGE;
			}else if (inventoryInfo.getMaterialtype().equals("原料")){
				rectype = InventoryConstant.Report_Type_RAW;
			}
					
			inrec.setRecordName(recordName);
			inrec.setRecordType(rectype);
			inrec.setLastBalance(BigDecimal.ZERO);
			inrec.setPeriodIn(BigDecimal.ZERO);
			inrec.setPeriodOut(BigDecimal.ZERO);
			inrec.setBalance(inventoryInfo.getQuantity());
			inrec.setStartDate(now);
			inrec.setEndDate(now);
			inrec.setBalanceDate(now);
			inrec.setRemarks("initialInventory");
            inrec.setColorCode(inventoryInfo.getColorcode());
			inrec.setIsnewest(1);
			utilsDao.insert(inrec);
		}
	}
	private String getInventoryList(StringBuilder res,Sheet sheet,List<InventoryInfo> inventoryInfoList){
		
		List<ColorInfo> colorList = utilsDao.find("from ColorInfo");
		Map<String,String> colorMap = new HashMap<String,String>();
		for (ColorInfo colorInfo:colorList){
			colorMap.put(colorInfo.getColorcode(), colorInfo.getColorname());
		}
		
		int gapcount = 0;
		int gaptop = 10;//over 10 gap row,suppose 
		for (int i=1; gapcount < gaptop && i < sheet.getRows(); i++){
			Cell cela = sheet.getCell(0,i);
			if (cela.getContents().trim().equals("")
					||cela.getContents().trim().equals("原料")
					||cela.getContents().trim().equals("半成品")){
				gapcount++;
				continue;
			}
			String quantity = sheet.getCell(5,i).getContents().trim();
			String colorCode = sheet.getCell(2,i).getContents().trim();//colorMap.get(sheet.getCell(2,i).getContents().trim());
			String materialcode = sheet.getCell(0,i).getContents().trim();
			String materialtype = sheet.getCell(4,i).getContents().trim();
			
			
			List<MaterialInfo> materialList = utilsDao.find("from MaterialInfo t where t.materialcode=?",materialcode);
			MaterialInfo materialInfo = null;
			if (materialList.size() > 0) {
				materialInfo = materialList.get(0);
				materialtype = materialInfo.getMaterialtype();
			}else{
				 res.append("物料编码【").append(materialcode).append("】在基础资料中不存在！");
				 continue;
			}
			if (quantity.equals("") ){
				continue;
			}
			gapcount = 0;
			if (materialtype.equals("半成品")||materialtype.equals("成品")){
				
				if (colorCode.equals("不分色")){
					colorCode = "";
				}else if (colorCode.equals("")){
					res.append("第【").append(i+1).append("】行，物料【").append(materialcode).append("】的颜色没有填写。");
				}else if (colorMap.get(colorCode) == null){
				    res.append("顏色编码【").append(colorCode).append("】在基础资料中不存在！");
				    continue;
				}
			}
			InventoryInfo inventoryInfo = new InventoryInfo();
			inventoryInfo.setMaterialcode( materialcode );
			inventoryInfo.setMaterialtype( materialtype ); 
			inventoryInfo.setColorcode(colorCode==null?"":colorCode);
			inventoryInfo.setQuantitytype("仓库库存");
			inventoryInfo.setWarehouse(inventoryInfo.getMaterialtype()+"仓库");
			if (materialtype.equals("原料")){
				inventoryInfo.setUnitcode("公斤");
			}else{
				inventoryInfo.setUnitcode("只");
			}
			inventoryInfo.setUserid("");
			inventoryInfo.setQuantity(new BigDecimal(quantity));
			inventoryInfo.setNotoccupyqty(inventoryInfo.getQuantity());
			inventoryInfoList.add(inventoryInfo);
		}
		return res.toString();
	}

	@Override
    @Transactional
	public String importBasicData(File file)  {
		Workbook workbook = null;
		String res = null;
		try{
			workbook = Workbook.getWorkbook(file);
			Sheet shmat = workbook.getSheet(0);
			Sheet shcolor = workbook.getSheet(1);
			Sheet shclient = workbook.getSheet(2);
			Sheet shbom = workbook.getSheet(3);
			Sheet shemp = workbook.getSheet(4);
			
			List<MaterialInfo> matList = new ArrayList<MaterialInfo>();
			List<Prodmodelinfo> prodmodelList = new ArrayList<Prodmodelinfo>();
			Map<String,MaterialInfo> matKeyMap = new HashMap<String,MaterialInfo>();
			StringBuilder resSb = new StringBuilder("");
			res = getMaterialList(resSb,shmat,matList,prodmodelList,matKeyMap);
			/*if (!res.equals(""))
				return res;*/
			/*System.out.println("matList size:"+matKeyMap.keySet().size());
			System.out.println("mat checkMap size:"+matKeyMap.keySet().size());*/
			List<BomInfo> bomList = new ArrayList<BomInfo>();
			res = getBomList(resSb,shbom,bomList,matList,matKeyMap);
			if (!res.equals(""))
				return res;
			List<ColorInfo> colorList = new ArrayList<ColorInfo>();
			res = getColorList(shcolor,colorList);
			if (!res.equals(""))
				return res;
			List<Employee> empList = new ArrayList<Employee>();
			res = getEmpList(shemp,empList);
			if (!res.equals(""))
				return res;
			List<CustomInfo> customList = new ArrayList<CustomInfo>();
			res = getCustomList(shclient,customList);
			if (!res.equals(""))
				return res;
			
			// clear old and import new
			utilsDao.execSql("truncate materialinfo", null);
			utilsDao.insert(matList);
			/*for (Iterator iterator = matList.iterator(); iterator.hasNext();) {
				MaterialInfo materialInfo = (MaterialInfo) iterator.next();
				System.out.println(materialInfo.getMaterialcode());
				System.out.println(materialInfo.getMaterialname());
				System.out.println(materialInfo.getMaterialtype());
			}*/
			utilsDao.execSql("truncate prodmodelinfo", null);
			utilsDao.insert(prodmodelList);
			/*for (Iterator iterator = prodmodelList.iterator(); iterator.hasNext();) {
				Prodmodelinfo prodmodel = (Prodmodelinfo) iterator.next();
			}*/
			utilsDao.execSql("truncate bominfo", null);
			utilsDao.insert(bomList);
			/*System.out.println("bomlist size:"+bomList.size());
			for (Iterator iterator = bomList.iterator(); iterator.hasNext();) {
				BomInfo bominfo = (BomInfo) iterator.next();
				System.out.println("getParentmaterialcode:"+bominfo.getParentmaterialcode());
				System.out.println("getSubmaterialcode:"+bominfo.getSubmaterialcode());
				System.out.println("getSubmaterialunit:"+bominfo.getSubmaterialunit());
				System.out.println("getSubmaterialquantity:"+bominfo.getSubmaterialquantity());
				System.out.println("getMaterialtype:"+bominfo.getMaterialtype());
			}*/
			
			utilsDao.execSql("truncate colorinfo", null);
			utilsDao.insert(colorList);
			/*System.out.println("colorList size:"+colorList.size());
			for (Iterator iterator = colorList.iterator(); iterator.hasNext();) {
				ColorInfo colorinfo = (ColorInfo) iterator.next();
				System.out.println("getColorcode:"+colorinfo.getColorcode());
				System.out.println("getColorname:"+colorinfo.getColorname());
				System.out.println("getCustcode:"+colorinfo.getCustcode());
			}*/
			utilsDao.execSql("truncate om_employee", null);
			utilsDao.insert(empList);
			/*System.out.println("empList size:"+empList.size());
			for (Iterator iterator = empList.iterator(); iterator.hasNext();) {
				Employee emp = (Employee) iterator.next();
				System.out.println("getUserid:"+emp.getUserid());
				System.out.println("getUsername:"+emp.getUsername());
				System.out.println("getCardtype:"+emp.getCardtype());
			}*/
			utilsDao.execSql("truncate custominfo", null);
			utilsDao.insert(customList);
			/*System.out.println("customList size:"+customList.size());
			for (Iterator iterator = customList.iterator(); iterator.hasNext();) {
				CustomInfo cust = (CustomInfo) iterator.next();
				System.out.println("getCustcode:"+cust.getCustcode());
				System.out.println("getCustname:"+cust.getCustname());
				System.out.println("getCustdesc:"+cust.getCustdesc());
			}*/
			return res;
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			workbook.close();
		}
	}
	private String getMaterialList(StringBuilder res,Sheet sheet,List<MaterialInfo> matList,List<Prodmodelinfo> prodmodelList,Map<String,MaterialInfo> checkMap){
		int gapcount = 0;
		int gaptop = 10;//over 10 gap row,suppose 
		//StringBuilder res = new StringBuilder("");
		//Map<String,String> checkMap = new HashMap<String,String>();
		for (int i=1; gapcount < gaptop && i < sheet.getRows(); i++){
			Cell cela = sheet.getCell(0,i);
			if (cela.getContents().trim().equals("")
					||cela.getContents().trim().equals("原料")
					||cela.getContents().trim().equals("半成品")){
				gapcount++;
				continue;
			}
			gapcount = 0;
			MaterialInfo material = new MaterialInfo();
			material.setMaterialcode( sheet.getCell(0,i).getContents().trim() );
			material.setMaterialname( sheet.getCell(1,i).getContents().trim() ); 
			material.setAliasname( 	sheet.getCell(2,i).getContents().trim() );
			material.setMaterialdesc( sheet.getCell(3,i).getContents().trim() ); 
			material.setMaterialtype( sheet.getCell(4,i).getContents().trim() ); 
			material.setMaterialstyle( sheet.getCell(5,i).getContents().trim() );
			
			if (material.getMaterialdesc().equals("外购,直接出货")){
				material.setIsneedprodction(2);
			}else
				material.setIsneedprodction(0);
			
			String unitprice = sheet.getCell(6,i).getContents().trim();
			if (!unitprice.equals("")&&StringUtil.isNumber(unitprice))
				material.setUnitprice( new BigDecimal( unitprice) );
			else{
				if (StringUtil.isNullOrBlank(unitprice))
					material.setUnitprice(BigDecimal.ZERO);
				else{
					res.append("在【"+sheet.getName()+"】物料："+material.getMaterialcode()+" 的单价应为数字,请检查<br>");
					//break;
				}
			}
			String unitCode = sheet.getCell(7,i).getContents().trim();
			material.setStandardunitcode( unitCode.equals("")?"只":unitCode);
			String unitweight = sheet.getCell(8,i).getContents().trim();
			//TODO 
			if (!unitweight.equals("")&&StringUtil.isNumber(unitweight))
				material.setUnitweight( new BigDecimal( unitweight) );
			else{
				if (StringUtil.isNullOrBlank(unitweight))
					material.setUnitweight(BigDecimal.ZERO);
				else{
					res.append("在【"+sheet.getName()+"】物料："+material.getMaterialcode()+" 的标准克重应为数字,请检查<br>");
					//break;
				}
			}
			
			material.setIsneedsetup(sheet.getCell(9,i).getContents().trim() );
			if (checkMap.get(material.getMaterialcode())!=null){
				res.append("在【"+sheet.getName()+"】有重复的物料编码："+material.getMaterialcode()+",请检查<br>");
				//break;
			}
			if (material.getMaterialtype().equals("成品")&& !material.getMaterialdesc().equals("外购,直接出货")){
				if (material.getIsneedsetup().equals("否") && sheet.getCell(10,i).getContents().trim().equals("")){
					res.append("在【"+sheet.getName()+"】的物料【"+material.getMaterialcode()+"】是不需安装的(即由原料组成)，但没有设置模具编码，请检查<br>");
				}
			}
			
			checkMap.put(material.getMaterialcode(), material);
			matList.add(material);
			Cell celb = sheet.getCell(10,i);
			if (!celb.getContents().trim().equals("")){
				Prodmodelinfo prodmodel = new Prodmodelinfo();
				prodmodel.setMaterialCode(sheet.getCell(0,i).getContents().trim());
				prodmodel.setModelCode(sheet.getCell(10,i).getContents().trim());
				prodmodel.setModelName(sheet.getCell(11,i).getContents().trim());
				prodmodel.setItemCount(Long.valueOf(sheet.getCell(12,i).getContents().trim()));
				
				//System.out.println(sheet.getCell(12,i).getContents());
				//try{
				prodmodel.setProdWeight( new BigDecimal( sheet.getCell(13,i).getContents().trim()) );
				prodmodel.setNozzleWeight(new BigDecimal( sheet.getCell(14,i).getContents().trim()));
				prodmodel.setTotalWeight(new BigDecimal( sheet.getCell(15,i).getContents().trim()));
				/*}catch(Exception e){
					System.out.println("cccccccc" + sheet.getCell(12,i).getContents());
				}*/
				prodmodelList.add(prodmodel);
			}
		}
		return res.toString();
	}
	//matlist: 用来判读按是否所有mat都有bom表记录
	private String getBomList(StringBuilder res,Sheet sheet,List<BomInfo> bomList,List<MaterialInfo> matlist,Map<String,MaterialInfo> matKeymap){
		//StringBuilder res = new StringBuilder("");
		int gapcount = 0;
		int gaptop = 5;//over 10 gap row,suppose 
		Map<String,String> keymap = new LinkedHashMap<String,String>();
		Map<String,String> prodKeyRepeatmap = new HashMap<String,String>();//用了检测只有一种物料组成的产品是否有重复
		
		for (int i=1; gapcount < gaptop && i < sheet.getRows(); i++){
			Cell cela = sheet.getCell(0,i);
			if (cela.getContents().trim().equals("")){
				gapcount++;
				continue;
			}
			gapcount = 0;
			String parentcode = sheet.getCell(0,i).getContents().trim();//合并单元格会取不到
			//String parentname = sheet.getCell(1,i).getContents().trim();
			String subcode = sheet.getCell(2,i).getContents().trim();
			String subtype = sheet.getCell(4,i).getContents().trim();
			String subqty = sheet.getCell(5,i).getContents().trim();
			String rawcode = sheet.getCell(6,i).getContents().trim();
			String rawtype = sheet.getCell(8,i).getContents().trim();
			/*System.out.println("parentcode:"+parentcode);
			//System.out.println("parentname:"+parentname);
			System.out.println("subcode:"+subcode);
			System.out.println("subtype:"+subtype);
			System.out.println("subqty:"+subqty);
			System.out.println("rawcode:"+rawcode);
			System.out.println("rawtype:"+rawtype);*/
			
			String subvalue = subcode+","+subtype+","+subqty;
			String rawvalue = rawcode+","+rawtype+",0";
			
			if (subcode.equals("")){//product which is composed by raw material
				if(keymap.get(parentcode)==null){
					keymap.put(parentcode, rawvalue);
				}
				if(prodKeyRepeatmap.get(parentcode)==null){
					prodKeyRepeatmap.put(parentcode, rawvalue);
				}else
					res.append("在BOM表中的成品【"+parentcode+"】有重复记录,请检查<br>");
				
			}else{
				if(keymap.get(parentcode)==null){
					keymap.put(parentcode, subvalue);
				}else{
					keymap.put(parentcode, keymap.get(parentcode)+";"+subvalue);
				}
				if (!rawcode.equals("")){//原料，一只半成品或成品，不会有两只原料
					if(keymap.get(subcode)==null){
						keymap.put(subcode, rawvalue);
					}/*else{
						keymap.put(subcode, keymap.get(subcode)+";"+rawvalue);
					}*/
				}
			}
		}
		//check if any product and semi-prod have no bom record
		for (Iterator iterator = matlist.iterator(); iterator.hasNext();) {
			MaterialInfo materialInfo = (MaterialInfo) iterator.next();
			if ( (materialInfo.getMaterialtype().equals("成品")
					|| materialInfo.getMaterialtype().equals("半成品"))
					&& materialInfo.getIsneedprodction().intValue() != 2){
				if (keymap.get(materialInfo.getMaterialcode())==null){
					res.append("物料【"+materialInfo.getMaterialcode()+"】在【"+sheet.getName()+"】没有bom记录,请检查<br>");
				}
			}
		}
		Map<String,String> havecheckedmap = new HashMap<String,String>();
		Iterator iter = keymap.entrySet().iterator(); 
		while (iter.hasNext()) {
		    Map.Entry<String,String> entry = (Map.Entry<String,String>) iter.next(); 
		    String materialcode = entry.getKey(); 
		    String val = entry.getValue(); 
			String[] valueList = val.split(";");
			for (int j = 0; j < valueList.length; j++) {
				try{
					String[] values = valueList[j].split(",");
					BomInfo bominfo = new BomInfo();
					bominfo.setBomcode("");
					bominfo.setBomlevel(0);
					bominfo.setParentmaterialcode(materialcode);
					bominfo.setSubmaterialcode(values[0]);
					bominfo.setMaterialtype(values[1]);
					bominfo.setSubmaterialquantity((new BigDecimal(values[2])).setScale(5));
					if (bominfo.getMaterialtype().equals("原料"))
						bominfo.setSubmaterialunit("克");
					else
						bominfo.setSubmaterialunit("只");
					
					if (havecheckedmap.get(bominfo.getParentmaterialcode())==null){
						havecheckedmap.put(bominfo.getParentmaterialcode(), bominfo.getParentmaterialcode());
						if (matKeymap.get(bominfo.getParentmaterialcode())==null)
							res.append("在BOM表中使用的物料【"+bominfo.getParentmaterialcode()+"】在物料表中没有记录,请检查<br>");
						else if (!matKeymap.get(bominfo.getParentmaterialcode()).getMaterialtype().equals("成品"))
							res.append("在BOM表中的成品【"+bominfo.getParentmaterialcode()+"】在物料表中是半成品,请检查<br>");
					}
					
					if (havecheckedmap.get(bominfo.getSubmaterialcode())==null){
						havecheckedmap.put(bominfo.getSubmaterialcode(), bominfo.getParentmaterialcode());
						if ( matKeymap.get(bominfo.getSubmaterialcode())==null)
							res.append("在BOM表中使用的物料【"+bominfo.getSubmaterialcode()+"】在物料表中没有记录,请检查<br>");
					}	
					bomList.add(bominfo);
				
				}catch(Exception e){
					System.out.println("bom error:"+materialcode);
					res.append("在BOM表中物料【"+materialcode+"】的bom记录有问题,请检查<br>");
					//throw new RuntimeException(e);
				}
			}
		}
		return res.toString();
	}
	private String getColorList(Sheet sheet,List<ColorInfo> colorList){
		StringBuilder res = new StringBuilder("");
		Map<String,String> colorMap = new HashMap<String,String>();
		int rowgapcount = 0;
		int colgapcount = 0;
		int rowgaptop = 5;//over 5 gap row,suppose it is finish 
		int colgaptop = 5;
		//System.out.println(sheet.getName() +" getRows "+sheet.getRows());
		//System.out.println(sheet.getName() +" getColumns "+sheet.getColumns());
		for (int col=0; colgapcount < colgaptop && col<sheet.getColumns(); col=col+3){
			Cell cellcust = sheet.getCell(col,0);
			if (cellcust.getContents().trim().equals("")){
				colgapcount++;
				continue;
			}
			colgapcount = 0;
			String custcode = cellcust.getContents().trim();
			
			rowgapcount = 0;
			for (int row=2; rowgapcount< rowgaptop && row < sheet.getRows(); row++){
				Cell cela = sheet.getCell(col,row);
				if (cela.getContents().trim().equals("")){
					rowgapcount++;
					continue;
				}
				rowgapcount = 0;
				String colorCode = sheet.getCell(col,row).getContents().trim();
	                        if (colorMap.get(colorCode) != null){
	                            res.append("顏色编码【").append(colorCode).append("】重复！");
	                        }
				
				ColorInfo colorinfo = new ColorInfo();
				colorinfo.setCustcode(custcode);
				colorinfo.setColorcode(colorCode);
				colorinfo.setColorname(sheet.getCell(col+1,row).getContents().trim());
				colorList.add(colorinfo);
				colorMap.put(colorCode, colorCode);
			}
		}
		return res.toString();
	}
	private String getEmpList(Sheet sheet,List<Employee> empList){
		StringBuilder res = new StringBuilder("");
		int rowgapcount = 0;
		int rowgaptop = 5;//over 5 gap row,suppose it is finish 
		for (int row=1; rowgapcount< rowgaptop && row < sheet.getRows(); row++){
			Cell cela = sheet.getCell(0,row);
			if (cela.getContents().trim().equals("")){
				rowgapcount++;
				continue;
			}
			rowgapcount = 0;
			Employee emp = new Employee();
			emp.setUserid(sheet.getCell(0,row).getContents().trim());
			emp.setUsername(sheet.getCell(1,row).getContents().trim());
			emp.setCardtype(sheet.getCell(2,row).getContents().trim());
			emp.setBirthdate(new Date());
			emp.setIndate(new Date());
			emp.setOutdate(new Date());
			empList.add(emp);
		}
		return res.toString();
	}
	private String getCustomList(Sheet sheet,List<CustomInfo> customList){
		StringBuilder res = new StringBuilder("");
		int rowgapcount = 0;
		int rowgaptop = 5;//over 5 gap row,suppose it is finish 
		System.out.println("getCustomList rows:"+sheet.getRows());
		for (int row=1; rowgapcount< rowgaptop && row < sheet.getRows(); row++){
			Cell cela = sheet.getCell(0,row);
			if (cela.getContents().trim().equals("")){
				rowgapcount++;
				continue;
			}
			rowgapcount = 0;
			CustomInfo cust = new CustomInfo();
			cust.setCustcode(sheet.getCell(0,row).getContents().trim());
			cust.setCustname(sheet.getCell(1,row).getContents().trim());
			cust.setCustdesc(sheet.getCell(2,row).getContents().trim());
			cust.setSeqno(row);
			customList.add(cust);
		}
		return res.toString();
	}
	@Override
	@Transactional(readOnly = true)
	public List<MaterialInfo> getMaterialInfos(String materilType,String materialdesc) {
		String hql = "from MaterialInfo t where t.materialtype= :materialtype ";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("materialtype", materilType);
		if (StringUtils.isNotBlank(materialdesc)){
			hql = hql + " and t.materialdesc=:materialdesc";
			param.put("materialdesc",materialdesc);
		}
		return utilsDao.find(hql,param);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomInfo> getCustomInfos() {
		return utilsDao.find("from CustomInfo order by seqno");		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Employee> getEmployees() {
		return utilsDao.find( "from Employee t ");
	}
	@Override
    @Transactional
	public void testTransaction() {
		
		/*ColorInfo colorinfo = new ColorInfo();
		colorinfo.setColorcode("g-semitran1");
		colorinfo.setColorname("colorname1");
		colorinfo.setCustcode("general");
		utilsDao.insert(colorinfo);

		//utilsDao.execSql("insert into colorinfo(colorCode,colorName,custCode) values('g-semitran1','colorname1','general')");
		//utilsDao.execute("insert into ColorInfo(colorcode,colorname,custcode) values('g-semitran2','colorname2','general')");
		ColorInfo record = new ColorInfo();
		record.setColorcode("g-semitran");
		record.setColorname("半透明");
		record.setCustcode("general");
		record.setColordesc("cccc");
		utilsDao.update(record);
		
		InventoryRecord inventoryRecord = new InventoryRecord();
		inventoryRecord.setRecordName("ccc");
		inventoryRecord.setBalance(BigDecimal.valueOf(3333));
		inventoryRecord.setStartDate(new Date());
		inventoryRecord.setEndDate(new Date());
		inventoryRecord.setBalanceDate(new Date());
		utilsDao.insert(inventoryRecord);*/
		
		ColorInfo colorInfo = utilsDao.load(ColorInfo.class, "g-semitran");
		//System.out.println(colorInfo.getColorname());
		
		//InventoryRecord inventoryRecord = utilsDao.get(InventoryRecord.class, Long.valueOf(1));
		//System.out.println(inventoryRecord.getRecordName());
		
		InventoryInfo inventoryInfo = new InventoryInfo();
		inventoryInfo.setColorcode("g-semitran");
		inventoryInfo.setMaterialcode("A1-05");
		/*List<InventoryInfo> invList = utilsDao.findByExample(inventoryInfo);
		for (InventoryInfo inv : invList){
			System.out.println(inv.getWarehouse());
			System.out.println(inv.getQuantity());
		}
		utilsDao.delete(62, inventoryInfo.getClass());
		*///utilsDao.insert(record);
		//colorInfoMapper.insert(record);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("colorcode", "g-semitran");
		param.put("materialcode", "A1-05");
		String hql = "from InventoryInfo t where t.materialcode=:materialcode and t.colorcode=:colorcode";
		List<InventoryInfo> invList = utilsDao.find(hql,param);
		/*for (InventoryInfo inv : invList){
			System.out.println(inv.getWarehouse());
			System.out.println(inv.getQuantity());
		}
		*/hql = "from InventoryInfo t where t.materialcode=? and t.colorcode=?";
		invList = null;
		invList = utilsDao.find(hql,"AB-11","jsl-gypsumJSL001");
		/*for (InventoryInfo inv : invList){
			System.out.println(inv.getWarehouse());
			System.out.println(inv.getQuantity());
		}*/
		
		
		/*String sql = "select * from InventoryInfo t where t.materialcode=?  and t.colorcode=?";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("colorcode", "g-semitran");
		params.put("materialcode", "A1-05");
		List<Object[]> resList = utilsDao.findBySql(sql,"A1-05","g-semitran");
		for (Object[] inv : resList){
			System.out.println(inv[0]);
			System.out.println(inv[1]);
		}*/
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("colorcode", "g-semitran");
		params.put("colordesc", "ccA1-05111111");
		utilsDao.execute("update ColorInfo set colordesc=:colordesc where colorcode=:colorcode", params);
		//utilsDao.execSql("update colorinfo set colordesc=? where colorcode=?",  "ccA1-05xxxx","g-semitran");
		//utilsDao.execute("delete from ColorInfo where colorname=?", "111");
	}

}
