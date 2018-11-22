package com.example.bozsi.animalabuse;

public class ReportController {
    public String getDatabaseName() {
        return BuildConfig.DatabaseName;
    }
    public String getApiKey() {
        return BuildConfig.Apikey;
    }
    public String getBaseUrl()
    {
        return "https://api.mlab.com/api/1/databases/"+getDatabaseName()+"/collections/";
    }
    public String apiKeyUrl()
    {
        return "?apiKey="+getApiKey();
    }
    public String collectionName()
    {
        return BuildConfig.CollectionName;
    }
    public String buildReportsSaveURL()
    {
        return getBaseUrl()+collectionName()+apiKeyUrl();
    }
    public String buildReportsFetchURL()
    {
        return getBaseUrl()+collectionName()+apiKeyUrl();
    }
    public String createReport(Report report) {
        return String.format("{\"username\": \"%s\", " + "\"image\": \"%s\", " + "\"longitude\": \"%s\", " + "\"latitude\": \"%s\"," + "\"message\": \"%s\"}", report.getUsername(), report.getImage(), report.getLongitude(), report.getLatitude(), report.getMessage());
    }
}
