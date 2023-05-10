package tn.insat.gl4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Arrays;
import java.util.List;

public class ApiReceiver implements Job {

    private static final String API_ENDPOINT = "https://data.seattle.gov/resource/kzjm-xkqj.json";
    private  String dataList = "";

    /* public static void main(String[] args) throws SchedulerException {
        // create a Quartz scheduler factory
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        // get a scheduler instance from the factory
        Scheduler scheduler = schedulerFactory.getScheduler();
        // create a job detail for the API request job
        JobDetail jobDetail = JobBuilder.newJob(ApiReceiver.class)
                .withIdentity("apiJob", "apiGroup")
                .build();
        // create a trigger for the job to run every 5 minutes
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("apiTrigger", "apiGroup")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
        // schedule the job with the trigger
        scheduler.scheduleJob(jobDetail, trigger);
        // start the scheduler
        scheduler.start();
    }*/

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // create an HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // create an HTTP GET request to the API endpoint
            HttpGet httpGet = new HttpGet(API_ENDPOINT);
            // execute the request and get the response
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // extract the response entity and convert it to a string
                HttpEntity entity = response.getEntity();
                String jsonString = EntityUtils.toString(entity);
                //System.out.println(jsonString);
                System.out.println("retrieving data from API ...");
                ObjectMapper mapper = new ObjectMapper();
                FireCall[] peopleArray = mapper.readValue(jsonString, FireCall[].class);
                List<FireCall> callList = Arrays.asList(peopleArray);

                System.out.println("performing operations on the retreived data ...");
                for (FireCall p : callList) {
                    dataList += p.getIncidentNumber()+" | "+p.getAddress()+" | "+p.getType()+" | "+p.getDatetime()+"\n";
                }
                dataList += "------------------------------------------------------------------------------------"+"\n" ;

            }
            catch (Exception e) {
                // handle any exceptions thrown by the API request
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("HTTPClient");
            // handle any exceptions thrown by the API request
            e.printStackTrace();
        }
    }

    public String getDataList() {
        return dataList;
    }
}