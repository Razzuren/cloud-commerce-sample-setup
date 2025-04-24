/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package raz.zuren.backoffice.services;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raz.zuren.backoffice.constants.RazzurenbackofficeConstants;
import raz.zuren.backoffice.jalo.ZRestaurant;
import raz.zuren.backoffice.model.UserIframeSettingsModel;
import raz.zuren.backoffice.model.ZRestaurantModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Hello World HeliodosbackofficeService
 */
public class RazzurenbackofficeService {
    private static final Logger LOG = LoggerFactory.getLogger(RazzurenbackofficeService.class);
    private FlexibleSearchService flexibleSearchService;

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    private ModelService modelService;

    private UserService userService;

    public String getHello() {
        return "Hello";
    }


    public UserIframeSettingsModel getDefaultUserIframe() {

        UserIframeSettingsModel standard = new UserIframeSettingsModel();

       /* get the
        default
        source from system property*/
        standard.setSrc(Config.getParameter(RazzurenbackofficeConstants.IFRAMEDEFAULTSRC));

        return standard;
    }

    public UserIframeSettingsModel getCurrentUserIframe() {

        UserModel user = getUserService().getCurrentUser();

        UserIframeSettingsModel userIframe = null;

        String query = "select pk from {UserIframeSettings} where {UserIframeSettings.user} = ?user";

        FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
        fsQuery.addQueryParameter("user", user.getPk().getLongValueAsString());

        try {
            userIframe = getFlexibleSearchService().searchUnique(fsQuery);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        userIframe = (userIframe != null) ? userIframe : getDefaultUserIframe();

        LOG.info("IFrame src " + userIframe.getSrc() + " for user " + userIframe.getUser() +
                " and wrapper " + userIframe.getWrapperclass());

        return userIframe;

    }

    public String getRestaurantsAsHTMLTable() {

        String query = "select pk from {ZRestaurant}";

        SearchResult<ZRestaurantModel> restaurants = getFlexibleSearchService().search(query);

        StringBuilder resultHTML = new StringBuilder();

        for (ZRestaurantModel r : restaurants.getResult()){
            resultHTML.append("<n:tr>");
            resultHTML.append("<n:td>").append(r.getName()).append("</n:td>");
            resultHTML.append("<n:td>").append(r.getFinalRank()).append("</n:td>");
            resultHTML.append("</n:tr>");

        }
        LOG.info(resultHTML.toString());
        return resultHTML.toString();

    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
