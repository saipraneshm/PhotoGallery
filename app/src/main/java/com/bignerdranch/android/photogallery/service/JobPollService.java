package com.bignerdranch.android.photogallery.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;



/**
 * Created by sai pranesh on 12-Sep-16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobPollService extends JobService {



    private PollTask mCurrentTask;
    private Context mContext;

    public JobPollService(){}

    public JobPollService(Context context){
        mContext = context;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mCurrentTask = new PollTask();
        mCurrentTask.execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mCurrentTask != null) {
            mCurrentTask.cancel(true);
        }
        return true;
    }


    class PollTask extends AsyncTask<JobParameters,Void,Void>{

        @Override
        protected Void doInBackground(JobParameters... jobParameters) {

            JobParameters jobParams = jobParameters[0];
            startService(PollService.newIntent(mContext));
            jobFinished(jobParams,false);
            return null;
        }
    }

}
