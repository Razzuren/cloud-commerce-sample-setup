
package raz.zuren;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

public class RazzSlf4jAuditableActionHandler implements AuditableActionHandler {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(de.hybris.platform.audit.actions.impl.Slf4jAuditableActionHandler.class);

    public RazzSlf4jAuditableActionHandler(UserService userService) {
        this.userService = (UserService)Objects.requireNonNull(userService);
    }

    public void auditAction(AuditableActions.Action action) {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();

        try {
            getSessionID().ifPresent((sid) -> {
                MDC.put("audit.sessionId", sid);
            });
            MDC.put("audit.actionName", action.getActionName());
            MDC.put("audit.actionId", action.getActionId());
            MDC.put("audit.user", (String)this.getCurrentUserPk().map(PK::toString).orElse("unknown"));
            action.getActionAttributes().forEach((s, o) -> {
                MDC.put("audit.data." + s, o.toString());
            });
            //stops the annoying db_audit
            if (LOGGER.isDebugEnabled()) LOGGER.debug(MarkerFactory.getMarker("AUDIT"), action.getActionName());
        } finally {
            MDC.setContextMap(copyOfContextMap);
        }

    }

    private static Optional<String> getSessionID() {
        return !JaloSession.hasCurrentSession() ? Optional.empty() : Optional.of(JaloSession.getCurrentSession().getSessionID());
    }

    private Optional<PK> getCurrentUserPk() {
        return Optional.ofNullable(this.userService.getCurrentUser()).map(AbstractItemModel::getPk);
    }
}
