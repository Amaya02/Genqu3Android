package com.gen.genqu3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver2 extends BroadcastReceiver {
    public AlarmReceiver2() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyNewIntentService2.class);
        context.startService(intent1);
    }
}
