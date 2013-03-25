package mybox.web.controller;

import mybox.service.DiskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/dk")
public class DiskController extends AbstractFileController {

	@Autowired
	protected DiskService diskService;
	
	@Override
	protected DiskService getService() {
		return diskService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
	}
}
