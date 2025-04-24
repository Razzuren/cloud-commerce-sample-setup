package raz.zuren.backoffice.widgets;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncResult;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.cronjob.impl.DefaultCronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import java.util.Collection;
import java.util.Collections;

public class SyncJobProgressWidgetController extends DefaultWidgetController {

    private Textbox jobCodeTextbox;
    private Button trackButton;
    private Label statusLabel;

    @Autowired
    private CatalogSynchronizationService syncJobService;

    @Autowired
    private DefaultCronJobService cronJobService;
    private SyncResult runningExecution;

    String label;

    @ViewEvent(componentID = "trackButton", eventName = "onClick")
    public void onTrackButtonClick() throws InterruptedException {
        String jobCode = jobCodeTextbox.getValue();

        if (syncJobService == null) {
            Clients.showNotification("SyncJobService not available", "error", null, "middle_center", 2000);
            return;
        }

        if (cronJobService == null) {
            Clients.showNotification("CronjobService not available", "error", null, "middle_center", 2000);
            return;
        }

        CatalogVersionSyncCronJobModel selectedCronJob = (CatalogVersionSyncCronJobModel) cronJobService.getCronJob("jobCode");

        if (selectedCronJob != null){
            runningExecution = new SyncResult(selectedCronJob);
            getModel().setValue("syncJobCode", jobCode);
            updateSyncProgress();
        } else {
            statusLabel.setValue("No active sync jobs found for code: " + jobCode);
        }
    }

    public void updateSyncProgress() throws InterruptedException {
        if (runningExecution != null) {
            boolean running = runningExecution.isRunning();

            if (running) {

                boolean fullSync = runningExecution.getCronJob().getFullSync();
                boolean forceUpdate = runningExecution.getCronJob().getForceUpdate();
                Collection<ItemModel> pending = runningExecution.getCronJob().getPendingItems();
                Collection<ItemModel> finished = runningExecution.getCronJob().getFinishedItems();

                // Create HTML formatted label
                StringBuilder labelBuilder = new StringBuilder();
                labelBuilder.append("<html>");
                labelBuilder.append("<strong>Sync Job Code: </strong>").append(runningExecution.getCronJob().getCode()).append("<br/>");
                labelBuilder.append("<strong>Status: </strong>").append(running ? "Running" : "Stopped").append("<br/>");
                labelBuilder.append("<strong>Full Sync: </strong>").append(fullSync ? "Yes" : "No").append("<br/>");
                labelBuilder.append("<strong>Force Update: </strong>").append(forceUpdate ? "Yes" : "No").append("<br/>");
                labelBuilder.append("<strong>Pending Items: </strong>").append(pending.size()).append("<br/>");
                labelBuilder.append("<strong>Finished Items: </strong>").append(finished.size()).append("<br/>");
                labelBuilder.append("</html>");

                String label = labelBuilder.toString();
                statusLabel.setValue(label);

                wait(10000);
                Events.sendEvent(new Event("updateSyncStatus"));
            } else statusLabel.setValue(runningExecution.getCronJob().getCode() + " has finished.");
        }
    }

    @SocketEvent(socketId = "jobCodeInput")
    public void onJobCodeInputEvent(String jobCode) {
        jobCodeTextbox.setValue(jobCode);
    }
}