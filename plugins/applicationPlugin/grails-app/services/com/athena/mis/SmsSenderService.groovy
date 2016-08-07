package com.athena.mis

import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.SysConfiguration
import org.apache.log4j.Logger

class SmsSenderService {

    Logger log = Logger.getLogger(getClass());

    private static final String UTF_8 = "UTF-8";
    private static final String RECIPIENT = "recipient";
    private static final String CONTENT = "content";
    private static final String COMA = ",";
    private static final String GET = "GET";

    AppConfigurationService appConfigurationService

    public synchronized void sendSms(AppSms sms) {
        try {
            String encodedContent = URLEncoder.encode(sms.getBody(), UTF_8);
            String phoneNumbers = sms.getRecipients();
            SysConfiguration sysConfigSmsUrl = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_SMS_URL, sms.companyId)
            for (String currentPhoneNumber : phoneNumbers.split(COMA)) {
                currentPhoneNumber = currentPhoneNumber.trim();
                // now evaluate full sms url
                Binding binding = new Binding();
                binding.setVariable(RECIPIENT, currentPhoneNumber);
                binding.setVariable(CONTENT, encodedContent);
                GroovyShell shell = new GroovyShell(binding);
                String strSms = (String) shell.evaluate(sysConfigSmsUrl.value);

                //open http connection to send sms
                URL smsUrl = new URL(strSms);
                HttpURLConnection smsConnection = (HttpURLConnection) smsUrl.openConnection();
                smsConnection.setDoOutput(false);
                smsConnection.setDoInput(true);
                smsConnection.setUseCaches(false);
                smsConnection.setDefaultUseCaches(false);
                smsConnection.setRequestMethod(GET);
                smsConnection.connect();

                //check response code
                smsConnection.getResponseCode();
                smsConnection.disconnect();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * send sms of which body contains variable
     * Note: sms must have recipients
     * @param sms - sms object
     * @param params - map of variables to evaluate body
     */
    public synchronized void send(AppSms sms, Map params) {
        Binding binding = new Binding()
        GroovyShell shell = new GroovyShell(binding)
        if (params && params.size() > 0) {
            params.each {
                binding.setVariable(it.key.toString(), it.value)
            }
        }
        sms.body = shell.evaluate(sms.body)
        sendSms(sms)
    }
}
