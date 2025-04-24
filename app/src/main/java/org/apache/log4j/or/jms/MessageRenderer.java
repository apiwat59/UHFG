package org.apache.log4j.or.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;

/* loaded from: classes.dex */
public class MessageRenderer implements ObjectRenderer {
    @Override // org.apache.log4j.or.ObjectRenderer
    public String doRender(Object obj) {
        if (!(obj instanceof Message)) {
            return obj.toString();
        }
        StringBuffer stringBuffer = new StringBuffer();
        Message message = (Message) obj;
        try {
            stringBuffer.append("DeliveryMode=");
            int jMSDeliveryMode = message.getJMSDeliveryMode();
            stringBuffer.append(jMSDeliveryMode != 1 ? jMSDeliveryMode != 2 ? "UNKNOWN" : "PERSISTENT" : "NON_PERSISTENT");
            stringBuffer.append(", CorrelationID=");
            stringBuffer.append(message.getJMSCorrelationID());
            stringBuffer.append(", Destination=");
            stringBuffer.append(message.getJMSDestination());
            stringBuffer.append(", Expiration=");
            stringBuffer.append(message.getJMSExpiration());
            stringBuffer.append(", MessageID=");
            stringBuffer.append(message.getJMSMessageID());
            stringBuffer.append(", Priority=");
            stringBuffer.append(message.getJMSPriority());
            stringBuffer.append(", Redelivered=");
            stringBuffer.append(message.getJMSRedelivered());
            stringBuffer.append(", ReplyTo=");
            stringBuffer.append(message.getJMSReplyTo());
            stringBuffer.append(", Timestamp=");
            stringBuffer.append(message.getJMSTimestamp());
            stringBuffer.append(", Type=");
            stringBuffer.append(message.getJMSType());
        } catch (JMSException e) {
            LogLog.error("Could not parse Message.", e);
        }
        return stringBuffer.toString();
    }
}
