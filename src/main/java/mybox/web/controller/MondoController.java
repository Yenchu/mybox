package mybox.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mybox.model.User;
import mybox.model.mondo.Group;
import mybox.model.mondo.MondoUser;
import mybox.service.MondoService;
import mybox.to.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/md")
public class MondoController extends AbstractFileController {

	private static final Logger log = LoggerFactory.getLogger(MondoController.class);
	
	@Autowired
	private MondoService mondoService;
	
	@RequestMapping(value = "/groups")
	public String getGroups(HttpServletRequest request) {
		return "groups";
	}

	@RequestMapping(value = "/groups", params={"sidx"}, method = RequestMethod.POST)
	@ResponseBody
	public Page<Group> pageGroups(HttpServletRequest request) {
		// sidx is from jgGrid
		User user = getUser(request);
		log.debug("User {} page groups", new Object[]{user.toString()});

		List<Group> groups = getService().getGroups((MondoUser) user);
		Page<Group> page = new Page<Group>(groups);
		return page;
	}
	
	@RequestMapping(value = "/groups", method = RequestMethod.POST)
	@ResponseBody
	public List<Group> listGroups(HttpServletRequest request) {
		User user = getUser(request);
		log.debug("User {} list groups", new Object[]{user.toString()});
		List<Group> groups = getService().getGroups((MondoUser) user);
		return groups;
	}
	
	@RequestMapping(value = "/metadata/**")
	public String getFiles(
			@RequestParam(value = "space", required = false) String spaceId,
			HttpServletRequest request) {
		super.getFiles(spaceId, request);
		return "groupFiles";
	}
	
	protected MondoService getService() {
		return mondoService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
	}
}
