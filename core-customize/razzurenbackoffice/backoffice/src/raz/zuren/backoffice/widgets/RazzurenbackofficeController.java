package raz.zuren.backoffice.widgets;

import com.hybris.cockpitng.util.DefaultWidgetController;
import raz.zuren.backoffice.model.UserIframeSettingsModel;
import raz.zuren.backoffice.services.RazzurenbackofficeService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

public class RazzurenbackofficeController extends DefaultWidgetController {
    private static final long serialVersionUID = 1L;
    private Label label;

    @WireVariable
    private transient RazzurenbackofficeService razzurenbackofficeService;

    @Override
    public void initialize(final Component comp) {
        super.initialize(comp);
        label.setValue(razzurenbackofficeService.getHello());
    }

    @Override
    public void preInitialize(final Component component) {
        super.preInitialize(component);

		UserIframeSettingsModel currentUserIframe = razzurenbackofficeService.getCurrentUserIframe();

        getModel().put("SRC", currentUserIframe.getSrc());
        getModel().put("WRAPPER", currentUserIframe.getWrapperclass().getCode());
    }


}
