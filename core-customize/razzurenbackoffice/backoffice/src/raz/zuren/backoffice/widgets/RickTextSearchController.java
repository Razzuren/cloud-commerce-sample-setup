package raz.zuren.backoffice.widgets;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.annotations.ViewEvents;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.util.Config;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import raz.zuren.backoffice.constants.RazzurenbackofficeConstants;

import java.util.Random;

public class RickTextSearchController extends DefaultWidgetController {
    public static final String SOCKET_IN_QUERY = "query";
    public static final String SOCKET_OUT_QUERY = "query";
    public static final String SOCKET_IN_CLEARQUERY = "clearQuery";
    public static final String SOCKET_IN_ENABLE = "enabled";
    protected static final String QUERY_ATTRIBUTE = "query";
    protected static final String ENABLED_ATTRIBUTE = "enabled";
    protected static final String SETTING_ENABLED_BY_DEFAULT = "enabledByDefault";
    protected static final String COMP_SEARCH_BUTTON = "searchButton";
    protected static final String COMP_SEARCH_INPUT = "searchBox";

    private Textbox searchBox;
    private Button searchButton;

    public RickTextSearchController() {
    }

    public void initialize(Component comp) {
        super.initialize(comp);
        Boolean enabled = this.isEnabled();
        if (enabled == null) {
            if (BooleanUtils.isFalse(this.getWidgetSettings().getBoolean("enabledByDefault"))) {
                this.enableTextSearchInternal(false);
            }
        } else {
            this.enableTextSearchInternal(enabled);
        }

        String query = this.getSearchQuery();
        if (query != null) {
            this.initializeQuery(query);
        }

    }

    private void enableTextSearchInternal(boolean enabled) {
        this.setEnabled(enabled ? Boolean.TRUE : Boolean.FALSE);
        this.searchBox.setDisabled(!enabled);
        this.searchButton.setDisabled(!enabled);
    }

    @SocketEvent(
            socketId = "clearQuery"
    )
    public void clearQuery() {
        this.initializeQuery("");
    }

    @SocketEvent(
            socketId = "query"
    )
    public void initializeQuery(String value) {
        this.setSearchQuery(value);
        this.searchBox.setValue((String)StringUtils.defaultIfBlank(value, ""));
    }

    @SocketEvent(
            socketId = "enabled"
    )
    public void enableTextSearch(Boolean enable) {
        this.enableTextSearchInternal(BooleanUtils.toBoolean(enable));
    }

    @ViewEvent(
            componentID = "searchBox",
            eventName = "onChange"
    )
    public void onQueryChange() {
        if (this.searchBox != null) {
            this.setSearchQuery(this.searchBox.getValue());
        }

    }

    @ViewEvents({@ViewEvent(
            componentID = "searchButton",
            eventName = "onClick"
    ), @ViewEvent(
            componentID = "searchBox",
            eventName = "onOK"
    )})
    public void onSearch() {
        if (this.searchBox != null) {
            String query = this.searchBox.getValue();
            this.sendOutput("query", query);

            Random random = new Random();
            int randomNumber = random.nextInt(10) ;

            if (randomNumber == 5) {
                showRickRollPopup();
            }
        }
    }

    private void showRickRollPopup() {
        Html rickRollHtml = new Html();
        String src = Config.getParameter(RazzurenbackofficeConstants.RICKROLL);
        rickRollHtml.setContent(
                "<iframe width=\"560\" height=\"315\" src=\""+ src + "\" frameborder=\"0\" allow=\"accelerometer; " +
                        "autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" " +
                        "allowfullscreen></iframe>"
        );
        Window popupWindow = new Window();
        popupWindow.setPage(searchBox.getPage());
        popupWindow.appendChild(rickRollHtml);
        popupWindow.setClosable(true);
        popupWindow.doModal();
    }

    private String getSearchQuery() {
        return (String)this.getModel().getValue("query", String.class);
    }

    private void setSearchQuery(String query) {
        this.getModel().setValue("query", query);
    }

    private Boolean isEnabled() {
        return (Boolean)this.getModel().getValue("enabled", Boolean.class);
    }

    private void setEnabled(Boolean enabled) {
        this.getModel().setValue("enabled", enabled);
    }

    public Textbox getSearchBox() {
        return this.searchBox;
    }

    public Button getSearchButton() {
        return this.searchButton;
    }
}