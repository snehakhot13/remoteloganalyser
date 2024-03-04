package com.logFileAnalyser.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.logFileAnalyser.Dto.LogFilesReqDto;

@RestController
public class LogfileController {
	
	
	@Autowired
	com.logFileAnalyser.srvice.LogfileService LogfileService;
	
	@RequestMapping(
		      value = {"/LogAnalyser"},
		      method = {RequestMethod.POST},
		    consumes = {"application/json", "application/xml"}
		     // produces = {"application/json"}
		   )
		   public ResponseEntity<?> logAnalyser(@RequestBody LogFilesReqDto req ) throws IOException {
		     
                System.out.println(req);
		      try {
		    	 
		    	 
		    	 // logFileService.logAnalyser(req);
		    	  LogfileService.LogfileService(req);
		          return ResponseEntity
		  				.ok()
		  				.contentType(MediaType.APPLICATION_JSON)
		  				.body("");
		      } catch (Exception e) {
		    	 e.printStackTrace();
		         return new ResponseEntity(HttpStatus.NOT_FOUND);
		      }
			
		   }
	 

}
