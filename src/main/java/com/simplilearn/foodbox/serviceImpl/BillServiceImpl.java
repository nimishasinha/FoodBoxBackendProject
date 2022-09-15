package com.simplilearn.foodbox.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Header;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.simplilearn.foodbox.JWT.JwtFilter;
import com.simplilearn.foodbox.constants.FoodboxConstants;
import com.simplilearn.foodbox.dao.BillDao;
import com.simplilearn.foodbox.pojo.Bill;
import com.simplilearn.foodbox.service.BillService;
import com.simplilearn.foodbox.utils.FoodBoxUtils;

@Service
public class BillServiceImpl implements BillService {
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	BillDao billDao;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			String fileName;
			if(validateRequestMap(requestMap)) {
				if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.containsKey("isGenerate"))
				{
					fileName = (String) requestMap.get("uuid");
				}
				else
				{
					fileName= FoodBoxUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}
				
				String data= "Name: " +requestMap.get("name") +"\n" +"Contact Number : "+ requestMap.get("contactNumber") + "\n" +
				          "Email : "+requestMap.get("email")+"\n" +"Payment Method : "+requestMap.get("paymentMethod") ;
				
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(FoodboxConstants.STORE_LOCATION+"\\"+fileName+".pdf"));
				
				document.open();
				setRectangleInPdf(document);
				
				Paragraph chunk = new Paragraph("Food Box Management System" , getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);
				
				Paragraph paragraph = new Paragraph(data +"\n \n " , getFont("Data"));
				document.add(paragraph);
				
				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);
				
				JSONArray jsonArray = FoodBoxUtils.getJsonArrayFromString((String)requestMap.get("productDetails"));
				for(int i=0;i<jsonArray.length();i++) {
					addRows(table,FoodBoxUtils.getMapFromJson(jsonArray.getString(i)));
				}
				
				document.add(table);
				
				Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount")+"\n"
						+"Thankyou for visit . Please visit again !!",getFont("Data"));
				
				document.add(footer);
				document.close();
				return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);
			}
			return FoodBoxUtils.getResponseEntity("Required data not found " , HttpStatus.BAD_REQUEST);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("total")));
		
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of("Name","Category","Quantity","Price","Sub Total")
		.forEach(columnTitle ->{
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(BaseColor.YELLOW);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
			
		});
		
	}

	private Font getFont(String type) {
		switch(type) {
		case "Header" :
			Font headerFont =FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
			
		case "Data" : 
			Font dataFont= FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
			
		default : 
			return new Font();
		}
	}

	private void setRectangleInPdf(Document document) throws DocumentException {
		Rectangle rect = new Rectangle(577,825,18,15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		document.add(rect); 
		
		
		
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String)requestMap.get("name"));
			bill.setContactNumber((String)requestMap.get("contactNumber"));
			bill.setEmail((String)requestMap.get("email"));
			bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")) );
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
			
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		// TODO Auto-generated method stub
		return requestMap.containsKey("name") &&
				requestMap.containsKey("contactNumber") &&
				requestMap.containsKey("email") &&
				requestMap.containsKey("paymentMethod") &&
				requestMap.containsKey("paymentDetails") &&
				requestMap.containsKey("totalAmount");
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		List<Bill> list = new ArrayList();
		if(jwtFilter.isAdmin()) {
			list=billDao.getAllBills();
		}else {
			list=billDao.getBillByUserName(jwtFilter.getCurrentUser());
		}
		
		return new ResponseEntity<>(list,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		try {
			byte[] byteArray = new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) 
				return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
			String filePath=FoodboxConstants.STORE_LOCATION + "\\" +(String) requestMap.get("uuid") + ".pdf";
			if(FoodBoxUtils.isFileExist(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}else
			{
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray=getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);

			}
			
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	private byte[] getByteArray(String filePath) throws Exception {
		File initialFile =new File(filePath);
		InputStream targetStream = new FileInputStream(initialFile);
		byte[] byteArray = IOUtils.toByteArray(targetStream);
		targetStream.close();
		return byteArray;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				return FoodBoxUtils.getResponseEntity("Bill successfully deleted", HttpStatus.OK);	
			}
			return FoodBoxUtils.getResponseEntity("Bill id does not exist", HttpStatus.OK);
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return  FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
