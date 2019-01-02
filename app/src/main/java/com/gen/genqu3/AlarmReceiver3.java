package com.gen.genqu3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver3 extends BroadcastReceiver {
    public AlarmReceiver3() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyNewIntentService3.class);
        context.startService(intent1);
    }
}
