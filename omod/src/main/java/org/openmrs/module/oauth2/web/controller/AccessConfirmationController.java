package org.openmrs.module.oauth2.web.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.api.context.Context;
import org.openmrs.module.oauth2.Client;
import org.openmrs.module.oauth2.api.db.hibernate.ClientDAO;
import org.openmrs.module.oauth2.api.smart.SmartAppLaunchService;
import org.openmrs.module.oauth2.api.smart.SmartAppManagementService;
import org.openmrs.module.oauth2.api.smart.model.LaunchValue;
import org.openmrs.module.oauth2.api.smart.model.SmartApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by OPSKMC on 8/4/15.
 */
@Controller
@SessionAttributes("authorizationRequest")
public class AccessConfirmationController {
    private ClientDetailsService clientDetailsService;

    @Autowired
    ClientDAO clientDAO;

    @RequestMapping("/oauth/confirm_access")
    public ModelAndView getAccessConfirmation(@ModelAttribute AuthorizationRequest clientAuth) throws Exception {
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());

        Client client1 = clientDAO.loadClientByClientId(clientAuth.getClientId());
        Client.ClientType clientType = client1.getClientType();

        if (clientType.equals(Client.ClientType.SMART_APPLICATION)) {
            if (clientAuth.getScope().contains("launch")) {
                SmartApp smartApp = getSmartService().loadSmartAppByClientId(client1.getId());
                LaunchValue launchValue = getLaunchService().getLaunchValue(smartApp.getSmartId());
//                String launch = clientAuth.getAuthorizationParameters().get("launch"); TODO getAuthorizationParameters is not available now
                String launch = clientAuth.getApprovalParameters().get("launch");
                if (!getLaunchService().verifyLaunchValue(launchValue, launch)) {
                    return new ModelAndView("/module/oauth2/launch_error", null);
                }
            }
        }

        TreeMap<String, Object> model = new TreeMap<String, Object>();
        model.put("auth_request", clientAuth);
        model.put("client", client);
        return new ModelAndView("/module/oauth2/access_confirmation", model);
    }

    @RequestMapping("/oauth/error")
    public String handleError(Map<String, Object> model) throws Exception {
        // We can add more stuff to the model here for JSP rendering. If the client was a machine then
        // the JSON will already have been rendered.
        model.put("message", "There was a problem with the OAuth2 protocol");
        return "/module/oauth2/oauth_error";
    }

    @Autowired
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public SmartAppManagementService getSmartService() {
        return Context.getService(SmartAppManagementService.class);
    }

    public SmartAppLaunchService getLaunchService() {
        return Context.getService(SmartAppLaunchService.class);
    }
}
