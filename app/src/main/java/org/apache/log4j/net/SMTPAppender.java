package org.apache.log4j.net;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/* loaded from: classes.dex */
public class SMTPAppender extends AppenderSkeleton {
    static /* synthetic */ Class class$org$apache$log4j$spi$TriggeringEventEvaluator;
    private String bcc;
    private int bufferSize;
    protected CyclicBuffer cb;
    private String cc;
    protected TriggeringEventEvaluator evaluator;
    private String from;
    private boolean locationInfo;
    protected Message msg;
    private boolean smtpDebug;
    private String smtpHost;
    private String smtpPassword;
    private String smtpUsername;
    private String subject;
    private String to;

    public SMTPAppender() {
        this(new DefaultEvaluator());
    }

    public SMTPAppender(TriggeringEventEvaluator triggeringEventEvaluator) {
        this.smtpDebug = false;
        this.bufferSize = 512;
        this.locationInfo = false;
        this.cb = new CyclicBuffer(512);
        this.evaluator = triggeringEventEvaluator;
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        MimeMessage mimeMessage = new MimeMessage(createSession());
        this.msg = mimeMessage;
        try {
            addressMessage(mimeMessage);
            String str = this.subject;
            if (str != null) {
                this.msg.setSubject(str);
            }
        } catch (MessagingException e) {
            LogLog.error("Could not activate SMTPAppender options.", e);
        }
    }

    protected void addressMessage(Message message) throws MessagingException {
        String str = this.from;
        if (str != null) {
            message.setFrom(getAddress(str));
        } else {
            message.setFrom();
        }
        String str2 = this.to;
        if (str2 != null && str2.length() > 0) {
            message.setRecipients(Message.RecipientType.TO, parseAddress(this.to));
        }
        String str3 = this.cc;
        if (str3 != null && str3.length() > 0) {
            message.setRecipients(Message.RecipientType.CC, parseAddress(this.cc));
        }
        String str4 = this.bcc;
        if (str4 == null || str4.length() <= 0) {
            return;
        }
        message.setRecipients(Message.RecipientType.BCC, parseAddress(this.bcc));
    }

    @Override // org.apache.log4j.AppenderSkeleton
    public void append(LoggingEvent loggingEvent) {
        if (checkEntryConditions()) {
            loggingEvent.getThreadName();
            loggingEvent.getNDC();
            loggingEvent.getMDCCopy();
            if (this.locationInfo) {
                loggingEvent.getLocationInformation();
            }
            this.cb.add(loggingEvent);
            if (this.evaluator.isTriggeringEvent(loggingEvent)) {
                sendBuffer();
            }
        }
    }

