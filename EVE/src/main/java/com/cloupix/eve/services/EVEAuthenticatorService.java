package com.cloupix.eve.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cloupix.eve.authentication.Authenticator;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class EVEAuthenticatorService extends Service
{

    @Override
    public IBinder onBind(Intent intent) {
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}
