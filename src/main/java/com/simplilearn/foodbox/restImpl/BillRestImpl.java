package com.simplilearn.foodbox.restImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.simplilearn.foodbox.constants.FoodboxConstants;
import com.simplilearn.foodbox.pojo.Bill;
import com.simplilearn.foodbox.rest.BillRest;
import com.simplilearn.foodbox.service.BillService;
import com.simplilearn.foodbox.utils.FoodBoxUtils;

@RestController
public class BillRestImpl implements BillRest {
	
	@Autowired
	BillService billService;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		try {
			return billService.getBills();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
	     try {
				return billService.getPdf(requestMap);
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		return null;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			billService.deleteBill(id);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return  FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
