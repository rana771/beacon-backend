package com.athena.mis

import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService

import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MailSenderService {

    private static final String GMAIL_SMTP = "smtp.gmail.com";
    private static final String AWS_SMTP = "email-smtp.us-east-1.amazonaws.com";
    private static final String MAIL_HOST = "mail.smtp.host";
    private static final String MAIL_PORT = "mail.smtp.port";
    private static final String SMTP_AUTH = "mail.smtp.auth";
    private static final String SMTP_SSL = "mail.smtp.ssl.enable";
    private static final String SMTP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_MIME = "text/html; charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TRUE = "true";
    private static final String EMPTY_SPACE = "";
    private static final String SMTP = "smtp";

    AppSystemEntityCacheService appSystemEntityCacheService

    private String emailFrom;
    private SystemEntity smtpHost;
    private SystemEntity smtpPort;
    private SystemEntity smtpEmail;
    private SystemEntity smtpPwd;

    // get system entity object for configuration
    private void resolveMailConfiguration(AppMail appMail) {
        emailFrom = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_MAIL_CONFIG_EMAIL_FROM, appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, appMail.companyId).value
        if(appMail.emailFrom){
            emailFrom = appMail.emailFrom
        }
        smtpHost = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_MAIL_CONFIG_SMTP_HOST, appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, appMail.companyId)
        smtpPort = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_MAIL_CONFIG_SMTP_PORT, appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, appMail.companyId)
        smtpEmail = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_MAIL_CONFIG_SMTP_EMAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, appMail.companyId)
        smtpPwd = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_MAIL_CONFIG_SMTP_PWD, appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, appMail.companyId)
    }

    // send mail
    public synchronized void sendMail(AppMail appMail) {
        try {
            if (appMail == null) return;
            // get session object
            Session session = getSession(appMail);
            // get message
            MimeMessage message = getMessage(session, appMail);

            // init body part
            Multipart multiPart = new MimeMultipart();
            MimeBodyPart bodyPartText = new MimeBodyPart();
            bodyPartText.setHeader(CONTENT_TYPE, MAIL_MIME);
            bodyPartText.setContent(appMail.getBody(), MAIL_MIME);

            // Add body parts to multi part
            multiPart.addBodyPart(bodyPartText);
            //set all content
            message.setContent(multiPart);
            // send mail
            Transport.send(message);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    // send mail with attachment
    public synchronized void sendMail(AppMail appMail, MailAttachment attachment) {
        try {
            if (appMail == null) return;
            // get session object
            Session session = getSession(appMail);
            // get message
            MimeMessage message = getMessage(session, appMail);

            // init body part
            Multipart multiPart = new MimeMultipart();
            MimeBodyPart bodyPartText = new MimeBodyPart();
            bodyPartText.setHeader(CONTENT_TYPE, MAIL_MIME);
            bodyPartText.setContent(appMail.getBody(), MAIL_MIME);

            // Add body parts to multi part
            multiPart.addBodyPart(bodyPartText);

            //add content part if mail has attachment
            if ((attachment.getAttachment() != null) && attachment.getAttachment().length > 0) {
                // create body part for Mail-Attachment
                MimeBodyPart bodyPartContent = new MimeBodyPart();
                bodyPartContent.setDataHandler(
                        new DataHandler(
                                new DataSource() {
                                    public String getContentType() {
                                        return attachment.getAttachmentMimeType();
                                    }

                                    public InputStream getInputStream() throws IOException {
                                        return new ByteArrayInputStream(attachment.getAttachment());
                                    }

                                    public String getName() {
                                        return null;
                                    }

                                    public OutputStream getOutputStream() throws IOException {
                                        return null;
                                    }
                                }
                        )
                );
                bodyPartContent.setFileName(attachment.getAttachmentName());
                multiPart.addBodyPart(bodyPartContent);
            }

            //set all content
            message.setContent(multiPart);
            // send mail
            Transport.send(message);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    // get message
    private MimeMessage getMessage(Session session, AppMail appMail) {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom, appMail.getDisplayName()));

        Address[] address = [new InternetAddress(emailFrom)]
        message.setReplyTo(address)

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(appMail.getRecipients()));
        if ((appMail.getRecipientsCc() != null) && !appMail.getRecipientsCc().equals(EMPTY_SPACE)) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(appMail.getRecipientsCc()));
        }
        message.setSubject(appMail.getSubject());
        return message;
    }

    // get session object
    private Session getSession(AppMail appMail) {
        Properties props = new Properties();
        resolveMailConfiguration(appMail);

        props.put(MAIL_HOST, smtpHost.value);
        props.put(MAIL_PORT, smtpPort.value);
        if (smtpHost.value.equalsIgnoreCase(GMAIL_SMTP)) {     //only set when using GMAIL smtp
            props.put(SMTP_STARTTLS, TRUE);
            props.put(SMTP_AUTH, TRUE);
            props.put(SMTP_SSL, TRUE);
        } else if (smtpHost.value.equalsIgnoreCase(AWS_SMTP)) {
            props.put(MAIL_TRANSPORT_PROTOCOL, SMTP);
            props.put(SMTP_AUTH, TRUE);
            props.put(SMTP_STARTTLS, TRUE);
            props.put(SMTP_STARTTLS_REQUIRED, TRUE);
        }

        // authenticate session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpEmail.value, smtpPwd.value);
                    }
                }
        );
        return session;
    }

    // send mail with attachment
    public synchronized void sendMail(AppMail appMail, List<MailAttachment> lstAttachment) {
        try {
            if (appMail == null) return;
            // get session object
            Session session = getSession(appMail);
            // get message
            MimeMessage message = getMessage(session, appMail);

            // init body part
            Multipart multiPart = new MimeMultipart();
            MimeBodyPart bodyPartText = new MimeBodyPart();
            bodyPartText.setHeader(CONTENT_TYPE, MAIL_MIME);
            bodyPartText.setContent(appMail.getBody(), MAIL_MIME);

            // Add body parts to multi part
            multiPart.addBodyPart(bodyPartText);

            //add content part if mail has attachment
            for (int i = 0; i < lstAttachment.size(); i++) {
                MailAttachment attachment = lstAttachment[i];
                if ((attachment.getAttachment() != null) && attachment.getAttachment().length > 0) {
                    // create body part for Mail-Attachment
                    MimeBodyPart bodyPartContent = new MimeBodyPart();
                    bodyPartContent.setDataHandler(
                            new DataHandler(
                                    new DataSource() {
                                        public String getContentType() {
                                            return attachment.getAttachmentMimeType();
                                        }

                                        public InputStream getInputStream() throws IOException {
                                            return new ByteArrayInputStream(attachment.getAttachment());
                                        }

                                        public String getName() {
                                            return null;
                                        }

                                        public OutputStream getOutputStream() throws IOException {
                                            return null;
                                        }
                                    }
                            )
                    );
                    bodyPartContent.setFileName(attachment.getAttachmentName());
                    multiPart.addBodyPart(bodyPartContent);
                }
            }

            //set all content
            message.setContent(multiPart);
            // send mail
            Transport.send(message);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
