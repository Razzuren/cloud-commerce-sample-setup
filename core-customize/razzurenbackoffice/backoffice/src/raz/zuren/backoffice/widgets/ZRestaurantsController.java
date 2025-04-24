package raz.zuren.backoffice.widgets;

import com.hybris.cockpitng.util.DefaultWidgetController;
import raz.zuren.backoffice.jalo.ZRestaurant;
import raz.zuren.backoffice.model.UserIframeSettingsModel;
import raz.zuren.backoffice.model.ZRestaurantModel;
import raz.zuren.backoffice.services.RazzurenbackofficeService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

public class ZRestaurantsController extends DefaultWidgetController {
    private static final long serialVersionUID = 1L;
    private Label label;

    @WireVariable
    private transient RazzurenbackofficeService razzurenbackofficeService;

    @Override
    public void initialize(final Component comp) {
        super.initialize(comp);
        label.setValue(razzurenbackofficeService.getHello());

        String resultHTML = razzurenbackofficeService.getRestaurantsAsHTMLTable();

        getModel().put("TABLE", resultHTML);
    }

    @Override
    public void preInitialize(final Component component) {
        super.preInitialize(component);

    }


}
