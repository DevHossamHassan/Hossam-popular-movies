package me.branded.hossamhassan.hossam_popular_movies.Models;

/**
 * Created by HossamHassan on 4/23/2016.
 */
public class VideoResult
{
    private String id;

    private ResultArray[] results;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public ResultArray[] getResults ()
    {
        return results;
    }

    public void setResults (ResultArray[] results)
    {
        this.results = results;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", results = "+results+"]";
    }
}