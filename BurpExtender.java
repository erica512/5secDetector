package burp;

import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class BurpExtender implements IBurpExtender, IHttpListener
{
    private final static String EXTENDER_NAME = "5secDetector";
    private IBurpExtenderCallbacks callbacks;
    private PrintWriter stdout;
    private LocalTime timeReq = null;
    
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks){
        this.callbacks = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(),true);
        
        // set our extension name
        callbacks.setExtensionName(EXTENDER_NAME);
        callbacks.registerHttpListener(this);
        this.stdout.println("Load " + EXTENDER_NAME + " successfully!\n"
                + "Set the number of intruder thread to 1");
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if(toolFlag == 32){
            // intruder
            if(messageIsRequest == true){
                //record the time seinding request
                this.timeReq = LocalTime.now();
                }
            else{
                //response
                LocalTime timeRes = LocalTime.now();
                long diffSec = ChronoUnit.SECONDS.between(this.timeReq, timeRes);
                if (diffSec >= 10){
                    messageInfo.setHighlight("red");
                }
                else if(diffSec >= 5){
                    messageInfo.setHighlight("yellow");
                }
            }
        }
    }
}