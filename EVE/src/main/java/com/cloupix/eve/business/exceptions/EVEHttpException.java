package com.cloupix.eve.business.exceptions;

import org.apache.http.HttpException;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class EVEHttpException extends HttpException
{
    private static final long serialVersionUID = 117L;

    private int statusCode;

    /**Constructors*/
    public EVEHttpException()
    {
        super();
        setStatusCode(0);
    }

    public EVEHttpException(int statusCode)
    {
        super();
        this.setStatusCode(statusCode);
    }

    public EVEHttpException(int statusCode, String message)
    {
        super(message);
        this.setStatusCode(statusCode);
    }

    /**Getters and Setters*/
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
