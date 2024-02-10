package TestJavaClient.utils;

import TestJavaClient.OrderDlg;

import com.ib.client.*;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class EwrapImpl implements EWrapper {

    private EJavaSignal m_signal = new EJavaSignal();
    private EClientSocket m_client = new EClientSocket(this, m_signal);
    private EReader m_reader;
    private OrderDlg m_orderDlg = new OrderDlg(null);
    private List<TagValue> m_chartOptions = new ArrayList<>();
    private List<TagValue> m_realTimeBarsOptions = new ArrayList<>();
    boolean m_bIsFAAccount = false;

    private List<ContractDetails> contractDetailsList = new LinkedList<>();

    private ContractDetails currentContract;

    private AtomicInteger counterMin = new AtomicInteger(0);

    private AtomicInteger counterMin5 = new AtomicInteger(1000005);

    private AtomicInteger counterHourly = new AtomicInteger(2000005);

    private AtomicInteger counterDaily = new AtomicInteger(3000005);


    public BarList minList = new BarList();

    public BarList min5List = new BarList();

    public BarList hourlyList = new BarList();

    public BarList dailyList = new BarList();

    public SampleCandlestick minChart;

    public SampleCandlestick min5Chart;

    public SampleCandlestick hourlyChart;

    public SampleCandlestick dailyChart;

    public MainFrame mainFrame;

//    private List<>

    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    JPanel scrollingWindowDisplayPanel;

    public EwrapImpl() {
        onConnect2();
        onReqMarketDataType2();
        Contract contract = m_orderDlg.contract();
        contract.symbol("ES");
        contract.secType("FUT");
        contract.exchange("CME");
        contract.currency("USD");

        try {
            List<ContractDetails> contractDetails = lookupContract(contract);
        } catch (InterruptedException e) {
            System.out.println(e.getCause());
        }
    }

    public void cronJob(HistoryEnum historyEnum) {
        onHistoricalData2(currentContract, historyEnum);

    }

    interface ContractDetailsCallback {
        void onContractDetails(ContractDetails contractDetails);

        void onContractDetailsEnd();

        void onError(int errorCode, String errorMsg);
    }

    private final Map<Integer, ContractDetailsCallback> m_callbackMap = new HashMap<>();

    List<ContractDetails> lookupContract(Contract contract) throws InterruptedException {
        final CompletableFuture<List<ContractDetails>> future = new CompletableFuture<>();

        synchronized (m_callbackMap) {
            m_callbackMap.put(0, new ContractDetailsCallback() {

                private final List<ContractDetails> list = new ArrayList<>();

                @Override
                public void onError(int errorCode, String errorMsg) {
                    System.out.println("ErrorCode: " + errorCode + " Message: " + errorCode);
                    future.complete(list);
                }

                @Override
                public void onContractDetailsEnd() {
                    future.complete(list);
                }

                @Override
                public void onContractDetails(ContractDetails contractDetails) {
                    list.add(contractDetails);
                }
            });
        }
        m_client.reqContractDetails(0, contract);
        try {
            return future.get();
        } catch (final ExecutionException e) {
            return null;
        } finally {
            synchronized (m_callbackMap) {
                m_callbackMap.remove(0);
            }
        }
    }


    private void onConnect2() {

        m_client.optionalCapabilities("");
        m_client.eConnect("", 7496, 0);
        if (m_client.isConnected()) {
            System.out.println("Connected to Tws server version " +
                    m_client.serverVersion() + " at " +
                    m_client.getTwsConnectionTime());
        }

        m_reader = new EReader(m_client, m_signal);

        m_reader.start();

        new Thread(() -> {
            processMessages2();

            int i = 0;
            System.out.println(i);
        }).start();
    }

    private void processMessages2() {

        while (m_client.isConnected()) {
            m_signal.waitForSignal();
            try {
                m_reader.processMsgs();
            } catch (Exception e) {
                error(e);
            }
        }
    }

    private void onDisconnect() {
        // disconnect from TWS
        m_client.eDisconnect();
    }

    private void onReqRealTimeBars2(ContractDetails contractDetails) {
        // run m_orderDlg
        m_orderDlg.init("RTB Options", true, "Real Time Bars Options", m_realTimeBarsOptions);
        m_orderDlg.onOk();
//        m_orderDlg.


        m_realTimeBarsOptions = m_orderDlg.options();

        // req real time bars
        m_client.reqRealTimeBars(m_orderDlg.id(), contractDetails.contract(),
                5 /* TODO: parse and use m_orderDlg.m_barSizeSetting */,
                m_orderDlg.whatToShow(), m_orderDlg.useRTH() > 0, m_realTimeBarsOptions);
    }


    private void onHeadTimestamp(Contract contract) {


        m_client.reqHeadTimestamp(m_orderDlg.id(), contract, "TRADES",
                m_orderDlg.useRTH(), m_orderDlg.formatDate());
    }

    private void onHistoricalData2(ContractDetails contractDetails, HistoryEnum historyEnum) {


        // run m_orderDlg
        m_orderDlg.init("Chart Options", true, "Chart Options", m_chartOptions);


        m_orderDlg.onOk();


        m_chartOptions = m_orderDlg.options();
        LocalDateTime date = LocalDateTime.now();
        String dateString = date.format(format);
        dateString = LocalDateTime.from(format.parse(dateString)).minusHours(1).format(format);
        dateString = dateString + " " + contractDetails.timeZoneId();
        switch (historyEnum){
            case MIN -> m_client.reqHistoricalData(counterMin.get(), contractDetails.contract(),
                    dateString, "7200 S",
                    "1 min", "TRADES",
                    1, 1, false, null);
            case MIN5 ->  m_client.reqHistoricalData(counterMin5.get(), contractDetails.contract(),
                    dateString, "36000 S",
                    "5 mins", "TRADES",
                    1, 1, false, null);
            case HOURLY -> m_client.reqHistoricalData(counterHourly.get(), contractDetails.contract(),
                    dateString, "3 D",
                    "1 hour", "TRADES",
                    1, 1, false, null);
            case DAILY -> m_client.reqHistoricalData(counterDaily.get(), contractDetails.contract(),
                    dateString, "2 M",
                    "1 day", "TRADES",
                    1, 1, false, null);

        }

        // req historical data








    }

    private void onCancelHistoricalData(int reqId) {
        m_client.cancelHistoricalData(reqId);
    }

    private void onReqContractData() {

        m_client.reqContractDetails(currentContract.conid(), currentContract.contract());
    }


    private void onReqMarketDataType2() {
        // run m_orderDlg

        m_client.reqMarketDataType(MarketDataType.DELAYED);

    }


    public void contractDetails(int reqId, ContractDetails contractDetails) {
        ContractDetailsCallback callback;
        synchronized (m_callbackMap) {
            callback = m_callbackMap.get(reqId);
        }
        if (callback != null) {
            callback.onContractDetails(contractDetails);
        }

        String msg = EWrapperMsgGenerator.contractDetails(reqId, contractDetails);
        contractDetailsList.add(contractDetails);
        System.out.println(msg);
    }

    @Override
    public void bondContractDetails(int i, ContractDetails contractDetails) {

    }

    public void contractDetailsEnd(int reqId) {
        ContractDetailsCallback callback;
        synchronized (m_callbackMap) {
            callback = m_callbackMap.get(reqId);
        }
        if (callback != null) {
            callback.onContractDetailsEnd();
        }

        String msg = EWrapperMsgGenerator.contractDetailsEnd(reqId);
        System.out.println(msg);
        getLatestContractDetail();
    }

    @Override
    public void execDetails(int i, Contract contract, Execution execution) {

    }

    @Override
    public void execDetailsEnd(int i) {

    }

    @Override
    public void updateMktDepth(int i, int i1, int i2, int i3, double v, Decimal decimal) {

    }

    @Override
    public void updateMktDepthL2(int i, int i1, String s, int i2, int i3, double v, Decimal decimal, boolean b) {

    }

    @Override
    public void updateNewsBulletin(int i, int i1, String s, String s1) {

    }

    @Override
    public void managedAccounts(String s) {

    }

    @Override
    public void receiveFA(int i, String s) {

    }

    public void getLatestContractDetail() {
        currentContract = contractDetailsList.get(0);
        contractDetailsList.forEach(contractDetails -> {
            if (Integer.valueOf(currentContract.contractMonth()) > Integer.valueOf(contractDetails.contractMonth())) {
                currentContract = contractDetails;
            }
        });
        onHeadTimestamp(currentContract.contract());
        onHistoricalData2(currentContract, HistoryEnum.MIN);
        onHistoricalData2(currentContract, HistoryEnum.MIN5);
        onHistoricalData2(currentContract, HistoryEnum.HOURLY);
        onHistoricalData2(currentContract, HistoryEnum.DAILY);
    }

    public void historicalData(int reqId, Bar bar) {
        if (reqId < 1000000) {
            setNewBar(reqId, bar, minList);

        } else if (reqId > 1000000 && reqId < 2000000) {
            setNewBar(reqId, bar, min5List);

        } else if (reqId > 2000000 && reqId < 3000000) {
            setNewBar(reqId, bar, hourlyList);
        } else {
            setNewBar(reqId, bar, dailyList);
        }
//        String msg = EWrapperMsgGenerator.historicalData(reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap());System.out.println(msg);
    }

    @Override
    public void scannerParameters(String s) {

    }

    @Override
    public void scannerData(int i, int i1, ContractDetails contractDetails, String s, String s1, String s2, String s3) {

    }

    @Override
    public void scannerDataEnd(int i) {

    }

    @Override
    public void realtimeBar(int i, long l, double v, double v1, double v2, double v3, Decimal decimal, Decimal decimal1, int i1) {

    }

    @Override
    public void currentTime(long l) {

    }

    @Override
    public void fundamentalData(int i, String s) {

    }

    @Override
    public void deltaNeutralValidation(int i, DeltaNeutralContract deltaNeutralContract) {

    }

    @Override
    public void tickSnapshotEnd(int i) {

    }

    @Override
    public void marketDataType(int i, int i1) {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

    }

    @Override
    public void position(String s, Contract contract, Decimal decimal, double v) {

    }

    @Override
    public void positionEnd() {

    }

    @Override
    public void accountSummary(int i, String s, String s1, String s2, String s3) {

    }

    @Override
    public void accountSummaryEnd(int i) {

    }

    @Override
    public void verifyMessageAPI(String s) {

    }

    @Override
    public void verifyCompleted(boolean b, String s) {

    }

    @Override
    public void verifyAndAuthMessageAPI(String s, String s1) {

    }

    @Override
    public void verifyAndAuthCompleted(boolean b, String s) {

    }

    @Override
    public void displayGroupList(int i, String s) {

    }

    @Override
    public void displayGroupUpdated(int i, String s) {

    }

    public void error(Exception ex) {
        String msg = EWrapperMsgGenerator.error(ex);
        System.out.println(msg);


    }

    public void error(String str) {
        String msg = EWrapperMsgGenerator.error(str);
        System.out.println(msg);
    }

    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {

        final ContractDetailsCallback callback;
        synchronized (m_callbackMap) {
            callback = m_callbackMap.get(id);
        }
        if (callback != null) {
            callback.onError(errorCode, errorMsg);
        } else if (id == -1) {
            final Collection<ContractDetailsCallback> callbacks;
            synchronized (m_callbackMap) {
                callbacks = new ArrayList<>(m_callbackMap.size());
                callbacks.addAll(m_callbackMap.values());
            }
            for (final ContractDetailsCallback cb : callbacks) {
                cb.onError(errorCode, errorMsg);
            }
        }

        String msg = EWrapperMsgGenerator.error(id, errorCode, errorMsg, advancedOrderRejectJson);
        System.out.println(msg);
    }


    @Override
    public void connectionClosed() {
        String msg = EWrapperMsgGenerator.connectionClosed();
        System.out.println(msg);

    }

    @Override
    public void connectAck() {
        if (m_client.isAsyncEConnect())
            m_client.startAPI();
    }

    @Override
    public void positionMulti(int i, String s, String s1, Contract contract, Decimal decimal, double v) {

    }

    @Override
    public void positionMultiEnd(int i) {

    }

    @Override
    public void accountUpdateMulti(int i, String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void accountUpdateMultiEnd(int i) {

    }

    public void setNewBar(int reqId, Bar bar, BarList barList) {
        if (barList.getReqId() != reqId) {
            barList.setReqId(reqId);
            barList.setBarList(new LinkedList<>());
            barList.getBarList().add(bar);
        } else {
            barList.getBarList().add(bar);
        }
    }


    public void historicalDataEnd(int reqId, String startDate, String endDate) {
        String msg = EWrapperMsgGenerator.historicalDataEnd(reqId, startDate, endDate);
        System.out.println(msg);
        if (reqId == counterMin.get()) {
            minChart = new SampleCandlestick("1 Min Data Chart", minList.getBarList());
            counterMin.incrementAndGet();
        } else if (reqId == counterMin5.get()) {
            min5Chart = new SampleCandlestick("5 Min Data Chart", min5List.getBarList());
            counterMin5.incrementAndGet();
        } else if (reqId == counterHourly.get()) {
            hourlyChart = new SampleCandlestick("1 Hour Data Chart", hourlyList.getBarList());
            counterHourly.incrementAndGet();
        } else if (reqId == counterDaily.get()) {
            dailyChart = new SampleCandlestick("1 Day Data Chart", dailyList.getBarList());
            counterDaily.incrementAndGet();
        }
        onCancelHistoricalData(reqId);

        if (minChart != null && min5Chart != null && hourlyChart != null && dailyChart != null) {
            if (mainFrame == null)
                mainFrame = new MainFrame(minChart, min5Chart, hourlyChart, dailyChart);
            } else {

        }
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass,
                                                    String multiplier, Set<String> expirations, Set<Double> strikes) {
        String msg = EWrapperMsgGenerator.securityDefinitionOptionalParameter(reqId, exchange, underlyingConId, tradingClass, multiplier, expirations, strikes);
        System.out.println(msg);
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        String msg = EWrapperMsgGenerator.softDollarTiers(tiers);

        System.out.println(msg);
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        String msg = EWrapperMsgGenerator.familyCodes(familyCodes);
        System.out.println(msg);
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        String msg = EWrapperMsgGenerator.symbolSamples(reqId, contractDescriptions);
        System.out.println(msg);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        String msg = EWrapperMsgGenerator.mktDepthExchanges(depthMktDataDescriptions);
        System.out.println(msg);
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {
        String msg = EWrapperMsgGenerator.tickNews(tickerId, timeStamp, providerCode, articleId, headline, extraData);
        System.out.println(msg);
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {
        String msg = EWrapperMsgGenerator.smartComponents(reqId, theMap);

        System.out.println(msg);
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
        String msg = EWrapperMsgGenerator.tickReqParams(tickerId, minTick, bboExchange, snapshotPermissions);
        System.out.println(msg);
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        String msg = EWrapperMsgGenerator.newsProviders(newsProviders);
        System.out.println(msg);
    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
        String msg = EWrapperMsgGenerator.newsArticle(requestId, articleType, articleText);
    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
        String msg = EWrapperMsgGenerator.historicalNews(requestId, time, providerCode, articleId, headline);
        System.out.println(msg);
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
        String msg = EWrapperMsgGenerator.historicalNewsEnd(requestId, hasMore);
        System.out.println(msg);
    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        String msg = EWrapperMsgGenerator.headTimestamp(reqId, headTimestamp);
        System.out.println(msg);
    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {
        String msg = EWrapperMsgGenerator.histogramData(reqId, items);

        System.out.println(msg);
    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        historicalData(reqId, bar);
    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
        String msg = EWrapperMsgGenerator.rerouteMktDataReq(reqId, conId, exchange);
        System.out.println(msg);

        System.out.println(msg);
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
        String msg = EWrapperMsgGenerator.rerouteMktDepthReq(reqId, conId, exchange);

        System.out.println(msg);
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
        String msg = EWrapperMsgGenerator.marketRule(marketRuleId, priceIncrements);

        System.out.println(msg);
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        String msg = EWrapperMsgGenerator.pnl(reqId, dailyPnL, unrealizedPnL, realizedPnL);

        System.out.println(msg);
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
        String msg = EWrapperMsgGenerator.pnlSingle(reqId, pos, dailyPnL, unrealizedPnL, realizedPnL, value);

        System.out.println(msg);
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean last) {
        StringBuilder msg = new StringBuilder();

        for (HistoricalTick tick : ticks) {
            msg.append(EWrapperMsgGenerator.historicalTick(reqId, tick.time(), tick.price(), tick.size()));
            msg.append("\n");
        }

        System.out.println(msg);
    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
        StringBuilder msg = new StringBuilder();

        for (HistoricalTickBidAsk tick : ticks) {
            msg.append(EWrapperMsgGenerator.historicalTickBidAsk(reqId, tick.time(), tick.tickAttribBidAsk(), tick.priceBid(), tick.priceAsk(), tick.sizeBid(),
                    tick.sizeAsk()));
            msg.append("\n");
        }

        System.out.println(msg);
    }


    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
        StringBuilder msg = new StringBuilder();

        for (HistoricalTickLast tick : ticks) {
            msg.append(EWrapperMsgGenerator.historicalTickLast(reqId, tick.time(), tick.tickAttribLast(), tick.price(), tick.size(), tick.exchange(),
                    tick.specialConditions()));
            msg.append("\n");
        }

        System.out.println(msg);
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast,
                                  String exchange, String specialConditions) {
        String msg = EWrapperMsgGenerator.tickByTickAllLast(reqId, tickType, time, price, size, tickAttribLast, exchange, specialConditions);
        System.out.println(msg);
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize,
                                 TickAttribBidAsk tickAttribBidAsk) {
        String msg = EWrapperMsgGenerator.tickByTickBidAsk(reqId, time, bidPrice, askPrice, bidSize, askSize, tickAttribBidAsk);
        System.out.println(msg);
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        String msg = EWrapperMsgGenerator.tickByTickMidPoint(reqId, time, midPoint);
        System.out.println(msg);
    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {
        String msg = EWrapperMsgGenerator.orderBound(orderId, apiClientId, apiOrderId);

        System.out.println(msg);
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        String msg = EWrapperMsgGenerator.completedOrder(contract, order, orderState);
        System.out.println(msg);
    }

    @Override
    public void completedOrdersEnd() {
        String msg = EWrapperMsgGenerator.completedOrdersEnd();
        System.out.println(msg);
    }

    @Override
    public void replaceFAEnd(int reqId, String text) {
        String msg = EWrapperMsgGenerator.replaceFAEnd(reqId, text);
        System.out.println(msg);
    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {
        String msg = EWrapperMsgGenerator.wshMetaData(reqId, dataJson);
        System.out.println(msg);
    }

    @Override
    public void wshEventData(int reqId, String dataJson) {
        String msg = EWrapperMsgGenerator.wshEventData(reqId, dataJson);
        System.out.println(msg);
    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {
        String msg = EWrapperMsgGenerator.historicalSchedule(reqId, startDateTime, endDateTime, timeZone, sessions);
        System.out.println(msg);
    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {
        String msg = EWrapperMsgGenerator.userInfo(reqId, whiteBrandingId);
        System.out.println(msg);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
        // received price tick
        String msg = EWrapperMsgGenerator.tickPrice(tickerId, field, price, attribs);
        System.out.println(msg);
    }

    @Override
    public void tickSize(int i, int i1, Decimal decimal) {

    }

    @Override
    public void tickOptionComputation(int i, int i1, int i2, double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7) {

    }

    @Override
    public void tickGeneric(int i, int i1, double v) {

    }

    @Override
    public void tickString(int i, int i1, String s) {

    }

    @Override
    public void tickEFP(int i, int i1, double v, String s, double v1, int i2, String s1, double v2, double v3) {

    }

    @Override
    public void orderStatus(int i, String s, Decimal decimal, Decimal decimal1, double v, int i1, int i2, double v1, int i3, String s1, double v2) {

    }

    @Override
    public void openOrder(int i, Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void openOrderEnd() {

    }

    @Override
    public void updateAccountValue(String s, String s1, String s2, String s3) {

    }

    @Override
    public void updatePortfolio(Contract contract, Decimal decimal, double v, double v1, double v2, double v3, double v4, String s) {

    }

    @Override
    public void updateAccountTime(String s) {

    }

    @Override
    public void accountDownloadEnd(String s) {

    }

    @Override
    public void nextValidId(int orderId) {
        String msg = EWrapperMsgGenerator.nextValidId(orderId);
        System.out.println(msg);

    }
}

