package pep.per.mint.solution.mi.service;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mococo.mi.server.api.ClientSocket;
import com.mococo.mi.server.api.ConnectionManager;
import com.mococo.mi.server.api.MIManagerAPIException;
import com.mococo.mi.server.api.data.AgentMonitorObject;
import com.mococo.mi.server.api.data.BrokerMonitorObject;
import com.mococo.mi.server.api.data.HostMonitorObject;
import com.mococo.mi.server.api.data.UserObject;
import com.mococo.mi.server.api.handler.LoginHandler;
import com.mococo.mi.server.api.handler.SystemMonitorHandler;

import pep.per.mint.common.data.basic.Server;
import pep.per.mint.common.data.basic.miagent.MIAgentState;
import pep.per.mint.common.data.basic.miagent.MIRunnerState;
import pep.per.mint.common.util.Util;

@Service
public class MIMonitorService {

    Logger logger = LoggerFactory.getLogger(MIMonitorService.class);

    Map<String, Server> serverMap = new HashMap<String, Server>();
    Map<String, MIAgentState> agentStatusMap = new HashMap<String, MIAgentState>();
    Map<String, MIRunnerState> runnerStatusMap = new HashMap<String, MIRunnerState>();

    String managerIp;
    int managerPort = 8991;
    String userName;
    String password;

    public Server getServer(String serverCd) {
        return serverMap.get(serverCd);
    }

    public MIAgentState getAgentStatus(String agentName) {
        return agentStatusMap.get(agentName);
    }

    public MIRunnerState getBrokerStatus(String runnerName) {
        return runnerStatusMap.get(runnerName);
    }

    public void setConnectionInfo(String ip, int port, String userName, String password) {
        this.managerIp = ip;
        this.managerPort = port;
        this.userName = userName;
        this.password = password;
    }

//	public void monitor() throws UnknownHostException, IOException, MIManagerAPIException {
//		// TODO Auto-generated method stub
//		// MI Manager ????????? ?????? ?????????????????? ??????
//		ConnectionManager connMgr = ConnectionManager.getInstance();
//        
//		// ?????? ?????? ??????
//		connMgr.setConnectionInfo(managerIp, managerPort);
//
//		// ?????? ?????? ?????? ???????????? ??????.
//		LoginHandler loginHandler = new LoginHandler(null, null);
//
//
//		UserObject userObj = loginHandler.checkUser(userName, password);
//
//		// ????????? ?????? ????????? ?????? ?????? ????????? ??????
//		SystemMonitorHandler systemMonitorHandler = new SystemMonitorHandler(userObj.getUuid(), userObj.getName());
//
//		// ????????? ??????????????? LIST??? ????????????. ????????? -> ???????????? -> ?????????(??????) ?????????
//		// ?????????(hierarchical)?????? ???????????? ??????.
//		// ??? List??? ????????? ??????????????? MonotorObject??? ?????? ?????? ????????? ?????????.
//		List systemList = systemMonitorHandler.getSystemMonitorInfo();
// 
//        
//        
//		// ????????? ?????? ????????? ????????????.
//		for (Object hostObject : systemList) {
//			if (hostObject == null)
//				continue;
//
//			// ????????? ??? ???????????? ????????? ????????????.
//			HostMonitorObject hmo = (HostMonitorObject) hostObject;
//			logger.debug("-----------------------------------------------");
//			logger.debug(Util.join("// HOST NAME : " + hmo.getHostName()));
//			logger.debug("-----------------------------------------------");
//
//			Server server = new Server();
//			server.setServerCd(hmo.getHostName());
//			serverMap.put(hmo.getHostName(), server);
//
//			List agentList = hmo.getAgentList();
//			for (Object agentObject : agentList) {
//				if (agentObject == null)
//					continue;
//
//				// ???????????? ??? ?????????(??????)????????? ????????????.
//				// ???????????? ????????? ??????????????? ????????? ??? ??????.
//				AgentMonitorObject amo = (AgentMonitorObject) agentObject;
//				logger.debug("-----------------------------------------------");
//				logger.debug(Util.join("// AGENT NAME : " , amo.getAgentName(), " (Online : " ,amo.isBOnline() , ")"));
//				logger.debug("-----------------------------------------------");
//
//				MIAgentState agentState = new MIAgentState();
//				agentState.setGetDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//				agentState.setRegApp("mint-batch");
//				agentState.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//				String status = MIAgentState.STAT_NOT_READY;
//				String msg = "";
//				if(amo.isBOnline()){
//					status = MIAgentState.STAT_ONLINE;
//					msg = "online";
//				}else{
//					status = MIAgentState.STAT_OFFLINE;
//					msg = "offline";
//				}
//				agentState.setStatus(status);
//				agentStatusMap.put(Util.join(server.getServerCd() ,"@",amo.getAgentName()), agentState);
//
//
//				List brokerList = amo.getBrokerObject();
//				for (Object brokerObject : brokerList) {
//					if (brokerObject == null)
//						continue;
//					// ?????????(??????)??? ????????? ??????????????? ????????? ??? ??????.
//					BrokerMonitorObject bmo = (BrokerMonitorObject) brokerObject;
//					logger.debug("-----------------------------------------------");
//					logger.debug(Util.join("// RUNNER NAME : " , bmo.getBrokerName() , " (Online : " ,bmo.isBOnline() , ")"));
//					logger.debug("-----------------------------------------------");
//
//					MIRunnerState runnerState = new MIRunnerState();
//					runnerState.setGetDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//					runnerState.setRegApp("mint-batch");
//					runnerState.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//					status = MIRunnerState.STAT_NOT_READY;
//					msg = "";
//					if(bmo.isBOnline()){
//						status = MIRunnerState.STAT_ONLINE;
//						msg = "online";
//					}else{
//						status = MIRunnerState.STAT_OFFLINE;
//						msg = "offline";
//					}
//					runnerState.setStatus(status);
//					runnerStatusMap.put(Util.join(server.getServerCd() ,"@",amo.getAgentName(),"@", bmo.getBrokerName()), runnerState);
//				}
//			}
//		}
//		// TODO ????????? ??????????????? ??????.
//	}
    ConnectionManager connMgr = null;
    LoginHandler loginHandler = null;
    SystemMonitorHandler systemMonitorHandler = null;

