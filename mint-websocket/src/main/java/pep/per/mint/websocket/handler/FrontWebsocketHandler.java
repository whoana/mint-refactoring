package pep.per.mint.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.Extension;
import pep.per.mint.common.data.basic.User;
import pep.per.mint.common.msg.handler.MessageHandler;
import pep.per.mint.common.util.Util;
import pep.per.mint.websocket.event.FrontServiceListener;
import pep.per.mint.websocket.event.ServiceEvent;
import pep.per.mint.websocket.exception.WebsocketException;

/**
 *
 * <pre>
 * pep.per.mint.websocket.handler
 * FrontWebsocketHandler.java
 * </pre>
 *
 * @author whoana
 * @date 2018. 7. 2.
 */
public class FrontWebsocketHandler extends TextWebSocketHandler {

	Logger logger = LoggerFactory.getLogger(FrontWebsocketHandler.class);

	@Autowired
	FrontServiceListener listener;

	@Autowired
	MessageHandler messageHandler;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		listener.login(new ServiceEvent(null, session, ServiceEvent.TYPE_FRONT));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		listener.logout(new ServiceEvent(null, session, ServiceEvent.TYPE_FRONT));
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		{
			User user = (User) session.getAttributes().get("user");
			if (user != null) {
				logger.debug(Util.join("receive from ", user.getUserId(),'@', session.getId(), ", msg:\n", message.getPayload()));
			}else {
				logger.debug(Util.join("receive from ", session.getId(), ", msg:\n", message.getPayload()));
			}
		}

		ComMessage<?, ?> request = null;
		try {
			request = messageHandler.deserialize(message.getPayload());
		} catch (Exception e) {
			throw new WebsocketException("Exception:messageHandler.deserialize(message.getPayload())",e);
		}

		Extension extension = request.getExtension();
		if (extension == null) {
			throw new WebsocketException("Exception:ComMessage must have Extension field value.");
		}
		String serviceCd = extension.getServiceCd();
		if (Util.isEmpty(serviceCd)) {
			throw new WebsocketException("Exception:ComMessage must have Extension.serviceCd field value.");
		}

		String msgType = extension.getMsgType();
		if (Util.isEmpty(msgType)) {
			throw new WebsocketException("Exception:ComMessage must have Extension.msgType field value.");
		}

		try {
			listener.requestService(new ServiceEvent(request, session, ServiceEvent.TYPE_FRONT));
		} catch (Exception e) {
			logger.error("The exception is throwed on doing business:", e);
		}

	}

}
