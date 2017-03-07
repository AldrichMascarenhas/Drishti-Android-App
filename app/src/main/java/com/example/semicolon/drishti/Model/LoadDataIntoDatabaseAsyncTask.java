package com.example.semicolon.drishti.Model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.semicolon.drishti.Dashboard;
import com.example.semicolon.drishti.SessionDetail;

/**
 * Created by Aldrich on 24-02-2017.
 */

public class LoadDataIntoDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {


    int random1;
    int random2;


    public LoadDataIntoDatabaseAsyncTask() {
    }

    @Override
    protected void onPreExecute() {
        random1 = (int) Math.random() * 54322 + 1;
        random2 = (int) Math.random() * 54432 + 1;


    }

    @Override
    protected Void doInBackground(Void... voids) {



        Sessions session5 = new Sessions("Sun, 26 Feb 2017, 06:30", "Park", random2, "Green Park, Verna", "Summary Summary Summary Summary Summary Summary Summary Summary Summary Summary Summary");
        session5.save();
        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //NEVER launch it again


    }


}
