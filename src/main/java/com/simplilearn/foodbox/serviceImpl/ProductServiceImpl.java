package com.simplilearn.foodbox.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.simplilearn.foodbox.JWT.JwtFilter;
import com.simplilearn.foodbox.constants.FoodboxConstants;
import com.simplilearn.foodbox.dao.ProductDao;
import com.simplilearn.foodbox.pojo.Category;
import com.simplilearn.foodbox.pojo.Product;
import com.simplilearn.foodbox.service.ProductService;
import com.simplilearn.foodbox.utils.FoodBoxUtils;
import com.simplilearn.foodbox.wrapper.ProductWrapper;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductDao productDao;

	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(ValidateProductMap(requestMap,false)) {
				   productDao.save(getProductFromMap(requestMap,false));
				   return FoodBoxUtils.getResponseEntity("Product added successfully",HttpStatus.OK);
			}
					return FoodBoxUtils.getResponseEntity(FoodboxConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
			}else
			{
				return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category = new Category();
		category.setId(Integer.parseInt(requestMap.get("categoryId")));
		Product product = new Product();
		if(isAdd) {
			product.setProductId(Integer.parseInt(requestMap.get("productId")));
		}else {
			product.setStatus("true");
			
			
		}
		
		product.setCategory(category);
		product.setName(requestMap.get("name"));
		product.setDescription(requestMap.get("description"));
		product.setPrice(Integer.parseInt(requestMap.get("price")));
		return product;
	}

	private boolean ValidateProductMap(Map<String, String> requestMap, boolean validateId) {
		 if(requestMap.containsKey("name")) {
			 if(requestMap.containsKey("id") && validateId) {
				 return true;
			 }else if(!validateId) {
				 return true;
			 }
		 }
		 return false;
	}

	@Override
	public ResponseEntity<List<ProductWrapper>> getAllProduct() {
		try {
			return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
		 try {
			 if(jwtFilter.isAdmin())
			 {
				 if(ValidateProductMap(requestMap,true)) {
					Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						Product product = getProductFromMap(requestMap,true);
						product.setStatus(optional.get().getStatus());
						productDao.save(product);
						return FoodBoxUtils.getResponseEntity("Product updated successfully",HttpStatus.OK);
					}else {
						return FoodBoxUtils.getResponseEntity("Product Id does not exist",HttpStatus.OK);
					}
					
				 }else {
					 return FoodBoxUtils.getResponseEntity(FoodboxConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
				 }
				 
			 }else {
				 return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			 }
		 }catch(Exception ex) {
			 ex.printStackTrace();
		 }
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> deleteProduct(Integer id) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional optional = productDao.findById(id);
				if(!optional.isEmpty()) {
					productDao.deleteById(id);
					return FoodBoxUtils.getResponseEntity("Product Deleted successfully", HttpStatus.OK);
				}
				return FoodBoxUtils.getResponseEntity("Product id does not exist", HttpStatus.BAD_REQUEST);
			}else {
				return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS,HttpStatus.UNAUTHORIZED);
				
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	@Override
	public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")))	;
			 if(!optional.isEmpty()) {
				productDao.updateStatusProduct(requestMap.get("status"),Integer.parseInt(requestMap.get("id"))); 
				return FoodBoxUtils.getResponseEntity("ProductStatus updated successfully", HttpStatus.OK);
			 }
			 return FoodBoxUtils.getResponseEntity("Product id does not exist", HttpStatus.BAD_REQUEST);
			}else {
				return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
		try {
			return new ResponseEntity(productDao.getProductByCategory(id),HttpStatus.OK);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<ProductWrapper> getProductById(Integer id) {
		try {
			return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
