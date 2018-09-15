package com.fblaproject.william.infinitelibrary;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Default Evernote Android-Job Code. Creates the job for future execution.
 */

public class AndroidJobCreator implements JobCreator{


    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        return new OverdueBookJob();
    }
}