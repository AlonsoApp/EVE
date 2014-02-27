package com.cloupix.eve.business.exceptions;

import org.apache.http.HttpException;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class EveHttpException extends HttpException
{
    private static final long serialVersionUID = 117L;

    private int statusCode;

    /**Constructors*/
    public EveHttpException()
    {
        super();
        setStatusCode(0);
    }

    public EveHttpException(int statusCode)
    {
        super();
        this.setStatusCode(statusCode);
    }

    public EveHttpException(int statusCode, String message)
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
