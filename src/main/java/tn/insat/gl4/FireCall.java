package tn.insat.gl4;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FireCall {
    @JsonProperty("address")
    private String address;

    @JsonProperty("type")
    private String type;

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("incident_number")
    private String incidentNumber;

    @JsonProperty("report_location")
    private ReportLocation reportLocation;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIncidentNumber() {
        return incidentNumber;
    }

    public void setIncidentNumber(String incidentNumber) {
        this.incidentNumber = incidentNumber;
    }

    public ReportLocation getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(ReportLocation reportLocation) {
        this.reportLocation = reportLocation;
    }

    // getters and setters
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ReportLocation {
    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private double[] coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    // getters and setters
}