    protected boolean checkEntryConditions() {
        ErrorHandler errorHandler;
        StringBuffer stringBuffer;
        String str;
        String stringBuffer2;
        if (this.msg == null) {
            errorHandler = this.errorHandler;
            stringBuffer2 = "Message object not configured.";
        } else {
            if (this.evaluator == null) {
                errorHandler = this.errorHandler;
                stringBuffer = new StringBuffer();
                str = "No TriggeringEventEvaluator is set for appender [";
            } else {
                if (this.layout != null) {
                    return true;
                }
                errorHandler = this.errorHandler;
                stringBuffer = new StringBuffer();
                str = "No layout set for appender named [";
            }
            stringBuffer.append(str);
            stringBuffer.append(this.name);
            stringBuffer.append("].");
            stringBuffer2 = stringBuffer.toString();
        }
        errorHandler.error(stringBuffer2);
        return false;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void close() {
        this.closed = true;
    }

    protected Session createSession() {
        Properties properties;
        try {
            properties = new Properties(System.getProperties());
        } catch (SecurityException e) {
            properties = new Properties();
        }
        String str = this.smtpHost;
        if (str != null) {
            properties.put("mail.smtp.host", str);
        }
        Authenticator authenticator = null;
        if (this.smtpPassword != null && this.smtpUsername != null) {
            properties.put("mail.smtp.auth", "true");
            authenticator = new Authenticator() { // from class: org.apache.log4j.net.SMTPAppender.1
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTPAppender.this.smtpUsername, SMTPAppender.this.smtpPassword);
                }
            };
        }
        Session session = Session.getInstance(properties, authenticator);
        boolean z = this.smtpDebug;
        if (z) {
            session.setDebug(z);
        }
        return session;
    }

    InternetAddress getAddress(String str) {
        try {
            return new InternetAddress(str);
        } catch (AddressException e) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not parse address [");
            stringBuffer.append(str);
            stringBuffer.append("].");
            errorHandler.error(stringBuffer.toString(), e, 6);
            return null;
        }
    }

    public String getBcc() {
        return this.bcc;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public String getCc() {
        return this.cc;
    }

    public String getEvaluatorClass() {
        TriggeringEventEvaluator triggeringEventEvaluator = this.evaluator;
        if (triggeringEventEvaluator == null) {
            return null;
        }
        return triggeringEventEvaluator.getClass().getName();
    }

    public String getFrom() {
        return this.from;
    }

    public boolean getLocationInfo() {
        return this.locationInfo;
    }

    public boolean getSMTPDebug() {
        return this.smtpDebug;
    }

    public String getSMTPHost() {
        return this.smtpHost;
    }

    public String getSMTPPassword() {
        return this.smtpPassword;
    }

    public String getSMTPUsername() {
        return this.smtpUsername;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getTo() {
        return this.to;
    }

    InternetAddress[] parseAddress(String str) {
        try {
            return InternetAddress.parse(str, true);
        } catch (AddressException e) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not parse address [");
            stringBuffer.append(str);
            stringBuffer.append("].");
            errorHandler.error(stringBuffer.toString(), e, 6);
            return null;
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return true;
    }

    protected void sendBuffer() {
        String[] throwableStrRep;
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            StringBuffer stringBuffer = new StringBuffer();
            String header = this.layout.getHeader();
            if (header != null) {
                stringBuffer.append(header);
            }
            int length = this.cb.length();
            for (int i = 0; i < length; i++) {
                LoggingEvent loggingEvent = this.cb.get();
                stringBuffer.append(this.layout.format(loggingEvent));
                if (this.layout.ignoresThrowable() && (throwableStrRep = loggingEvent.getThrowableStrRep()) != null) {
                    for (String str : throwableStrRep) {
                        stringBuffer.append(str);
                        stringBuffer.append(Layout.LINE_SEP);
                    }
                }
            }
            String footer = this.layout.getFooter();
            if (footer != null) {
                stringBuffer.append(footer);
            }
            mimeBodyPart.setContent(stringBuffer.toString(), this.layout.getContentType());
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);
            this.msg.setContent(mimeMultipart);
            this.msg.setSentDate(new Date());
            Transport.send(this.msg);
        } catch (Exception e) {
            LogLog.error("Error occured while sending e-mail notification.", e);
        }
    }

    public void setBcc(String str) {
        this.bcc = str;
    }

    public void setBufferSize(int i) {
        this.bufferSize = i;
        this.cb.resize(i);
    }

    public void setCc(String str) {
        this.cc = str;
    }

    public void setEvaluatorClass(String str) {
        Class cls = class$org$apache$log4j$spi$TriggeringEventEvaluator;
        if (cls == null) {
            cls = class$("org.apache.log4j.spi.TriggeringEventEvaluator");
            class$org$apache$log4j$spi$TriggeringEventEvaluator = cls;
        }
        this.evaluator = (TriggeringEventEvaluator) OptionConverter.instantiateByClassName(str, cls, this.evaluator);
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public void setLocationInfo(boolean z) {
        this.locationInfo = z;
    }

    public void setSMTPDebug(boolean z) {
        this.smtpDebug = z;
    }

    public void setSMTPHost(String str) {
        this.smtpHost = str;
    }

    public void setSMTPPassword(String str) {
        this.smtpPassword = str;
    }

    public void setSMTPUsername(String str) {
        this.smtpUsername = str;
    }

    public void setSubject(String str) {
        this.subject = str;
    }

    public void setTo(String str) {
        this.to = str;
    }
}
