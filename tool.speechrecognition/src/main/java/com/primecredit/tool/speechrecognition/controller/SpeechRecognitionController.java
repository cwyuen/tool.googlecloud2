package com.primecredit.tool.speechrecognition.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primecredit.tool.common.wsobject.request.RecongnitionRequest;
import com.primecredit.tool.common.wsobject.response.RecognitionResponse;
import com.primecredit.tool.speechrecognition.services.GoogleSpeechConvertService;


@RestController
@RequestMapping("/SpeechRecognition")
public class SpeechRecognitionController {

	private static Logger logger = LoggerFactory.getLogger(SpeechRecognitionController.class);
	
	@Autowired
	private GoogleSpeechConvertService googleSpeechConvertService;
	
	@Value("${temp.path}")
	private String tempPath;

	@RequestMapping(value = "/recognition", method = RequestMethod.POST)
	public RecognitionResponse recognition(@RequestBody RecongnitionRequest request) {
		return null;
		
	}
		

	
}
