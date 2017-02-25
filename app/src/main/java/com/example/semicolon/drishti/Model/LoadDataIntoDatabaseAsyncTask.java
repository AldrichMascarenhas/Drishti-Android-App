package com.example.semicolon.drishti.Model;

import android.os.AsyncTask;

/**
 * Created by Aldrich on 24-02-2017.
 */

public class LoadDataIntoDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {


    int random1;
    int random2;



    @Override
    protected void onPreExecute() {
        random1 = (int) Math.random() * 54322 + 1;
        random2 = (int) Math.random() * 54432 + 1;


    }

    @Override
    protected Void doInBackground(Void... voids) {


        Sessions session1 = new Sessions("20/02/2017", "Meeting", random1);
        session1.save();






        //Second

        Sessions session2 = new Sessions("20/02/2017", "Office Meet", random2);
        session2.save();




        Sessions session3 = new Sessions("20/02/2017", "Party", random2);
        session3.save();


        Sessions session4 = new Sessions("20/02/2017", "Breakfast", random2);
        session4.save();

        Sessions session5 = new Sessions("20/02/2017", "Social Meet+", random2);
        session5.save();
        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }


}
