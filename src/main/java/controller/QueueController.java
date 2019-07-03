package controller;

import com.rabbitmq.client.DeliverCallback;
import exceptions.ApplicationException;
import exceptions.QueueException;
import lombok.extern.log4j.Log4j2;
import model.AppAndTrendsInfo;
import model.ApplicationInfo;
import model.CSVable;
import model.SummarizedTrendInfo;
import persist.LocalCsvStorage;
import queue.QueueService;
import queue.RabbitMQService;
import utils.Config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class QueueController implements Runnable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.getValue("time.datetime_format"));
    private String dataQueue;
    private String requestQueue;
    private QueueService queueService;
    private List<LocalCsvStorage> storages = new ArrayList<>();

    public QueueController(QueueService queueService, String dataQueue, String requestQueue) {
        this.queueService = queueService;
        this.dataQueue = dataQueue;
        this.requestQueue = requestQueue;
        storages.add(new LocalCsvStorage(Paths.get(Config.getValue("file.buffer_play_market"))));
        storages.add(new LocalCsvStorage(Paths.get(Config.getValue("file.buffer_google_trends"))));
        initQueues();
    }

    public QueueController() throws ApplicationException {
        this(new RabbitMQService(), Config.getValue("queue.name.data"), Config.getValue("queue.name.request"));
    }

    private void initQueues() {
        log.debug("Declaring queues...");
        queueService.declareQueue(dataQueue);
        queueService.declareQueue(requestQueue);
        log.debug("Declared.");
    }

    private void hookToRequests() {
        DeliverCallback deliver = getRequestsConsumer(requestQueue);
        queueService.consume(requestQueue, true, deliver, consumerTag -> log.debug("[" + requestQueue + "] Cancelled receiving"));
        log.debug("[" + requestQueue + "] Hooked to requests queue!");
    }

    private DeliverCallback getRequestsConsumer(String queueName) {
        return (consumerTag, content) -> {
            String message = new String(content.getBody(), StandardCharsets.UTF_8);
            String[] parts = message.split(";");
            if (parts.length != 2) {
                QueueException exception = new QueueException("[" + queueName + "] Invalid request message: " + message);
                log.warn(exception);
                throw exception;
            }
            LocalDateTime from = LocalDateTime.parse(parts[0], formatter);
            LocalDateTime to = LocalDateTime.parse(parts[1], formatter);

            log.info("[" + queueName + "] Received request from " + from.format(formatter) + " to " + to.format(formatter));
//            sendData(from, to);
            sendInfoAndTrends(from, to);
        };
    }

    private void sendInfoAndTrends(LocalDateTime from, LocalDateTime to) {
        List<ApplicationInfo> infos = new ArrayList<>();
        List<SummarizedTrendInfo> trends = new ArrayList<>();
        for (LocalCsvStorage storage : storages) {
            List<CSVable> readList = storage.read(from, to);
            if (readList.size() == 0) {
                return;
            }
            CSVable firstElem = readList.get(0);
            if (firstElem instanceof ApplicationInfo) {
                for (CSVable csVable : readList) {
                    infos.add((ApplicationInfo) csVable);
                }
            } else if (firstElem instanceof SummarizedTrendInfo) {
                for (CSVable csVable : readList) {
                    trends.add((SummarizedTrendInfo) csVable);
                }
            }
        }

        List<AppAndTrendsInfo> infoToSend = new ArrayList<>();
        for (ApplicationInfo info : infos) {
            infoToSend.add(fromSuitableTrends(info, trends.toArray(new SummarizedTrendInfo[0])));
        }

        if (infoToSend.size() == 0) {
            return;
        }
        String[] csvHeader = infoToSend.get(0).getCsvHeader();

        String[][] payload = new String[infoToSend.size() + 1][csvHeader.length];
        payload[0] = csvHeader;
        int payloadIndex = 1;
        for (AppAndTrendsInfo appAndTrendsInfo : infoToSend) {
            payload[payloadIndex] = appAndTrendsInfo.toCsv();
            payloadIndex++;
        }
        queueService.publish(dataQueue, payload);
    }

    private AppAndTrendsInfo fromSuitableTrends(ApplicationInfo info, SummarizedTrendInfo[] trends) {
        LocalDateTime infoTime = info.getTimestamp();
        List<SummarizedTrendInfo> suitableTrends = new ArrayList<>();
        for (SummarizedTrendInfo trend : trends) {
            if (infoTime.minusHours(1).compareTo(trend.getTimestamp()) <= 0 &&
                    infoTime.plusHours(1).compareTo(trend.getTimestamp()) >= 0) {
                suitableTrends.add(trend);
            }
        }

        int suitableTrendsSize = suitableTrends.size();
        if (suitableTrendsSize != 3) {
            log.warn("Suitable Trends size is not 3 (" + suitableTrendsSize + "), maybe search query size got bigger? " +
                    "Timestamp is " + info.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        return new AppAndTrendsInfo(info, suitableTrends.toArray(new SummarizedTrendInfo[0]));
    }

    private void sendData(LocalDateTime from, LocalDateTime to) {
        List<String[]> payload = new ArrayList<>();
        for (LocalCsvStorage storage : storages) {
            List<CSVable> infos = storage.read(from, to);
            if (infos.size() > 0) {
                String[] headers = infos.get(0).getCsvHeader();
                payload.add(headers);
                for (CSVable info : infos) {
                    payload.add(info.toCsv());
                }
                int payloadSize = payload.size();
                String[][] rawPayload = new String[payloadSize][headers.length];
                for (int i = 0; i < payloadSize; i++) {
                    System.arraycopy(payload.get(i), 0, rawPayload[i], 0, headers.length);
                }
                queueService.publish(dataQueue, rawPayload);
            }
            payload.clear();
        }
    }

    @Override
    public void run() {
        hookToRequests();
    }
}