    public void open() throws IOException, UnknownHostException, MIManagerAPIException {
        connMgr = ConnectionManager.getInstance();
        connMgr.setConnectionInfo(managerIp, managerPort);

        // ?????? ?????? ?????? ???????????? ??????.
        loginHandler = new LoginHandler(null, null);
        UserObject userObj = loginHandler.checkUser(userName, password);
        systemMonitorHandler = new SystemMonitorHandler(userObj.getUuid(), userObj.getName());
    }

    public void monitor() throws UnknownHostException, IOException, MIManagerAPIException {

        try {

            // ????????? ??????????????? LIST??? ????????????. ????????? -> ???????????? -> ?????????(??????) ?????????
            // ?????????(hierarchical)?????? ???????????? ??????.
            // ??? List??? ????????? ??????????????? MonotorObject??? ?????? ?????? ????????? ?????????.
            List systemList = systemMonitorHandler.getSystemMonitorInfo();

            // ????????? ?????? ????????? ????????????.
            for (Object hostObject : systemList) {
                if (hostObject == null) {
                    continue;
                }

                // ????????? ??? ???????????? ????????? ????????????.
                HostMonitorObject hmo = (HostMonitorObject) hostObject;
                logger.debug("-----------------------------------------------");
                logger.debug(Util.join("// HOST NAME : " + hmo.getHostName()));
                logger.debug("-----------------------------------------------");

                Server server = new Server();
                server.setServerCd(hmo.getHostName());
                serverMap.put(hmo.getHostName(), server);

                List agentList = hmo.getAgentList();
                for (Object agentObject : agentList) {
                    if (agentObject == null) {
                        continue;
                    }

                    // ???????????? ??? ?????????(??????)????????? ????????????.
                    // ???????????? ????????? ??????????????? ????????? ??? ??????.
                    AgentMonitorObject amo = (AgentMonitorObject) agentObject;
                    logger.debug("-----------------------------------------------");
                    logger.debug(Util.join("// AGENT NAME : ", amo.getAgentName(), " (Online : ", amo.isBOnline(), ")"));
                    logger.debug("-----------------------------------------------");

                    MIAgentState agentState = new MIAgentState();
                    agentState.setGetDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
                    agentState.setRegApp("mint-batch");
                    agentState.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
                    String status = MIAgentState.STAT_NOT_READY;
                    String msg = "";
                    if (amo.isBOnline()) {
                        status = MIAgentState.STAT_ONLINE;
                        msg = "online";
                    } else {
                        status = MIAgentState.STAT_OFFLINE;
                        msg = "offline";
                    }
                    agentState.setStatus(status);
                    agentStatusMap.put(Util.join(server.getServerCd(), "@", amo.getAgentName()), agentState);

                    List brokerList = amo.getBrokerObject();
                    for (Object brokerObject : brokerList) {
                        if (brokerObject == null) {
                            continue;
                        }
                        // ?????????(??????)??? ????????? ??????????????? ????????? ??? ??????.
                        BrokerMonitorObject bmo = (BrokerMonitorObject) brokerObject;
                        logger.debug("-----------------------------------------------");
                        logger.debug(Util.join("// RUNNER NAME : ", bmo.getBrokerName(), " (Online : ", bmo.isBOnline(), ")"));
                        logger.debug("-----------------------------------------------");

                        MIRunnerState runnerState = new MIRunnerState();
                        runnerState.setGetDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
                        runnerState.setRegApp("mint-batch");
                        runnerState.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
                        status = MIRunnerState.STAT_NOT_READY;
                        msg = "";
                        if (bmo.isBOnline()) {
                            status = MIRunnerState.STAT_ONLINE;
                            msg = "online";
                        } else {
                            status = MIRunnerState.STAT_OFFLINE;
                            msg = "offline";
                        }
                        runnerState.setStatus(status);
                        runnerStatusMap.put(Util.join(server.getServerCd(), "@", amo.getAgentName(), "@", bmo.getBrokerName()), runnerState);
                    }
                }
            }
            // TODO ????????? ??????????????? ??????.
        } finally {
// ????????? ?????? .        	
//            if(connMgr != null) {
//                try{
//                    ClientSocket cs = connMgr.getConnection();
//                    logger.debug("987234028485-ConnectionManager.ClientSocket.close()-MIAgent ?????? ????????? ????????? ?????????");
//                    if(cs != null) cs.close();
//                }catch(Exception e){
//                    logger.error("987234028485-ConnectionManager.ClientSocket.close()-MIAgent ?????? ????????? ????????? ?????? ?????? ??????", e);
//                }
//            } 
        }
    }

    public Map<String, Server> getServerMap() {
        return serverMap;
    }

    public Map<String, MIAgentState> getAgentStateMap() {
        return agentStatusMap;
    }

    public Map<String, MIRunnerState> getRunnerStateMap() {
        return runnerStatusMap;
    }

}
